package com.mo16.flow;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface DataSink<I> extends QueueSubscriber<I> {
    void onNewMessage(Consumer<I> consumer);
}
