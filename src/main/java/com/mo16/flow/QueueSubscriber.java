package com.mo16.flow;

import java.util.List;

public interface QueueSubscriber<I> {
    void setQueue(Queue<I> queue);

    Queue<I> getQueue();

    void startPolling();

    I pollMessage();

    void onFlowTerminated();
}
