package com.example.frontend.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.Window;
import android.view.WindowInsetsController;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.example.frontend.R;

public class PaletteUtils {

    public interface PaletteCallback {
        void onApplied(int primary, int primaryVariant, int secondary);
        void onError(Throwable t);
    }

    public static void applyPaletteFromImage(@NonNull Activity activity, @DrawableRes int drawableRes, @NonNull PaletteCallback callback) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), drawableRes);
            if (bitmap == null) {
                callback.onError(new IllegalStateException("Bitmap is null"));
                return;
            }
            Palette.from(bitmap).generate(palette -> {
                try {
                    if (palette == null) {
                        callback.onError(new IllegalStateException("Palette is null"));
                        return;
                    }
                    int defaultPrimary = ContextCompat.getColor(activity, R.color.colorPrimary);
                    int defaultVariant = ContextCompat.getColor(activity, R.color.colorPrimaryVariant);
                    int defaultSecondary = ContextCompat.getColor(activity, R.color.colorSecondary);

                    int primary = palette.getDominantColor(defaultPrimary);
                    int primaryVariant = palette.getDarkVibrantColor(palette.getMutedColor(defaultVariant));
                    int secondary = palette.getVibrantColor(palette.getLightVibrantColor(defaultSecondary));

                    applySystemBars(activity, primary, primaryVariant);

                    callback.onApplied(primary, primaryVariant, secondary);
                } catch (Exception e) {
                    callback.onError(e);
                }
            });
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    private static void applySystemBars(Activity activity, int primary, int primaryVariant) {
        Window window = activity.getWindow();
        window.setStatusBarColor(primaryVariant);
        window.setNavigationBarColor(primary);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                // Ajuste de iconos claros/oscuro según luminosidad del color principal
                boolean lightIcons = isColorDark(primaryVariant);
                controller.setSystemBarsAppearance(
                        lightIcons ? 0 : WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        }
    }

    private static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * ((color >> 16) & 0xFF) + 0.587 * ((color >> 8) & 0xFF) + 0.114 * (color & 0xFF)) / 255;
        return darkness >= 0.5;
    }
}
