package com.peergreen.pax.exam;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ops4j.pax.exam.TestContainerException;
import org.ops4j.pax.exam.util.Injector;
import org.ops4j.pax.swissbox.tracker.ServiceLookup;
import org.osgi.framework.BundleContext;

/**
 * Manages instance of a class for a given bundle context.
 * @author Florent Benoit
 *
 */
public class InstanceManager {

    private Injector injector;

    private String className;

    private BundleContext bundleContext;

    private Object singletonInstance;


    public InstanceManager(String className, BundleContext bundleContext) {
        this.className = className;
        this.bundleContext = bundleContext;
        this.injector = ServiceLookup.getService(bundleContext, Injector.class);
    }



    protected synchronized Object getInstance() {
        if (singletonInstance == null) {
            try {
                Class<?> testClass = bundleContext.getBundle().loadClass(className);
                singletonInstance = testClass.newInstance();
            } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
                throw new TestContainerException(e);
            }
            injector.injectFields(singletonInstance);
        }

        return singletonInstance;
    }

    public void call(String methodName, Object... args) {


        // find matching method
        for (Method m : getInstance().getClass().getMethods()) {
            if (m.getName().equals(methodName)) {
                final Class<?>[] paramTypes = m.getParameterTypes();
                try {
                    // runBefores( testInstance );
                    if (paramTypes.length == 0) {
                        m.invoke(getInstance());
                    } else {
                        Object[] parameters = injectHook(m, args);
                        m.invoke(getInstance(), parameters);
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new TestContainerException(e);
                }
            }
        }
    }




    /**
     * This method practcally makes sure the method that is going to be invoked has the right types
     * and instances injected as parameters.
     *
     * The following rules apply: You either have no arguments. You have arguments, then
     * BundleContext must be your first. Parameters with @Inject Annotation must come next. All
     * remaining arguments are set the params values.
     *
     * @param testMethod
     *            method in question
     * @param params
     *            derived from caller. Addditional injections may apply
     *
     * @return filled parameters ready for method invokation
     */
    private Object[] injectHook(Method testMethod, Object[] params) {
        // skip all injections
        Class<?>[] paramTypes = testMethod.getParameterTypes();
        Object[] ret = new Object[paramTypes.length];
        Annotation[][] paramAnnotations = testMethod.getParameterAnnotations();
        int paramCursor = 0;

        for (int i = 0; i < ret.length; i++) {
            if (i == 0) {
                ret[0] = bundleContext;
            }
            else {
                if (paramAnnotations[i].length > 0) {
                    // skip
                    throw new RuntimeException("Parameter " + i + " on " + testMethod.getName()
                        + " has Annotation. Not supported until Pax Exam 2.1");
                }
                else {
                    if (params.length > paramCursor) {
                        ret[i] = params[paramCursor++];
                    }
                    else {
                        // set default to null
                        ret[i] = null;
                    }
                }
            }
        }

        return ret;
    }

}
