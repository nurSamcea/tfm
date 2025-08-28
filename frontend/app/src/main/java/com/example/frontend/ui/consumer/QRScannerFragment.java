package com.example.frontend.ui.consumer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.example.frontend.models.ProductTraceability;
import com.example.frontend.network.ApiClient;
import com.example.frontend.network.ApiService;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import com.google.zxing.ResultPoint;

public class QRScannerFragment extends Fragment {

    private DecoratedBarcodeView barcodeView;
    private TextView traceabilityInfo;
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qr_scanner, container, false);

        barcodeView = root.findViewById(R.id.barcode_scanner);
        traceabilityInfo = root.findViewById(R.id.text_traceability_info);

        setupScanner();
        checkCameraPermission();

        return root;
    }

    private void setupScanner() {
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    handleQRCode(result.getText());
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Opcional: mostrar puntos de resultado
            }
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {
            startScanner();
        }
    }

    private void startScanner() {
        barcodeView.resume();
    }

    private void handleQRCode(String qrData) {
        // Pausar el scanner para evitar múltiples lecturas
        barcodeView.pause();
        
        // Mostrar que se está procesando
        traceabilityInfo.setText("Procesando código QR...");
        
        // Extraer el hash del QR (asumiendo formato: "product_hash_12345")
        String productHash = extractProductHash(qrData);
        
        if (productHash != null) {
            fetchProductTraceability(productHash);
        } else {
            traceabilityInfo.setText("Código QR no válido");
            Toast.makeText(getContext(), "Código QR no reconocido", Toast.LENGTH_SHORT).show();
            // Reanudar scanner después de un delay
            barcodeView.postDelayed(() -> barcodeView.resume(), 2000);
        }
    }

    private String extractProductHash(String qrData) {
        // Implementar lógica para extraer el hash del producto del QR
        // Por ahora asumimos que el QR contiene directamente el hash
        if (qrData.startsWith("product_")) {
            return qrData;
        }
        return null;
    }

    private void fetchProductTraceability(String productHash) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ProductTraceability> call = apiService.getProductTraceability(productHash);

        call.enqueue(new Callback<ProductTraceability>() {
            @Override
            public void onResponse(Call<ProductTraceability> call, Response<ProductTraceability> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayTraceabilityInfo(response.body());
                } else {
                    traceabilityInfo.setText("No se pudo obtener la información de trazabilidad");
                    Toast.makeText(getContext(), "Error al obtener trazabilidad", Toast.LENGTH_SHORT).show();
                }
                // Reanudar scanner
                barcodeView.postDelayed(() -> barcodeView.resume(), 3000);
            }

            @Override
            public void onFailure(Call<ProductTraceability> call, Throwable t) {
                traceabilityInfo.setText("Error de conexión");
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Reanudar scanner
                barcodeView.postDelayed(() -> barcodeView.resume(), 3000);
            }
        });
    }

    private void displayTraceabilityInfo(ProductTraceability traceability) {
        StringBuilder info = new StringBuilder();
        info.append("🌱 ").append(traceability.getProductName()).append("\n\n");
        info.append("📍 Origen: ").append(traceability.getOriginLocation()).append("\n");
        info.append("👨‍🌾 Productor: ").append(traceability.getProducerName()).append("\n");
        info.append("🌡️ Temperatura actual: ").append(traceability.getCurrentTemperature()).append("\n");
        info.append("💧 Humedad: ").append(traceability.getCurrentHumidity()).append("\n");
        
        if (traceability.isEcoCertified()) {
            info.append("✅ Certificación ecológica\n");
        }
        if (traceability.isLocalProduct()) {
            info.append("🏠 Producto local\n");
        }
        
        info.append("\n📋 Historial de trazabilidad:\n");
        List<String> events = traceability.getTraceabilityEventsAsStrings();
        if (events != null) {
            for (String event : events) {
                info.append("• ").append(event).append("\n");
            }
        } else {
            info.append("• No hay eventos registrados\n");
        }

        traceabilityInfo.setText(info.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner();
            } else {
                Toast.makeText(getContext(), "Se requiere permiso de cámara para escanear QR", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
