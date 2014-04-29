package org.waterprinciple.cultivar.locks;

import static org.mockito.Mockito.verify;

import org.apache.curator.framework.recipes.locks.ChildReaper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChildReaperManagerTest {

    @Mock
    private ChildReaper reaper;

    private ChildReaperManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new ChildReaperManager(reaper);
    }

    @Test
    public void startUp_DelegatesToReaper() throws Exception {
        manager.startUp();

        verify(reaper).start();
    }

    @Test
    public void shutdown_DelegatesToReaper() throws Exception {
        manager.shutDown();

        verify(reaper).close();
    }

}
