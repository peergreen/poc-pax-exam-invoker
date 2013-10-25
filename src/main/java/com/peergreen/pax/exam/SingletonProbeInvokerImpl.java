package com.peergreen.pax.exam;

import org.ops4j.pax.exam.ProbeInvoker;

public class SingletonProbeInvokerImpl implements ProbeInvoker {

    private InstanceManager instanceManager;
    private String methodName;

    public SingletonProbeInvokerImpl(String methodName, InstanceManager instanceManager) {
        this.methodName = methodName;
        this.instanceManager = instanceManager;
    }

    public void call(Object... args) {
        instanceManager.call(methodName, args);
    }

}