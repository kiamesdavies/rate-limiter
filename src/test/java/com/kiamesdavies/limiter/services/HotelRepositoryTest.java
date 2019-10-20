package com.kiamesdavies.limiter.services;

import com.kiamesdavies.limiter.models.Hotel;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class HotelRepositoryTest {

    @Test
    public void shouldReturnListOfHotelsGivenACity(){
        HotelRepository hotelRepository = HotelRepository.getInstance();
        assertThat(hotelRepository.getHotelByCity("Bangkok"), hasItems(new Hotel(1),new Hotel(6),new Hotel(8)));
        assertThat(hotelRepository.getHotelByCity("Bangkok"), not(hasItem(new Hotel(2))));
    }

    @Test
    public void shouldReturnListOfHotelsGivenARoom(){
        HotelRepository hotelRepository = HotelRepository.getInstance();
        assertThat(hotelRepository.getHotelByRoom("Deluxe"), hasItems(new Hotel(1),new Hotel(4),new Hotel(7)));
        assertThat(hotelRepository.getHotelByRoom("Deluxe"), not(hasItem(new Hotel(8))));
    }

}
