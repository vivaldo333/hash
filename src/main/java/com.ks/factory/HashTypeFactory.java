package com.ks.factory;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA512;

public final class HashTypeFactory {
    private static final SHA1.Digest SHA1_DIGEST = new SHA1.Digest();
    private static final SHA256.Digest SHA2_256_DIGEST = new SHA256.Digest();
    private static final SHA512.Digest SHA2_512_DIGEST = new SHA512.Digest();
    private static final SHA3.DigestSHA3 SHA3_256_DIGEST = new SHA3.Digest256();
    private static final SHA3.DigestSHA3 SHA3_512_DIGEST = new SHA3.Digest512();

    public HashTypeFactory() {
        throw new UnsupportedOperationException();
    }

    public static HashFunction getHashType(HashTypeEnum hashType) {
        switch (hashType) {
            case SHA1:
                //return SHA1_DIGEST;
            case SHA2_256:
                return Hashing.sha256();
            case SHA3_256:
                return Hashing.sha512();
            default:
        }

        throw new IllegalArgumentException();
    }


    private enum HashTypeEnum {
        SHA1,
        SHA2_256,
        SHA2_512,
        SHA3_256,
        SHA3_512
    }

}
