package com.kiamesdavies.limiter.services.impl;

import com.kiamesdavies.limiter.models.Hotel;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class HotelRepository {

    private final Logger logger = LoggerFactory.getLogger(HotelRepository.class);

    private volatile static HotelRepository instance;
    private final Map<String, List<Hotel>> CACHE;
    private final List<Hotel> hotels;

    private HotelRepository() {
        CACHE = new ConcurrentHashMap<>();
        BeanListProcessor<Hotel> rowProcessor = new BeanListProcessor<>(Hotel.class);
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setLineSeparatorDetectionEnabled(true);
        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(HotelRepository.class.getResourceAsStream("/hoteldb.csv"));
        hotels = rowProcessor.getBeans();
        logger.debug("Loaded hotels {}", hotels);
    }

    public static HotelRepository getInstance() {
        if (instance == null) {
            synchronized (HotelRepository.class) {
                if (instance == null) {
                    instance = new HotelRepository();
                }
            }
        }
        return instance;
    }

    public List<Hotel> getHotelByCity(String city) {
        String lowerCased = city.toLowerCase().trim();
        CACHE.computeIfAbsent("city-" + lowerCased, j -> getHotels().stream()
                .filter(m -> lowerCased.equals(m.getCity().toLowerCase()))
                .collect(collectingAndThen(toList(), Collections::unmodifiableList))
        );
        return CACHE.get("city-" + lowerCased);
    }

    private List<Hotel> getHotels() {
        return hotels;
    }

    public List<Hotel> getHotelByRoom(String room) {
        String lowerCased = room.toLowerCase().trim();
        CACHE.computeIfAbsent("room-" + lowerCased, j -> getHotels().stream()
                .filter(m -> lowerCased.equals(m.getRoom().toLowerCase()))
                .collect(collectingAndThen(toList(), Collections::unmodifiableList))
        );
        return CACHE.get("room-" + lowerCased);
    }
}
