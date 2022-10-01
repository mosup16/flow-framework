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
        SequentialStep<Integer,Integer> step = mock(new SequentialStep<Integer, Integer>(){}.getClass());
        channel.setSubscriber(step);
        channel.push(1);
        assertEquals(1, channel.poll());
        verify(step, times(1)).startPolling();
    }

}