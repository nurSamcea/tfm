package com.example.frontend.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import com.example.frontend.utils.Constants;
import com.example.frontend.utils.DateDeserializer;
import com.example.frontend.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

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
    
    /**
     * Crea un cliente con autenticación JWT para servicios que lo requieran
     */
    public static Retrofit getAuthenticatedClient(Context context) {
        if (retrofit == null) {
            // Configurar interceptor para logging detallado
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configurar cliente OkHttp con autenticación
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(createAuthInterceptor(context))
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
    
    /**
     * Crea un interceptor de autenticación JWT
     */
    private static Interceptor createAuthInterceptor(Context context) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                
                // No añadir token para login y registro
                if (original.url().encodedPath().contains("/auth/")) {
                    return chain.proceed(original);
                }
                
                // Añadir token para otras peticiones
                SessionManager sessionManager = new SessionManager(context);
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
        };
    }
    
    /**
     * Obtiene el servicio principal de API
     */
    public static ApiService getApiService(Context context) {
        return getAuthenticatedClient(context).create(ApiService.class);
    }
    
    /**
     * Obtiene el servicio de sensores
     */
    public static com.example.frontend.services.SensorApiService getSensorApiService(Context context) {
        return getAuthenticatedClient(context).create(com.example.frontend.services.SensorApiService.class);
    }
    
    /**
     * Obtiene el servicio de métricas de farmer
     */
    public static com.example.frontend.services.FarmerMetricsApiService getFarmerMetricsApiService(Context context) {
        return getAuthenticatedClient(context).create(com.example.frontend.services.FarmerMetricsApiService.class);
    }
    
    /**
     * Obtiene el servicio de trazabilidad
     */
    public static com.example.frontend.services.TraceabilityApiService getTraceabilityApiService(Context context) {
        return getAuthenticatedClient(context).create(com.example.frontend.services.TraceabilityApiService.class);
    }
}