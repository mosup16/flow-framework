package com.mo16.flow.loadbalancing;

import com.mo16.flow.Channel;

import java.util.*;

public class RoundRobinLoadBalancer<T> implements LoadBalancer<T> {

    List<Channel<T>> channels = new ArrayList<>();
    Map<Channel<T>, Boolean> channelsActivationStates = new HashMap<>();
    private int nextChannel = 0;

    public void registerChannel(Channel<T> channel){
        channels.add(channel);
        channelsActivationStates.put(channel, true);
    }

    public Channel<T> selectChannel(){
        for (int i = 0; i < channels.size(); i++) {
            nextChannel = nextChannel % channels.size();
            Channel<T> channel = channels.get(nextChannel);
            nextChannel++;
            if (channelsActivationStates.get(channel))
                return channel;
        }
        throw new RuntimeException("channel couldn't be selected");
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
