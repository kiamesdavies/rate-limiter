package com.kiamesdavies.limiter.services;


import com.kiamesdavies.limiter.services.impl.SimpleRateLimiter;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class RateLimiterTest {

    @Test
    public void shouldReleaseAllTokensWihSameRequests() throws InterruptedException {
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(5);
        RateLimiter rate = new SimpleRateLimiter("test", 1, 5);
        CompletionService<Integer> service = new ExecutorCompletionService<>(newFixedThreadPool);
        IntStream.range(0, 5).forEach(a -> service.submit(() -> rate.getPermit()));
        
        List<Integer> result = IntStream.range(0, 5).mapToObj(i -> Try.of(() -> service.poll().get(1, TimeUnit.SECONDS) ).get()).collect(Collectors.toList());
        assertThat(result, contains(1,1,1,1,1));
        assertThat(rate.getPermitsLeft(), equalTo(0));

    }

    @Test
    public void shouldFailToReleaseTokenGivenRequestsAreMoreThanToken() {
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(5);
        RateLimiter rate = new SimpleRateLimiter("test", 1, 5);
        CompletionService<Integer> service = new ExecutorCompletionService<>(newFixedThreadPool);

        IntStream.range(0, 6).forEach(a -> service.submit(() -> rate.getPermit()));

        List<Integer> result = IntStream.range(0, 6).mapToObj(i -> Try.of(() -> service.poll().get(1, TimeUnit.SECONDS) ).get()).collect(Collectors.toList());
        assertThat(result, containsInAnyOrder(1,1,1,1,1,0));
        assertThat(rate.getPermitsLeft(), equalTo(0));
    }

    @Test
    public void shouldReleaseAllTokenGivenRequestsAreWithinTimeQuota() {

    }

}
