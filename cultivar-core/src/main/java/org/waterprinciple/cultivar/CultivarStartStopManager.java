package org.waterprinciple.cultivar;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.Service;

/**
 * A service that manages starting and stopping Curator in the correct order.
 */

@ThreadSafe
@Beta
public interface CultivarStartStopManager extends Service {

}
