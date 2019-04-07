package com.ks.service;

public interface HashService {

    String getHash(Long mobile);

    Boolean isHashExists(String hash);

    void addHash(String hash, Long mobile);

    Long getMobile(String hash);

    void addMobile(Long mobile);

    Boolean isMobileExists(Long mobile);

    void initProjectData() throws InterruptedException;
}
