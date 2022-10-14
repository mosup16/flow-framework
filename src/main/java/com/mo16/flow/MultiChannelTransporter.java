package com.mo16.flow;

import com.mo16.flow.loadbalancing.LoadBalancer;

import java.util.ArrayList;
import java.util.List;

public class MultiChannelTransporter<T> implements Transporter<T> {
    private final LoadBalancer<T> loadBalancer;
    private List<Channel<T>> channels = new ArrayList<>();

    private final long channelMaxBufferSize;
    private final long flowMaxLoadLimit;


    public MultiChannelTransporter(LoadBalancer<T> loadBalancer, BackPressureConfigs configs) {
        this.loadBalancer = loadBalancer;
        this.channelMaxBufferSize = configs.getChannelMaxBufferSize();
        this.flowMaxLoadLimit = configs.getFlowMaxLoadLimit();
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
        activateChannels();
        Channel<T> channel = loadBalancer.selectChannel();
        channel.push(msg);
        deactivateOverloadedChannels();
    }

    private void deactivateOverloadedChannels() {
        if (channelMaxBufferSize > 0)
            channels.stream()
                    .filter(c -> c.countOfAvailableMessages() >= channelMaxBufferSize || c.isOverloaded())
                    .forEach(loadBalancer::deactivateChannel);
    }

    private void activateChannels() {
         channels.stream()
                .filter(channel -> channel.countOfAvailableMessages() < channelMaxBufferSize)
                .forEach(loadBalancer::activateChannel);
    }

    @Override
    public void closeChannel() { //TODO rename method
        channels.forEach(Channel::close);
    }

    @Override
    public boolean isOverloaded() {
        return flowMaxLoadLimit > 0 && channels.stream()
                .mapToLong(Channel::countOfAvailableMessages)
                .sum() >= flowMaxLoadLimit;
    }

}
