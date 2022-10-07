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

    @Test
    @DisplayName("test channel closing via a MultiChannelTransporter")
    void closeChannel() {
        Channel<Integer> channel_1 = mock(Channel.class);
        Channel<Integer> channel_2 = mock(Channel.class);
        Channel<Integer> channel_3 = mock(Channel.class);
        LoadBalancer<Integer> loadBalancer = mock(LoadBalancer.class);

        var transporter = new MultiChannelTransporter<>(loadBalancer);

        transporter.addChannel(channel_1);
        transporter.addChannel(channel_2);
        transporter.addChannel(channel_3);
        transporter.closeChannel();
        verify(channel_1).close();
        verify(channel_2).close();
        verify(channel_3).close();
    }

}