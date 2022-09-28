package com.mo16.flow;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class ParallelizedStep<T> extends SequentialStep<T, T> {
    private final ExecutorService executorService;
    private final DataSource source;
    private Consumer<T, T> consumer;
    private boolean channelClosed;

    public ParallelizedStep(ExecutorService executorService, DataSource source) {
        this.executorService = executorService;
        this.source = source;
    }

    @Override
    public void channelClosed() {
        channelClosed = true;
    }

    @Override
    public Step<T, T> copy() {
        ParallelizedStep<T> step = new ParallelizedStep<>(this.executorService, this.source);
        step.onNewMessage(this.getMessageHandler());

        step.setQueue(this.getQueue());
        step.setTransporter(this.getTransporter());
        return step;
    }



    @Override
    public void startPolling() {
        consumer = new Consumer<>((BufferedBlockingChannel<T>) getQueue(),
                getTransporter(), getMessageHandler());

        executorService.submit(consumer);
    }

    static class Consumer<I, O> implements Runnable {

        private boolean isDone = false;
        private final BufferedBlockingChannel<I> channel;
        private final Function<I, O> messageHandler;
        private final Transporter<O> transporter;

        public Consumer(BufferedBlockingChannel<I> channel, Transporter<O> transporter,
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
