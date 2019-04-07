package com.ks.facade.impl;

import com.datastax.driver.core.Session;
import com.ks.config.CassandraConnector;
import com.ks.config.DbClient;
import com.ks.dao.MobileDao;
import com.ks.dao.impl.MobileDaoImpl;
import com.ks.facade.MobileFacade;

public class MobileFacadeImpl implements MobileFacade {

    private DbClient client;
    private MobileDao mobileDao;

    public MobileFacadeImpl() {
        this.client = new CassandraConnector();
        client.connect("127.0.0.1", 9042);

        this.mobileDao = new MobileDaoImpl(client.getSession());
    }

    /*DbClient client = new CassandraConnector();
    client.connect("127.0.0.1", 9042);

    Session session = client.getSession();

    MobileDao mobileDao = new MobileDaoImpl(session);
    mobileDao.findMobile("380681112233").ifPresent(System.out::println);

    client.close();*/

    @Override
    public String getMobile(String hash) {
        return null;
    }

    @Override
    public String getHash(String mobile) {
        mobileDao.findMobile(mobile);

        return null;
    }
}
