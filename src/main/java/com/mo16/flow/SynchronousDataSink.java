package com.mo16.flow;

import java.util.function.Consumer;

public class SynchronousDataSink<I> implements DataSink<I> {
    private Channel<I> channel;
    private Consumer<I> consumer;


    @Override
    public void subscribeTo(Channel<I> channel) {
        this.channel = channel;
    }

    @Override
    public Channel<I> getSourceChannel() {
        return this.channel;
    }

    @Override
    public void startPolling() {
        int numberOfMessages = getSourceChannel().countOfAvailableMessages();
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
        return this.getSourceChannel().poll();
    }

    @Override
    public void channelClosed() {
        return;
    }

    @Override
    public boolean isOverloaded() {
        return false;
    }


}
