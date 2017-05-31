package com.example.wangyicheng.gotopaste;

/**
 * Created by wangyicheng on 2017/5/31.
 * Used to encrypt the password
 */

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class MD5 {
    static String getMD5(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return new BigInteger(1, m).toString(16).toUpperCase();
    }
}