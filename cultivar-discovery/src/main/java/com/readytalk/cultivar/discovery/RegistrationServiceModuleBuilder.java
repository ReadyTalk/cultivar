package com.readytalk.cultivar.discovery;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnegative;
import javax.inject.Provider;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;
import com.readytalk.cultivar.internal.AnnotationHolder;
import com.readytalk.cultivar.internal.BinderAssistant;
import com.readytalk.cultivar.internal.Private;

/**
 *
 * Constructs a service that registers itself when started with discovery. This is similar to using the Curator built-in
 * instance registration, with a few tweaks:
 * 
 * <ul>
 * <li>Allows/encourages the rest of the system to finish starting up before registration takes place.</li>
 * <li>Allows/encourages deregistration before the rest of the system has been torn down.</li>
 * <li>Allows for the slightly more complex scenario of self-updating instances.</li>
 * <li>Facilitates the use-case where one service may register multiple discovery endpoints.</li>
 * </ul>
 *
 * @param <T>
 *            The type of the Service Discovery system.
 */
@Beta
public class RegistrationServiceModuleBuilder<T> {

    private final Class<T> clazz;

    private AnnotationHolder discoveryHolder = null;

    private AnnotationHolder targetHolder = null;

    private BinderAssistant<ServiceInstance<T>> binderAssistant;

    private AbstractScheduledService.Scheduler scheduler = null;

    private ScheduledExecutorService executorService = null;

    @SuppressWarnings("rawtypes")
    private Class<? extends RegistrationService> bindType = DefaultRegistrationService.class;

    RegistrationServiceModuleBuilder(final Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Creates a RegistrationServiceModuleBuilder of Void type.
     */
    public static RegistrationServiceModuleBuilder<Void> create() {
        return create(Void.class);
    }

    /**
     * Creates a RegistrationServiceModuleBuilder.
     */
    public static <T> RegistrationServiceModuleBuilder<T> create(final Class<T> clazz) {
        return new RegistrationServiceModuleBuilder<>(clazz);
    }

    /**
     * The annotation to put the RegistrationService under.
     */
    public RegistrationServiceModuleBuilder<T> targetAnnotation(final Annotation annotation) {

        targetHolder = AnnotationHolder.create(annotation);

        return this;
    }

    /**
     * The annotation to put the RegistrationService under.
     */
    public RegistrationServiceModuleBuilder<T> targetAnnotation(final Class<? extends Annotation> annotation) {

        targetHolder = AnnotationHolder.create(annotation);

        return this;
    }

    /**
     * The annotation to look up the DiscoveryService under.
     */
    public RegistrationServiceModuleBuilder<T> discoveryAnnotation(final Annotation annotation) {

        discoveryHolder = AnnotationHolder.create(annotation);

        return this;
    }

    /**
     * The annotation to put the RegistrationService under.
     */
    public RegistrationServiceModuleBuilder<T> discoveryAnnotation(final Class<? extends Annotation> annotation) {

        discoveryHolder = AnnotationHolder.create(annotation);

        return this;
    }

    /**
     * Reference to the provider for the ServiceInstance to use. This should be a consistent instance between calls,
     * though it is allowed to change.
     */
    public RegistrationServiceModuleBuilder<T> provider(
            final Class<? extends Provider<ServiceInstance<T>>> instanceProvider) {

        binderAssistant = new BinderAssistant<>(instanceProvider);

        return this;
    }

    public RegistrationServiceModuleBuilder<T> provider(
            final Key<? extends Provider<ServiceInstance<T>>> instanceProvider) {

        binderAssistant = new BinderAssistant<>(instanceProvider);

        return this;
    }

    public RegistrationServiceModuleBuilder<T> provider(final Provider<? extends ServiceInstance<T>> instanceProvider) {

        binderAssistant = new BinderAssistant<>(instanceProvider);

        return this;
    }

    public RegistrationServiceModuleBuilder<T> provider(
            final TypeLiteral<? extends Provider<ServiceInstance<T>>> instanceProvider) {

        binderAssistant = new BinderAssistant<>(instanceProvider);

        return this;
    }

    public RegistrationServiceModuleBuilder<T> updating(@Nonnegative final long time, final TimeUnit unit) {
        return this.updating(time, unit,
                MoreExecutors.getExitingScheduledExecutorService(new ScheduledThreadPoolExecutor(1)));
    }

    /**
     * Set the bound instance to update from the provider at a certain delay.
     * 
     * @param time
     *            The amount of time to spend before the first registration and between registrations.
     * @param unit
     *            The time unit for time.
     * @param service
     *            An optional ScheduledExecutorService. By default this will be an exitingScheduledExecutorService with
     *            default parameters.
     */
    @SuppressWarnings("unchecked")
    public RegistrationServiceModuleBuilder<T> updating(@Nonnegative final long time, final TimeUnit unit,
            final ScheduledExecutorService service) {
        checkArgument(time > 0, "Time must be positive.");
        checkNotNull(unit, "TimeUnit must not be null.");
        checkNotNull(service, "Provided executor service must not be null.");

        scheduler = AbstractScheduledService.Scheduler.newFixedDelaySchedule(time, time, unit);

        executorService = service;

        bindType = UpdatingRegistrationService.class;

        return this;
    }

    public Module build() {
        checkState(targetHolder != null, "Target annotation has not been set.");
        checkState(discoveryHolder != null, "Discovery annotation has not been set.");
        checkState(binderAssistant != null, "No provider provided.");

        return new AbstractModule() {
            @SuppressWarnings("unchecked")
            @Override
            protected void configure() {
                final Key<RegistrationService<T>> registrationServiceKey = (Key<RegistrationService<T>>) targetHolder
                        .generateKey(Types.newParameterizedType(RegistrationService.class, clazz));

                install(new PrivateModule() {
                    @SuppressWarnings("unchecked")
                    @Override
                    protected void configure() {

                        if (scheduler != null) {
                            bind(AbstractScheduledService.Scheduler.class).annotatedWith(Private.class).toInstance(
                                    scheduler);
                            bind(ScheduledExecutorService.class).annotatedWith(Private.class).toInstance(
                                    executorService);
                        }

                        bind(
                                (Key<ServiceDiscovery<T>>) Key.get(
                                        Types.newParameterizedType(ServiceDiscovery.class, clazz), Private.class)).to(
                                (Key<ServiceDiscovery<T>>) discoveryHolder.generateKey(Types.newParameterizedType(
                                        ServiceDiscovery.class, clazz)));

                        binderAssistant.bindToProvider(
                                binder(),
                                (Key<ServiceInstance<T>>) Key.get(
                                        Types.newParameterizedType(ServiceInstance.class, clazz), Private.class));

                        bind(registrationServiceKey).to(
                                (Key<? extends RegistrationService<T>>) Key.get(Types.newParameterizedType(bindType,
                                        clazz))).in(Scopes.SINGLETON);

                        expose(registrationServiceKey);
                    }
                });

                Multibinder<RegistrationService<?>> services = Multibinder.newSetBinder(binder(),
                        new TypeLiteral<RegistrationService<?>>() {
                        });

                services.addBinding().to(registrationServiceKey);

            }
        };
    }
}
