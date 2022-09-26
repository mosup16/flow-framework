package com.mo16.flow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
}