package com.peergreen.pax.exam;

import java.util.Dictionary;
import java.util.Hashtable;

import org.ops4j.pax.exam.ProbeInvokerFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public void start(BundleContext bundleContext) throws Exception {
        ProbeInvokerFactory probeInvokerFactory = new SingletonProbeInvokerFactory();
        Dictionary<String, String> props = new Hashtable<>();
        props.put("driver", "singleton");
        bundleContext.registerService(ProbeInvokerFactory.class, probeInvokerFactory, props);
    }

    public void stop(BundleContext context) throws Exception {

    }
}
