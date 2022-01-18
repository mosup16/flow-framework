package com.mo16.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SequentialDataSink<I> implements DataSink<I> {
    private Queue<I> queue;
    private Consumer<I> consumer;

    @Override
    public void setQueue(Queue<I> queue) {
        this.queue = queue;
    }

    @Override
    public Queue<I> getQueue() {
        return this.queue;
    }

    @Override
    public void newMessagesArrived(int numberOfMessages) {
        for (int i = 0; i < numberOfMessages; i++)
            consume(pollMessage());
    }


    private void consume(I msg) {
        consumer.accept(msg);
    }

    @Override
    public void onNewMessage(Consumer<I> consumer) {
        this.consumer = consumer;
    }

    @Override
    public I pollMessage() {
        return this.getQueue().poll();
    }

    @Override
    public List<I> pollMessageChunk(int cSize) {
        var list = new ArrayList<I>(cSize);
        for (int i = 0; i < cSize; i++) {
            if (queue.hasAvailableMessages())
                list.add(pollMessage());
            else break;
        }
        return list;
    }

}
