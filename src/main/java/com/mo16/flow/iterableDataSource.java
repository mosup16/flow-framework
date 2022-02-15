package com.mo16.flow;

import java.util.Iterator;

public class iterableDataSource<O> implements DataSource<O> {

    private final Iterator<O> iterator;
    private boolean completed;
    private Transporter<O> transporter;
    private MessageContainer<O> container;

    public iterableDataSource(Iterable<O> iterable) {
        this.completed = false;
        this.iterator = iterable.iterator();
        this.container = new DefaultMessageContainer<>();
    }

    @Override
    public void generate() {
        while (!completed && iterator.hasNext()) {
            MessageContainer<O> container = getMessageContainer();
            container.setTerminationMessageCondition(false);
            container.setMessage(iterator.next());
            transporter.publishMessage(container);
        }

        complete();
    }

    @Override
    public void setTransporter(Transporter<O> transporter) {
        this.transporter = transporter;
    }

    @Override
    public MessageContainer<O> getMessageContainer() {
        return this.container;
    }

    private void complete() {
        setCompleted();
        MessageContainer<O> container = getMessageContainer();
        container.setTerminationMessageCondition(true);
        container.setMessage(null);
        transporter.publishMessage(container);
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    private void setCompleted() {
        this.completed = true;
    }
}
