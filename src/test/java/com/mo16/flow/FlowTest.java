package com.mo16.flow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FlowTest {

    @Test
    void test_basic_list_iteration() {
        //given
        List<Integer> testInput = List.of(1, 2, 3, 4, 5, 6);
        List<Integer> res = new ArrayList<>();

        // when
        Flow.of(testInput).forEach(res::add);

        //then
        assert testInput.size() == res.size();

        for (int i = 0; i < res.size(); i++)
            assert (res.get(i).equals(testInput.get(i)));
    }

    @Test
    void test_basic_list_transformation() {
        //given
        List<Integer> expected = List.of(2, 3, 4, 5, 6, 7);

        List<Integer> res = new ArrayList<>();

        // when
        Flow.of(List.of(1, 2, 3, 4, 5, 6))
                .map(integer -> ++integer)
                .forEach(res::add);

        //then
        for (int i = 0; i < res.size(); i++)
            assert (res.get(i).equals(expected.get(i)));
    }

    @Test
    void test_basic_list_filtration() {
        //given
        List<Integer> expected = List.of(4, 5, 6);
        List<Integer> res = new ArrayList<>();

        // when
        Flow.of(List.of(1, 2, 3, 4, 5, 6))
                .filter(integer -> integer > 3)
                .forEach(res::add);

        //then
        for (int i = 0; i < res.size(); i++)
            assert (res.get(i).equals(expected.get(i)));
    }

    @Test
    void test_parallelMap_number_of_executed_threads() {
        //given
        List<Integer> expected = List.of(4, 5, 6);
        Set<Long> executedThreads = new HashSet<>();
        // when
        int d = 0;
        Flow.of(List.of(1, 2, 3, 4, 5, 6))
                .parallelMap(2, i -> {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return ++i;
                })
                .forEach(integer -> executedThreads.add(Thread.currentThread().getId()));

        System.out.println("executedThreads.size() = " + executedThreads.size());
        assert executedThreads.size() == 2;
    }

    @Test
    void test_parallelMap_transformation_result() {
        //given
        List<Integer> expected = IntStream.range(0, 1000)
                .map(i -> ++i)
                .boxed()
                .collect(Collectors.toList());

        List<Integer> input = IntStream.range(0, 1000).boxed().collect(Collectors.toList());

        LinkedBlockingDeque<Integer> res = new LinkedBlockingDeque<>();

        // when
        Flow.of(input)
                .parallelMap(2, i -> {
//                    assertNotNull(i,String.join("input value ",i.toString()," is null"));
                    System.out.println("input = " + i);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return ++i;
                })
                .forEach(e -> {
                    try {
                        res.put(e);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });
        var orderedRes = res.stream().sorted().collect(Collectors.toList());

        //then
        for (int i = 0; i < res.size(); i++)
            assertEquals(expected.get(i), orderedRes.get(i));
    }

    @Test
    void test(){
        class Counter {
            int counter;
        }

        Counter counter = new Counter();

        IntStream.range(0, 100000000)
                .map(i -> ++i)
                .boxed()
                .parallel()
                .forEach(integer -> counter.counter  = counter.counter + 1)
        ;

        System.out.println("counter.counter = " + counter.counter);
    }

}