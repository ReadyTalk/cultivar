package org.waterprinciple.cultivar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionStateLoggerTest {

    @Mock
    private Logger logger;

    @Mock
    private CuratorFramework framework;

    private ConnectionStateLogger connectionStateLogger;

    @Before
    public void setUp() {
        connectionStateLogger = new ConnectionStateLogger(logger);
    }

    @Test
    public void stateChanged_AnyState_LogsInfo() {
        connectionStateLogger.stateChanged(framework, ConnectionState.RECONNECTED);

        verify(logger).info(anyString(), any(ConnectionState.class));
    }


}
