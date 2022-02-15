package com.mo16.flow;

import java.util.List;

public interface Queue<T> {
    void push(T msg);

    void push(MessageContainer<T> message);

    T poll();

    List<T> pollChunk(int cSize);

    void notifySubscriber();

    QueueSubscriber<T> getSubscriber();

    void setSubscriber(QueueSubscriber<T> s);

    boolean hasAvailableMessages();

    int countOfAvailableMessages();


    static <I> LinkedQueue<I> createLinkedQueue(QueueSubscriber<I> s) {
        LinkedQueue<I> queue = new LinkedQueue<>();
        queue.setSubscriber(s);
        return queue;
    }
}

