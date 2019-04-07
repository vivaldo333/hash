package com.ks.dao.impl;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.ks.config.Configs;
import com.ks.constants.AppConstants;
import com.ks.dao.MobileDao;
import io.reactivex.Completable;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class MobileDaoImpl implements MobileDao {

    public static final String DOT = ".";
    private static final String KEY_SPACE = "hashing";
    private static final String TABLE = "cellphone";
    private static final String MOBILE_ATTRIBUTE = "mobile";
    private static final String INSERT_QUERY = "INSERT INTO " + KEY_SPACE + DOT + TABLE +
            " (" + MOBILE_ATTRIBUTE + ") VALUES (?)"; // IF NOT EXISTS
    private static final String QUERY_GET_MOBILE = "SELECT " + MOBILE_ATTRIBUTE +
            " FROM " + KEY_SPACE + DOT + TABLE +
            " WHERE " + MOBILE_ATTRIBUTE + "=?";

    private Session session;

    public MobileDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public Optional<String> findMobile(String mobile) {
        //QueryBuilder.select().from(KEY_SPACE, TABLE);
        PreparedStatement statements = session.prepare(QUERY_GET_MOBILE);
        ResultSet result = session.execute(statements.bind(mobile));
        Optional<Row> row = result.all().stream().findFirst();

        if (row.isPresent()) {
            return Optional.ofNullable(row.get().get(0, String.class));
        }

        return Optional.empty();
    }

    @Override
    public void addMobile(String mobile) {
        Insert insert = QueryBuilder.insertInto(KEY_SPACE, TABLE)
                .value(MOBILE_ATTRIBUTE, mobile)
                .ifNotExists();

        session.execute(insert.toString());
    }

    @Override
    public void initDataLoad() {
        int totalRowsCount = getTotalRowsCount();
        int batchRowsCount = getBatchRowsCount();
        int countOfLoadPool = getLoadIterationsCount(totalRowsCount, batchRowsCount);
        int countAvailableProcessors = Runtime.getRuntime().availableProcessors();
        BatchStatement batchStatement = new BatchStatement();
        ExecutorService executor = Executors.newFixedThreadPool(countAvailableProcessors);

        IntStream.range(1, countOfLoadPool).forEach(loadPoolNumber -> {
            Completable completable = Completable.fromFuture(
                    executor.submit(getTask(countOfLoadPool, session, batchStatement)));
            completable.blockingAwait();
        });

        executor.shutdown();
    }

    private int getLoadIterationsCount(int totalRowsCount, int batchRowsCount) {
        return Math.round(totalRowsCount / batchRowsCount);
    }

    private int getBatchRowsCount() {
        return Configs.getConfig().getInt(AppConstants.DATA.ROWS_BATCH_AMOUNT);
    }

    private int getTotalRowsCount() {
        return Configs.getConfig().getInt(AppConstants.DATA.ROWS_TOTAL_AMOUNT);
    }

    private String getMobileMask() {
        return Configs.getConfig().getString(AppConstants.HASH_MOBILE_MASK);
    }

    private Runnable getTask(int countOfLoadPool, Session session, BatchStatement batchStatement) {
        return () -> {
            PreparedStatement preparedStatement = session.prepare(INSERT_QUERY);
            int initRowNumber = 1;
            int batchRowsCount = getRecalculatedBatchRowCount(countOfLoadPool);

            for (int rowNumber = initRowNumber; rowNumber <= batchRowsCount; rowNumber++) {
                //sequence generator
                String generatedMobile = generateMobileNumber(countOfLoadPool, batchRowsCount, rowNumber);
                batchStatement.add(preparedStatement.bind(generatedMobile));
            }

            session.execute(batchStatement);
            batchStatement.clear();
        };
    }

    private String generateMobileNumber(int countOfLoadPool, int batchRowsCount, int rowNumber) {
        return String.valueOf(Long.valueOf(getMobileMask()) + batchRowsCount * countOfLoadPool + rowNumber);
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
