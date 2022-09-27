package com.mo16.flow;


public interface Channel<T> {
    void push(T msg);

    T poll();

    void notifySubscriber();

    ChannelSubscriber<T> getSubscriber();

    void setSubscriber(ChannelSubscriber<T> s);

    boolean hasAvailableMessages();

    int countOfAvailableMessages();


    static <I> SingularMessageChannel<I> createLinkedQueue(ChannelSubscriber<I> s) {
        SingularMessageChannel<I> queue = new SingularMessageChannel<>();
        queue.setSubscriber(s);
        return queue;
    }
}

