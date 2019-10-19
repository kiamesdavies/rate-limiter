package com.kiamesdavies.limiter.models;

import com.univocity.parsers.annotations.LowerCase;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

import java.math.BigDecimal;
import java.util.Objects;

public class Hotel {

    @Parsed(field = "HOTELID")
    private int id;
    @Trim
    @LowerCase
    @Parsed(field = "CITY")
    private String city;
    @Trim
    @LowerCase
    @Parsed(field = "ROOM")
    private String room;
    @Parsed(field = "PRICE")
    private BigDecimal price;

    public Hotel() {
    }

    public Hotel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel)) return false;
        Hotel hotel = (Hotel) o;
        return id == hotel.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                '}';
    }
}
