package com.mo16.flow.loadbalancing;

import com.mo16.flow.Channel;

import java.util.*;

public class LeastBufferSizeLoadBalancer<T> implements LoadBalancer<T>{

    Set<Channel<T>> channels = new HashSet<>();

    public void registerChannel(Channel<T> channel){
        channels.add(channel);
    }

    public Channel<T> selectChannel(){
        return channels.stream().min(Comparator.comparingInt(Channel::countOfAvailableMessages))
                .orElseThrow(() -> new RuntimeException("no channel could be selected"));
    }

    @Override
    public Channel<T> removeChannel(Channel<T> channel) throws IllegalArgumentException {
        boolean removed = channels.remove(channel);
        if (removed)
            return channel;
        else throw new IllegalArgumentException("provided channel can't be removed");
    }
}
