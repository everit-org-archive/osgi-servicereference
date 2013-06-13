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

import java.lang.reflect.Method;

/**
 * The default behaviour of a {@link Reference} when the service is not available during a method call is that this
 * exception will be thrown. <br>
 * <br>
 * The method call arguments are not part of this exception as it could raise several security risks. Imagine that a
 * password is passed as an argument when the service is not available.
 */
public class ServiceUnavailableException extends RuntimeException {

    /**
     * Generated seriable version.
     */
    private static final long serialVersionUID = -1437684627303559487L;

    /**
     * The original method that was called.
     */
    private Method method;

    /**
     * The filter of the service.
     */
    private String serviceFilter;

    /**
     * The timeout until the method call was waiting.
     */
    private long timeout;

    /**
     * The only one constructor of this class.
     * 
     * @param serviceFilter
     *            The filter that the service tracking is based on.
     * @param method
     *            The method that was called on the proxy object.
     * @param timeout
     *            The timeout until the proxy object waited before throwing this exception.
     */
    public ServiceUnavailableException(final String serviceFilter, final Method method, final long timeout) {
        super("The service '" + serviceFilter + "' was not available even after " + timeout
                + " ms. during the call of the method " + method.toString() + ".");
        this.serviceFilter = serviceFilter;
        this.method = method;
        this.timeout = timeout;
    }

    public Method getMethod() {
        return method;
    }

    public String getServiceFilter() {
        return serviceFilter;
    }

    public long getTimeout() {
        return timeout;
    }
}
