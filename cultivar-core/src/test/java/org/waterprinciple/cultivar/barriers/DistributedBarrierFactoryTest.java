package org.waterprinciple.cultivar.barriers;

import static org.junit.Assert.assertNotNull;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("ConstantConditions")
@RunWith(MockitoJUnitRunner.class)
public class DistributedBarrierFactoryTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private CuratorFramework framework;

    private DistributedBarrierFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new DistributedBarrierFactory(framework);
    }

    @Test
    public void barrier_ValidPath_ReturnsBarrier() {
        assertNotNull(factory.create("/path"));
    }

    @Test
    public void barrier_NullPath_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        factory.create(null);
    }

}
