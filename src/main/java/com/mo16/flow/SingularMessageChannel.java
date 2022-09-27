package com.mo16.flow;

import java.util.Deque;
import java.util.LinkedList;

public class SingularMessageChannel<T> implements Channel<T> {

    private final Deque<T> queue;
    private QueueSubscriber<T> subscriber;

    public SingularMessageChannel() {
        queue = new LinkedList<>();
    }


    @Override
    public void push(T msg) {
        queue.addLast(msg);
         notifySubscriber();
    }

    @Override
    public T poll() {
        //TODO should handle thrown exception if the data structure is empty properly
        return queue.removeFirst();
    }

    @Override
    public void notifySubscriber() {
        subscriber.startPolling();
    }

    @Override
    public QueueSubscriber<T> getSubscriber() {
        return subscriber;
    }

    @Override
    public void setSubscriber(QueueSubscriber<T> s) {
        this.subscriber = s;
    }

    @Override
    public boolean hasAvailableMessages() {
        return !queue.isEmpty();
    }

    @Override
    public int countOfAvailableMessages() {
        return queue.size();
    }

}
