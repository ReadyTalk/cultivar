package com.readytalk.cultivar.locks;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.SetDataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RevokerTest {

    /**
     * Pulled from inside of Curator's LockInternals.REVOKE_MESSAGE. It is equivalent to "__REVOKE__".getBytes().
     */
    private final byte[] revocationValue = new byte[] { 95, 95, 82, 69, 86, 79, 75, 69, 95, 95 };

    @Mock
    private CuratorFramework framework;

    @Mock
    private SetDataBuilder setDataBuilder;

    private Revoker revoker;

    @Before
    public void setUp() throws Exception {
        when(framework.setData()).thenReturn(setDataBuilder);

        revoker = new Revoker(framework);
    }

    @Test
    public void attemptRevoke_LockPath_SetsRevokeMessageOnPath() throws Exception {
        revoker.attemptRevoke("/path");

        verify(setDataBuilder).forPath(eq("/path"), eq(revocationValue));
    }
}
