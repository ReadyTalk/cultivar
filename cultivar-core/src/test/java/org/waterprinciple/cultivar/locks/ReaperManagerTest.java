package org.waterprinciple.cultivar.locks;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.apache.curator.framework.recipes.locks.Reaper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReaperManagerTest {

    @Mock
    private Reaper reaper;

    private ReaperManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new ReaperManager(reaper);
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
