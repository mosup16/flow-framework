package com.mo16.flow;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class AsynchronousStep<T> implements ProcessingStep<T, T> {
    private final ExecutorService executorService;
    private Consumer<T, T> consumer;
    private boolean channelClosed;
    private boolean started;

    private Channel<T> channel;
    private Function<T, T> messageHandler;
    private Transporter<T> transporter;

    public AsynchronousStep(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void subscribeTo(Channel<T> channel) {
        this.channel = channel;
    }

    @Override
    public Channel<T> getSourceChannel() {
        return this.channel;
    }

    @Override
    public T pollMessage() {
        return this.getSourceChannel().poll();
    }

    @Override
    public void setMessageProcessor(Function<T, T> messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public Function<T, T> getMessageProcessor() {
        return this.messageHandler;
    }

    @Override
    public void setTransporter(Transporter<T> transporter) {
        this.transporter = transporter;
    }

    @Override
    public Transporter<T> getTransporter() {
        return this.transporter;
    }


    @Override
    public void channelClosed() {
        channelClosed = true;
    }
    @Override
    public Step<T, T> copy() {
        AsynchronousStep<T> step = new AsynchronousStep<>(this.executorService);
        step.setMessageProcessor(this.getMessageProcessor());

        step.subscribeTo(this.getSourceChannel());
        step.setTransporter(this.getTransporter());
        return step;
    }



    @Override
    public void startPolling() {
        if (started) return;
        consumer = new Consumer<>(getSourceChannel(), getTransporter(), getMessageProcessor());

        executorService.submit(consumer);
        started = true;
    }

    static class Consumer<I, O> implements Runnable {

        private boolean isDone = false;
        private final Channel<I> channel;
        private final Function<I, O> messageHandler;
        private final Transporter<O> transporter;

        public Consumer(Channel<I> channel, Transporter<O> transporter,
                        Function<I, O> messageHandler) {
            this.channel = channel;
            this.messageHandler = messageHandler;
            this.transporter = transporter;
        }

        @Override
        public void run() {
            while (!isDone) {
                    I input = channel.poll();
                    if (input == null && channel.isClosed()){
                        transporter.closeChannel();
                        break;
                    }
                    O output = messageHandler.apply(input);
                    transporter.publishMessage(output);
            }

        }

        void stop() {
            isDone = true;
        }
    }
}
