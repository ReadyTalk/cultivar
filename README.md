Cultivar
========

[![Build Status](https://drone.io/github.com/dclements/cultivar/status.png)](https://drone.io/github.com/dclements/cultivar/latest)

Cultivar is a lifecycle manager around [Curator](http://curator.apache.org) using [Guice](https://code.google.com/p/google-guice/) and [Guava](https://code.google.com/p/guava-libraries/). It is mostly an excuse for me to learn Zookeeper and Curator by trying to think through how to build a generic lifecycle system around it. 

What is Cultivar?
-----------------

Curator is in many ways a great framework, but it is rather unopinionated about how you use it relative to the lifecycle of your application. Things need to be started and shut down, but you can create them in the middle of your application and tear them down shortly thereafter, or they can run for the entire life of your app.  Cultivar makes the following assumptions about how one is using Curator/Zookeeper:

 * You are using Guice or can at least figure out how to strap a few Guice modules into your application lifecycle. 
 * You know what Curator objects you will need around the time your application starts up (this will change in the future as an absolute requirement, but is still going to be the primary method of using Cultivar).
 * Your use cases in how you access services are relatively homogenous (so if you access service A using a round-robin strategy from Service X you will probably use a round robin strategy from Service Y as well) and you want the ability to share these practices in the form of a client library.
 
Future Work
-----------

In no particular order:

 * Support more of the patterns from Curator.
 * Create clean patterns around post-initialization creation of Curator patterns.
 * Allow for multiple ZK instances.
 * Allow the use of bound objects instead of instances for things like `ProviderStrategy`.
 * Document various binding options, making it clearer how to configure instances.
