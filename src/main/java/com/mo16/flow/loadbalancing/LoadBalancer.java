package com.mo16.flow.loadbalancing;

import com.mo16.flow.Channel;

public interface LoadBalancer<T> {

    void registerChannel(Channel<T> channel);

    Channel<T> selectChannel();

    Channel<T> removeChannel(Channel<T> channel) throws IllegalArgumentException;

}
