package com.readytalk.cultivar.health;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.util.concurrent.Service;
import com.readytalk.cultivar.CultivarStartStopManager;

@RunWith(MockitoJUnitRunner.class)
public class CuratorManagerStatusTest {

    @Mock
    private CultivarStartStopManager startStopManager;

    private CuratorManagerStatus test;

    @Before
    public void setUp() throws Exception {
        test = new CuratorManagerStatus(startStopManager);
    }

    @Test
    public void check_ManagerNotStarted_ReturnsHealthy() throws Exception {
        when(startStopManager.state()).thenReturn(Service.State.NEW);

        assertTrue(test.check().isHealthy());
    }

    @Test
    public void check_ManagerRunning_ReturnsHealthy() throws Exception {
        when(startStopManager.state()).thenReturn(Service.State.RUNNING);

        assertTrue(test.check().isHealthy());
    }

    @Test
    public void check_ManagerFailed_ReturnsUnhealthyWithError() throws Exception {
        Exception ex = new IOException();

        when(startStopManager.state()).thenReturn(Service.State.FAILED);

        when(startStopManager.failureCause()).thenReturn(ex);

        assertEquals(ex, test.check().getError());
    }

    @Test
    public void check_ManagerTerminated_ReturnsUnhealthy() throws Exception {

        when(startStopManager.state()).thenReturn(Service.State.TERMINATED);

        assertFalse(test.check().isHealthy());
    }

    @Test
    public void check_ManagerStopping_ReturnsUnhealthy() throws Exception {

        when(startStopManager.state()).thenReturn(Service.State.STOPPING);

        assertFalse(test.check().isHealthy());
    }

}
