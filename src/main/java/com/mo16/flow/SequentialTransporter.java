package com.mo16.flow;

public class SequentialTransporter<T> implements Transporter<T> {
    private Queue<T> queue;

    @Override
    public void setQueue(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public Queue<T> getQueue() {
        return this.queue;
    }

    @Override
    public void publishMessage(T msg) {
        queue.push(msg);
    }
}
