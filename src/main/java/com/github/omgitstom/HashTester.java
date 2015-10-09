package com.github.omgitstom;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.format.Shiro1CryptFormat;

import java.util.HashMap;
import java.util.Map;


public class HashTester {

    public static void main(String[] args) throws Exception {

        /* Testing existing Hashes */
        DefaultPasswordService passwordService = new DefaultPasswordService();
        DefaultHashService service =  new DefaultHashService();

        HashMap <String, String> map = new HashMap<String, String>();
        map.put("123456", "$stormpath2$MD5$1$OWI3OTQwYjEwODEwOTdkNTcwZDY5NjQ2ZDNlNmZjNzM=$ULWTW74NXPyLYj3VfYHWrg==");
        map.put("password", "$stormpath2$MD5$1$NzEyN2ZhYzdkZTAyMjJlMGQyMWYxMWRmZmY2YjA1MWI=$K18Ak0YikAFrqgglhIaY5g==");
        map.put("qwerty", "$stormpath2$MD5$1$OGYyMmM5YzVlMDEwODEwZTg3MzM4ZTA2YjljZjMxYmE=$EuFAr2NTM83PrizVAYuOvw==");
        map.put("monkey", "$stormpath2$MD5$1$MWI5MmI3YTNjNTRiNjRiZWQ1OWRlZjY3NzFjMjdjMjk=$6RehlZ3ANAfW9nvq/zUdpw==");
        map.put("monkey", "$stormpath2$MD5$1$$0HY+2qnZvSqVFigOkETYhQ==");
        map.put("123456", "$stormpath2$MD5$1$$4QrcOUm6Wau+VuBX8g+IPg==");
        map.put("superpassword", "$stormpath2$MD5$1$$0eV2txzO9ZeNIh+t9PDiiQ==");
        map.put("testing12", "$stormpath2$SHA-512$1$ZFhBRmpFSnEwVEx2ekhKS0JTMDJBNTNmcg==$Q+sGFg9e+pe9QsUdfnbJUMDtrQNf27ezTnnGllBVkQpMRc9bqH6WkyE3y0svD/7cBk8uJW9Wb3dolWwDtDLFjg==");

        System.out.println("\nTesting existing Hashes");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("Password " + entry.getKey() + " matched: " + testMcf(entry.getKey(), entry.getValue()));
        }

        /* END Testing existing Hashes */


        /* Generated Hash for Testing */

        String clearTextPassword = "test42";
        HashRequest.Builder hashRequestBuilder = new HashRequest.Builder();

        hashRequestBuilder.setAlgorithmName("SHA-1");
        hashRequestBuilder.setSalt("ks3V5ACNPn");
        hashRequestBuilder.setIterations(2);
        hashRequestBuilder.setSource(clearTextPassword);
        HashRequest request = hashRequestBuilder.build();

        Hash hash = service.computeHash(request);

        boolean resultAgainstHash = passwordService.passwordsMatch(clearTextPassword, hash);

        System.out.println("\nGenerated Hash for Testing");
        System.out.println("Password Matched against Derived Hash: " + resultAgainstHash);

        Shiro1CryptFormat format = new Shiro1CryptFormat();
        System.out.println("Formatted Hash: " + format.format(hash).replace("$shiro1$", "$stormpath2$"));

        /* END Generated Hash for Testing */
    }

    public static boolean testMcf(String password, String mcf){

        DefaultPasswordService passwordService = new DefaultPasswordService();
        DefaultHashService service = new DefaultHashService();
        service.setGeneratePublicSalt(false);
        passwordService.setHashService(service);

        String shiroMcf = mcf.replace("$stormpath2$", "$shiro1$");

        boolean match = passwordService.passwordsMatch(password, shiroMcf);

        if (!match){
            System.out.println("Password \"" + password + "\" doesn't match " + mcf);
        }

        return match;
    }
}
