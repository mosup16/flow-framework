package com.mo16.flow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class BufferedBlockingChannel<T> implements Channel<T> {

    private final BlockingQueue<T> queue;
    private ChannelSubscriber<T> subscriber;
    private boolean closed;


    public BufferedBlockingChannel() {

        queue = new LinkedBlockingDeque<>(1024);
    }

    @Override
    public void push(T msg) {
        try {
            queue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            startSubscriber();
    }

    @Override
    public T poll() {
        try {
            return queue.poll(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public boolean hasAvailableMessages() {
        return !queue.isEmpty();
    }

    @Override
    public int countOfAvailableMessages() {
        return queue.size();
    }

    @Override
    public void notifySubscriber() {
        subscriber.startPolling();
    }

    @Override
    public ChannelSubscriber<T> getSubscriber() {
        return subscriber;
    }

    @Override
    public void setSubscriber(ChannelSubscriber<T> s) {
        this.subscriber = s;
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            subscriber.channelClosed();
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    public void startSubscriber() {
        this.getSubscriber().startPolling();
    }

}
