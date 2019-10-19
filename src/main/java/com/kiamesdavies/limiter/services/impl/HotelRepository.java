package com.kiamesdavies.limiter.services.impl;

import com.kiamesdavies.limiter.models.Hotel;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

    private volatile List<Hotel> hotels;

    private HotelRepository() {
        CACHE = new ConcurrentHashMap<>();
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
        String lowerCased = city.toLowerCase();
        CACHE.computeIfAbsent("CITY-"+lowerCased, j -> getHotels().stream()
                .filter(m -> lowerCased.equals(m.getCity()))
                .collect(collectingAndThen(toList(), Collections::unmodifiableList))
        );
        return CACHE.get("CITY-"+lowerCased);
    }


    private List<Hotel> getHotels() {
        if (hotels == null) {
            synchronized (this) {
                if (hotels == null) {
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
            }
        }
        return hotels;
    }
}
