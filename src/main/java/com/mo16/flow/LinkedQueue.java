package com.mo16.flow;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class LinkedQueue<T> implements Queue<T> {

    private final Deque<T> queue;
    private QueueSubscriber subscriber;

    public LinkedQueue() {
        queue = new LinkedList<T>();
    }


    protected Deque<T> getQueue() {
        return queue;
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
    public List<T> pollChunk(int cSize) {
        var list = new ArrayList<T>(cSize);
        for (int i = 0; i < cSize; i++) {
            if (queue.isEmpty())
                break;
            else
                list.add(poll());
        }
        notifySubscriber();
        return list;
    }

    @Override
    public void notifySubscriber() {
        subscriber.newMessagesArrived(queue.size());
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
