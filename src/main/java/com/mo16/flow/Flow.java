package com.mo16.flow;

import com.mo16.flow.loadbalancing.LoadBalancer;
import com.mo16.flow.loadbalancing.LoadBalancerFactory;
import com.mo16.flow.loadbalancing.LoadBalancingStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Flow<T> {
    private DataSource source;
    private Channel dataSourceChannel;
    private List<Channel<T>> pipelineLastChannels;
    private ExecutorService executorService;
    private boolean isParallel = false;

    private Flow() {

    }


    static <O> Flow<O> of(Iterable<O> iterable) {

        Flow<O> flow = new Flow<>();

        DataSource<O> source = DataSource.newIterableDataSource(iterable);

        Transporter<O> transporter = new SingleChannelTransporter<>();
        source.setTransporter(transporter);

        Channel<O> dataSourceChannel = new SingularMessageChannel<>();
        transporter.addChannel(dataSourceChannel);


        flow.source = source;
        flow.dataSourceChannel = dataSourceChannel;

        List<Channel<O>> channels = new LinkedList<>();
        channels.add(dataSourceChannel);
        flow.pipelineLastChannels = channels;
        return flow;
    }

    public <O> Flow<O> map(Function<T, O> function) {
        ProcessingStep<T, O> step = new SynchronousStep<>();
        step.setMessageProcessor(function);
        return chainStep(step, this.pipelineLastChannels, this);
    }

    public boolean isParallel() {
        return this.isParallel;
    }

    public <O> Flow<O> parallelMap(int numOfThreads, Function<T, O> function){
        return parallelMap(numOfThreads, LoadBalancingStrategy.ROUND_ROBIN, function);
    }

    public <O> Flow<O> parallelMap(int numOfThreads, LoadBalancingStrategy loadBalancingStrategy,
                                   Function<T, O> function) {
        if (isParallel)
            return map(function);

        var loadBalancerFactory = new LoadBalancerFactory<T>();
        List<Channel<T>> newPipelineLastChannels = new LinkedList<>();

        for (Channel<T> channel : this.pipelineLastChannels) {
            LoadBalancer<T> loadBalancer = loadBalancerFactory.getInstanceFor(loadBalancingStrategy);
            var transporter = new MultiChannelTransporter<T>(loadBalancer);
            for (int i = 0; i < numOfThreads; i++)
                transporter.addChannel(new BufferedBlockingChannel<>());
            newPipelineLastChannels.addAll(transporter.getChannels());

            ProcessingStep<T, T> s = new SynchronousStep<>();
            s.setMessageProcessor(t -> t);
            s.subscribeTo(channel);
            channel.setSubscriber(s);
            s.setTransporter(transporter);
        }

        executorService = Executors.newFixedThreadPool(numOfThreads);
        ProcessingStep<T, T> parallelStep = new AsynchronousStep<T>(executorService);
        parallelStep.setMessageProcessor(t -> t);
        Flow<O> flow = chainStep(parallelStep, newPipelineLastChannels, this)
                .map(function);
        flow.isParallel = true;
        return flow;

    }

    public Flow<T> filter(Predicate<T> predicate) {
        FiltrationStep<T> step = new FiltrationStep<>();
        step.setFilter(predicate);

        return chainStep(step, this.pipelineLastChannels, this);
    }

    private <O> Flow<O> chainStep(Step<T, O> stepTobeChained,
                                  List<Channel<T>> pipelineLastChannels, Flow<T> sourceFlow) {
        Flow<O> flow = new Flow<>();
        LinkedList<Channel<O>> newPipelineLastChannels = new LinkedList<>();
        for (Channel channelTobeSubscribedTo : pipelineLastChannels) {
            Step<T, O> step = stepTobeChained.copy();
            step.subscribeTo(channelTobeSubscribedTo);
            channelTobeSubscribedTo.setSubscriber(step);

            Transporter<O> transporter = new SingleChannelTransporter<>();
            SingularMessageChannel<O> queue = new SingularMessageChannel<>();
            transporter.addChannel(queue);
            step.setTransporter(transporter);
            newPipelineLastChannels.add(queue);
        }

        flow.source = sourceFlow.source;
        flow.dataSourceChannel = sourceFlow.dataSourceChannel;
        flow.pipelineLastChannels = newPipelineLastChannels;
        flow.isParallel = sourceFlow.isParallel;
        flow.executorService = sourceFlow.executorService;
        return flow;
    }


    public void forEach(Consumer<T> consumer) {
        for (Channel channel : this.pipelineLastChannels) {
            SynchronousDataSink<T> sink = new SynchronousDataSink<>();
            channel.setSubscriber(sink);
            sink.subscribeTo(channel);
            sink.onNewMessage(consumer);
        }
        source.generate();
        if (isParallel) {
            executorService.shutdown();
            try {
                boolean isTerminated = false;
                while (!isTerminated)
                    isTerminated = executorService.awaitTermination(100L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
