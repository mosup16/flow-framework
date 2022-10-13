package com.mo16.flow;

/**
 * Class that holds the configurations used to support the internal back pressure strategy
 */
public class BackPressureConfigs {
    private final long channelMaxBufferSize;
    private final long flowMaxLoadLimit;

    /**
     * @param channelMaxBufferSize identifies the maximum number of buffered messages in a single channel.
     *                             if value less than 1 , it will be ignored and the max size will be infinite
     * @param flowMaxLoadLimit identifies the maximum number of buffered messages for a single step.
     *                        if value less than 1 , it will be ignored and the limit will be infinite.
     * */
    public BackPressureConfigs(long channelMaxBufferSize, long flowMaxLoadLimit) {
        this.channelMaxBufferSize = channelMaxBufferSize;
        this.flowMaxLoadLimit = flowMaxLoadLimit;
    }

    public long getChannelMaxBufferSize() {
        return channelMaxBufferSize;
    }

    public long getFlowMaxLoadLimit() {
        return flowMaxLoadLimit;
    }

}
