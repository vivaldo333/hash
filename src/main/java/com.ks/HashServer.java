package com.ks;

import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import com.ks.config.Configs;
import com.ks.constants.AppConstants;
import com.ks.dao.MobileDao;
import com.ks.dao.impl.CassandraDaoImpl;
import com.ks.facade.MobileFacade;
import com.ks.facade.impl.MobileFacadeImpl;
import com.ks.service.HashService;
import com.ks.service.impl.HashServiceImpl;

import java.util.concurrent.ExecutionException;

public class HashServer extends HttpApp {

    private MobileFacade mobileFacade;

    public HashServer() throws InterruptedException {
        initialization();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final HashServer myServer = new HashServer();
        myServer.startServer(myServer.getAppHost(), myServer.getAppPort());
    }

    private void initialization() throws InterruptedException {
        MobileDao mobileDao = new CassandraDaoImpl();
        HashService hashService = new HashServiceImpl(mobileDao);
        hashService.initProjectData();

        mobileFacade = new MobileFacadeImpl(hashService);
    }

    @Override
    protected Route routes() {
        return route(mobileFacade.getHashRoute(), mobileFacade.getMobileRoute());
    }

    private String getAppHost() {
        return Configs.getConfig().getString(AppConstants.AKKA.HOST);
    }

    private Integer getAppPort() {
        return Configs.getConfig().getInt(AppConstants.AKKA.PORT);
    }
}
