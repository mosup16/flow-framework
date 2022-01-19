package com.mo16.flow;

import java.util.function.Predicate;

public class FiltrationStep<I> extends SequentiallyExecutedStep<I, I> {

    private Predicate<I> filter;

    public void setFilter(Predicate<I> predicate){
        this.filter = predicate;
    }

    @Override
    public void newMessagesArrived(int numberOfMessages) {
        for (int i = 0; i < numberOfMessages; i++) {
            if (super.getQueue().hasAvailableMessages()) {
                I msg = pollMessage();
                if (filter.test(msg))
                    super.getTransporter().publishMessage(msg);
            } else break;
        }
    }

    @Override
    public Step<I, I> copy() {
        FiltrationStep<I> step = new FiltrationStep<>();
        step.setFilter(filter);
        step.onNewMessage(super.getMessageHandler());
        step.setQueue(super.getQueue());
        step.setTransporter(super.getTransporter());
        return step;
    }
}
