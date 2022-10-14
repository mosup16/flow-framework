package com.mo16.flow.loadbalancing;

import com.mo16.flow.Channel;

import java.util.ArrayList;
import java.util.List;

public class RoundRobinLoadBalancer<T> implements LoadBalancer<T> {

    List<Channel<T>> channels = new ArrayList<>();
    private int nextChannel = 0;

    public void registerChannel(Channel<T> channel){
        channels.add(channel);
    }

    public Channel<T> selectChannel(){
        nextChannel = nextChannel % channels.size();
        Channel<T> channel = channels.get(nextChannel);
        nextChannel++;
        return channel;
    }

    @Override
    public Channel<T> removeChannel(Channel<T> channel) throws IllegalArgumentException {
        boolean removed = channels.remove(channel);
        if (removed)
            return channel;
        else throw new IllegalArgumentException("provided channel can't be removed");
    }
}
