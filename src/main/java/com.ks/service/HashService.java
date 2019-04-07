package com.ks.service;

public interface HashService {

    String getHash(String mobile);

    Boolean isHashExists(String hash);

    void addHash(String hash);

    String getMobile(String hashCode);
}
