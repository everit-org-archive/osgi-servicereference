package org.everit.osgi.servicereference.core.internal;

/*
 * Copyright (c) 2011, Everit Kft.
 *
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.everit.osgi.servicereference.core.Reference;
import org.everit.osgi.servicereference.core.ServiceUnavailableException;
import org.everit.osgi.servicereference.core.ServiceUnavailableHandler;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The invocation handler that supports that calls the service refernces if they are available otherwise it will wait
 * until a timeout.
 * 
 */
public class ReferenceInvocationHandler implements InvocationHandler {

    /**
     * The timeout until the thread (function call) will wait if there is no service available.
     */
    private final long timeout;

    /**
     * Tracks the services that fit to the filter.
     */
    private final ServiceTracker<?, ?> serviceTracker;

    /**
     * The filter represented as a string that the {@link #serviceTracker} tracks.
     */
    private final String filter;

    private final Reference reference;

    /**
     * The object that handles if a service is not available even after the timeout.
     */
    private ServiceUnavailableHandler serviceNotAvailableHandler;

    /**
     * Simple constructor that sets the fields.
     * 
     * @param serviceTracker
     *            value of {@link #serviceTracker}.
     * @param filter
     *            value of {@link #filter}.
     * @param timeout
     *            value of {@link #timeout}.
     */
    public ReferenceInvocationHandler(final Reference reference, final ServiceTracker<Object, Object> serviceTracker,
            final String filter,
            final long timeout) {
        this.timeout = timeout;
        this.filter = filter;
        this.serviceTracker = serviceTracker;
        this.reference = reference;
    }

    /**
     * If a service object is available it will be called otherwise the function call will wait until {@link #timeout}.
     * {@inheritDoc}
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Object service = serviceTracker.getService();
        if (service == null) {
            if (!reference.isOpened()) {
                throw new IllegalStateException("Reference is not opened. Filter of reference: '" + filter + "'.");
            }
            service = serviceTracker.waitForService(timeout);
        }
        if (service == null) {
            if (serviceNotAvailableHandler != null) {
                return serviceNotAvailableHandler.handle(filter, method, args, timeout);
            } else {
                throw new ServiceUnavailableException(filter, method, timeout);
            }
        } else {
            try {
                return method.invoke(service, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    public void setServiceNotAvailableHandler(final ServiceUnavailableHandler serviceNotAvailableHandler) {
        this.serviceNotAvailableHandler = serviceNotAvailableHandler;
    }
}
