package com.mo16.flow;

import java.util.List;

public interface Transporter<T> {
    void addChannel(Channel<T> channel);
    List<Channel<T>> getChannels();
    void publishMessage(T msg);
    void closeChannel();
    boolean isOverloaded();
}
