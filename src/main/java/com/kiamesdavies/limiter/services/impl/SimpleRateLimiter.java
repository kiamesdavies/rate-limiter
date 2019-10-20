package com.kiamesdavies.limiter.services.impl;

import com.kiamesdavies.limiter.services.RateLimiter;
import com.typesafe.config.ConfigFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleRateLimiter implements RateLimiter {

    private final Logger logger = LoggerFactory.getLogger(SimpleRateLimiter.class);
    

    private final String name;
    private final int timeQuota;
    private final int permits;

    private volatile AtomicLong lastInstanceQuotaStarted;
    private volatile AtomicInteger permitsLeft;

    private volatile AtomicBoolean locked = new AtomicBoolean(false);
    private final int lockedTime;

    /**
     * @param name used for differentiating differ
     * @param timeQuota number of seconds before resetting quota
     * @param permits number of records per the seconds given
     */
    public SimpleRateLimiter(String name, int timeQuota, int permits) {
        this.name = name;
        this.timeQuota = timeQuota * 1000;
        this.permits = permits;
        this.permitsLeft = new AtomicInteger(permits);
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
        if (lastInstanceQuotaStarted == null) {
            lastInstanceQuotaStarted = new AtomicLong(System.currentTimeMillis());
        }
        if (permitsLeft.getAndAccumulate(1, (current, j) -> current > 0 ? current - j : 0) > 0) {
            return 1;
        }

        long currentInstance = System.currentTimeMillis();
        //if there is request before quota ends i.e the rate gets higher than the threshold , increase delay by lockedTime(5 seconds)
        if (lastInstanceQuotaStarted.longValue() + timeQuota > currentInstance && locked.compareAndSet(false, true)) {
            long expected = lastInstanceQuotaStarted.longValue();
            lastInstanceQuotaStarted.compareAndExchange(expected, expected+lockedTime);
        }
        if (lastInstanceQuotaStarted.longValue() + timeQuota <= currentInstance && permitsLeft.compareAndSet(0, permits-1)) {
            lastInstanceQuotaStarted.set(currentInstance);
            locked.set(true);
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
