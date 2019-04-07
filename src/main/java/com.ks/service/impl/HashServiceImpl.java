package com.ks.service.impl;

import com.ks.config.Configs;
import com.ks.constants.AppConstants;
import com.ks.dao.MobileDao;
import com.ks.enums.HashTypeEnum;
import com.ks.enums.SaltJoinTypeEnum;
import com.ks.exceptions.InvalidHashAlgorithmConfigurationException;
import com.ks.exceptions.MobileNotFoundException;
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

    private Map<String, Long> hashToMobile;
    private MobileDao mobileDao;

    public HashServiceImpl() {
    }

    public HashServiceImpl(Map<String, Long> hashToMobile, MobileDao mobileDao) {
        this.hashToMobile = hashToMobile;
        this.mobileDao = mobileDao;
    }

    public HashServiceImpl(MobileDao mobileDao) {
        this.mobileDao = mobileDao;
        addAllHashesAndMobilesToMap();
    }

    @Override
    public String getHash(Long mobile) {

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
    public void addHash(String hash, Long mobile) {
        hashToMobile.putIfAbsent(hash, mobile);
    }

    @Override
    public Long getMobile(String hash) {
        if (isHashExists(hash)) {
            return hashToMobile.get(hash);
        }

        throw new MobileNotFoundException("Mobile not found");
    }

    @Override
    public void addMobile(Long mobile) {
        mobileDao.addMobile(mobile);
    }

    @Override
    public Boolean isMobileExists(Long mobile) {
        return mobileDao.findMobile(mobile).isPresent();
    }

    private void addAllHashesAndMobilesToMap() {
        //TODO iteration during adding mobile from DB to Batch
        mobileDao.findAllMobiles().forEach(mobile -> hashToMobile.putIfAbsent(getHash(mobile), mobile));

    }

    @Override
    public void initProjectData() throws InterruptedException {
        mobileDao.initLoad();
    }

    private String getHexHashSHA1(Long mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA1_DIGEST.digest(mobileWithSaltBytes));
    }

    private String getHexHashSHA2256(Long mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA2_256_DIGEST.digest(mobileWithSaltBytes));
    }

    private String getHexHashSHA2512(Long mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA2_512_DIGEST.digest(mobileWithSaltBytes));
    }

    private String getHexHashSHA3256(Long mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA3_256_DIGEST.digest(mobileWithSaltBytes));
    }

    private String getHexHashSHA3512(Long mobile) {
        byte[] mobileWithSaltBytes = getMobileWithSaltBytes(mobile);

        return Hex.toHexString(SHA3_512_DIGEST.digest(mobileWithSaltBytes));
    }

    private byte[] getMobileWithSaltBytes(Long mobile) {
        String salt = getSalt();

        if (isJoinSaltToLeft()) {
            return StringUtils.join(salt, mobile).getBytes();
        }

        return StringUtils.join(mobile, salt).getBytes();
    }

    private boolean isJoinSaltToLeft() {
        return SaltJoinTypeEnum.LEFT.name() == getSaltJoinSide();
    }

    private String getSalt() {
        return Configs.getConfig().getString(AppConstants.HASH_SALT);
    }

    private String getSaltJoinSide() {
        return Configs.getConfig().getString(AppConstants.HASH_SALT_JOIN);
    }

    private String getHashAlgorithmConfig() {
        return Configs.getConfig().getString(AppConstants.HASH_ALGORITHM);
    }

    private HashTypeEnum getHashAlgorithm() {
        String hashAlgorithmConfig = getHashAlgorithmConfig();

        if (isExistsHashAlgorithmConfig(hashAlgorithmConfig)) {
            return HashTypeEnum.valueOf(hashAlgorithmConfig);
        }

        throw new InvalidHashAlgorithmConfigurationException("Configured hash algorithm is not found");
    }

    private boolean isExistsHashAlgorithmConfig(String hashAlgorithmConfig) {
        //Arrays.stream(HashTypeEnum.values()).allMatch(hashType -> hashType.name() == hashAlgorithmConfig)
        return EnumUtils.isValidEnum(HashTypeEnum.class, hashAlgorithmConfig);
    }
}
