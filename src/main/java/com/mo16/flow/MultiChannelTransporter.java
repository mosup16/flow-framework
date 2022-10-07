package com.mo16.flow;

import com.mo16.flow.loadbalancing.LoadBalancer;
import java.util.ArrayList;
import java.util.List;

public class MultiChannelTransporter<T> implements Transporter<T> {
    private final LoadBalancer<T> loadBalancer;
    private List<Channel<T>> channels = new ArrayList<>();

    public MultiChannelTransporter(LoadBalancer<T> loadBalancer){
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void addChannel(Channel<T> channel) {
        this.channels.add(channel);
        loadBalancer.registerChannel(channel);
    }

    @Override
    public List<Channel<T>> getChannels() {
        return channels;
    }

    @Override
    public void publishMessage(T msg) {
        Channel<T> channel = loadBalancer.selectChannel();
        channel.push(msg);
    }

    @Override
    public void closeChannel() {
        channels.forEach(Channel::close);
    }

}
