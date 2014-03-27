package org.waterprinciple.cultivar.leader;

import org.waterprinciple.cultivar.CuratorService;

import com.google.common.annotations.Beta;

/**
 * A CuratorService that uses leader election.
 */
@Beta
public interface LeaderService extends CuratorService {
}
