package org.everit.osgi.servicereference.core.internal;

import java.lang.reflect.Method;

import org.everit.osgi.servicereference.core.ServiceUnavailableException;
import org.everit.osgi.servicereference.core.ServiceUnavailableHandler;

/**
 * Default implementation of {@link ServiceUnavailableHandler} that simply throws a {@link ServiceUnavailableException}
 * always when the service is not available during a method call after timeout.
 */
public class DefaultServiceNotAvailableHandlerImpl implements ServiceUnavailableHandler {

    @Override
    public Object handle(final String serviceFilter, final Method method, final Object[] args, final long timeout) {
        throw new ServiceUnavailableException(serviceFilter, method, timeout);
    }

}
