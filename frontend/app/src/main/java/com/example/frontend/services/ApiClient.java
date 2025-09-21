package com.example.frontend.services;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.frontend.utils.Constants;
import com.example.frontend.utils.SessionManager;
import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static Context context = null;
    
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
    
    public static void init(Context ctx) {
        context = ctx;
    }
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            
            // Agregar interceptor de autenticación si tenemos contexto
            if (context != null) {
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        
                        android.util.Log.d("SensorApiClient", "Request URL: " + original.url());
                        android.util.Log.d("SensorApiClient", "Request path: " + original.url().encodedPath());
                        android.util.Log.d("SensorApiClient", "Request method: " + original.method());
                        
                        // No añadir token para endpoints públicos
                        if (original.url().encodedPath().contains("/public")) {
                            android.util.Log.d("SensorApiClient", "Using public endpoint, no auth needed");
                            return chain.proceed(original);
                        }
                        
                        // Añadir token para otras peticiones
                        SessionManager sessionManager = new SessionManager(context);
                        String token = sessionManager.getFullToken();
                        boolean isLoggedIn = sessionManager.isLoggedIn();
                        
                        android.util.Log.d("SensorApiClient", "Is logged in: " + isLoggedIn);
                        android.util.Log.d("SensorApiClient", "Token available: " + (token != null));
                        
                        if (token != null) {
                            android.util.Log.d("SensorApiClient", "Adding Authorization header: " + token);
                            Request request = original.newBuilder()
                                    .header("Authorization", token)
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        } else {
                            android.util.Log.w("SensorApiClient", "No token available, proceeding without auth");
                        }
                        
                        return chain.proceed(original);
                    }
                });
            }
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrlSafely())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
    
    public static SensorApiService getSensorApiService() {
        return getClient().create(SensorApiService.class);
    }
    
    public static FarmerMetricsApiService getFarmerMetricsApiService() {
        return getClient().create(FarmerMetricsApiService.class);
    }
}
