package com.readytalk.cultivar.ensemble;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.exhibitor.DefaultExhibitorRestClient;
import org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider;
import org.apache.curator.ensemble.exhibitor.ExhibitorRestClient;
import org.apache.curator.ensemble.exhibitor.Exhibitors;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.retry.RetryUntilElapsed;

/**
 * Binds an ExhibitorEnsembleProvider if sufficient information is provided, otherwise provides a FixedEnsembleProvider.
 */
@Beta
class EnsembleProviderProvider implements Provider<EnsembleProvider> {

    private static final int DEFAULT_REST_PORT = 8080;
    private static final String DEFAULT_REST_PATH = "/exhibitor/v1/cluster/list";
    private static final int DEFAULT_EXHIBITOR_POLLING_TIME_MINS = 5;
    private static final int RETRY_DEFAULT_SLEEP_BETWEEN_SECONDS = 5;
    private static final int RETRY_DEFAULT_MAX_WAIT_MINUTES = 1;

    private final Optional<String> exhibitorInstances;
    private final Optional<String> backupConnections;

    private int restPort = DEFAULT_REST_PORT;

    private String restPath = DEFAULT_REST_PATH;

    private int pollingTimeMs = (int) TimeUnit.MINUTES.toMillis(DEFAULT_EXHIBITOR_POLLING_TIME_MINS);

    private ExhibitorRestClient restClient = new DefaultExhibitorRestClient();

    private RetryPolicy retryPolicy = new RetryUntilElapsed(
            (int) TimeUnit.MINUTES.toMillis(RETRY_DEFAULT_MAX_WAIT_MINUTES),
            (int) TimeUnit.SECONDS.toMillis(RETRY_DEFAULT_SLEEP_BETWEEN_SECONDS));

    @Inject
    EnsembleProviderProvider(
            @Named("Cultivar.private.properties.exhibitorInstances") final Optional<String> exhibitorInstances,
            @Named("Cultivar.private.properties.backupConnections") final Optional<String> backupConnections) {

        checkArgument(backupConnections.isPresent(), "Backup connections must be provided.");

        this.exhibitorInstances = exhibitorInstances;
        this.backupConnections = backupConnections;
    }

    @Inject(optional = true)
    public void setRestPort(@Named("Cultivar.properties.exhibitor.restPort") final int port) {
        this.restPort = port;
    }

    @Inject(optional = true)
    public void setRestPath(@Named("Cultivar.properties.exhibitor.restPath") final String path) {
        this.restPath = path;
    }

    @Inject(optional = true)
    public void setPollingTimeMs(@Named("Cultivar.properties.exhibitor.pollingTimeMillis") final int pollingTimeMillis) {
        this.pollingTimeMs = pollingTimeMillis;
    }

    @Inject(optional = true)
    public void setRetryPolicy(@Named("Cultivar.properties.exhibitor.retryPolicy") final RetryPolicy retry) {
        this.retryPolicy = retry;
    }

    @Inject(optional = true)
    public void setRestClient(final ExhibitorRestClient client) {
        this.restClient = client;
    }

    private ExhibitorEnsembleProvider exhibitorEnsembleProvider() {

        Exhibitors exhibitors = new Exhibitors(Arrays.asList(exhibitorInstances.get().split(";")), restPort,
                new Exhibitors.BackupConnectionStringProvider() {

                    @Override
                    public String getBackupConnectionString() throws Exception {
                        return backupConnections.get();
                    }
                });

        return new ExhibitorEnsembleProvider(exhibitors, restClient, restPath, pollingTimeMs, retryPolicy);
    }

    @Override
    public EnsembleProvider get() {

        if (exhibitorInstances.isPresent()) {
            return exhibitorEnsembleProvider();
        }

        return new FixedEnsembleProvider(backupConnections.get());
    }
}
