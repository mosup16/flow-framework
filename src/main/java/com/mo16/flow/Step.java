package com.mo16.flow;

public interface Step<I,O> extends ChannelSubscriber<I> {
    void setTransporter(Transporter<O> transporter);
    Transporter<O> getTransporter();

    Step<I,O> copy();
}
