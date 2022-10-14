package com.mo16.flow.loadbalancing;

import com.mo16.flow.Channel;

public interface LoadBalancer<T> {

    void registerChannel(Channel<T> channel);

    Channel<T> selectChannel();

    void activateChannel(Channel<T> channel);

    void deactivateChannel(Channel<T> channel);

}
