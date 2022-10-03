package com.mo16.flow;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class AsynchronousStep<T> implements Step<T, T> {
    private final ExecutorService executorService;
    private final DataSource source;
    private Consumer<T, T> consumer;
    private boolean channelClosed;

    private Channel<T> channel;
    private Function<T, T> messageHandler;
    private Transporter<T> transporter;

    public AsynchronousStep(ExecutorService executorService, DataSource source) {
        this.executorService = executorService;
        this.source = source;
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
    public void onNewMessage(Function<T, T> messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public Function<T, T> getMessageHandler() {
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
        AsynchronousStep<T> step = new AsynchronousStep<>(this.executorService, this.source);
        step.onNewMessage(this.getMessageHandler());

        step.subscribeTo(this.getSourceChannel());
        step.setTransporter(this.getTransporter());
        return step;
    }



    @Override
    public void startPolling() {
        consumer = new Consumer<>(getSourceChannel(), getTransporter(), getMessageHandler());

        executorService.submit(consumer);
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
            while (channel.hasAvailableMessages()) { //clean the queue
                System.out.println("hhhhh");
                I input = channel.poll();
                O output = messageHandler.apply(input);
                transporter.publishMessage(output);
            }

        }

        void stop() {
            isDone = true;
        }
    }
}
