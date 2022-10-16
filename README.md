# Flow Framework [![Maven Central](https://img.shields.io/maven-central/v/io.github.mosup16/flow-framework.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.mosup16%22%20AND%20a:%22flow-framework%22)  [![](https://jitpack.io/v/mosup16/flow-framework.svg)](https://jitpack.io/#mosup16/flow-framework)
A framework that Implements and facilitates reactive programming with a support for parallel processing and back
pressure

#### to get the dependency for maven add JitPack repository
```    
         <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
         </repositories>
```
#### and then add the following dependency to your pom.xml 
```
	<dependency>
	    <groupId>com.github.mosup16</groupId>
	    <artifactId>flow-framework</artifactId>
	    <version>439d7dd0b4</version>
	</dependency>
```
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

#### To define a round robin load balancing strategy 
```
	.parallelMap(numOfThreads, LoadBalancingStrategy.ROUND_ROBIN , func)
```
also note that round robin is the default strategy.

#### To define a least buffer size load balancing strategy
```
	.parallelMap(numOfThreads, LoadBalancingStrategy.LEAST_BUFFER_SIZE , func)
```
this strategy tries to pick the least busy thread with the least buffer size every time 
#### To configure back pressure
```
   var configs = new BackPressureConfigs(maxBufferSizePerThread, maxBufferedMessagesForAllThreads);
   Flow.of(data, configs)
```
If the bufferd messages of any thread reached ``maxBufferSizePerThread`` ,
then this thread will be overloaded and the load will be shifted away to other threads.

If all threads were overloaded or the count of all buffered messages for all threads reached ``maxBufferedMessagesForAllThreads``,
then the flow will be throttled and the ``DataSource`` implementation will define the throttling behaviour.

**Note that if any of ``maxBufferSizePerThread`` or ``maxBufferedMessagesForAllThreads`` variables was given a value less than 1,
this means that they will be ignored and considered as if they were infinity.**
