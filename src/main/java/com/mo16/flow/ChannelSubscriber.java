package com.mo16.flow;

public interface ChannelSubscriber<I> {
    void subscribeTo(Channel<I> channel);

    Channel<I> getSourceChannel();

    void startPolling();

    I pollMessage();

    void channelClosed();
}
