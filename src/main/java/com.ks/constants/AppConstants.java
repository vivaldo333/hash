package com.ks.constants;

public final class AppConstants {

    public static final String CONFIG_RESOURCE = "application.conf";
    public static final String HASH_SALT = "app.hash.salt.value";
    public static final String HASH_SALT_JOIN = "app.hash.salt.join-to-mobile";
    public static final String HASH_ALGORITHM = "app.hash.algorithm";
    public static final String HASH_MOBILE_MASK = "app.mobile.mask";

    private AppConstants() {
    }

    public static final class DATA {
        public static final String CASSANDRA_HOST = "data.cassandra.host";
        public static final String CASSANDRA_PORT = "data.cassandra.port";
        public static final String ROWS_TOTAL_AMOUNT = "data.rows.total-amount";
        public static final String ROWS_BATCH_AMOUNT = "data.rows.batch-amount";
    }

    public static final class AKKA {
        public static final String HOST = "akka.http.server.host";
        public static final String PORT = "akka.http.server.default-http-port";
    }
}
