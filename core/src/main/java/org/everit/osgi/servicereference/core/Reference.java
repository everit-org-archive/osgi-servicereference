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
     * True if this reference is currently opened.
     */
    private boolean opened = false;

    /**
     * The filter that is used to track services in this reference.
     */
    private Filter filter;

    /**
     * The invocation handler that catches method calls coming to the proxy object.
     */
    private final ReferenceInvocationHandler referenceInvocationHandler;

    /**
     * Customizer of the serviceTracker that is used by this reference to track services.
     */
    private final ReferenceTrackerCustomizer serviceTrackerCustomizer;

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
        if (filter == null) {
            throw new IllegalArgumentException("The filter parameter cannot be null");
        }
        if ((interfaces == null) || (interfaces.length == 0)) {
            throw new IllegalArgumentException("The number of required interfaces must be at least one.");
        }
        this.filter = filter;
        serviceTrackerCustomizer = new ReferenceTrackerCustomizer(context, interfaces);

        serviceTracker = new ServiceTracker<Object, Object>(context, filter, serviceTrackerCustomizer);
        referenceInvocationHandler =
                new ReferenceInvocationHandler(this, serviceTracker, filter.toString(), timeout);

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
        opened = false;
        serviceTracker.close();
        serviceTrackerCustomizer.reset();
    }

    public Filter getFilter() {
        return filter;
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

    public boolean isOpened() {
        return opened;
    }

    /**
     * Opens the inner {@link ServiceTracker} that is used to track the available services.
     */
    public void open() {
        opened = true;
        serviceTracker.open();
    }

    public void setServiceUnavailableHander(final ServiceUnavailableHandler handler) {
        referenceInvocationHandler.setServiceNotAvailableHandler(handler);
    }

    /**
     * Setting the {@link WarmUpListener} of this reference. For more information please see the doc of that class.
     * 
     * @param listener
     *            The listener object or null if no warming up notification should be caughed.
     * @throws IllegalStateException
     *             if the reference is opened.
     */
    public void setWarmUpListener(final WarmUpListener listener) {
        if (opened) {
            throw new IllegalStateException("Warmup listener can be set only for a stopped reference");
        }
        serviceTrackerCustomizer.setWarmUpListener(listener);
    }

    /**
     * See {@link ServiceTracker#waitForService(long)}.
     * 
     * @param timeout
     *            See the timeout parameter of {@link ServiceTracker#waitForService(long)}.
     * @return True if service is available before timeout false otherwise.
     * @throws InterruptedException
     *             See throws tag of {@link ServiceTracker#waitForService(long)}.
     * @throws IllegalStateException
     *             if the tracker is not opened.
     */
    public boolean waitForService(final long timeout) throws InterruptedException {
        if (!opened) {
            throw new IllegalStateException("waitForService should be called only on an opened reference");
        }
        Object service = serviceTracker.waitForService(timeout);
        return service != null;
    }
}
