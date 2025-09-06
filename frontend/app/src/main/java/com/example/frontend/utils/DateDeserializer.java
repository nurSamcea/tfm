package com.example.frontend.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateDeserializer implements JsonDeserializer<Date> {
    private static final String TAG = "DateDeserializer";
    
    // Formatos de fecha que puede recibir el backend
    private static final String[] DATE_FORMATS = {
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",  // 2025-09-06T21:35:55.818556
        "yyyy-MM-dd'T'HH:mm:ss.SSS",     // 2025-09-06T21:35:55.818
        "yyyy-MM-dd'T'HH:mm:ss",         // 2025-09-06T21:35:55
        "yyyy-MM-dd HH:mm:ss",           // 2025-09-06 21:35:55
        "yyyy-MM-dd"                     // 2025-09-06
    };

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateString = json.getAsString();
        
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        // Intentar parsear con cada formato
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return sdf.parse(dateString);
            } catch (ParseException e) {
                // Continuar con el siguiente formato
            }
        }
        
        // Si ningún formato funciona, log del error y retornar null
        android.util.Log.e(TAG, "No se pudo parsear la fecha: " + dateString);
        return null;
    }
}
