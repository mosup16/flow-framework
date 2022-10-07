package com.mo16.flow.loadbalancing;

import com.mo16.flow.Channel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeastBufferSizeLoadBalancer<T> implements LoadBalancer<T>{

    List<Channel<T>> channels = new ArrayList<>();

    public void registerChannel(Channel<T> channel){
        channels.add(channel);
    }

    public Channel<T> selectChannel(){
        return channels.stream().min(Comparator.comparingInt(Channel::countOfAvailableMessages))
                .orElseThrow(() -> new RuntimeException("no channel could be selected"));
    }
}
