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

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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

        Reference reference = new Reference(bundleContext, new Class<?>[] { List.class },
                createTestFilter(), 1);
        reference.open();
        List<String> proxyInstance = reference.getProxyInstance();

        Assert.assertTrue(proxyInstance.contains("Test"));

        reference.close();

        existingSR.unregister();
    }

    @Override
    @Test
    public void testLaterAvailableService() {
        Reference reference = new Reference(bundleContext, new Class<?>[] { List.class },
                createTestFilter(), 2000);
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
        Reference reference = new Reference(bundleContext, new Class<?>[] { List.class },
                createTestFilter(), 1);
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

    @Override
    public void testException() {
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("testservice", "true");
        ServiceRegistration<Closeable> existingSR = bundleContext.registerService(Closeable.class,
                new Closeable() {

                    @Override
                    public void close() throws IOException {
                        throw new IOException("Test exception");
                    }
                }, properties);

        Reference reference = new Reference(bundleContext, new Class[] { Closeable.class }, createTestFilter(), 1);
        reference.open();
        Closeable proxyInstance = reference.getProxyInstance();
        try {
            proxyInstance.close();
            Assert.fail("An IOException should have been dropped");
        } catch (IOException e) {
            // Good behavior
        }
        reference.close();
        existingSR.unregister();
    }

    @Override
    public void testNotOpenedReference() {
        Reference reference = new Reference(bundleContext, new Class[] { List.class }, createTestFilter(), 1);
        List<Integer> proxyInstance = reference.getProxyInstance();
        try {
            proxyInstance.add(1);
            Assert.fail("IllegalStateException should have happened as reference is not opened");
        } catch (IllegalStateException e) {
            // Good behavior
        }
    }

    @Override
    public void testNoInterfaceDefinition() {
        try {
            new Reference(bundleContext, new Class[0], createTestFilter(), 1);
            Assert.fail("In case of an empty interface array an exception should be thrown");
        } catch (IllegalArgumentException e) {
            // Right behavior
        }

        try {
            new Reference(bundleContext, null, createTestFilter(), 1);
            Assert.fail("In case null interface array an exception should be thrown");
        } catch (IllegalArgumentException e) {
            // Right behavior
        }
    }

    @Override
    public void testNotAllRequiredInterfaces() {
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("testservice", "true");
        ServiceRegistration<List> existingSR = bundleContext.registerService(List.class,
                new ArrayList<String>(Arrays.asList("Test")), properties);

        Reference reference = new Reference(bundleContext, new Class<?>[] { List.class, Comparable.class },
                createTestFilter(), 1);

        reference.open();

        List<String> proxyInstance = reference.getProxyInstance();
        try {
            proxyInstance.contains("Test");
            Assert.fail("Should throw a ServiceUnavailable exception as there is no service registered which"
                    + " implements all the necessary interfaces");
        } catch (ServiceUnavailableException e) {
            // Correct. An exception should be thrown as not all the required interfaces are implemented by the
            // registered service object.
        }
        reference.close();
        existingSR.unregister();
    }

    private Filter createTestFilter() {
        Filter filter = null;
        try {
            filter = bundleContext.createFilter("(testservice=true)");
        } catch (InvalidSyntaxException e) {
            Assert.fail(e.getMessage());
        }
        return filter;
    }

}
