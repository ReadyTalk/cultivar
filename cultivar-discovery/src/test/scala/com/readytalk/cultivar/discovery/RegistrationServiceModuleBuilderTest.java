package com.readytalk.cultivar.discovery;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import com.readytalk.cultivar.Cultivar;

@RunWith(Enclosed.class)
@SuppressWarnings("unchecked")
public class RegistrationServiceModuleBuilderTest {
    public static final ServiceInstance<Void> INSTANCE = generateInstance();

    private static ServiceInstance<Void> generateInstance() {
        try {
            return ServiceInstance.<Void> builder().address("localhost").name("foo").build();
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    public static class BuilderTest {

        public static class ServiceInstanceProvider implements Provider<ServiceInstance<Void>> {
            @Override
            public ServiceInstance<Void> get() {
                return INSTANCE;
            }
        }

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Test
        public void updating_ZeroTime_ThrowsIAE() {
            thrown.expect(IllegalArgumentException.class);

            RegistrationServiceModuleBuilder.create().updating(0, TimeUnit.SECONDS);

        }

        @Test
        public void build_DiscoveryNotSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            RegistrationServiceModuleBuilder.create().provider(ServiceInstanceProvider.class)
                    .targetAnnotation(Cultivar.class).build();

        }

        @Test
        public void build_TargetNotSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            RegistrationServiceModuleBuilder.create().provider(ServiceInstanceProvider.class)
                    .discoveryAnnotation(Cultivar.class).build();

        }

        @Test
        public void build_ProviderNotSet_ThrowsISE() {
            thrown.expect(IllegalStateException.class);

            RegistrationServiceModuleBuilder.create().targetAnnotation(Names.named("target"))
                    .discoveryAnnotation(Names.named("discovery")).build();

        }

        @Test
        public void build_AllValuesSetWithProviderInstance_ReturnsNotNull() {
            assertNotNull(RegistrationServiceModuleBuilder.create().targetAnnotation(Names.named("target"))
                    .discoveryAnnotation(Names.named("discovery")).provider(new ServiceInstanceProvider()).build());

        }

        @Test
        public void build_AllValuesSetWithProviderKey_ReturnsNotNull() {
            assertNotNull(RegistrationServiceModuleBuilder.create().targetAnnotation(Names.named("target"))
                    .discoveryAnnotation(Names.named("discovery")).provider(Key.get(ServiceInstanceProvider.class))
                    .build());

        }

        @Test
        public void build_AllValuesSetWithProviderTypeLiteral_ReturnsNotNull() {
            assertNotNull(RegistrationServiceModuleBuilder.create().targetAnnotation(Names.named("target"))
                    .discoveryAnnotation(Names.named("discovery")).provider(new TypeLiteral<ServiceInstanceProvider>() {
                    }).build());

        }

    }

    @RunWith(MockitoJUnitRunner.class)
    public static class ModuleTest {

        public static class ServiceInstanceProvider implements Provider<ServiceInstance<Void>> {
            @Override
            public ServiceInstance<Void> get() {
                return INSTANCE;
            }
        }

        @Rule
        public final ExpectedException thrown = ExpectedException.none();

        @Mock
        private ServiceDiscovery<Void> discovery;

        private Module module;

        @Before
        public void setUp() {
            module = RegistrationServiceModuleBuilder.create().targetAnnotation(Cultivar.class)
                    .discoveryAnnotation(Cultivar.class).provider(new ServiceInstanceProvider()).build();
        }

        @Test
        public void createInjector_NoDiscoveryDefined_ThrowsCreationException() {
            thrown.expect(CreationException.class);

            Guice.createInjector(module);
        }

        @Test
        public void createInjector_DiscoveryDefined_ReturnsNotNull() {

            assertNotNull(Guice.createInjector(Stage.PRODUCTION, module, new AbstractModule() {
                @Override
                protected void configure() {
                    bind(
                            (Key<ServiceDiscovery<Void>>) Key.get(
                                    Types.newParameterizedType(ServiceDiscovery.class, Void.class), Cultivar.class))
                            .toInstance(discovery);
                }
            }));
        }

        @Test
        public void createInjector_DiscoveryDefinedAndScheduleDefinedWithExecutor_ReturnsNotNull() {

            assertNotNull(Guice.createInjector(
                    RegistrationServiceModuleBuilder.create().targetAnnotation(Cultivar.class)
                            .discoveryAnnotation(Cultivar.class).provider(new ServiceInstanceProvider())
                            .updating(10, TimeUnit.SECONDS, new ScheduledThreadPoolExecutor(1)).build(),
                    new AbstractModule() {
                        @Override
                        protected void configure() {
                            bind(
                                    (Key<ServiceDiscovery<Void>>) Key.get(
                                            Types.newParameterizedType(ServiceDiscovery.class, Void.class),
                                            Cultivar.class)).toInstance(discovery);
                        }
                    }));
        }

        @Test
        public void createInjector_DiscoveryDefinedAndScheduleDefinedSansExecutor_ReturnsNotNull() {

            assertNotNull(Guice.createInjector(
                    RegistrationServiceModuleBuilder.create().targetAnnotation(Cultivar.class)
                            .discoveryAnnotation(Cultivar.class).provider(new ServiceInstanceProvider())
                            .updating(10, TimeUnit.SECONDS).build(), new AbstractModule() {
                        @Override
                        protected void configure() {
                            bind(
                                    (Key<ServiceDiscovery<Void>>) Key.get(
                                            Types.newParameterizedType(ServiceDiscovery.class, Void.class),
                                            Cultivar.class)).toInstance(discovery);
                        }
                    }));
        }

    }

}
