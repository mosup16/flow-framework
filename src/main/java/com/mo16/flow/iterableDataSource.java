package com.mo16.flow;

import java.util.Iterator;

public class iterableDataSource<O> implements DataSource<O> {

    private final Iterator<O> iterator;
    private boolean completed;
    private Transporter<O> transporter;

    public iterableDataSource(Iterable<O> iterable) {
        this.completed = false;
        this.iterator = iterable.iterator();
    }

    @Override
    public void generate() {
        while (!completed && iterator.hasNext()) {
            O msg = iterator.next();
            transporter.publishMessage(msg);
        }

        complete();
    }

    @Override
    public void setTransporter(Transporter<O> transporter) {
        this.transporter = transporter;
    }


    private void complete() {
        setCompleted();
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    private void setCompleted() {
        this.completed = true;
    }
}
