package com.mo16.flow.loadbalancing;

import com.mo16.flow.Channel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeastBufferSizeLoadBalancerTest {

    @Test
    @DisplayName("test channel selection on LeastBufferSizeLoadBalancer")
    void selectChannel() {
        Channel<Integer> channel_1 = mock(Channel.class);
        Channel<Integer> channel_2 = mock(Channel.class);

        var loadBalancer = new LeastBufferSizeLoadBalancer<Integer>();

        loadBalancer.registerChannel(channel_1);
        loadBalancer.registerChannel(channel_2);

        when(channel_1.countOfAvailableMessages()).thenReturn(1000);
        when(channel_2.countOfAvailableMessages()).thenReturn(2000);

        Channel<Integer> selectedChannel = loadBalancer.selectChannel();

        assertEquals(channel_1, selectedChannel);
    }
}