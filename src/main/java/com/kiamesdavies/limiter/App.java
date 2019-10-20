package com.kiamesdavies.limiter;

import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import com.kiamesdavies.limiter.controllers.HotelController;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.ExecutionException;

/**
 * Start of App
 */
public class App extends HttpApp {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        App app = new App();
        app.startServer("0.0.0.0",
                ConfigFactory.load().getInt("server.port"));
    }

    /**
     * Override to implement the route that will be served by this http server.
     *
     * @return routes
     */
    @Override
    protected Route routes() {
        return new HotelController().createRoute();
    }


}
