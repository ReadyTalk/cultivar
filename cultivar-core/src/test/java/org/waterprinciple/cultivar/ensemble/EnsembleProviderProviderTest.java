package org.waterprinciple.cultivar.ensemble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Optional;

public class EnsembleProviderProviderTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void get_ExhibitorNotPresent_ReturnsFixedEnsembleWithConnectionString() {
        String conn = "localhost:2181";

        EnsembleProviderProvider provider = new EnsembleProviderProvider(Optional.<String> absent(), Optional.of(conn));

        EnsembleProvider ensembleProvider = provider.get();

        assertTrue(ensembleProvider instanceof FixedEnsembleProvider);
        assertEquals(conn, ensembleProvider.getConnectionString());
    }

    @Test
    public void cons_BackupConnectionsNotPresent_ThrowsIAE() {
        thrown.expect(IllegalArgumentException.class);

        new EnsembleProviderProvider(Optional.of("exhibitors"), Optional.<String> absent());
    }

    @Test
    public void get_ExhibitorPresentWithBackup_ReturnsExhbitorEnsembleProvider() {
        String conn = "localhost:2181";
        String exhbitor = "exhibitor:2181";

        EnsembleProviderProvider provider = new EnsembleProviderProvider(Optional.of(exhbitor), Optional.of(conn));

        EnsembleProvider ensembleProvider = provider.get();

        assertTrue(ensembleProvider instanceof ExhibitorEnsembleProvider);
    }
}
