package com.example.frontend.network;

import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import java.io.IOException;

public class DebugInterceptor implements Interceptor {
    private static final String TAG = "DebugInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        // Log de la petición
        Log.d(TAG, "=== REQUEST ===");
        Log.d(TAG, "URL: " + request.url());
        Log.d(TAG, "Method: " + request.method());
        Log.d(TAG, "Headers: " + request.headers());
        
        if (request.body() != null) {
            Log.d(TAG, "Request Body: " + request.body().toString());
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            Response response = chain.proceed(request);
            long endTime = System.currentTimeMillis();
            
            // Log de la respuesta
            Log.d(TAG, "=== RESPONSE ===");
            Log.d(TAG, "Status Code: " + response.code());
            Log.d(TAG, "Response Time: " + (endTime - startTime) + "ms");
            Log.d(TAG, "Headers: " + response.headers());
            
            // Log del cuerpo de la respuesta
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseString = responseBody.string();
                Log.d(TAG, "Response Body: " + responseString);
                
                // Crear una nueva respuesta con el cuerpo leído
                ResponseBody newResponseBody = ResponseBody.create(
                    responseBody.contentType(),
                    responseString
                );
                return response.newBuilder().body(newResponseBody).build();
            }
            
            return response;
            
        } catch (IOException e) {
            long endTime = System.currentTimeMillis();
            Log.e(TAG, "=== CONNECTION ERROR ===");
            Log.e(TAG, "Error: " + e.getMessage());
            Log.e(TAG, "Response Time: " + (endTime - startTime) + "ms");
            Log.e(TAG, "URL: " + request.url());
            throw e;
        }
    }
}
