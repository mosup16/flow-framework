package com.mo16.flow.loadbalancing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadBalancerFactoryTest {

    @Test
    @DisplayName("test LoadBalancerFactory getInstanceFor()")
    void getInstanceFor() {
        LoadBalancer<Integer> roundRobinLB =
                new LoadBalancerFactory<Integer>().getInstanceFor(LoadBalancingStrategy.ROUND_ROBIN);
        LoadBalancer<Integer> leastBufferSizeLB =
                new LoadBalancerFactory<Integer>().getInstanceFor(LoadBalancingStrategy.LEAST_BUFFER_SIZE);


        assertTrue(roundRobinLB instanceof RoundRobinLoadBalancer);
        assertTrue(leastBufferSizeLB instanceof LeastBufferSizeLoadBalancer);
    }
}