package com.mo16.flow;

public interface DataSource<O> {
    void generate();

    void setTransporter(Transporter<O> transporter);


    static  <U> DataSource<U> newIterableDataSource(Iterable<U> iterable){
        return new iterableDataSource<>(iterable);
    }

    boolean isCompleted();
}
