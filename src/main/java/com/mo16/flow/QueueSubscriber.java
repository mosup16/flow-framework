package com.mo16.flow;

import java.util.List;

public interface QueueSubscriber<I> {
    void setQueue(Queue<I> queue);

    Queue<I> getQueue();

    void newMessagesArrived(int numberOfMessages);

    I pollMessage();

    List<I> pollMessageChunk(int cSize);
}
