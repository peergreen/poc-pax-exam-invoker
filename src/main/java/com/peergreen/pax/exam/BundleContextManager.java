package com.peergreen.pax.exam;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ops4j.pax.exam.ProbeInvoker;
import org.osgi.framework.BundleContext;

public class BundleContextManager {

    private BundleContext bundleContext;

    private Map<String, InstanceManager> instanceManagers;


    public BundleContextManager(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.instanceManagers = new ConcurrentHashMap<>();
    }


    public ProbeInvoker getProbeInvoker(String methodName, String className) {

        // search instance manager
        InstanceManager instanceManager = instanceManagers.get(className);
        if (instanceManager == null) {
            instanceManager = new InstanceManager(className, bundleContext);
            instanceManagers.put(className, instanceManager);
        }

        return new SingletonProbeInvokerImpl(methodName, instanceManager);


    }

}
