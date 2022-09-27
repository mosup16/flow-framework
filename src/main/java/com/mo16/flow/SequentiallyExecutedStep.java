package com.mo16.flow;

import java.util.function.Function;

// a step designed to be executed in a sequential manner
public class SequentiallyExecutedStep<I, O> implements Step<I, O> {

    private Queue<I> queue;
    private Transporter<O> transporter;
    private Function<I, O> function;

    public SequentiallyExecutedStep(){
    }

    @Override
    public void setQueue(Queue<I> queue) {
        this.queue = queue;
    }

    @Override
    public Queue<I> getQueue() {
        return this.queue;
    }

    @Override
    public void startPolling() {
        int numberOfMessages = getQueue().countOfAvailableMessages();
        for (int i = 0; i < numberOfMessages; i++) {
            if (queue.hasAvailableMessages()) {
                O output = function.apply(pollMessage());
                transporter.publishMessage(output);
            } else break;
        }
    }

    @Override
    public void onNewMessage(Function<I, O> function) {
        this.function = function;

    }

    @Override
    public I pollMessage() {
        return this.getQueue().poll();
    }

    @Override
    public void setTransporter(Transporter<O> transporter) {
        this.transporter = transporter;
    }

    @Override
    public Transporter<O> getTransporter() {
        return this.transporter;
    }

    @Override
    public Function<I, O> getMessageHandler() {
        return this.function;
    }

    @Override
    public Step<I, O> copy() {
        SequentiallyExecutedStep<I, O> step = new SequentiallyExecutedStep<>();
        step.function = function;
        step.queue = queue;
        step.transporter = transporter;
        return step;
    }
}
