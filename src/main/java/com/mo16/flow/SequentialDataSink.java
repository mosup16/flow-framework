package com.mo16.flow;

import java.util.function.Consumer;

public class SequentialDataSink<I> implements DataSink<I> {
    private Channel<I> channel;
    private Consumer<I> consumer;


    @Override
    public void setQueue(Channel<I> channel) {
        this.channel = channel;
    }

    @Override
    public Channel<I> getQueue() {
        return this.channel;
    }

    @Override
    public void startPolling() {
        int numberOfMessages = getQueue().countOfAvailableMessages();
        for (int i = 0; i < numberOfMessages; i++)
            consume(pollMessage());
    }


    private void consume(I msg) {
        consumer.accept(msg);
    }

    @Override
    public void onNewMessage(Consumer<I> consumer) {
        this.consumer = consumer;
    }

    @Override
    public I pollMessage() {
        return this.getQueue().poll();
    }

    @Override
    public void channelClosed() {
        return;
    }


}
