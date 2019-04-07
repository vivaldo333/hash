package com.ks.service.impl;

import com.ks.config.Configs;
import com.ks.constants.AppConstants;
import com.ks.enums.HashTypeEnum;
import com.ks.exceptions.InvalidHashAlgorithmConfiguration;
import com.ks.service.HashService;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA512;
import org.bouncycastle.util.encoders.Hex;

import java.util.Map;

public class HashServiceImpl implements HashService {

    private static final SHA1.Digest SHA1_DIGEST = new SHA1.Digest();
    private static final SHA256.Digest SHA2_256_DIGEST = new SHA256.Digest();
    private static final SHA512.Digest SHA2_512_DIGEST = new SHA512.Digest();
    private static final SHA3.DigestSHA3 SHA3_256_DIGEST = new SHA3.Digest256();
    private static final SHA3.DigestSHA3 SHA3_512_DIGEST = new SHA3.Digest512();

    private Map<String, String> hashToMobile;

    public HashServiceImpl(Map<String, String> hashToMobile) {
        this.hashToMobile = hashToMobile;
    }

    //Hashing.sha1().hashString( "password", Charsets.UTF_8 ).toString()
    @Override
    public String getHash(String mobile) {
        HashTypeEnum hashAlgorithmType = getHashAlgorithm();
        String hash = StringUtils.EMPTY;

        switch (hashAlgorithmType) {
            case SHA1:
                hash = getHexHashSHA1(mobile);
                break;
            case SHA2_256:
                hash = getHexHashSHA2256(mobile);
                break;
            case SHA2_512:
                hash = getHexHashSHA2512(mobile);
                break;
            case SHA3_256:
                hash = getHexHashSHA3256(mobile);
                break;
            case SHA3_512:
                hash = getHexHashSHA3512(mobile);
                break;
            default:
        }

        return hash;
    }

    @Override
    public Boolean isHashExists(String hash) {
        return hashToMobile.containsKey(hash);
    }

    @Override
    public void addHash(String hash) {

    }

    @Override
    public String getMobile(String hash) {
        return null;
    }

    private String getHexHashSHA1(String mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA1_DIGEST.digest(mobileWithSaltBytes));
    }

    private String getHexHashSHA2256(String mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA2_256_DIGEST.digest(mobileWithSaltBytes));
    }

    private String getHexHashSHA2512(String mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA2_512_DIGEST.digest(mobileWithSaltBytes));
    }

    private String getHexHashSHA3256(String mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA3_256_DIGEST.digest(mobileWithSaltBytes));
    }

    private String getHexHashSHA3512(String mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA3_512_DIGEST.digest(mobileWithSaltBytes));
    }

    private byte[] getMobileWithSaltBytes(String mobile) {
        String salt = getSalt();

        return mobile.concat(salt).getBytes();
    }

    private String getSalt() {
        return Configs.getConfig().getString(AppConstants.HASH_SALT);
    }

    private String getHashAlgorithmConfig() {
        return Configs.getConfig().getString(AppConstants.HASH_ALGORITHM);
    }

    private HashTypeEnum getHashAlgorithm() {
        String hashAlgorithmConfig = getHashAlgorithmConfig();

        if (isExistsHashAlgorithmConfig(hashAlgorithmConfig)) {
            return HashTypeEnum.valueOf(hashAlgorithmConfig);
        }

        throw new InvalidHashAlgorithmConfiguration("Configured hash algorithm is not found");
    }

    private boolean isExistsHashAlgorithmConfig(String hashAlgorithmConfig) {
        //Arrays.stream(HashTypeEnum.values()).allMatch(hashType -> hashType.name() == hashAlgorithmConfig)
        return EnumUtils.isValidEnum(HashTypeEnum.class, hashAlgorithmConfig);
    }
}
