package org.everit.osgi.servicereference.core;

import java.lang.reflect.Method;

/**
 * In case a service is not available when a methos is called on a proxy an implementation of this class will decide
 * what should happen. The implementation of this class must be thread safe.
 */
public interface ServiceUnavailableHandler {

    /**
     * Handling when a method is not available.
     * 
     * @param serviceFilter
     *            The filter of the service that is tracked and not available.
     * @param method
     *            The method that was invoked.
     * @param args
     *            The arguments that were used during the original method call.
     * @param timeout
     *            The time until the Reference waited for the service to be available.
     * @return In case this function returns an object that will be returned as the result of the original method call.
     */
    Object handle(String serviceFilter, Method method, Object[] args, long timeout);
}
