package com.example.frontend.api;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.frontend.utils.SessionManager;
import com.example.frontend.utils.Constants;

public class RetrofitClient {
    private static final String BASE_URL = Constants.getBaseUrl(); // URL desde archivo .env
    private static RetrofitClient instance;
    private final Retrofit retrofit;
    private SessionManager sessionManager;

    private RetrofitClient(Context context) {
        this.sessionManager = new SessionManager(context);
        
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(Constants.getConnectTimeout(), java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(Constants.getReadTimeout(), java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(Constants.getWriteTimeout(), java.util.concurrent.TimeUnit.SECONDS);
        
        // Interceptor para añadir el token JWT a todas las peticiones
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                
                // No añadir token para login y registro
                if (original.url().encodedPath().contains("/auth/")) {
                    return chain.proceed(original);
                }
                
                // Añadir token para otras peticiones
                String token = sessionManager.getFullToken();
                if (token != null) {
                    Request request = original.newBuilder()
                            .header("Authorization", token)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
                
                return chain.proceed(original);
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public AuthService getAuthService() {
        return retrofit.create(AuthService.class);
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
} 