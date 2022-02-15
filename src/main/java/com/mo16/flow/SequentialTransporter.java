package com.mo16.flow;

import java.util.List;

public class SequentialTransporter<T> implements Transporter<T> {
    private Queue<T> queue;
    private MessageContainer<T> container;
    
    public SequentialTransporter(){
        this.container = new DefaultMessageContainer<>();
    }

    @Override
    public void addQueue(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public List<Queue<T>> getQueues() {
        return List.of(this.queue);
    }

    @Override
    public void publishMessage(T msg) {
        queue.push(msg);
    }

    @Override
    public void publishMessage(MessageContainer<T> msg) {
        MessageContainer<T> container = getMessageContainer();
        container.setMessage(msg.getMessage());
        container.setTerminationMessageCondition(msg.isFlowTerminatorMessage());
        queue.push(container);
    }

    @Override
    public MessageContainer<T> getMessageContainer() {
        return this.container;
    }
}
