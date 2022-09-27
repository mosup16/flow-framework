package com.mo16.flow;


public interface Queue<T> {
    void push(T msg);

    T poll();

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

