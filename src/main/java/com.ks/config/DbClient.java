package com.ks.config;

import com.datastax.driver.core.Session;

public interface DbClient {
    void connect(String node, Integer port);

    Session getSession();

    void close();
}
