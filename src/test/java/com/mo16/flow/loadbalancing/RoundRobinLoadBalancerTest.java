package com.mo16.flow.loadbalancing;

import com.mo16.flow.Channel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RoundRobinLoadBalancerTest {

    @Test
    @DisplayName("test channel selection on RoundRobinLoadBalancer")
    void selectChannel() {
        Channel<Integer> channel_1 = mock(Channel.class);
        Channel<Integer> channel_2 = mock(Channel.class);
        Channel<Integer> channel_3 = mock(Channel.class);

        var loadBalancer = new RoundRobinLoadBalancer<Integer>();

        loadBalancer.registerChannel(channel_1);
        loadBalancer.registerChannel(channel_2);
        loadBalancer.registerChannel(channel_3);

        assertEquals(channel_1, loadBalancer.selectChannel());
        assertEquals(channel_2, loadBalancer.selectChannel());
        assertEquals(channel_3, loadBalancer.selectChannel());
        assertEquals(channel_1, loadBalancer.selectChannel());
        assertEquals(channel_2, loadBalancer.selectChannel());
        assertEquals(channel_3, loadBalancer.selectChannel());
        assertEquals(channel_1, loadBalancer.selectChannel());
    }
}