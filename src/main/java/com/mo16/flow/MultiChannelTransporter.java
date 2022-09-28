package com.mo16.flow;

import java.util.ArrayList;
import java.util.List;

public class MultiChannelTransporter<T> implements Transporter<T> {
    private List<Channel<T>> channels = new ArrayList<>();
    private int nextQueue = 0;

    @Override
    public void addChannel(Channel<T> channel) {
        this.channels.add(channel);
    }

    @Override
    public List<Channel<T>> getChannels() {
        return channels;
    }

    @Override
    public void publishMessage(T msg) {
        nextQueue = nextQueue % this.channels.size();
        channels.get(nextQueue).push(msg);
        nextQueue++;

    }

    @Override
    public void closeChannel() {
        channels.forEach(Channel::close);
    }

}
