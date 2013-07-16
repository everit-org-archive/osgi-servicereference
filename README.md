osgi-servicereference
=====================

Helper classes to be able to reference an OSGi service with a proxy
object and hold method calls until a timeout if no service is available
behind a proxy.

Motivation
----------

During implementing OSGi Blueprint specification it was realised that the
reference handling that is specified there is really cool. The
functionality is implemented in this atomic library. Separating the code
from the blueprint implementation has several advantages:

 - Reference functionality can be used programmatically without using
   blueprint.
 - Releases can be more frequent as the version of this module does not
   depend on the versioning lifecycle of the blueprint implementation.

Sample usage
------------

    // Creating a filter to use OSGi services
    Filter filter = null;
    try {
        filter = bundleContext.createFilter("(testservice=true)");
    } catch (InvalidSyntaxException e) {
        Assert.fail(e.getMessage());
    }
    
    // With the help of the filter we create a new Reference instance.
    // The timeout is 3000 ms and all tracked services will implement
    // the two interfaces we provide as the second parameter. Please
    // note that only interfaces can be specified and not classes.
    Reference reference = new Reference(
            bundleContext,
            new Class<?>[] { Foo1.class, Foo2.class },
            filter,
            3000);   
    
    // To be able to be used reference must be opened
    reference.open();

    // We get the proxy object that implements the two interfaces we
    // specified. 
    Foo foo = reference.getProxyInstance();
    
    // Calling a function of the proxy instance. It either calls an OSGi
    // service behind or throws a ServiceUnavailableException if there is
    // no available service till the timeout. Please note that custom
    // ServiceUnavailableHandler can be defined for a Reference.
    foo.doSomething();
    
    // References must be closed when they are not used anymore.
    reference.close();

For more samples please see the source of ReferenceTestImpl class!


Questions and answers
---------------------

_Is it possible to make the proxy object wait until a service is available?_

Reference has a waitForService function with a timeout parameter. It a
similar functionality as ServiceTracker.waitForService(timeout) function.

_Will be ASM, Javassist or other bytecode manipulation tool supported?_

No. We will not support bytecode manipulation, hooks, etc...
