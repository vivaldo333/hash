package com.ks.facade;

import akka.http.javadsl.server.Route;

public interface MobileFacade {
    Route getMobileRoute();

    Route getHashRoute();
}
