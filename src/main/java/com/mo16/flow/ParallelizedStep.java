package com.mo16.flow;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class ParallelizedStep<T> extends SequentialStep<T, T> {
    private final ExecutorService executorService;
    private final DataSource source;
    private Consumer<T, T> consumer;

    public ParallelizedStep(ExecutorService executorService, DataSource source) {
        this.executorService = executorService;
        this.source = source;
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
        consumer = new Consumer<>((ParallelQueue<T>) getQueue(),
                getTransporter(), getMessageHandler());

        executorService.submit(consumer);
    }

    static class Consumer<I, O> implements Runnable {

        private boolean isDone = false;
        private final ParallelQueue<I> queue;
        private final Function<I, O> messageHandler;
        private final Transporter<O> transporter;

        public Consumer(ParallelQueue<I> queue, Transporter<O> transporter,
                        Function<I, O> messageHandler) {
            this.queue = queue;
            this.messageHandler = messageHandler;
            this.transporter = transporter;
        }

        @Override
        public void run() {
            while (!isDone) {
                    I input = queue.poll();
                    O output = messageHandler.apply(input);
                    transporter.publishMessage(output);
            }
            while (queue.hasAvailableMessages()) { //clean the queue
                System.out.println("hhhhh");
                I input = queue.poll();
                O output = messageHandler.apply(input);
                transporter.publishMessage(output);
            }

        }

        void stop() {
            isDone = true;
        }
    }
}
