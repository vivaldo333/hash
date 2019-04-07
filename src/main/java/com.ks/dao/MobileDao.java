package com.ks.dao;

import java.util.List;
import java.util.Optional;

public interface MobileDao {
    Optional<Long> findMobile(Long mobile);

    List<Long> findAllMobiles();

    void addMobile(Long mobile);

    void initLoad() throws InterruptedException;

}
