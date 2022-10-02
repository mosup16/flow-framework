package com.mo16.flow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SequentialStepTest {

    private SequentialStep<Integer, Integer> step;
    private Transporter<Integer> transporter;
    private Channel<Integer> channel;
    private Function<Integer, Integer> handler;

    @BeforeEach
    void setUp() {
        step = new SequentialStep<>();
        transporter = mock(Transporter.class);
        step.setTransporter(transporter);
        channel = mock(Channel.class);
        step.subscribeTo(channel);
        handler = mock(Function.class);
        step.onNewMessage(handler);
    }

    @Test
    @DisplayName("test startPolling() on sequential step")
    void startPolling() {
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