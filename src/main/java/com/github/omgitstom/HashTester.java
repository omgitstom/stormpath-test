package com.github.omgitstom;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.format.Shiro1CryptFormat;
import org.apache.shiro.util.ByteSource;

import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Map;


public class HashTester {

    public static void main(String[] args) throws Exception {

        /* Testing existing Hashes */
        DefaultPasswordService passwordService = new DefaultPasswordService();
        DefaultHashService service =  new DefaultHashService();

        HashMap <String, String> map = new HashMap<String, String>();
        /*
        map.put("123456", "$stormpath2$MD5$1$OWI3OTQwYjEwODEwOTdkNTcwZDY5NjQ2ZDNlNmZjNzM=$ULWTW74NXPyLYj3VfYHWrg==");
        map.put("password", "$stormpath2$MD5$1$NzEyN2ZhYzdkZTAyMjJlMGQyMWYxMWRmZmY2YjA1MWI=$K18Ak0YikAFrqgglhIaY5g==");
        map.put("qwerty", "$stormpath2$MD5$1$OGYyMmM5YzVlMDEwODEwZTg3MzM4ZTA2YjljZjMxYmE=$EuFAr2NTM83PrizVAYuOvw==");
        map.put("monkey", "$stormpath2$MD5$1$MWI5MmI3YTNjNTRiNjRiZWQ1OWRlZjY3NzFjMjdjMjk=$6RehlZ3ANAfW9nvq/zUdpw==");
        map.put("monkey", "$stormpath2$MD5$1$$0HY+2qnZvSqVFigOkETYhQ==");
        map.put("123456", "$stormpath2$MD5$1$$4QrcOUm6Wau+VuBX8g+IPg==");
        map.put("superpassword", "$stormpath2$MD5$1$$0eV2txzO9ZeNIh+t9PDiiQ==");
        */
        //map.put("testing12", "$stormpath2$SHA-512$1$ZFhBRmpFSnEwVEx2ekhKS0JTMDJBNTNmcg==$Q+sGFg9e+pe9QsUdfnbJUMDtrQNf27ezTnnGllBVkQpMRc9bqH6WkyE3y0svD/7cBk8uJW9Wb3dolWwDtDLFjg==");
        //map.put("password", "$stormpath2$SHA-512$1023$ZZvxrLs2uA8GfHA0HSmQRskIBhIzEyXaEuEq9cEcTQySeWxF2a4WZ9pxX0M8jY3Vti2hkyDFcbg3A7kbW6kohs5hqTO9oHwRqu5JEvXseblh9H9O5A5V2utdqmmSpH/xKlJ9RwYgUABWPSdXJlW+A9D98s6LJtKbGheRufP2hootwhHSrEH/5xwz1/nu0FPPhHHibwCwjybH+1TwwgkRzG2Yhih21smnI4ApAr7F1/teSo3xRLCHOtT3lw5VpjswAPRgaxIfX60+N2RsmhJuwHTxHr0JQGgc5u2SRKGnEs+pI0GaRi+w/9+Oq5+4bJ71qrz61cjdGsfWJkE8IS5+JA==$w8+U0iq75SCxxSzqfAUFuPDszvPIKFlnSYdEN25nNMKqajswfynC+xvJxSes6FK9MH+8afP369Ex2j7KVUbCZzlHNj8FBkYzjerYf1iw5RtGEHhaqWDkV4mnKqbZaxqupVHn0NdX4Kkt7kcGdcP6tJso69k+m0AOdToVvOHJVsr27g/fo3n9tGJU+EPyb1M4+scf7F5XEQH26uCb5npVLGCbcJVUHJNtCquWK7EsbRTEw8JvTkKO2xMj9eSVywia8tqbo4vkUrMIFUM9VjsezrQgGZZoGq0HhWTw01nY2REPvqmhl0sPnvZ3SyHweCcPuNJwbohp6UTaiVvmnY3lTb85Dra2cWiqwy1LO1TuZh2wMPxIez9LorDsS8GyD+hBvHclr8oKoVoq3Jl4xugAPcE5K2LgbiaQTjLVmjxNMNRk09l88uKcaHx2FpVLubqhox4NQxNAom0A3fd6CLX2+ZEKGiZIo6xGtUok1Bxz5Zs4MYvWZYOzEpF9cG2KoHqXPSFpDSwOVVe/zw5+Jvn8eqDA4kc2C+IwKh0oTcHhggyqPzw5dfZWL+6EnI+2AOhOtoThYVdN5SAZZ0oWbgxMHTiPelFIq2hyaXWszKm6FIJ6fy/3lFyo8fQpI5iQSQr9gkXj9uUf6dxhUcsVezaA2kXjCw3vMfNqY+eiv3HxWZ0=");
        //map.put("password", "$stormpath2$SHA-512$1024$ZZvxrLs2uA8GfHA0HSmQRskIBhIzEyXaEuEq9cEcTQySeWxF2a4WZ9pxX0M8jY3Vti2hkyDFcbg3A7kbW6kohs5hqTO9oHwRqu5JEvXseblh9H9O5A5V2utdqmmSpH/xKlJ9RwYgUABWPSdXJlW+A9D98s6LJtKbGheRufP2hootwhHSrEH/5xwz1/nu0FPPhHHibwCwjybH+1TwwgkRzG2Yhih21smnI4ApAr7F1/teSo3xRLCHOtT3lw5VpjswAPRgaxIfX60+N2RsmhJuwHTxHr0JQGgc5u2SRKGnEs+pI0GaRi+w/9+Oq5+4bJ71qrz61cjdGsfWJkE8IS5+JA==$w8+U0iq75SCxxSzqfAUFuPDszvPIKFlnSYdEN25nNMKqajswfynC+xvJxSes6FK9MH+8afP369Ex2j7KVUbCZzlHNj8FBkYzjerYf1iw5RtGEHhaqWDkV4mnKqbZaxqupVHn0NdX4Kkt7kcGdcP6tJso69k+m0AOdToVvOHJVsr27g/fo3n9tGJU+EPyb1M4+scf7F5XEQH26uCb5npVLGCbcJVUHJNtCquWK7EsbRTEw8JvTkKO2xMj9eSVywia8tqbo4vkUrMIFUM9VjsezrQgGZZoGq0HhWTw01nY2REPvqmhl0sPnvZ3SyHweCcPuNJwbohp6UTaiVvmnY3lTb85Dra2cWiqwy1LO1TuZh2wMPxIez9LorDsS8GyD+hBvHclr8oKoVoq3Jl4xugAPcE5K2LgbiaQTjLVmjxNMNRk09l88uKcaHx2FpVLubqhox4NQxNAom0A3fd6CLX2+ZEKGiZIo6xGtUok1Bxz5Zs4MYvWZYOzEpF9cG2KoHqXPSFpDSwOVVe/zw5+Jvn8eqDA4kc2C+IwKh0oTcHhggyqPzw5dfZWL+6EnI+2AOhOtoThYVdN5SAZZ0oWbgxMHTiPelFIq2hyaXWszKm6FIJ6fy/3lFyo8fQpI5iQSQr9gkXj9uUf6dxhUcsVezaA2kXjCw3vMfNqY+eiv3HxWZ0=");
        //map.put("password", "$stormpath2$SHA-512$1025$ZZvxrLs2uA8GfHA0HSmQRskIBhIzEyXaEuEq9cEcTQySeWxF2a4WZ9pxX0M8jY3Vti2hkyDFcbg3A7kbW6kohs5hqTO9oHwRqu5JEvXseblh9H9O5A5V2utdqmmSpH/xKlJ9RwYgUABWPSdXJlW+A9D98s6LJtKbGheRufP2hootwhHSrEH/5xwz1/nu0FPPhHHibwCwjybH+1TwwgkRzG2Yhih21smnI4ApAr7F1/teSo3xRLCHOtT3lw5VpjswAPRgaxIfX60+N2RsmhJuwHTxHr0JQGgc5u2SRKGnEs+pI0GaRi+w/9+Oq5+4bJ71qrz61cjdGsfWJkE8IS5+JA==$w8+U0iq75SCxxSzqfAUFuPDszvPIKFlnSYdEN25nNMKqajswfynC+xvJxSes6FK9MH+8afP369Ex2j7KVUbCZzlHNj8FBkYzjerYf1iw5RtGEHhaqWDkV4mnKqbZaxqupVHn0NdX4Kkt7kcGdcP6tJso69k+m0AOdToVvOHJVsr27g/fo3n9tGJU+EPyb1M4+scf7F5XEQH26uCb5npVLGCbcJVUHJNtCquWK7EsbRTEw8JvTkKO2xMj9eSVywia8tqbo4vkUrMIFUM9VjsezrQgGZZoGq0HhWTw01nY2REPvqmhl0sPnvZ3SyHweCcPuNJwbohp6UTaiVvmnY3lTb85Dra2cWiqwy1LO1TuZh2wMPxIez9LorDsS8GyD+hBvHclr8oKoVoq3Jl4xugAPcE5K2LgbiaQTjLVmjxNMNRk09l88uKcaHx2FpVLubqhox4NQxNAom0A3fd6CLX2+ZEKGiZIo6xGtUok1Bxz5Zs4MYvWZYOzEpF9cG2KoHqXPSFpDSwOVVe/zw5+Jvn8eqDA4kc2C+IwKh0oTcHhggyqPzw5dfZWL+6EnI+2AOhOtoThYVdN5SAZZ0oWbgxMHTiPelFIq2hyaXWszKm6FIJ6fy/3lFyo8fQpI5iQSQr9gkXj9uUf6dxhUcsVezaA2kXjCw3vMfNqY+eiv3HxWZ0=");

        System.out.println("\nTesting existing Hashes");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("Password " + entry.getKey() + " matched: " + testMcf(entry.getKey(), entry.getValue()));
        }

        /* END Testing existing Hashes */


        /* Generated Hash for Testing */


        String clearTextPassword = "password";

        HashRequest.Builder hashRequestBuilder = new HashRequest.Builder();

        //Salt
        byte[] bytes = DatatypeConverter.parseBase64Binary("3ErOWdjz");
        ByteSource source = ByteSource.Util.bytes(bytes);

        //Prepare the hash request
        hashRequestBuilder.setAlgorithmName("MD5");
        hashRequestBuilder.setSalt(source);
        hashRequestBuilder.setIterations(16);
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
