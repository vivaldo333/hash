package com.ks;

import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import com.datastax.driver.core.Session;
import com.ks.config.CassandraConnector;
import com.ks.config.DbClient;
import com.ks.dao.MobileDao;
import com.ks.dao.impl.MobileDaoImpl;
import com.ks.facade.MobileFacade;
import com.ks.facade.impl.MobileFacadeImpl;
import sun.security.validator.SimpleValidator;

import javax.xml.validation.Validator;
import java.util.concurrent.ExecutionException;

import static akka.http.javadsl.server.PathMatchers.segment;
import static java.util.regex.Pattern.compile;

public class HashServer extends HttpApp {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //final HashServer myServer = new HashServer();

        //myServer.startServer("localhost", 8080);

        //2 GET
        //PROPERTY
        //app
        //cassandra
        //docker???


        //номер -  хеширую и ищу в мапе,
            //асинхронно отдать хеш

            //если номера нету в мапе, то создать хеш и закинуть в мапу и сохранить в бд
            //get(key) - посмотреть по хешу, есть ли в мапе
            //если уже есть такой хеш в мапе, то warning (уже есть такой хеш и номер телефона)
            //put in map

            //если номера нету в мапе - то положить в мапу и в БД, но нужно убедится, что в мапе нету уже такого хеша
        //хеш - искать в мапе номер (валуе) и отдать номер

        //убидится что у тебя уникальный хеш

        DbClient client = new CassandraConnector();
        client.connect("127.0.0.1", 9042);

        MobileDao mobileDao = new MobileDaoImpl(client.getSession());
        mobileDao.initDataLoad();
        //mobileDao.addMobile("380672223355");
        client.close();

    }

    @Override
    protected Route routes() {
        MobileFacade mobileFacade = new MobileFacadeImpl();
        Route getHashRoute = path(segment("mobile").slash(segment(compile("\\d+"))),
                (mobile) ->
                        complete(mobileFacade.getMobile(mobile)));
        Route getMobileRoute = path(segment("hash").slash(segment()),
                (hash) -> complete(mobileFacade.getMobile(hash)));
        //complete(StatusCodes.OK)

        return route(getHashRoute, getMobileRoute);
    }
}
