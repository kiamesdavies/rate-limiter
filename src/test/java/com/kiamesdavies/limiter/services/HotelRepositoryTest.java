package com.kiamesdavies.limiter.services;

import com.kiamesdavies.limiter.services.impl.HotelRepository;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class HotelRepositoryTest {

    @Test
    public void shouldReturnListOfHotelsGivenACityId(){
        HotelRepository hotelRepository = HotelRepository.getInstance();
        assertThat(hotelRepository.getHotelByCityId("Bangkok"), contains(new Hotel(1),new Hotel(6),new Hotel(8)));
    }
}
