package com.ks.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;

public class CassandraConnector implements DbClient {

    private Cluster cluster;
    private Session session;

    @Override
    public void connect(String node, Integer port) {

        this.cluster = Cluster.builder()
                .addContactPoint(node)
                .withPort(port)
                //.withCredentials(dbUser, dbPassword)
                .withPoolingOptions(new PoolingOptions()
                        .setMaxConnectionsPerHost(HostDistance.REMOTE, 100)
                        .setMaxRequestsPerConnection(HostDistance.REMOTE, 2048)
                        .setMaxConnectionsPerHost(HostDistance.LOCAL, 200)
                        .setMaxRequestsPerConnection(HostDistance.LOCAL, 4096)
                        //.setPoolTimeoutMillis(5000)
                )
                .build();

        this.session = cluster.connect();
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public void close() {
        this.session.close();
        this.cluster.close();
    }
}
