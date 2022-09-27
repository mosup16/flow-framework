package com.mo16.flow;


public interface Channel<T> {
    void push(T msg);

    T poll();

    void notifySubscriber();

    QueueSubscriber<T> getSubscriber();

    void setSubscriber(QueueSubscriber<T> s);

    boolean hasAvailableMessages();

    int countOfAvailableMessages();


    static <I> SingularMessageChannel<I> createLinkedQueue(QueueSubscriber<I> s) {
        SingularMessageChannel<I> queue = new SingularMessageChannel<>();
        queue.setSubscriber(s);
        return queue;
    }
}

