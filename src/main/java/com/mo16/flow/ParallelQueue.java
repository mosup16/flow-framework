package com.mo16.flow;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ParallelQueue<T> extends LinkedQueue<T> {

    private boolean isSubscriberStarted;
    private BlockingQueue<T> queue;

    public ParallelQueue() {
        isSubscriberStarted = false;

        queue = new ArrayBlockingQueue<T>(1024);
    }

    @Override
    public void push(T msg) {
        try {
            queue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!this.isSubscriberStarted) {
            startSubscriber();
            this.isSubscriberStarted = true;
        }
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
    public QueueSubscriber<T> getSubscriber() {
        return super.getSubscriber();
    }

    public void startSubscriber() {
        ((ParallelizedStep) this.getSubscriber()).startPolling();
    }

}
