package com.mo16.flow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SingleChannelTransporterTest {

    @Test
    @DisplayName("test message publishing via SingleChannelTransporter")
    void publishMessage() {
        var transporter = new SingleChannelTransporter<Integer>();
        Channel<Integer> channel = mock(Channel.class);
        transporter.addChannel(channel);
        transporter.publishMessage(1);
        verify(channel, times(1)).push(1);
    }

}