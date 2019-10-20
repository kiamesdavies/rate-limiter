/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kiamesdavies.limiter.tests;

/**
 *
 * @author atlantis
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BasicMultipleRequest.class,
        FailedSingleRequest.class,
})
public class CombinedTestIT {
    
}
