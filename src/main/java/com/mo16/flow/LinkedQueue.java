package com.mo16.flow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LinkedQueue<T> implements Queue<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private QueueSubscriber subscriber;


    @Override
    public void push(T msg) {
        queue.addLast(msg);
        notifySubscriber();
    }

    @Override
    public T poll() {
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
