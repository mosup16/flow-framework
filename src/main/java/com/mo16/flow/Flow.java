package com.mo16.flow;

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
    private Queue dataSourceQueue;
    private List<Queue<T>> pipelineLastQueues;
    private ExecutorService executorService;
    private boolean isParallel = false;

    private Flow() {

    }


    static <O> Flow<O> of(Iterable<O> iterable) {

        Flow<O> flow = new Flow<>();

        DataSource<O> source = DataSource.newIterableDataSource(iterable);

        Transporter<O> transporter = new SequentialTransporter<>();
        source.setTransporter(transporter);

        Queue<O> dataSourceQueue = new LinkedQueue<>();
        transporter.addQueue(dataSourceQueue);


        flow.source = source;
        flow.dataSourceQueue = dataSourceQueue;

        List<Queue<O>> queues = new LinkedList<>();
        queues.add(dataSourceQueue);
        flow.pipelineLastQueues = queues;
        return flow;
    }

    public <O> Flow<O> map(Function<T, O> function) {
        Step<T, O> step = new SequentialStep<>();
        step.onNewMessage(function);
        return chainSequentialStep(step, this.pipelineLastQueues, this);
    }

    public boolean isParallel() {
        return this.isParallel;
    }

    public <O> Flow<O> parallelMap(int numOfThreads, Function<T, O> function) {
        if (isParallel)
            return map(function);
        List<Queue<T>> newPipelineLastQueues = new LinkedList<>();
        for (Queue<T> queue : this.pipelineLastQueues) {
            // create round-robin transporter
            var transporter = new RoundRobinParallelTransporter<T>();
            for (int i = 0; i < numOfThreads; i++)
                transporter.addQueue(new ParallelQueue<>());
            newPipelineLastQueues.addAll(transporter.getQueues());

            Step<T, T> s = new SequentialStep<>();
            s.onNewMessage(t -> t);
            s.setQueue(queue);
            queue.setSubscriber(s);
            s.setTransporter(transporter);
        }

        executorService = Executors.newFixedThreadPool(numOfThreads);
        Step<T, T> parallelStep = new ParallelizedStep<T>(executorService, this.source);
        parallelStep.onNewMessage(t -> t);
        Flow<O> flow = chainSequentialStep(parallelStep, newPipelineLastQueues, this)
                .map(function);
        flow.isParallel = true;
        return flow;

    }

    public Flow<T> filter(Predicate<T> predicate) {
        FiltrationStep<T> step = new FiltrationStep<>();
        step.setFilter(predicate);
        step.onNewMessage(t -> t);

        return chainSequentialStep(step, this.pipelineLastQueues, this);
    }

    private <O> Flow<O> chainSequentialStep(Step<T, O> stepTobeChained,
                                            List<Queue<T>> pipelineLastQueues, Flow<T> sourceFlow) {
        Flow<O> flow = new Flow<>();
        LinkedList<Queue<O>> newPipelineLastQueues = new LinkedList<>();
        for (Queue queueTobeSubscribedTo : pipelineLastQueues) {
            Step<T, O> step = stepTobeChained.copy();
            step.onNewMessage(stepTobeChained.getMessageHandler());
            step.setQueue(queueTobeSubscribedTo);
            queueTobeSubscribedTo.setSubscriber(step);

            Transporter<O> transporter = new SequentialTransporter<>();
            LinkedQueue<O> queue = new LinkedQueue<>();
            transporter.addQueue(queue);
            step.setTransporter(transporter);
            newPipelineLastQueues.add(queue);
        }

        flow.source = sourceFlow.source;
        flow.dataSourceQueue = sourceFlow.dataSourceQueue;
        flow.pipelineLastQueues = newPipelineLastQueues;
        flow.isParallel = sourceFlow.isParallel;
        flow.executorService = sourceFlow.executorService;
        return flow;
    }


    public void forEach(Consumer<T> consumer) {
        for (Queue queue : this.pipelineLastQueues) {
            SequentialDataSink<T> sink = new SequentialDataSink<>();
            queue.setSubscriber(sink);
            sink.setQueue(queue);
            sink.onNewMessage(consumer);
        }
        source.generate();
        if (isParallel) {
            executorService.shutdown();
            try {
                executorService.awaitTermination(1L, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("executorService.isTerminated() = " + executorService.isTerminated());
        }


    }

}
