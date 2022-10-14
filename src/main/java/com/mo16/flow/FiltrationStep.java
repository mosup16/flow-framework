package com.mo16.flow;

import java.util.function.Predicate;

public class FiltrationStep<I> implements Step<I, I> {

    private Predicate<I> filter;
    private Channel<I> channel;
    private Transporter<I> transporter;

    public void setFilter(Predicate<I> predicate){
        this.filter = predicate;
    }

    public Predicate<I> getFilter() {
        return filter;
    }

    @Override
    public void subscribeTo(Channel<I> channel) {
        this.channel = channel;
    }

    @Override
    public Channel<I> getSourceChannel() {
        return this.channel;
    }


    @Override
    public I pollMessage() {
        return this.getSourceChannel().poll();
    }

    @Override
    public void channelClosed() {
        transporter.closeChannel();
    }

    @Override
    public boolean isOverloaded() {
        return transporter.isOverloaded();
    }

    @Override
    public void setTransporter(Transporter<I> transporter) {
        this.transporter = transporter;
    }

    @Override
    public Transporter<I> getTransporter() {
        return this.transporter;
    }


    @Override
    public void startPolling() {
        int numberOfMessages = getSourceChannel().countOfAvailableMessages();
        for (int i = 0; i < numberOfMessages; i++) {
            if (this.getSourceChannel().hasAvailableMessages()) {
                I msg = pollMessage();
                if (filter.test(msg))
                    this.getTransporter().publishMessage(msg);
            } else break;
        }
    }

    @Override
    public FiltrationStep<I> copy() {
        FiltrationStep<I> step = new FiltrationStep<>();
        step.setFilter(filter);
        step.subscribeTo(this.getSourceChannel());
        step.setTransporter(this.getTransporter());
        return step;
    }
}
