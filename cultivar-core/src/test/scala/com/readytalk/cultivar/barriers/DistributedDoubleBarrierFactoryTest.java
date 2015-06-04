package com.readytalk.cultivar.barriers;

import static org.junit.Assert.assertNotNull;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DistributedDoubleBarrierFactoryTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private CuratorFramework framework;

    private DistributedDoubleBarrierFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new DistributedDoubleBarrierFactory(framework);
    }

    @Test
    public void doubleBarrier_ValidPathPositiveValue_ReturnsBarrier() {
        assertNotNull(factory.create("/path", 1));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void doubleBarrier_NullPath_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        factory.create(null, 1);
    }

    @Test
    public void doubleBarrier_ZeroSize_ThrowsIAE() {
        thrown.expect(IllegalArgumentException.class);

        factory.create("/path", 0);
    }
}
