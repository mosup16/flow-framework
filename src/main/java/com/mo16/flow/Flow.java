package com.mo16.flow;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Flow<T> {
    private DataSource source;
    private Queue dataSourceQueue;
    private LinkedList<Step> pipeline;
    private Queue<T> pipelineLastQueue;

    private Flow(){

    }

    private Flow(DataSource source, Queue dataSourceQueue, LinkedList<Step> pipeline) {
        this.source = source;
        this.dataSourceQueue = dataSourceQueue;
        this.pipeline = pipeline;
    }

    static <O> Flow<O> of(Iterable<O> iterable){
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
        flow.pipelineLastQueue = dataSourceQueue;
        return flow;
    }

    public <O> Flow<O> map(Function<T,O> function){
        Step<T,O> step = new SequentiallyExecutedStep<>();
        step.onNewMessage(function);

        Flow<O> flow = new Flow<>();
        chainSequentialStep(step, flow);
        return flow;
    }

    public Flow<T> filter(Predicate<T> predicate){
        FiltrationStep<T> step = new FiltrationStep<>();
        step.setFilter(predicate);
        step.onNewMessage(t -> t);

        Flow<T> flow = new Flow<>();
        chainSequentialStep(step, flow);
        return flow;
    }

        private <O> void chainSequentialStep(Step<T, O> step, Flow<O> flow) {
        step.setQueue(this.pipelineLastQueue);
        this.pipelineLastQueue.setSubscriber(step);

        Transporter<O> transporter = new SequentialTransporter<>();
        LinkedQueue<O> queue = new LinkedQueue<>();
        transporter.setQueue(queue);
        step.setTransporter(transporter);

        LinkedList<Step> newPipeline = new LinkedList<>(this.pipeline);
        newPipeline.add(step);

        flow.source = this.source;
        flow.dataSourceQueue = this.dataSourceQueue;
        flow.pipeline = newPipeline;
        flow.pipelineLastQueue = queue;
    }



    public void forEach(Consumer<T> consumer){
        SequentialDataSink<T> sink = new SequentialDataSink<>();
        Queue<T> queue = this.pipelineLastQueue;
        queue.setSubscriber(sink);
        sink.setQueue(queue);
        sink.onNewMessage(consumer);
        source.generate();
    }

}
