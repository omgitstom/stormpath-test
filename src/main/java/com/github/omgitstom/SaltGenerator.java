package com.github.omgitstom;

import com.stormpath.sdk.impl.security.DefaultSaltGenerator;

/**
 * Created by tom on 4/7/16.
 */
public class SaltGenerator {
    public static void main(String[] args) throws Exception {
        DefaultSaltGenerator saltg = new DefaultSaltGenerator(24);
        System.out.println(saltg.generate());
    }
}
