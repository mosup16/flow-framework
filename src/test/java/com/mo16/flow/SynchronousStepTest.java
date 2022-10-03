package com.mo16.flow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SynchronousStepTest {

    private SynchronousStep<Integer, Integer> step;
    private Transporter<Integer> transporter;
    private Channel<Integer> channel;
    private Function<Integer, Integer> handler;

    @BeforeEach
    void setUp() {
        step = new SynchronousStep<>();
        transporter = mock(Transporter.class);
        step.setTransporter(transporter);
        channel = mock(Channel.class);
        step.subscribeTo(channel);
        handler = mock(Function.class);
        step.onNewMessage(handler);
    }

    @Test
    @DisplayName("test startPolling() on synchronous step")
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
    @DisplayName("test SynchronousStep pollMessage()")
    void pollMessage() {
        when(channel.poll()).thenReturn(2);
        assertEquals(2 ,step.pollMessage());
        verify(channel, times(1)).poll();
    }

    @Test
    @DisplayName("test channelClosed()")
    void channelClosed() {
        step.channelClosed();
        verify(transporter, times(1)).closeChannel();
    }

    @Test
    @DisplayName("test copy()")
    void copy() {
        Step<Integer, Integer> copiedStep = step.copy();
        assertEquals(handler, copiedStep.getMessageHandler());
        assertEquals(channel, copiedStep.getSourceChannel());
        assertEquals(transporter, copiedStep.getTransporter());
    }
}