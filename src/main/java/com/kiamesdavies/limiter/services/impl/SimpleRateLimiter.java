package com.kiamesdavies.limiter.services.impl;

import com.kiamesdavies.limiter.services.RateLimiter;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleRateLimiter implements RateLimiter {

    private final Logger logger = LoggerFactory.getLogger(SimpleRateLimiter.class);


    private final String name;
    private final int timeQuota;
    private final int permits;

    private final AtomicLong lastInstanceQuotaStarted;
    private final AtomicInteger permitsLeft;

    private final AtomicBoolean locked;
    private final int lockedTime;

    /**
     * @param name      used for differentiating differ
     * @param timeQuota number of seconds before resetting quota
     * @param permits   number of records per the seconds given
     */
    public SimpleRateLimiter(String name, int timeQuota, int permits) {
        this.name = name;
        this.timeQuota = timeQuota * 1000;
        this.permits = permits;
        this.permitsLeft = new AtomicInteger(permits);
        this.lastInstanceQuotaStarted = new AtomicLong(0);
        this.locked = new AtomicBoolean(false);
        this.lockedTime = ConfigFactory.load().getInt("api.back-pressure-seconds") * 1000;
        logger.info("Created limiter {} for {} permits within {} seconds", name, permits, timeQuota);
    }

    /**
     * get a token from this limiter
     *
     * @return 1 if token is available, 0 otherwise
     */
    @Override
    public int getPermit() {
        lastInstanceQuotaStarted.compareAndExchange(0, System.currentTimeMillis());
        if (permitsLeft.getAndAccumulate(1, (current, j) -> current > 0 ? current - j : 0) > 0) {
            return 1;
        }

        long currentInstance = System.currentTimeMillis();
        //if there is request to obtain a permit before quota ends i.e the rate gets higher than the threshold and not previously locked then increase delay by lockedTime(5 seconds)
        if (lastInstanceQuotaStarted.get() + timeQuota > currentInstance && locked.compareAndSet(false, true)) {
            long expected = lastInstanceQuotaStarted.longValue();
            lastInstanceQuotaStarted.compareAndExchange(expected, expected + lockedTime);
        }
        //another quota can be allocated
        if (lastInstanceQuotaStarted.get() + timeQuota <= currentInstance && permitsLeft.compareAndSet(0, permits - 1)) {
            lastInstanceQuotaStarted.set(currentInstance);
            locked.set(false);
            logger.debug("Added another quota for {}", name);
            return 1;
        }
        if (permitsLeft.getAndAccumulate(1, (current, j) -> current > 0 ? current - j : 0) > 0) {
            return 1;
        }

        return 0;
    }

    /**
     * @return number of permits left, this should not be used for
     * synchronization
     */
    @Override
    public int getPermitsLeft() {
        return permitsLeft.get();
    }

}
