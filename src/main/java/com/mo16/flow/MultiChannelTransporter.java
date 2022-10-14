package com.mo16.flow;

import com.mo16.flow.loadbalancing.LoadBalancer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiChannelTransporter<T> implements Transporter<T> {
    private final LoadBalancer<T> loadBalancer;
    private List<Channel<T>> channels = new ArrayList<>();

    private final long channelMaxBufferSize;
    private final long flowMaxLoadLimit;

    private Set<Channel<T>> removedChannels = new HashSet<>();


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
        reRegisterRemovedChannels();
        Channel<T> channel = loadBalancer.selectChannel();
        channel.push(msg);
        unregisterOverloadedChannels();
    }

    private void unregisterOverloadedChannels() {
        if (channelMaxBufferSize > 0)
            channels.stream()
                    .filter(c -> c.countOfAvailableMessages() >= channelMaxBufferSize || c.isOverloaded())
                    .forEach(c -> {
                        loadBalancer.removeChannel(c);
                        removedChannels.add(c);
                    });
    }

    private void reRegisterRemovedChannels() {
        List<Channel<T>> registeredChannels = removedChannels.stream()
                .filter(channel -> channel.countOfAvailableMessages() < channelMaxBufferSize)
                .peek(loadBalancer::registerChannel)
                .collect(Collectors.toList());
        removedChannels.removeAll(registeredChannels);
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
