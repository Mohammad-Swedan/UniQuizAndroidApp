// AuthInterceptor.java
package com.example.myapplicationuq.Utils;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private PreferenceManager preferenceManager;

    public AuthInterceptor(Context context) {
        preferenceManager = new PreferenceManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = preferenceManager.getToken();
        Request originalRequest = chain.request();

        if (token == null || originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest);
        }

        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token);

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }
}
