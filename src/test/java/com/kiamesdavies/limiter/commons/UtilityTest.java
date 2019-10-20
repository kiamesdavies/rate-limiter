/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kiamesdavies.limiter.commons;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author atlantis
 */
public class UtilityTest {
 
    
    @Test
    public void testBaseExtractor(){

        assertThat(Utility.extractBaseUrl("/city/sss").get(), is("city"));
        assertThat(Utility.extractBaseUrl("/city/sss/").get(), is("city"));
    }
}
