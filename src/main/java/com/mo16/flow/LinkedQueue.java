package com.mo16.flow;

import java.util.Deque;
import java.util.LinkedList;

public class LinkedQueue<T> extends AbstractQueue<T> {

    protected final Deque<T> queue;
    private QueueSubscriber subscriber;

    public LinkedQueue() {
        queue = new LinkedList<T>();
    }


    protected Deque<T> getQueue() {
        return queue;
    }

    @Override
    public void push(T msg) {
        getQueue().addLast(msg);
         notifySubscriber();
    }

    @Override
    public T poll() {
        //TODO should handle thrown exception if the data structure is empty properly
        return getQueue().removeFirst();
    }

    @Override
    public void notifySubscriber() {
        getSubscriber().startPolling();
    }

    @Override
    public boolean hasAvailableMessages() {
        return !getQueue().isEmpty();
    }

    @Override
    public int countOfAvailableMessages() {
        return getQueue().size();
    }

    @Override
    public void push(MessageContainer<T> message) {
        if (message.isFlowTerminatorMessage()) {
            getSubscriber().onFlowTerminated();
        } else {
            getQueue().addLast(message.getMessage());
            notifySubscriber();
        }
    }

    @Override
    public QueueSubscriber<T> getSubscriber() {
        return subscriber;
    }

    @Override
    public void setSubscriber(QueueSubscriber<T> s) {
        this.subscriber = s;
    }
}
