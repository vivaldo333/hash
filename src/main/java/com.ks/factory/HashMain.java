package com.ks.factory;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class HashMain {
    public static void main(String[] args) {
        String m = "380000000000";

        System.out.println(
                DigestUtils.sha1Hex(m)
        );

        System.out.println(
                Hashing.sha1().hashString(m, Charsets.UTF_8)
        );

        System.out.println(DigestUtils.sha256Hex(m));

        System.out.println(Hashing.sha256().hashString(m, Charsets.UTF_8));


        SHA3.DigestSHA3 sha3512 = new SHA3.Digest512();
        System.out.println(Hex.toHexString(
                sha3512.digest(m.getBytes())
        ));
        System.out.println(Hashing.sha512().hashString(m, Charsets.UTF_8));

        /*org.bouncycastle.jcajce.provider.digest.SHA1.Digest
        org.bouncycastle.jcajce.provider.digest.SHA256.Digest
        org.bouncycastle.jcajce.provider.digest.SHA512.Digest*/

    }
}
