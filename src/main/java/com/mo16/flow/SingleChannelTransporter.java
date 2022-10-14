package com.mo16.flow;

import java.util.List;

public class SingleChannelTransporter<T> implements Transporter<T> {
    private Channel<T> channel;
    private final Long channelMaxBufferSize;

    public SingleChannelTransporter(BackPressureConfigs configs){
        channelMaxBufferSize = configs.getChannelMaxBufferSize();
    }

    @Override
    public void addChannel(Channel<T> channel) {
        this.channel = channel;
    }

    @Override
    public List<Channel<T>> getChannels() {
        return List.of(this.channel);
    }

    @Override
    public void publishMessage(T msg) {
        channel.push(msg);
    }

    @Override
    public void closeChannel() {
        channel.close();
    }

    @Override
    public boolean isOverloaded() {
        return (channelMaxBufferSize > 0 && channel.countOfAvailableMessages() >= channelMaxBufferSize)
                || channel.isOverloaded();
    }
}
