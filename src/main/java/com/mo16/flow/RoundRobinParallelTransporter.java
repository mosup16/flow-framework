package com.mo16.flow;

import java.util.ArrayList;
import java.util.List;

public class RoundRobinParallelTransporter<T> implements Transporter<T> {
    private List<Queue<T>> queues = new ArrayList<>();
    private int nextQueue = 0;

    @Override
    public void addQueue(Queue<T> queue) {
        this.queues.add(queue);
    }

    @Override
    public List<Queue<T>> getQueues() {
        return queues;
    }

    @Override
    public void publishMessage(T msg) {
        nextQueue = nextQueue % this.queues.size();
        queues.get(nextQueue).push(msg);
        nextQueue++;

    }

}
