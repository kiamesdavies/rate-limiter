package com.kiamesdavies.limiter.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    private final static Pattern PATTERN = Pattern.compile("^/(.+)/.+$");
    private final  static ObjectMapper MAPPER = new ObjectMapper();

    private Utility() {

    }


    public static byte[] toBytes(Object obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            return new byte[0];
        }
    }


    /**
     *
     * @param load
     * @param path
     * @return the value in the path of the config
     */
    public static Optional<Integer> optionalInteger(Config load, String path) {
        Integer result = null;
        if (load.hasPath(path)) {
            result = load.getInt(path);
        }
        return Optional.ofNullable(result);
    }


    /**
     * Extract the base of an endpoint
     * @param url
     * @return
     */
    public static Optional<String> extractBaseUrl(String url) {
        Matcher matcher = PATTERN.matcher(url);
        String result = null;
        if (matcher.matches()) {
            result = matcher.group(1);
        }

        return Optional.ofNullable(result);
    }
}
