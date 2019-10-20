/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kiamesdavies.limiter.services;

import com.kiamesdavies.limiter.commons.Utility;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Test;

/**
 *
 * @author atlantis
 */
public class UtilityTest {
 
    
    @Test
    public void testBaseExtractor(){
        assertThat(Utility.extractBaseUrl("/city/sss").get(), is("city"));
    }
}
