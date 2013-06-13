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
