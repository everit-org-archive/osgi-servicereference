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

/**
 * When a warmup listener is specified for a service reference it will be called when the first service is available for
 * the reference.
 */
public interface WarmUpListener {

    /**
     * Called when the first service is available for the reference.<br />
     * <br />
     * At the time this function is called the service is not registered by the reference yet but on this thread the
     * first incoming service is available. On the same thread reference can be called and backend service will be
     * available for sure, however if someone opens a new thread in this function that thread has to wait at least until
     * this function finishes to be able to call functions on the proxy instance of the reference.<br />
     * <br />
     * Implement this function in the way that it does not do lot's of things so it will return as soon as possible!
     */
    void warming();
}
