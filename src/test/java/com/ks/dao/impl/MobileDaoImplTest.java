package com.ks.dao.impl;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.google.common.hash.Hashing;
import com.ks.config.Configs;
import com.ks.constants.AppConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MobileDaoImplTest {

    private MobileDaoImpl testInstance;

    @Mock
    private Session session;
    @Mock
    PreparedStatement preparedStatement;

    @Before
    public void setUp() {
        testInstance = new MobileDaoImpl(session);
    }

    @Test
    public void initDataLoad() {
        when(session.prepare(anyString())).thenReturn(preparedStatement);
        testInstance.initLoad();
    }

    @Test
    public void test() {
        Map<String, String> mobileMap = new HashMap<>();

        String initMobile = Configs.getConfig().getString(AppConstants.HASH_MOBILE_MASK);
        int totalRowsCount = Configs.getConfig().getInt(AppConstants.DATA.ROWS_TOTAL_AMOUNT);
        int batchRowsCount = Configs.getConfig().getInt(AppConstants.DATA.ROWS_BATCH_AMOUNT);

        int countOfLoadPool = Math.round(totalRowsCount / batchRowsCount);

        for (int i = 1; i <= countOfLoadPool; i++) {

            int countToLoad = (batchRowsCount * countOfLoadPool) - totalRowsCount;
            if (countToLoad > 0) {
                batchRowsCount = batchRowsCount - countToLoad;
            }

            for (int j = 1; j <= batchRowsCount; j++) {

                //sequence generator
                Long generatedMobile = Long.valueOf(initMobile) + batchRowsCount * i - j;
                mobileMap.putIfAbsent(Hashing.sha512().hashString(
                        generatedMobile.toString(), StandardCharsets.UTF_8).toString(),
                        generatedMobile.toString());
            }
        }

        System.out.println(mobileMap.get(initMobile));
    }
}