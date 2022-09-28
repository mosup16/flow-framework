package com.mo16.flow;


/**
 * a channel that contains a single message at a time.
 * The channel implementation blocks till the subscriber processing of the message is over.
 * @param <T> the type of messages allowed
 */
public class SingularMessageChannel<T> implements Channel<T> {

    private T message;
    private ChannelSubscriber<T> subscriber;
    private boolean closed;

    public SingularMessageChannel() {
    }


    @Override
    public void push(T msg) {
        message = msg;
        notifySubscriber();
    }

    @Override
    public T poll() {
        //TODO should handle thrown exception if the data structure is empty properly
        return message;
    }

    @Override
    public void notifySubscriber() {
        subscriber.startPolling();
    }

    @Override
    public ChannelSubscriber<T> getSubscriber() {
        return subscriber;
    }

    @Override
    public void setSubscriber(ChannelSubscriber<T> s) {
        this.subscriber = s;
    }

    @Override
    public boolean hasAvailableMessages() {
        return message != null;
    }

    @Override
    public int countOfAvailableMessages() {
        return message == null ? 0 : 1;
    }

    @Override
    public void close() {
        subscriber.channelClosed();
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

}
