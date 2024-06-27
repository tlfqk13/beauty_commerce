package com.example.sampleroad.common;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptTest {
    public static void main(String[] args) {

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("");

        System.out.println("path : " + encryptor.encrypt(""));
        System.out.println("");

        System.out.println("----------------------------------------------------");

    }
}
