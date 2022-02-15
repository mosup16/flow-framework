package com.mo16.flow;

public interface MessageContainer<T> {
    boolean isFlowTerminatorMessage();

    T getMessage();

    void setMessage(T msg);

    void setTerminationMessageCondition(boolean isFlowTerminatorMessage);

    void clear();
}
