package com.mo16.flow;

public class DefaultMessageContainer<T> implements MessageContainer<T> {

    private boolean isFlowTerminatorMessage;
    private T message;

    public DefaultMessageContainer(T message, boolean isFlowTerminatorMessage) {
        this.isFlowTerminatorMessage = isFlowTerminatorMessage;
        this.message = message;
    }


    public DefaultMessageContainer() {
    }

    @Override
    public boolean isFlowTerminatorMessage() {
        return this.isFlowTerminatorMessage;
    }

    @Override
    public T getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(T message) {
        this.message = message;
    }

    @Override
    public void setTerminationMessageCondition(boolean isFlowTerminatorMessage) {
        this.isFlowTerminatorMessage = isFlowTerminatorMessage;
    }

    @Override
    public void clear() {
        this.message = null;
        this.isFlowTerminatorMessage = false;
    }
}
