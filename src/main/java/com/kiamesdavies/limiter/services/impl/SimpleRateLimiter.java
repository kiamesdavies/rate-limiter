package com.kiamesdavies.limiter.services.impl;

import com.kiamesdavies.limiter.services.RateLimiter;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleRateLimiter implements RateLimiter {

    private final Logger logger = LoggerFactory.getLogger(SimpleRateLimiter.class);
    private final ReentrantLock rwl = new ReentrantLock();


    private final String name;
    private final int timeQuota;
    private final int permits;

    private volatile long lastInstanceQuotaStarted;
    private volatile int permitsLeft;

    private volatile boolean locked;
    private final int lockedTime;

    /**
     * @param name           used for differentiating differ
     * @param timeQuota      number of seconds before resetting quota
     * @param permits number of records per the seconds given
     */
    public SimpleRateLimiter(String name, int timeQuota, int permits) {
        this.name = name;
        this.timeQuota = timeQuota * 1000;
        this.permits = permits;
        this.permitsLeft = permits;
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
        try {
            rwl.tryLock(1, TimeUnit.SECONDS);
            if (lastInstanceQuotaStarted == 0) {
                lastInstanceQuotaStarted = System.currentTimeMillis();
            }
            if (permitsLeft > 0) {
                permitsLeft--;
                return 1;
            }


            long currentInstance = System.currentTimeMillis();
            //if there is request before quota ends, increase delay by lockedTime
            if(lastInstanceQuotaStarted + timeQuota > currentInstance && !locked){
                lastInstanceQuotaStarted += lockedTime ;
                logger.debug("locking {} till lastInstanceQuotaStarted {} from currentInstance {} using a delay of {} miils", name, lastInstanceQuotaStarted, currentInstance, lockedTime);
                locked = true;
            }
            if (lastInstanceQuotaStarted + timeQuota <= currentInstance) {
                lastInstanceQuotaStarted = currentInstance;
                permitsLeft = permits - 1;
                locked = false;
                return 1;
            }
            logger.debug(" still have {} milliseconds to go before resetting for {}  ", lastInstanceQuotaStarted-currentInstance+timeQuota, name);

            return 0;
        }
        catch (InterruptedException e) {
            logger.error("Failed in acquiring lock for {}",name,e);
            return 0;
        }
        finally {
            rwl.unlock();
        }
    }

    /**
     * @return number of permits left, this should not be used for synchronization
     */
    @Override
    public int getPermitsLeft() {
        return permitsLeft;
    }


}
