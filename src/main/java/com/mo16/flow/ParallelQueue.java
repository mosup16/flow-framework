package com.mo16.flow;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ParallelQueue<T> implements Queue<T> {

    private boolean isSubscriberStarted;
    private BlockingQueue<T> queue;
    private boolean isTerminated;

    public ParallelQueue() {
        isSubscriberStarted = false;

        queue = new ArrayBlockingQueue<T>(1024);
    }

    @Override
    public void push(T msg) {
        try {
            getBlockingQueue().put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!this.isSubscriberStarted) {
            startSubscriber();
            this.isSubscriberStarted = true;
        }
    }

    private BlockingQueue<T> getBlockingQueue() {
        return queue;
    }

    @Override
    public T poll() {
        try {
            return getBlockingQueue().poll(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void notifySubscriber() {
        getSubscriber().startPolling();
    }

    @Override
    public QueueSubscriber<T> getSubscriber() {
        return null;
    }

    @Override
    public void setSubscriber(QueueSubscriber<T> s) {

    }

    @Override
    public void push(MessageContainer<T> message) {
        if (message.isFlowTerminatorMessage()) {
            getSubscriber().onFlowTerminated();
            isTerminated = true;

        } else {
            try {
                getBlockingQueue().put(message.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!this.isSubscriberStarted) {
                startSubscriber();
                this.isSubscriberStarted = true;
            }
        }
    }

    @Override
    public boolean hasAvailableMessages() {
        return !getBlockingQueue().isEmpty();
    }

    @Override
    public int countOfAvailableMessages() {
        return 0;
    }


    public void startSubscriber(){
        ((ParallelStep) this.getSubscriber()).startPolling();
    }

}
