package com.kiamesdavies.limiter.services;


import com.kiamesdavies.limiter.services.impl.SimpleRateLimiter;
import io.vavr.control.Try;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class RateLimiterTest {

    private final static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);


    @Test
    public void shouldReleaseAllTokensWihSameRequests() {

        RateLimiter rate = new SimpleRateLimiter("test", 1, 5);
        CompletionService<Integer> service = new ExecutorCompletionService<>(EXECUTOR_SERVICE);
        IntStream.range(0, 5).forEach(a -> service.submit(() -> rate.getPermit()));
        
        List<Integer> result = IntStream.range(0, 5).mapToObj(i -> Try.of(() -> service.take().get(1, TimeUnit.SECONDS) ).get()).collect(Collectors.toList());
        assertThat(result, contains(1,1,1,1,1));
        assertThat(rate.getPermitsLeft(), equalTo(0));

    }

    @Test
    public void shouldFailToReleaseTokenGivenRequestsAreMoreThanToken() {

        RateLimiter rate = new SimpleRateLimiter("test2", 1, 5);
        CompletionService<Integer> service = new ExecutorCompletionService<>(EXECUTOR_SERVICE);

        IntStream.range(0, 6).forEach(a -> service.submit(() -> rate.getPermit()));

        List<Integer> result = IntStream.range(0, 6).mapToObj(i -> Try.of(() -> service.take().get(1, TimeUnit.SECONDS) ).get()).collect(Collectors.toList());
        assertThat(result, containsInAnyOrder(1,1,1,1,1,0));
        assertThat(rate.getPermitsLeft(), equalTo(0));
    }

    @Test
    public void shouldReleaseAllTokenGivenRequestsAreWithinTimeQuota() throws InterruptedException {
        RateLimiter rate = new SimpleRateLimiter("test3", 1, 5);
        CompletionService<Integer> service = new ExecutorCompletionService<>(EXECUTOR_SERVICE);
        IntStream.range(0, 5).forEach(a -> service.submit(() -> rate.getPermit()));

        List<Integer> result = IntStream.range(0, 5).mapToObj(i -> Try.of(() -> service.take().get(1, TimeUnit.SECONDS) ).get()).collect(Collectors.toList());
        assertThat(result, contains(1,1,1,1,1));
        assertThat(rate.getPermitsLeft(), equalTo(0));

        Thread.sleep(1000);
        IntStream.range(0, 5).forEach(a -> service.submit(() -> rate.getPermit()));

        result = IntStream.range(0, 5).mapToObj(i -> Try.of(() -> service.take().get(1, TimeUnit.SECONDS) ).get()).collect(Collectors.toList());
        assertThat(result, contains(1,1,1,1,1));


    }

    @Test
    public void shouldReleaseAllTokenGivenRequestsAreWithinTimeQuotaWithExcessiveRequest() throws InterruptedException {

        RateLimiter rate = new SimpleRateLimiter("test3", 1, 5);
        CompletionService<Integer> service = new ExecutorCompletionService<>(EXECUTOR_SERVICE);
        IntStream.range(0, 6).forEach(a -> service.submit(() -> rate.getPermit()));

        List<Integer> result = IntStream.range(0, 6).mapToObj(i -> Try.of(() -> service.take().get(1, TimeUnit.SECONDS) ).get()).collect(Collectors.toList());
        assertThat(result, containsInAnyOrder(1,1,1,1,1,0));
        assertThat(rate.getPermitsLeft(), equalTo(0));

        //limiter is locked, subsequent request will still fail
        Thread.sleep(1000);
        IntStream.range(0, 6).forEach(a -> service.submit(() -> rate.getPermit()));

        result = IntStream.range(0, 6).mapToObj(i -> Try.of(() -> service.take().get(1, TimeUnit.SECONDS) ).get()).collect(Collectors.toList());
        assertThat(result, contains(0,0,0,0,0,0));
        assertThat(rate.getPermitsLeft(), equalTo(0));

        //test again
        Thread.sleep(5000);
        IntStream.range(0, 6).forEach(a -> service.submit(() -> rate.getPermit()));

        result = IntStream.range(0, 6).mapToObj(i -> Try.of(() -> service.take().get(1, TimeUnit.SECONDS) ).get()).collect(Collectors.toList());
        assertThat(result, containsInAnyOrder(1,1,1,1,1,0));
        assertThat(rate.getPermitsLeft(), equalTo(0));
    }

    @AfterClass
    public  static  void close(){
        EXECUTOR_SERVICE.shutdownNow();
    }

}
