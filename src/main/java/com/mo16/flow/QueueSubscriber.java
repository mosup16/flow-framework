package com.mo16.flow;

public interface QueueSubscriber<I> {
    void setQueue(Channel<I> channel);

    Channel<I> getQueue();

    void startPolling();

    I pollMessage();
}
