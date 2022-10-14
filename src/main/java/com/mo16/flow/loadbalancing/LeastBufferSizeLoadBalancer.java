package com.mo16.flow.loadbalancing;

import com.mo16.flow.Channel;

import java.util.*;

public class LeastBufferSizeLoadBalancer<T> implements LoadBalancer<T>{

    Set<Channel<T>> channels = new HashSet<>();
    Map<Channel<T>, Boolean> channelsActivationStates = new HashMap<>();

    public void registerChannel(Channel<T> channel){
        channels.add(channel);
        channelsActivationStates.put(channel, true);
    }

    public Channel<T> selectChannel(){
        return channels.stream()
                .filter(channel -> channelsActivationStates.get(channel))
                .min(Comparator.comparingInt(Channel::countOfAvailableMessages))
                .orElseThrow(() -> new RuntimeException("channel couldn't be selected"));
    }

    @Override
    public void deactivateChannel(Channel<T> channel){
        channelsActivationStates.put(channel, false);
    }

    @Override
    public void activateChannel(Channel<T> channel){
        channelsActivationStates.put(channel, true);
    }
}
