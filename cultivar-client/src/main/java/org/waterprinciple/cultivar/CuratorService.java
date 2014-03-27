package org.waterprinciple.cultivar;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.Service;

/**
 * Represents a Guava service that uses Curator and needs its own Start/Stop.
 */
@Beta
public interface CuratorService extends Service {
}
