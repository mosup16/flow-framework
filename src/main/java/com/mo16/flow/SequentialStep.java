package com.mo16.flow;

import java.util.function.Function;

// a step designed to be executed in a sequential manner
public class SequentialStep<I, O> implements Step<I, O> {

    private Channel<I> channel;
    private Transporter<O> transporter;
    private Function<I, O> function;

    public SequentialStep(){
    }

    @Override
    public void setQueue(Channel<I> channel) {
        this.channel = channel;
    }

    @Override
    public Channel<I> getQueue() {
        return this.channel;
    }

    @Override
    public void startPolling() {
        int numberOfMessages = getQueue().countOfAvailableMessages();
        for (int i = 0; i < numberOfMessages; i++) {
            if (channel.hasAvailableMessages()) {
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
    public void channelClosed() {
        transporter.closeChannel();
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
        SequentialStep<I, O> step = new SequentialStep<>();
        step.function = function;
        step.channel = channel;
        step.transporter = transporter;
        return step;
    }
}
