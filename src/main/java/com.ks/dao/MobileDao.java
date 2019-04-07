package com.ks.dao;

import java.util.Optional;

public interface MobileDao {
    Optional<String> findMobile(String mobile);

    void addMobile(String mobile);

    void initDataLoad();

}
