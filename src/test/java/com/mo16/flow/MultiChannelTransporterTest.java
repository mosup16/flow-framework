package com.mo16.flow;

import com.mo16.flow.loadbalancing.LoadBalancer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MultiChannelTransporterTest {

    @Test
    @DisplayName("test message publishing via a MultiChannelTransporter")
    void publishMessage() {
        Channel<Integer> channel = mock(Channel.class);
        LoadBalancer<Integer> loadBalancer = mock(LoadBalancer.class);

        when(loadBalancer.selectChannel()).thenReturn(channel);

        var transporter = new MultiChannelTransporter<>(loadBalancer);
        transporter.addChannel(channel);
        transporter.publishMessage(1);

        verify(loadBalancer, times(1)).selectChannel();
        verify(channel, times(1)).push(1);
    }

}