# Flow Framework [![Maven Central](https://img.shields.io/maven-central/v/io.github.mosup16/flow-framework.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.mosup16%22%20AND%20a:%22flow-framework%22)
A framework that aims to implement a reactive programming model

#### Generate some data for testing the library

       var data = IntStream.range(0, 1000)
                .boxed()
                .collect(Collectors.toList());

#### Define a function that simulates a long running task

        Function<Integer, Integer> func = integer -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return integer + 1;
        };

#### The liberary exposes a very familiar api and it can be used as follows

            Flow.of(data)
                .parallelMap(16, func)
                .filter(integer -> integer > 40)
                .map(integer -> "hi : " + integer)
                .forEach(x -> System.out.println(Thread.currentThread().getId() + " :" + x));

Thanks for reading.
