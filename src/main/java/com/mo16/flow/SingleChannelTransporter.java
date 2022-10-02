package com.mo16.flow;

import java.util.List;

public class SingleChannelTransporter<T> implements Transporter<T> {
    private Channel<T> channel;

    public SingleChannelTransporter(){
    }

    @Override
    public void addChannel(Channel<T> channel) {
        this.channel = channel;
    }

    @Override
    public List<Channel<T>> getChannels() {
        return List.of(this.channel);
    }

    @Override
    public void publishMessage(T msg) {
        channel.push(msg);
    }

    @Override
    public void closeChannel() {
        channel.close();
    }
}
