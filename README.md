Cultivar
========

Cultivar is a lifecycle manager around [Curator](http://curator.apache.org) using [Guice](https://code.google.com/p/google-guice/) and [Guava](https://code.google.com/p/guava-libraries/). It is mostly an excuse for me to learn Zookeeper and Curator by trying to think through how to build a generic lifecycle system around it.

What is Cultivar?
-----------------

Curator is in many ways a great framework, but it is rather unopinionated about how you use it relative to the lifecycle of your application. Things need to be started and shut down, but you can create them in the middle of your application and tear them down shortly thereafter, or they can run for the entire life of your app.  Cultivar makes the following assumptions about how one is using Curator/Zookeeper:

 * You are using Guice or can at least figure out how to strap a few Guice modules into your application lifecycle.
 * You know what Curator objects you will need around the time your application starts up (this will change in the future as an absolute requirement, but is still going to be the primary method of using Cultivar).
 * Your use cases in how you access services are relatively homogenous (so if you access service A using a round-robin strategy from Service X you will probably use a round robin strategy from Service Y as well) and you want the ability to share these practices in the form of a client library.
 
Philosophy
----------

Core principles behind using Cultivar:

* Builders are used for constructing Guice Modules that inject specific objects.
* Construction of objects is, to the degree that it is reasonable, *fail-fast*. If Cultivar knows that a value should not be `null`, it will try to tell you that when you set the value, rather than at the time the `Module` is built. If something is required for a `Module` to be constructed, it will try to tell you at the time the `Module` is constructed, rather than at the time the Injector is built, etc. 
* The lifecycle of most objects in the most common use cases is to spin everything up at approximately the same time when the application starts, then to tear it down when the application is shut down.
* Modules should be *reusable* between applications using a client library.  
* In the general case, you are using Curator objects directly or using encapsulation, rather than inheritance. 

Building and Testing
--------------------

```bash
$ ./gradlew build
```

This will:

 * Compile everything.
 * Validate against [FindBugs](http://findbugs.sourceforge.net) and [Checkstyle](http://checkstyle.sourceforge.net).
 * Run the unit tests.
 * Run the integration tests.
 * Put together code quality and code coverage reports.


General Usage
-------------

For general usage you might do something like this:

```java
public class DiscoveryModule extends AbstractModule {
    private static final String NAMESPACE_PROPERTY = "config.zookeeper.namespace";

    @Override
    protected void configure() {

        String namespace = System.getProperty(NAMESPACE_PROPERTY);

        if (namespace == null) {
            throw new IllegalStateException("Namespace needs to be set in order to continue.");
        }

        bindConstant().annotatedWith(Names.named("Cultivar.Curator.baseNamespace")).to(namespace);

        install(new EnsembleProviderModule());
        install(new MetricsModule());
        install(new CuratorModule());
        install(LeaderServiceModuleBuilder
                        .create(Key.get(ScheduledLoggingLeaderService.class, Names.named("service1")))
                        .implementation(ScheduledLoggingLeaderService.class)
                        .build());
    }

    @Curator
    @Singleton
    @Provides
    RetryPolicy retryPolicy() {
        return new BoundedExponentialBackoffRetry(100, 5000, 10);
    }
}

```

Then, where you started up the application:

```java
Injector inj = Guice.createInjector(/*...*/);

inj.getInstance(CultivarStartStopManager.class).startAsync();
```

This would:

 * Initialize a Curator instance with a given [namespace](http://curator.apache.org/curator-framework/#namespace).
 * Throw an exception on the Guice instantiation if the namespace is not set.
 * Use system properties or environment variables to build either a FixedEnsembleProvider or a Exhibitor-based EnsembleProvider.
 * Use the specified [retry policy](http://curator.apache.org/apidocs/org/apache/curator/RetryPolicy.html) within Curator.
 * Build a ScheduledLoggingLeaderService, bound with the appropriate annotation.
 * Hook in your [MetricRegistry](http://metrics.codahale.com) into the [TracerDriver](https://curator.apache.org/apidocs/org/apache/curator/drivers/TracerDriver.html) to report to your existing metrics services.
 * Start everything asynchronously.
 * Ensure that the Curator instance is started first, then any additional services.

### Tearing Down

When the application is shutting down:

```java
inj.getInstance(CultivarStartStopManager.class).stopAsync().awaitTerminated();
```

This will stop any services started by Cultivar, then close out the `CuratorFramework` instance.


Discovery
---------

Thinking of a server as three components:

 * The **protocol**, which the server implements and the client knows how to talk to.
 * The **server**, which needs to be discovered.
 * The **client**, which discovers the server and knows how to talk to it.

### Protocol

```java
@Qualifier
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
@Beta
public @interface DiscoveryAnnotation {
}


public class DiscoveryProtocolModule extends AbstractModule {
    @Override
    protected void configure() {

        install(ServiceDiscoveryModuleBuilder.create(DiscoveryType.class).basePath("/servicename/discovery")
                .annotation(DiscoveryAnnotation.class).build());
    }
}
```

### Server

```java
public class DiscoveryServerModule extends AbstractModule {
    @Override
    protected void configure() {
        /*...*/

        install(new DiscoveryProtocolModule());
    }

    /*...*/
}

```

Then the server is responsible for registering any provided services after `startAsync()` has been called:

```java
ServiceInstance<DiscoveryType> service = ServiceInstance.<DiscoveryType> builder().name("service").payload(data).build();

discovery.registerService(service1);
```

### Client

```java
public class DiscoveryClientModule extends AbstractModule {
    @Override
    protected void configure() {

        install(new DiscoveryProtocolModule());
        install(ServiceProviderModuleBuilder.create(DiscoveryType.class).annotation(DiscoveryAnnotation.class)
                .discovery(DiscoveryAnnotation.class).build());

    }
}
```

Any server that implements the client library is then responsible for installing the `DiscoveryClientModule` and starting Cultivar.

Note that both the client and the server may specify multiple annotations to cover several different services.

Testing
-------

Cultivar provides a few utilities for helping test ZK based services.

### AbstractZookeeperClusterTest

On a per-test basis:

 * Spins up a 3-node ZK cluster.
 * Tears down that cluster.
 * Logs information about that cluster.
 * Enforces a global timeout on tests.

In order to use it simply extend it:

```java
public class DiscoveryIntegTest extends AbstractZookeeperClusterTest {
  /* ... */
}
```

### ConditionalWait

Sometimes you have to wait for the ZK cluster to receive and propagate the information to another object. This tool will spin–testing periodically–to see if the passed in condition is met.

For example, in testing to for when a different server instances has received a registered service:


```java
new ConditionalWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !provider.getAllInstances().isEmpty();
            }
        }).await();
```

Future Work
-----------

In no particular order:

 * Support more of the patterns from Curator.
 * Create clean patterns around post-initialization creation of Curator patterns.
 * Allow for multiple ZK instances.
 * Cleanly allow for differing namespaces.
 * Allow the use of bound objects instead of instances for things like `ProviderStrategy`.
 * Create HealthCheck that returns unhealthy until the initial connection is established.
 * Switch from `Preconditions` to `Verification` where appropriate. 

Cultivar can still help with many of the "unsupported" use cases since it will already manage starting the relevant services, however, they can be made much easier and more automatic.
