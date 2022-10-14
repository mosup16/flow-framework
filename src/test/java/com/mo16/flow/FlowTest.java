package com.mo16.flow;

import com.mo16.flow.loadbalancing.LoadBalancingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class FlowTest {

    @Test
    @DisplayName("test iteration with forEach()")
    void testListIteration(){
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
    @DisplayName("test list transformation with map()")
    void testTransformation(){
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
    @DisplayName("test list filtration with filter()")
    void testListFiltration(){
        //given
        List<Integer> expected = List.of(4, 5,6);
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
    @DisplayName("test parallel processing for data with parallelMap()")
    void testParallelMao(){
        var expectedSum = IntStream.range(0, 100000).map(i -> i + 1).filter(value -> value > 100).sum();
        var threadsIds = new HashSet<Long>();
        AtomicInteger sum = new AtomicInteger();
        Flow.of(IntStream.range(0, 100000).boxed().collect(Collectors.toSet()))
                .parallelMap(5, integer -> integer)
                .map(integer -> integer + 1)
                .filter(integer -> integer > 100)
                .forEach(integer -> {
                    threadsIds.add(Thread.currentThread().getId());
                    sum.addAndGet(integer);
                });
        assertEquals(expectedSum, sum.get());
        assertFalse(threadsIds.contains(Thread.currentThread().getId()));
        assertEquals(5, threadsIds.size());
    }

    @Test
    @DisplayName("test parallel processing for data with parallelMap() and round robin load balancing")
    void testParallelMapWithRoundRobinLoadBalancing(){
        var lock = new ReentrantLock();
        var expectedSum = IntStream.range(0, 100000).map(i -> i + 1).filter(value -> value > 100).sum();
        var messagesPerThread = new Hashtable<Long, Integer>();
        AtomicInteger sum = new AtomicInteger();
        Flow.of(IntStream.range(0, 100000).boxed().collect(Collectors.toSet()))
                .parallelMap(5, LoadBalancingStrategy.ROUND_ROBIN , integer -> integer)
                .map(integer -> integer + 1)
                .filter(integer -> integer > 100)
                .forEach(integer -> {
                    long id = Thread.currentThread().getId();
                    lock.lock(); // use a lock here as this function isn't stateless and got some side effects
                    var counter = messagesPerThread.getOrDefault(id, 0);
                    messagesPerThread.put(id, ++counter);
                    lock.unlock();
                    sum.addAndGet(integer);
                });
        assertEquals(expectedSum, sum.get());
        assertFalse(messagesPerThread.contains(Thread.currentThread().getId()));
        assertEquals(5, messagesPerThread.size());
        // asserts true if and only if each thread processed messages count are the same
        assertEquals(1, messagesPerThread.values().stream().distinct().count());
        // notice (100000 - 100) / 5 = 19980 , note also that we filter the first 100 element out
        assertTrue(messagesPerThread.values().stream().allMatch(integer -> integer.equals(19980)));
    }

    @Test
    @DisplayName("test parallel processing for data with parallelMap() and least buffer size load balancing")
    void testParallelMapWithLeastBufferSizeLoadBalancing(){
        var lock = new ReentrantLock();
        var expectedSum = IntStream.range(0, 100000).map(i -> i + 1).filter(value -> value > 100).sum();
        var messagesPerThread = new Hashtable<Long, Integer>();
        AtomicInteger sum = new AtomicInteger();
        Flow.of(IntStream.range(0, 100000).boxed().collect(Collectors.toSet()))
                .parallelMap(5, LoadBalancingStrategy.LEAST_BUFFER_SIZE , integer -> integer)
                .map(integer -> integer + 1)
                .filter(integer -> integer > 100)
                .forEach(integer -> {
                    long id = Thread.currentThread().getId();
                    lock.lock(); // use a lock here as this function isn't stateless and got some side effects
                    var counter = messagesPerThread.getOrDefault(id, 0);
                    messagesPerThread.put(id, ++counter);
                    lock.unlock();
                    sum.addAndGet(integer);
                });
        assertEquals(expectedSum, sum.get());
        assertFalse(messagesPerThread.contains(Thread.currentThread().getId()));
        assertEquals(5, messagesPerThread.size());
        // asserts true if each thread processed messages count are distinctive
        assertNotEquals(1, messagesPerThread.values().stream().distinct().count());
    }


    @Test
    @DisplayName("test back pressure with limited channel and flow size")
    void testBackPressureWithLimitedChannelAndFlowSize(){
        var expectedSum = IntStream.range(0, 10000).map(i -> i + 1).filter(value -> value > 100).sum();
        var threadsIds = new HashSet<Long>();
        AtomicInteger sum = new AtomicInteger();
        Set<Integer> input = IntStream.range(0, 10000).boxed().collect(Collectors.toSet());
        Flow.of(input, new BackPressureConfigs(600, 5000))
                .parallelMap(10, integer -> integer)
                .map(integer -> integer + 1)
                .filter(integer -> integer > 100)
                .forEach(integer -> {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    threadsIds.add(Thread.currentThread().getId());
                    sum.addAndGet(integer);
                });
        assertEquals(expectedSum, sum.get());
        assertFalse(threadsIds.contains(Thread.currentThread().getId()));
        assertEquals(10, threadsIds.size());
    }

    @Test
    @DisplayName("test back pressure with unlimited channel and limited flow size")
    void testBackPressureWithUnlimitedChannelAndLimitedFlowSize(){
        var expectedSum = IntStream.range(0, 10000).map(i -> i + 1).filter(value -> value > 100).sum();
        var threadsIds = new HashSet<Long>();
        AtomicInteger sum = new AtomicInteger();
        Set<Integer> input = IntStream.range(0, 10000).boxed().collect(Collectors.toSet());
        Flow.of(input, new BackPressureConfigs(-1, 5000))
                .parallelMap(10, integer -> integer)
                .map(integer -> integer + 1)
                .filter(integer -> integer > 100)
                .forEach(integer -> {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    threadsIds.add(Thread.currentThread().getId());
                    sum.addAndGet(integer);
                });
        assertEquals(expectedSum, sum.get());
        assertFalse(threadsIds.contains(Thread.currentThread().getId()));
        assertEquals(10, threadsIds.size());
    }

    @Test
    @DisplayName("test back pressure with limited channel and unlimited flow size")
    void testBackPressureWithLimitedChannelAndUnlimitedFlowSize(){
        var expectedSum = IntStream.range(0, 10000).map(i -> i + 1).filter(value -> value > 100).sum();
        var threadsIds = new HashSet<Long>();
        AtomicInteger sum = new AtomicInteger();
        Set<Integer> input = IntStream.range(0, 10000).boxed().collect(Collectors.toSet());
        Flow.of(input, new BackPressureConfigs(600, -1))
                .parallelMap(10, integer -> integer)
                .map(integer -> integer + 1)
                .filter(integer -> integer > 100)
                .forEach(integer -> {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    threadsIds.add(Thread.currentThread().getId());
                    sum.addAndGet(integer);
                });
        assertEquals(expectedSum, sum.get());
        assertFalse(threadsIds.contains(Thread.currentThread().getId()));
        assertEquals(10, threadsIds.size());
    }

    @Test
    @DisplayName("test back pressure with least buffer size Load Balancing")
    void testBackPressureWithLeastBufferSizeLoadBalancing(){
        var expectedSum = IntStream.range(0, 10000).map(i -> i + 1).filter(value -> value > 100).sum();
        var threadsIds = new HashSet<Long>();
        AtomicInteger sum = new AtomicInteger();
        Set<Integer> input = IntStream.range(0, 10000).boxed().collect(Collectors.toSet());
        Flow.of(input, new BackPressureConfigs(600, 5000))
                .parallelMap(10, LoadBalancingStrategy.LEAST_BUFFER_SIZE, integer -> integer)
                .map(integer -> integer + 1)
                .filter(integer -> integer > 100)
                .forEach(integer -> {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    threadsIds.add(Thread.currentThread().getId());
                    sum.addAndGet(integer);
                });
        assertEquals(expectedSum, sum.get());
        assertFalse(threadsIds.contains(Thread.currentThread().getId()));
        assertEquals(10, threadsIds.size());
    }
}