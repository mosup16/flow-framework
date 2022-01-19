package com.mo16.flow;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Flow<T> {
    private DataSource source;
    private Queue dataSourceQueue;
    private LinkedList<Step> pipeline;
    private List<Queue<T>> pipelineLastQueues;

    private Flow() {

    }

    private Flow(DataSource source, Queue dataSourceQueue, LinkedList<Step> pipeline) {
        this.source = source;
        this.dataSourceQueue = dataSourceQueue;
        this.pipeline = pipeline;
    }

    static <O> Flow<O> of(Iterable<O> iterable) {
//        Queue<O> dataSourceQueue = new LinkedQueue<>();
//
//        Transporter<O> dataSourceTransporter = new SequentialTransporter<>();
//        dataSourceTransporter.setQueue(dataSourceQueue);
//
////        Step<O,O> step = new SequentiallyExecutedStep<>();
////        step.onNewMessage(o -> o);
////        step.setQueue(dataSourceQueue);
////        dataSourceQueue.setSubscriber(step);
//
//        DataSource<O> source = DataSource.newIterableDataSource(iterable);
//        source.setTransporter(dataSourceTransporter);
//
//        LinkedList<Step> pipeline = new LinkedList<>();
////        pipeline.addLast(step);

        Flow<O> flow = new Flow<>();

        DataSource<O> source = DataSource.newIterableDataSource(iterable);

        Transporter<O> transporter = new SequentialTransporter<>();
        source.setTransporter(transporter);

        Queue<O> dataSourceQueue = new LinkedQueue<>();
        transporter.setQueue(dataSourceQueue);


        flow.source = source;
        flow.pipeline = new LinkedList<>();
        flow.dataSourceQueue = dataSourceQueue;

        List<Queue<O>> queues = new LinkedList<>();
        queues.add(dataSourceQueue);
        flow.pipelineLastQueues = queues;
        return flow;
    }

    public <O> Flow<O> map(Function<T, O> function) {
        Step<T, O> step = new SequentiallyExecutedStep<>();
        step.onNewMessage(function);

        Flow<O> flow = new Flow<>();
        chainSequentialStep(step, flow);
        return flow;
    }

    public Flow<T> filter(Predicate<T> predicate) {
        FiltrationStep<T> step = new FiltrationStep<>();
        step.setFilter(predicate);
        step.onNewMessage(t -> t);

        Flow<T> flow = new Flow<>();
        chainSequentialStep(step, flow);
        return flow;
    }

    private <O> void chainSequentialStep(Step<T, O> stepTobeChained, Flow<O> flow) {
        LinkedList<Step> newPipeline = new LinkedList<>(this.pipeline);
        LinkedList<Queue<O>> newPipelineLastQueues = new LinkedList<>();
        for (Queue queueTobeSubscribedTo : this.pipelineLastQueues) {
            Step<T, O> step = stepTobeChained.copy();
            step.onNewMessage(stepTobeChained.getMessageHandler());
            step.setQueue(queueTobeSubscribedTo);
            queueTobeSubscribedTo.setSubscriber(step);

            Transporter<O> transporter = new SequentialTransporter<>();
            LinkedQueue<O> queue = new LinkedQueue<>();
            transporter.setQueue(queue);
            step.setTransporter(transporter);
            newPipeline.add(step);
            newPipelineLastQueues.add(queue);
        }
//        Queue<T> queueTobeSubscribedTo = this.pipelineLastQueues.get(0);


        flow.source = this.source;
        flow.dataSourceQueue = this.dataSourceQueue;
        flow.pipeline = newPipeline;
        flow.pipelineLastQueues = newPipelineLastQueues;
    }


    public void forEach(Consumer<T> consumer) {
        for (Queue queue : this.pipelineLastQueues) {
            SequentialDataSink<T> sink = new SequentialDataSink<>();
            queue.setSubscriber(sink);
            sink.setQueue(queue);
            sink.onNewMessage(consumer);
        }
//        Queue<T> queue = this.pipelineLastQueues;
        source.generate();
    }

}
