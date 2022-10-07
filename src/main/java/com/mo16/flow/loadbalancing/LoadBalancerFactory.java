package com.mo16.flow.loadbalancing;

public class LoadBalancerFactory<T> {

    public LoadBalancer<T> getInstanceFor(LoadBalancingStrategy strategy){
        switch (strategy) { //TODO use switch expression after upgrading the java version
            case ROUND_ROBIN: return new RoundRobinLoadBalancer<>();
            case LEAST_BUFFER_SIZE: return new LeastBufferSizeLoadBalancer<>();
        }
        return null;
    }

}
