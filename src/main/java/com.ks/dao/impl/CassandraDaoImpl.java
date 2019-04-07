package com.ks.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.ks.config.CassandraConnector;
import com.ks.config.Configs;
import com.ks.config.DbClient;
import com.ks.constants.AppConstants;
import com.ks.dao.MobileDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class CassandraDaoImpl implements MobileDao {

    public static final String DOT = ".";
    private static final String KEY_SPACE = "hashing";
    private static final String TABLE = "cellphone";
    private static final String MOBILE_ATTRIBUTE = "mobile";
    private static final String INSERT_QUERY = "INSERT INTO " + KEY_SPACE + DOT + TABLE +
            " (" + MOBILE_ATTRIBUTE + ") VALUES (?)";

    private DbClient client;
    private Session session;

    public CassandraDaoImpl() {
        this.client = new CassandraConnector();
        client.connect(getDbHost(), getDbPort());

        this.session = client.getSession();
    }

    @Override
    public Optional<Long> findMobile(Long mobile) {
        Select selectQuery = QueryBuilder.select().column(MOBILE_ATTRIBUTE).from(KEY_SPACE, TABLE);
        Select.Where selectWhere = selectQuery.where();
        Clause clause = QueryBuilder.eq(MOBILE_ATTRIBUTE, mobile);
        selectWhere.and(clause);

        ResultSet result = session.execute(selectQuery);
        Optional<Row> row = result.all().stream().findFirst();

        if (row.isPresent()) {
            return Optional.ofNullable(row.get().get(0, Long.class));
        }

        return Optional.empty();
    }

    @Override
    public List<Long> findAllMobiles() {
        //TODO replace on batch load
        Select selectQuery = QueryBuilder.select().column(MOBILE_ATTRIBUTE).from(KEY_SPACE, TABLE);

        ResultSet result = session.execute(selectQuery);
        List<Row> rowList = result.all();

        if (!rowList.isEmpty()) {
            List<Long> mobiles = new ArrayList<>();
            Iterator<Row> rows = rowList.iterator();

            while (rows.hasNext()) {
                mobiles.add(rows.next().getLong(MOBILE_ATTRIBUTE));
            }

            return mobiles;
        }

        return Collections.emptyList();
    }

    @Override
    public void addMobile(Long mobile) {
        Insert insert = QueryBuilder.insertInto(KEY_SPACE, TABLE)
                .value(MOBILE_ATTRIBUTE, mobile)
                .ifNotExists();

        session.execute(insert.toString());
    }

    @Override
    public void initLoad() throws InterruptedException {
        int totalRowsCount = getTotalRowsCount();
        int initialBatchCount = getBatchRowsCount();
        int fromRow = 1;
        int toRow = initialBatchCount;

        while (true) {
            if (toRow < totalRowsCount) {
                loadBatch(fromRow, toRow);

                fromRow += initialBatchCount;
                toRow += initialBatchCount;
            } else {
                toRow = totalRowsCount;

                loadBatch(fromRow, toRow);
                return;
            }
        }
    }

    private void loadBatch(int fromRow, int toRow) throws InterruptedException {
        int batchRowsCount = toRow - fromRow + 1;

        CountDownLatch batchLatch = new CountDownLatch(batchRowsCount);

        IntStream.range(fromRow, toRow + 1).parallel().forEach(rowNumber -> {
            ResultSetFuture resultSetFuture = save(generateMobileNumber(rowNumber));

            Futures.addCallback(resultSetFuture, new FutureCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet result) {
                    batchLatch.countDown();
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println(t);
                    batchLatch.countDown();
                }
            });
        });

        batchLatch.await();
    }


    private ResultSetFuture save(final Long mobile) {
        Insert insert = QueryBuilder.insertInto(KEY_SPACE, TABLE)
                .value(MOBILE_ATTRIBUTE, mobile)
                .ifNotExists();

        return session.executeAsync(insert);
    }

    private String getDbHost() {
        return Configs.getConfig().getString(AppConstants.DATA.CASSANDRA_HOST);
    }

    private Integer getDbPort() {
        return Configs.getConfig().getInt(AppConstants.DATA.CASSANDRA_PORT);
    }

    private int getBatchRowsCount() {
        return Configs.getConfig().getInt(AppConstants.DATA.ROWS_BATCH_AMOUNT);
    }

    private int getTotalRowsCount() {
        return Configs.getConfig().getInt(AppConstants.DATA.ROWS_TOTAL_AMOUNT);
    }

    private Long getMobileMask() {
        return Configs.getConfig().getLong(AppConstants.HASH_MOBILE_MASK);
    }

    private Long generateMobileNumber(int rowNumber) {
        return getMobileMask() + rowNumber;
    }

    private int getLoadIterationsCount(int totalRowsCount, int batchRowsCount) {
        return Math.round(totalRowsCount / batchRowsCount);
    }

    private int getRecalculatedBatchRowCount(int countOfLoadPool) {
        int batchRowsCount = getBatchRowsCount();
        int countToLoad = (batchRowsCount * countOfLoadPool) - getTotalRowsCount();

        if (countToLoad > 0) {
            return batchRowsCount - countToLoad;
        }

        return batchRowsCount;
    }
}
