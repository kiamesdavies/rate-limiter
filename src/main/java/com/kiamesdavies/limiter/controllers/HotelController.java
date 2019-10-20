package com.kiamesdavies.limiter.controllers;

import akka.http.javadsl.model.ContentType;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.RequestContext;
import akka.http.javadsl.server.Route;
import com.google.common.collect.ImmutableMap;
import com.kiamesdavies.limiter.commons.Utility;
import com.kiamesdavies.limiter.models.Hotel;
import com.kiamesdavies.limiter.services.RateLimiter;
import com.kiamesdavies.limiter.services.HotelRepository;
import com.kiamesdavies.limiter.services.impl.SimpleRateLimiter;
import com.typesafe.config.ConfigFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static akka.http.javadsl.server.PathMatchers.segment;
import com.typesafe.config.Config;
import org.checkerframework.checker.units.qual.h;

public class HotelController extends AllDirectives {

    private final Map<String, RateLimiter> limiter;
    private final Config config;


    public  HotelController(){
        limiter = new ConcurrentHashMap<>();
        config = ConfigFactory.load();
    }

    public Route createRoute() {


        return extractRequestContext(ctx ->  pathPrefix(segment("city").slash(segment()), city ->
                pathEndOrSingleSlash(() ->
                        parameterOptional("sord", order ->
                                get(() -> complete(this.getHotelsByCity(ctx,city, order))))
                ))
                .orElse(pathPrefix(segment("room").slash(segment()), room ->
                        pathEndOrSingleSlash(() ->
                                parameterOptional("sord", order ->
                                        get(() -> complete(this.getHotelsByRoom(ctx,room, order))))
                        ))));
    }


    private HttpResponse getHotelsByCity(RequestContext ctx, String city, Optional<String> order ){
        HttpResponse response = HttpResponse.create();
        String base = Utility.extractBaseUrl(ctx.getRequest().getUri().getPathString()).orElse("base");
        initLimiter(base);
        if(limiter.get(base).getPermit() == 1){
            List<Hotel> hotels = HotelRepository.getInstance().getHotelByCity(city);
            if(order.isPresent() && "desc".equalsIgnoreCase(order.get())){
                hotels = new ArrayList<>(hotels);
                Collections.reverse(hotels);
            }
            return response.withStatus(StatusCodes.OK).withEntity(ContentTypes.APPLICATION_JSON, Utility.toBytes(ImmutableMap.of("data",hotels)));
        }
       
        return response.withStatus(StatusCodes.FORBIDDEN).withEntity(ContentTypes.TEXT_PLAIN_UTF8, "You have exceed api requests allowed");
    }
    
    

    private HttpResponse getHotelsByRoom(RequestContext ctx,String room, Optional<String> order ){
        HttpResponse response = HttpResponse.create();
        String base = Utility.extractBaseUrl(ctx.getRequest().getUri().getPathString()).orElse("base");
        initLimiter(base);
        if(limiter.get(base).getPermit() == 1){
            List<Hotel> hotels = HotelRepository.getInstance().getHotelByRoom(room);
            if(order.isPresent() && "desc".equalsIgnoreCase(order.get())){
                hotels = new ArrayList<>(hotels);
                Collections.reverse(hotels);
            }
            return response.withStatus(StatusCodes.OK).withEntity(ContentTypes.APPLICATION_JSON, Utility.toBytes(ImmutableMap.of("data", hotels)));
        }
        
        return response.withStatus(StatusCodes.FORBIDDEN).withEntity(ContentTypes.TEXT_PLAIN_UTF8, "You have exceed api requests allowed");
    }

    private void initLimiter(String base){
        limiter.computeIfAbsent(base, j -> 
                new SimpleRateLimiter(j, 
                        Utility.optionalInteger(config, String.format("api.%s.timer", j)).orElse(config.getInt("api.timer")),
                        Utility.optionalInteger(config, String.format("api.%s.permits", j)).orElse(config.getInt("api.permits")))
        );
    }
    
}
