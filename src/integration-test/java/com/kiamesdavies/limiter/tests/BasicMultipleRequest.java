/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kiamesdavies.limiter.tests;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.runner.parallel.ZeroCodeLoadRunner;
import org.junit.runner.RunWith;

/**
 *
 * @author atlantis
 */
@LoadWith("load_generation.properties")
@TestMapping(testClass = BasinSingleRequest.class, testMethod = "testGetPermit")
@RunWith(ZeroCodeLoadRunner.class)
public class BasicMultipleRequest {
    
    
}
