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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * {@link ServiceTrackerCustomizer} that checks the provided interfaces when it is available and adds it to the tracker
 * if all the {@link #requiredInterfaces} are implemented by the service object.
 */
public class ReferenceTrackerCustomizer implements ServiceTrackerCustomizer<Object, Object> {

    /**
     * The interfaces that have to be implemented by the service objects that are tracked.
     */
    private final Class<?>[] requiredInterfaces;

    /**
     * The context of the bundle that references the service.
     */
    private final BundleContext bundleContext;

    /**
     * Simple constructor that sets the properties of this class.
     * 
     * @param bundleContext
     *            Value of {@link #bundleContext}.
     * @param requiredInterfaces
     *            Value of {@link #requiredInterfaces}.
     * 
     *            throws IllegalArgumentException if not interface is specified. At least one interface has to be
     *            specified as the tracked service will be proxied and the proxy object will implement the required
     *            interfaces.
     */
    public ReferenceTrackerCustomizer(final BundleContext bundleContext, final Class<?>[] requiredInterfaces) {
        if ((requiredInterfaces == null) || (requiredInterfaces.length == 0)) {
            throw new IllegalArgumentException("The number of required interfaces must be at least one.");
        }
        this.requiredInterfaces = requiredInterfaces;
        this.bundleContext = bundleContext;
    }

    /**
     * Adding a service to the tracker only if all the {@link #requiredInterfaces} are implemented by the service
     * object. <br>
     * <br>
     * {@inheritDoc}
     */
    @Override
    public Object addingService(final ServiceReference<Object> reference) {
        boolean implementsAll = true;

        Object service = bundleContext.getService(reference);
        Class<? extends Object> classOfService = service.getClass();
        for (int i = 0, n = requiredInterfaces.length; (i < n) && implementsAll; i++) {
            implementsAll = (requiredInterfaces[i].isAssignableFrom(classOfService));
        }
        if (implementsAll) {
            return service;
        } else {
            bundleContext.ungetService(reference);
            return null;
        }

    }

    @Override
    public void modifiedService(final ServiceReference<Object> reference, final Object service) {
        // Do nothing

    }

    @Override
    public void removedService(final ServiceReference<Object> reference, final Object service) {
        bundleContext.ungetService(reference);
    }

}
