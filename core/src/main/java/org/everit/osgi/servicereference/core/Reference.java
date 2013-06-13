package org.everit.osgi.servicereference.core;

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

import java.lang.reflect.Proxy;

import org.everit.osgi.servicereference.core.internal.ReferenceInvocationHandler;
import org.everit.osgi.servicereference.core.internal.ReferenceTrackerCustomizer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The holder class that contains the proxied reference. Before any usage of the proxy instance the {@link #open()}
 * function of the instance has to be called and the {@link #close()} function has to be called when we do not need the
 * reference anymore.
 */
public class Reference {

    /**
     * The proxied service object. In case there is no real service it will wait for a timeout.
     */
    private Object proxyInstance;

    /**
     * Tracks the available services that can be used by the {@link #proxyInstance}.
     */
    private ServiceTracker<Object, Object> serviceTracker;

    /**
     * A constructor that initializes the object and creates the necessary {@link ServiceTracker}. The {@link #open()}
     * function has to be called before using the {@link #proxyInstance}.
     * 
     * @param context
     *            The context of the bundle that needs the reference.
     * @param interfaces
     *            The interfaces that the {@link #proxyInstance} should be able to be casted.
     * @param filter
     *            The filter expression that the available services will be checked against.
     * @param timeout
     *            The timeout until the functions calls on {@link #proxyInstance} will wait if no service is available.
     */
    public Reference(final BundleContext context, final Class<?>[] interfaces, final Filter filter,
            final long timeout) {
        ReferenceTrackerCustomizer customizer = null;
        if ((interfaces != null) && (interfaces.length > 0)) {
            customizer = new ReferenceTrackerCustomizer(context, interfaces);
        }

        serviceTracker = new ServiceTracker<Object, Object>(context, filter, customizer);
        ReferenceInvocationHandler referenceInvocationHandler =
                new ReferenceInvocationHandler(serviceTracker, filter.toString(), timeout);
        Bundle blueprintBundle = context.getBundle();
        ClassLoader classLoader = blueprintBundle.adapt(BundleWiring.class).getClassLoader();
        // TODO check if classloader is null and handle it. It could be null in case of special security circumstances.

        proxyInstance = Proxy.newProxyInstance(classLoader, interfaces,
                referenceInvocationHandler);

    }

    /**
     * Releases the inner {@link ServiceTracker} that is used to track available services.
     */
    public void close() {
        serviceTracker.close();
    }

    /**
     * Getter for the {@link #proxyInstance}.
     * 
     * @param <T>
     *            The type of the proxy instance that should be casted to.
     * @return the proxy instance for the available services.
     */
    public <T> T getProxyInstance() {
        @SuppressWarnings("unchecked")
        T result = (T) proxyInstance;
        return result;
    }

    /**
     * Opens the inner {@link ServiceTracker} that is used to track the available services.
     */
    public void open() {
        serviceTracker.open();
    }

    /**
     * See {@link ServiceTracker#waitForService(long)}.
     * 
     * @param timeout
     *            See the timeout parameter of {@link ServiceTracker#waitForService(long)}.
     * @return True if service is available before timeout false otherwise.
     * @throws InterruptedException
     *             See throws tag of {@link ServiceTracker#waitForService(long)}.
     */
    public boolean waitForService(final long timeout) throws InterruptedException {
        Object service = serviceTracker.waitForService(timeout);
        return service != null;
    }
}
