package com.mo16.flow;

import java.util.ArrayList;
import java.util.List;

public class RoundRobinParallelTransporter<T> implements Transporter<T> {
    private List<Queue<T>> queues = new ArrayList<>();
    private List<MessageContainer<T>> messageContainers = new ArrayList<>();
    private int nextQueue = 0;

    @Override
    public void addQueue(Queue<T> queue) {
        this.queues.add(queue);
        this.messageContainers.add(new DefaultMessageContainer<>());
    }

    @Override
    public List<Queue<T>> getQueues() {
        return queues;
    }

    @Override
    public void publishMessage(T msg) {
        nextQueue = nextQueue % this.queues.size();
        queues.get(nextQueue).push(msg);
        nextQueue++;

    }

    @Override
    public void publishMessage(MessageContainer<T> msg) {

        if (msg.isFlowTerminatorMessage()) {
            nextQueue = nextQueue % this.queues.size();

            //get queue's associated MessageContainer object
            MessageContainer<T> container = messageContainers.get(nextQueue);

            container.setMessage(msg.getMessage());
            container.setTerminationMessageCondition(msg.isFlowTerminatorMessage());

            queues.get(nextQueue).push(container);
            nextQueue++;

        } else {
            System.out.println("RoundRobinParallelTransporter.publishMessage");
            for (int i = 0; i < queues.size(); i++) {
                //get queue's associated MessageContainer object
                MessageContainer<T> container = messageContainers.get(nextQueue);
                container.setMessage(msg.getMessage());
                container.setTerminationMessageCondition(msg.isFlowTerminatorMessage());

                queues.get(nextQueue).push(container);
            }
        }
    }

    @Override
    public MessageContainer<T> getMessageContainer() {
        return null;
    }
}
