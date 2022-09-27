package com.mo16.flow;

import java.util.List;

public interface Transporter<T> {
    void addQueue(Queue<T> queue);
    List<Queue<T>> getQueues();
    void publishMessage(T msg);

}
