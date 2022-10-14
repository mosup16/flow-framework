package com.mo16.flow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SingleChannelTransporterTest {

    @Test
    @DisplayName("test message publishing via SingleChannelTransporter")
    void publishMessage() {
        var configs = new BackPressureConfigs(-1, -1);
        var transporter = new SingleChannelTransporter<Integer>(configs);
        Channel<Integer> channel = mock(Channel.class);
        transporter.addChannel(channel);
        transporter.publishMessage(1);
        verify(channel, times(1)).push(1);
    }

    @Test
    @DisplayName("test channel closing via SingleChannelTransporter")
    void closeChannel() {
        var configs = new BackPressureConfigs(-1, -1);
        var transporter = new SingleChannelTransporter<Integer>(configs);
        Channel<Integer> channel = mock(Channel.class);
        transporter.addChannel(channel);
        transporter.closeChannel();
        verify(channel).close();
    }

}