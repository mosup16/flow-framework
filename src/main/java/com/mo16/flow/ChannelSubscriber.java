package com.mo16.flow;

public interface ChannelSubscriber<I> {
    void setQueue(Channel<I> channel);

    Channel<I> getQueue();

    void startPolling();

    I pollMessage();

    void channelClosed();
}
