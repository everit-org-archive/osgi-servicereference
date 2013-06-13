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
