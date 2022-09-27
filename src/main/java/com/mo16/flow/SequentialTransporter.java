package com.mo16.flow;

import java.util.List;

public class SequentialTransporter<T> implements Transporter<T> {
    private Queue<T> queue;

    public SequentialTransporter(){
    }

    @Override
    public void addQueue(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public List<Queue<T>> getQueues() {
        return List.of(this.queue);
    }

    @Override
    public void publishMessage(T msg) {
        queue.push(msg);
    }
}
