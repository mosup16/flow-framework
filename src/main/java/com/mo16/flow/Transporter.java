package com.mo16.flow;

public interface Transporter<T> {
    void setQueue(Queue<T> queue);
    Queue<T> getQueue();
    void publishMessage(T msg);
}
