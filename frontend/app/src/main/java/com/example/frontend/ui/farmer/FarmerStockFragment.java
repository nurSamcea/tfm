package com.example.frontend.ui.farmer;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.ui.adapters.FarmerStockAdapter;
import com.example.frontend.api.ApiService;
import com.example.frontend.network.ApiClient;
import com.example.frontend.utils.SessionManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FarmerStockFragment extends Fragment implements FarmerStockAdapter.OnProductActionListener {
    private static final String TAG = "FarmerStockFragment";

    private RecyclerView recyclerStock;
    private FarmerStockAdapter stockAdapter;
    private List<Product> stockList;
    private ImageButton addProductButton;
    private Uri selectedImageUri = null;
    private SessionManager sessionManager;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando la aplicación");
        View view = inflater.inflate(R.layout.fragment_farmer_stock, container, false);

        sessionManager = new SessionManager(requireContext());
        addProductButton = view.findViewById(R.id.add_product_button);
        recyclerStock = view.findViewById(R.id.recycler_stock);

        // Inicializar lista y adapter
        stockList = new ArrayList<>();
        stockAdapter = new FarmerStockAdapter(stockList);
        stockAdapter.setOnProductActionListener(this);
        recyclerStock.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerStock.setAdapter(stockAdapter);

        // Configurar botón de añadir
        addProductButton.setOnClickListener(v -> openAddProductDialog());

        // Inicializar el launcher para seleccionar imagen
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Actualizar la vista previa de la imagen en el diálogo
                    if (selectedImageUri != null) {
                        // Buscar el diálogo actual y actualizar la imagen
                        View dialogView = getView();
                        if (dialogView != null) {
                            ImageView imagePreview = dialogView.findViewById(R.id.image_preview);
                            if (imagePreview != null) {
                                imagePreview.setImageURI(selectedImageUri);
                            }
                        }
                    }
                }
            }
        );

        // Cargar productos del farmer
        loadFarmerProducts();

        return view;
    }

    private void loadFarmerProducts() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        int farmerId = sessionManager.getUserId();
        
        Call<List<Product>> call = api.getFarmerProducts(farmerId);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stockList.clear();
                    stockList.addAll(response.body());
                    stockAdapter.updateProducts(stockList);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar productos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddProductDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        EditText inputName = dialogView.findViewById(R.id.input_name);
        EditText inputDesc = dialogView.findViewById(R.id.input_description);
        EditText inputPrice = dialogView.findViewById(R.id.input_price);
        EditText inputStock = dialogView.findViewById(R.id.input_stock);
        EditText inputCategory = dialogView.findViewById(R.id.input_category);
        EditText inputUnit = dialogView.findViewById(R.id.input_unit);
        EditText inputExpiration = dialogView.findViewById(R.id.input_expiration);
        CheckBox checkEco = dialogView.findViewById(R.id.check_eco);
        ImageView imagePreview = dialogView.findViewById(R.id.image_preview);
        Button btnPick = dialogView.findViewById(R.id.btn_pick_image);

        btnPick.setOnClickListener(v -> pickImage());

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Nuevo producto")
                .setView(dialogView)
                .setPositiveButton("Publicar", (d, which) -> {
                    publishProduct(
                            inputName.getText().toString(),
                            inputDesc.getText().toString(),
                            inputPrice.getText().toString(),
                            inputStock.getText().toString(),
                            inputCategory.getText().toString(),
                            inputUnit.getText().toString(),
                            inputExpiration.getText().toString(),
                            checkEco.isChecked()
                    );
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Seleccionar imagen"));
    }

    /**
     * Valida que la fecha tenga el formato YYYY-MM-DD
     */
    private boolean isValidDateFormat(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return true; // Fecha vacía es válida
        }
        
        // Patrón para YYYY-MM-DD
        String pattern = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!dateStr.matches(pattern)) {
            return false;
        }
        
        try {
            // Verificar que la fecha sea válida
            java.time.LocalDate.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    private void publishProduct(String name, String desc, String priceStr, String stockStr,
                                String category, String unit, String expiration, boolean isEco) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        int providerId = sessionManager.getUserId();

        // Validar que el nombre no esté vacío
        if (name.trim().isEmpty()) {
            Toast.makeText(getContext(), "El nombre del producto es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar formato de fecha si se proporciona
        String validatedExpiration = null;
        if (expiration != null && !expiration.trim().isEmpty()) {
            if (!isValidDateFormat(expiration.trim())) {
                Toast.makeText(getContext(), "Formato de fecha inválido. Use YYYY-MM-DD o deje vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            validatedExpiration = expiration.trim();
        }

        RequestBody rbName = RequestBody.create(MediaType.parse("text/plain"), name.trim());
        RequestBody rbProvider = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(providerId));
        RequestBody rbDesc = RequestBody.create(MediaType.parse("text/plain"), desc == null ? "" : desc.trim());
        RequestBody rbPrice = RequestBody.create(MediaType.parse("text/plain"), priceStr == null ? "" : priceStr.trim());
        RequestBody rbCurrency = RequestBody.create(MediaType.parse("text/plain"), "EUR");
        RequestBody rbUnit = RequestBody.create(MediaType.parse("text/plain"), unit == null ? "" : unit.trim());
        RequestBody rbCategory = RequestBody.create(MediaType.parse("text/plain"), category == null ? "" : category.trim());
        RequestBody rbStock = RequestBody.create(MediaType.parse("text/plain"), stockStr == null ? "" : stockStr.trim());
        RequestBody rbExp = RequestBody.create(MediaType.parse("text/plain"), validatedExpiration != null ? validatedExpiration : "");
        RequestBody rbEco = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(isEco));

        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            try {
                String fileName = "product_" + System.currentTimeMillis() + ".jpg";
                java.io.InputStream is = requireContext().getContentResolver().openInputStream(selectedImageUri);
                java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                byte[] data = new byte[4096];
                int nRead;
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] bytes = buffer.toByteArray();
                RequestBody req = RequestBody.create(MediaType.parse("image/*"), bytes);
                imagePart = MultipartBody.Part.createFormData("image", fileName, req);
            } catch (Exception e) {
                imagePart = null;
            }
        }

        Call<Product> call = api.createProductWithImage(
                rbName, rbProvider, rbDesc, rbPrice, rbCurrency, rbUnit, rbCategory, rbStock, rbExp, rbEco, imagePart
        );
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                    loadFarmerProducts(); // Recargar lista
                } else {
                    Toast.makeText(getContext(), "Error al crear producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onToggleHidden(Product product) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = api.toggleProductHidden(Integer.parseInt(product.getId()));
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Producto " + (product.isHidden() ? "oculto" : "visible"), Toast.LENGTH_SHORT).show();
                    loadFarmerProducts(); // Recargar lista
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cambiar visibilidad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDelete(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar este producto?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    Call<Void> call = api.deleteProduct(Integer.parseInt(product.getId()));
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
                                loadFarmerProducts(); // Recargar lista
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error al eliminar producto", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
