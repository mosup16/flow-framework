package com.mo16.flow;

import java.util.function.Function;

public interface Step<I,O> extends QueueSubscriber<I>{
    void onNewMessage(Function<I,O> function);
    void setTransporter(Transporter<O> transporter);
    Transporter<O> getTransporter();
    void pushToTransporter(O msg);

    Function<I,O> getMessageHandler();

    Step<I,O> copy();
}
