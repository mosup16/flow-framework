package com.mo16.flow;

import java.util.function.Consumer;

public interface DataSink<I> extends ChannelSubscriber<I> {
    void onNewMessage(Consumer<I> consumer);
}
