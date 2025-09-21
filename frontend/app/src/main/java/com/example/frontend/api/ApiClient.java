package com.example.frontend.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import com.example.frontend.utils.Constants;
import com.example.frontend.utils.DateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;

public class ApiClient {
    private static final String BASE_URL = getBaseUrlSafely(); // URL desde archivo .env
    private static Retrofit retrofit = null;
    
    private static String getBaseUrlSafely() {
        try {
            String baseUrl = Constants.getBaseUrl();
            // Asegurar que la URL termine con /
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            return baseUrl;
        } catch (Exception e) {
            // Fallback a URL por defecto si hay problemas
            return "http://10.35.89.237:8000/";
        }
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Configurar interceptor para logging detallado
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configurar cliente OkHttp
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new DebugInterceptor()) // Interceptor de debug personalizado
                    .addInterceptor(loggingInterceptor)
                    // Remover AuthInterceptor temporalmente para evitar IllegalStateException
                    // .addInterceptor(new AuthInterceptor()) 
                    .connectTimeout(Constants.getConnectTimeout(), TimeUnit.SECONDS)
                    .readTimeout(Constants.getReadTimeout(), TimeUnit.SECONDS)
                    .writeTimeout(Constants.getWriteTimeout(), TimeUnit.SECONDS)
                    .build();

            // Configurar Gson con deserializador personalizado para fechas
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();

            // Configurar Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
    }
    
    public static void resetClientAndRecreate() {
        retrofit = null;
        getClient(); // Forzar recreación
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
