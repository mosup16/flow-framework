package com.mo16.flow;

import java.util.function.Function;

/**
 * this step is synchronous in the sense that it runs on the same thread which was created at.
 */
public class SynchronousStep<I, O> implements ProcessingStep<I, O> {

    private Channel<I> channel;
    private Transporter<O> transporter;
    private Function<I, O> function;

    public SynchronousStep(){
    }

    @Override
    public void subscribeTo(Channel<I> channel) {
        this.channel = channel;
    }

    @Override
    public Channel<I> getSourceChannel() {
        return this.channel;
    }

    @Override
    public void startPolling() {
        int numberOfMessages = getSourceChannel().countOfAvailableMessages();
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
        return this.getSourceChannel().poll();
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
    public Function<I, O> getMessageProcessor() {
        return this.function;
    }

    @Override
    public SynchronousStep<I, O> copy() {
        SynchronousStep<I, O> step = new SynchronousStep<>();
        step.function = function;
        step.channel = channel;
        step.transporter = transporter;
        return step;
    }
}
