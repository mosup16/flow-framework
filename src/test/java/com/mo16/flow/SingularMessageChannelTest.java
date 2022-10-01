package com.mo16.flow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class SingularMessageChannelTest {

    @BeforeEach
    void setUp() {
    }


    @Test
    @DisplayName("test message push to a singular channel")
    void push() {
        SingularMessageChannel<Integer> channel = new SingularMessageChannel<>();
        ChannelSubscriber<Integer> subscriber = mock(ChannelSubscriber.class);
        channel.setSubscriber(subscriber);
        channel.push(1);
        assertEquals(1, channel.poll());
        verify(subscriber, times(1)).startPolling();
    }

    @Test
    @DisplayName("test message polling from a singular channel")
    void poll() {
        SingularMessageChannel<Integer> channel = new SingularMessageChannel<>();
        channel.setSubscriber(mock(ChannelSubscriber.class));

        channel.push(1);
        assertEquals(1, channel.countOfAvailableMessages());
        assertTrue(channel.hasAvailableMessages());

        Integer polledMsg = channel.poll();
        assertEquals(1, polledMsg);

        assertEquals(0, channel.countOfAvailableMessages());
        assertFalse(channel.hasAvailableMessages());
        assertNull(channel.poll()); // message should be removed after being polled
    }
}