package com.mo16.flow;


public interface Channel<T> {
    void push(T msg);

    T poll();

    void notifySubscriber();

    ChannelSubscriber<T> getSubscriber();

    void setSubscriber(ChannelSubscriber<T> s);

    boolean hasAvailableMessages();

    int countOfAvailableMessages();

    void close();

    boolean isClosed();

    boolean isOverloaded();
}

