package com.mo16.flow;

import java.util.function.Function;

public interface ProcessingStep<I, O> extends Step<I, O>{
    void onNewMessage(Function<I, O> messageProcessor);
    Function<I, O> getMessageProcessor();
}
