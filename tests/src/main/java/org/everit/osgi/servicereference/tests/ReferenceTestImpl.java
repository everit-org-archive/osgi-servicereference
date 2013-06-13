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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.everit.osgi.servicereference.core.Reference;
import org.everit.osgi.servicereference.core.ServiceUnavailableException;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

/**
 * Implementation that contains tests for reference holder classes.
 */
public class ReferenceTestImpl implements ReferenceTest {

    /**
     * The context of the test bundle.
     */
    private final BundleContext bundleContext;

    /**
     * Simple constructor that sets fields.
     * 
     * @param bundleContext
     *            The value of {@link #bundleContext} property.
     */
    ReferenceTestImpl(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void testExistingReference() {
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("testservice", "true");
        ServiceRegistration<List> existingSR = bundleContext.registerService(List.class,
                new ArrayList<String>(Arrays.asList("Test")), properties);

        Filter filter = null;
        try {
            filter = bundleContext.createFilter("(testservice=true)");
        } catch (InvalidSyntaxException e) {
            Assert.fail(e.getMessage());
        }
        Reference reference = new Reference(bundleContext, new Class<?>[] { List.class },
                filter, 1);
        reference.open();
        List<String> proxyInstance = reference.getProxyInstance();

        Assert.assertTrue(proxyInstance.contains("Test"));

        reference.close();

        existingSR.unregister();
    }

    @Override
    @Test
    public void testLaterAvailableService() {
        Filter filter = null;
        try {
            filter = bundleContext.createFilter("(testservice=true)");
        } catch (InvalidSyntaxException e) {
            Assert.fail(e.getMessage());
        }
        Reference reference = new Reference(bundleContext, new Class<?>[] { List.class },
                filter, 2000);
        reference.open();
        final List<Integer> proxyInstance = reference.getProxyInstance();
        final AtomicInteger result = new AtomicInteger(0);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    result.set(proxyInstance.get(0));
                } catch (ServiceUnavailableException e) {
                    Assert.fail(e.getMessage());
                }
            }
        }).start();

        try {
            // Wait a bit to be sure that the get method is called on the proxy instance before the service is
            // registered. Not the nicest solution but for a test it is ok.
            Thread.sleep(50);
        } catch (InterruptedException e1) {
            Assert.fail(e1.getMessage());
        }
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("testservice", "true");
        ServiceRegistration<List> existingSR = bundleContext.registerService(List.class,
                new ArrayList<Integer>(Arrays.asList(1)), properties);

        for (int i = 0; (i < 100) && (result.get() != 1); i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Assert.fail(e.getMessage());
            }
        }
        Assert.assertEquals(1, result.get());
        reference.close();
        existingSR.unregister();
    }

    @Override
    public void testTimeout() {
        Filter filter = null;
        try {
            filter = bundleContext.createFilter("(testservice=true)");
        } catch (InvalidSyntaxException e) {
            Assert.fail(e.getMessage());
        }
        Reference reference = new Reference(bundleContext, new Class<?>[] { List.class },
                filter, 1);
        reference.open();
        List<String> proxyInstance = reference.getProxyInstance();
        try {
            proxyInstance.contains("Test");
            Assert.fail("Should throw a ServiceUnavailable exception");
        } catch (ServiceUnavailableException e) {
            // Correct. The exception has to be thrown after 1 millisec.
        }
        reference.close();
    }

}
