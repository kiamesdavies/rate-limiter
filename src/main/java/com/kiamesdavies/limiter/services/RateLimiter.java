/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kiamesdavies.limiter.services;

/**
 *
 * @author atlantis
 */
public interface RateLimiter {
    
    /**
     * get a token from this limiter
     * 
     * @return 1 if token is available, 0 otherwise
     */
    int getPermit();

    /**
     *
     * @return number of permits left
     */
    int getPermitsLeft();
}
