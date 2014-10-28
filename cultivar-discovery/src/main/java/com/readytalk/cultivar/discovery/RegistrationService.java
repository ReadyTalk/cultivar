package com.readytalk.cultivar.discovery;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Registers a service with discovery. Typed to represent the different payloads.
 */
@ThreadSafe
public interface RegistrationService<T> extends DiscoveryService {

}
