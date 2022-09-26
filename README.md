# flow-framework
a framework that implements reactive programming model

#### generate some data for testing the library

       var data = IntStream.range(0, 1000)
                .boxed()
                .collect(Collectors.toList());

#### define a function that simulates a long running task

        Function<Integer, Integer> func = integer -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return integer + 1;
        };

#### The liberary exposes a very familiar api and it can be used as follows.

            Flow.of(data)
                .parallelMap(16, func)
                .filter(integer -> integer > 40)
                .map(integer -> "hi : " + integer)
                .forEach(x -> System.out.println(Thread.currentThread().getId() + " :" + x));

Thanks for reading.
