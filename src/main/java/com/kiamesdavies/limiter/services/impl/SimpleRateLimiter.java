package com.kiamesdavies.limiter.services.impl;

import com.kiamesdavies.limiter.services.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleRateLimiter implements RateLimiter {

    private final Logger logger = LoggerFactory.getLogger(SimpleRateLimiter.class);
    private final ReentrantLock rwl = new ReentrantLock();


    private final String name;
    private final int timeQuota;
    private final int permits;

    private volatile int lastInstanceQuotaStarted;
    private volatile int permitsLeft;

    /**
     * @param name           used for differentiating differ
     * @param timeQuota      number of seconds before resetting quota
     * @param permits number of records per the seconds given
     */
    public SimpleRateLimiter(String name, int timeQuota, int permits) {
        this.name = name;
        this.timeQuota = timeQuota;
        this.permits = permits;
        permitsLeft = permits;
        logger.debug("Created limiter {} for {} in {} seconds", name, permits, timeQuota);
    }




    /**
     * get a token from this limiter
     *
     * @return 1 if token is available, 0 otherwise
     */
    @Override
    public int getPermit() {
        try {
            rwl.lock();
            if (lastInstanceQuotaStarted == 0) {
                lastInstanceQuotaStarted = Calendar.getInstance().get(Calendar.SECOND);
            }
            if (permitsLeft > 0) {
                permitsLeft--;
                return 1;
            }
            int currentInstance = Calendar.getInstance().get(Calendar.SECOND);
            if (lastInstanceQuotaStarted + timeQuota < currentInstance) {
                lastInstanceQuotaStarted = currentInstance;
                permitsLeft = permits - 1;
                return 1;
            }
            return 0;
        } finally {
            rwl.unlock();
        }
    }

    /**
     * @return number of permits left
     */
    @Override
    public int getPermitsLeft() {
        return permitsLeft;
    }


}
