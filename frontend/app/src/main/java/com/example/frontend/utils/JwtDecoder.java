package com.example.frontend.utils;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JwtDecoder {
    private static final String TAG = "JwtDecoder";

    public static class JwtPayload {
        public int userId;
        public String email;
        public String role;
        public String name;
        public long exp;
    }

    public static JwtPayload decode(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                Log.e(TAG, "Invalid JWT token format");
                return null;
            }

            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes, "UTF-8");

            JSONObject jsonPayload = new JSONObject(decodedPayload);
            JwtPayload jwtPayload = new JwtPayload();

            // Extraer información del payload
            if (jsonPayload.has("sub")) {
                jwtPayload.userId = Integer.parseInt(jsonPayload.getString("sub"));
            }
            if (jsonPayload.has("email")) {
                jwtPayload.email = jsonPayload.getString("email");
            }
            if (jsonPayload.has("role")) {
                jwtPayload.role = jsonPayload.getString("role");
            }
            if (jsonPayload.has("name")) {
                jwtPayload.name = jsonPayload.getString("name");
            }
            if (jsonPayload.has("exp")) {
                jwtPayload.exp = jsonPayload.getLong("exp");
            }

            return jwtPayload;

        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error decoding JWT payload", e);
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JWT payload", e);
            return null;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing user ID from JWT", e);
            return null;
        }
    }

    public static boolean isTokenExpired(String token) {
        JwtPayload payload = decode(token);
        if (payload == null) {
            return true;
        }

        // Convertir timestamp de expiración a milisegundos
        long expirationTime = payload.exp * 1000;
        long currentTime = System.currentTimeMillis();

        return currentTime >= expirationTime;
    }

    public static int getUserIdFromToken(String token) {
        JwtPayload payload = decode(token);
        return payload != null ? payload.userId : -1;
    }

    public static String getRoleFromToken(String token) {
        JwtPayload payload = decode(token);
        return payload != null ? payload.role : null;
    }

    public static String getNameFromToken(String token) {
        JwtPayload payload = decode(token);
        return payload != null ? payload.name : null;
    }
}
