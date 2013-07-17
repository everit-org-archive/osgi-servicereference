package org.everit.osgi.servicereference.tests;

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

import org.junit.Test;

public interface ReferenceTest {

    /**
     * Testing when a custom handler is used. It is tested when it returns a special value as well as when it throws a
     * special exception.
     */
    @Test
    public void testCustomHandler();

    /**
     * When a service throws an exception via the proxy object it should come back to the caller.
     */
    @Test
    void testException();

    /**
     * Testing when there is already a service when reference is opened and called.
     */
    @Test
    void testExistingReference();

    /**
     * Testing when the service is available only after the method is called on the reference but within the timeout.
     */
    @Test
    void testLaterAvailableService();

    /**
     * In case no filter is provided for Reference Constructor an IllegalArgument should be thrown.
     */
    @Test
    void testNoFilter();

    /**
     * When the interfaces parameter of Reference is null or an empty array is passed an IllegalArgumentException should
     * be thrown.
     */
    @Test
    void testNoInterfaceDefinition();

    /**
     * Testing if a service does not implement all the required interfaces.
     */
    @Test
    void testNotAllRequiredInterfaces();

    /**
     * When a reference is not opened and a method is called on the proxy object the reference should throw an
     * IllegalStateException.
     */
    @Test
    void testNotOpenedReference();

    /**
     * Testing service property modifications.
     */
    @Test
    void testServiceModification();

    /**
     * When there is no service available and till the timeout and no custom ServiceUnavailableHandler is used a
     * ServiceUnavailableException is thrown.
     */
    @Test
    void testTimeout();

    /**
     * Testing waitForService function of Reference.
     */
    @Test
    void testWaitForService();

    /**
     * Testing the warmup callback fuctionality
     */
    @Test
    void testWarmUp();

}
