package com.mo16.flow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SequentialStepTest {

    @Test
    @DisplayName("test startPolling() on sequential step")
    void startPolling() {
        var step = new SequentialStep<Integer, Integer>();
        Transporter<Integer> transporter = mock(Transporter.class);
        step.setTransporter(transporter);
        Channel<Integer> channel = mock(Channel.class);
        step.subscribeTo(channel);
        Function<Integer, Integer> handler = mock(Function.class);
        step.onNewMessage(handler);

        when(channel.countOfAvailableMessages()).thenReturn(1);
        when(channel.hasAvailableMessages()).thenReturn(true);
        when(channel.poll()).thenReturn(1);
        when(handler.apply(1)).thenReturn(1);

        step.startPolling();

        verify(channel).countOfAvailableMessages();
        verify(channel).hasAvailableMessages();
        verify(channel).poll();
        verify(handler).apply(1);
        verify(transporter).publishMessage(1);
    }

    @Test
    void pollMessage() {
    }

    @Test
    void channelClosed() {
    }

    @Test
    void copy() {
    }
}