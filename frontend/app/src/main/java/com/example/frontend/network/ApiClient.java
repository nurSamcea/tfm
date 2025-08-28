package com.example.frontend.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import com.example.frontend.utils.Constants;

public class ApiClient {
    private static final String BASE_URL = Constants.getBaseUrl(); // URL desde archivo .env
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Configurar interceptor para logging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configurar cliente OkHttp
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new AuthInterceptor()) // Interceptor para JWT
                    .connectTimeout(Constants.getConnectTimeout(), TimeUnit.SECONDS)
                    .readTimeout(Constants.getReadTimeout(), TimeUnit.SECONDS)
                    .writeTimeout(Constants.getWriteTimeout(), TimeUnit.SECONDS)
                    .build();

            // Configurar Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
