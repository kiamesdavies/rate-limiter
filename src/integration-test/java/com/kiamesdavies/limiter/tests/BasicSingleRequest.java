/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kiamesdavies.limiter.tests;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author atlantis
 */

@TargetEnv("server_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class BasicSingleRequest {

    @Test
    @JsonTestCase("load_tests/get_valid_token.json")
    public void testGetPermit() throws Exception {

    }
}
