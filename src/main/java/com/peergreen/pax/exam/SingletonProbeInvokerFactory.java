package com.peergreen.pax.exam;

import java.util.Map;
import java.util.WeakHashMap;

import org.ops4j.pax.exam.ProbeInvoker;
import org.ops4j.pax.exam.ProbeInvokerFactory;
import org.osgi.framework.BundleContext;

public class SingletonProbeInvokerFactory implements ProbeInvokerFactory {


    private Map<BundleContext, BundleContextManager> bundleContextManagers;

    public SingletonProbeInvokerFactory() {
        this.bundleContextManagers = new WeakHashMap<>();
    }


    @Override
    public ProbeInvoker createProbeInvoker(Object context, String expr) {

        BundleContext bundleContext = (BundleContext) context;

        String[] parts = expr.split(";");
        String className = parts[0];
        String methodName = parts[1];


        // Check if we've an existing entry
        BundleContextManager bundleContextManager = bundleContextManagers.get(bundleContext);
        if (bundleContextManager == null) {
            bundleContextManager = new BundleContextManager(bundleContext);
            bundleContextManagers.put(bundleContext, bundleContextManager);
        }

        // return a probe invoker
        return bundleContextManager.getProbeInvoker(methodName, className);

    }

}
