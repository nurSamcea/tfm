package com.example.frontend.ui.farmer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FarmerStockFragment extends Fragment implements FarmerStockAdapter.OnProductActionListener {
    private static final String TAG = "FarmerStockFragment";

    private RecyclerView recyclerStock;
    private FarmerStockAdapter stockAdapter;
    private List<Product> stockList;
    private ImageButton addProductButton;
    private Uri selectedImageUri = null;
    private String selectedCategory = "";
    private String selectedExpirationDate = "";
    private Spinner categorySpinner;
    private ArrayAdapter<String> categoryAdapter;
    private SessionManager sessionManager;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView currentImagePreview = null;
    private com.google.android.material.button.MaterialButton currentBtnRemoveImage = null;

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
                    // Actualizar preview si hay un diálogo abierto
                    updateCurrentImagePreview();
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
        
        // Establecer el título del diálogo
        android.widget.TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText("Nuevo Producto");
        
        // Obtener referencias a los campos del formulario
        com.google.android.material.textfield.TextInputEditText inputName = dialogView.findViewById(R.id.input_name);
        com.google.android.material.textfield.TextInputEditText inputDesc = dialogView.findViewById(R.id.input_description);
        com.google.android.material.textfield.TextInputEditText inputPrice = dialogView.findViewById(R.id.input_price);
        com.google.android.material.textfield.TextInputEditText inputStock = dialogView.findViewById(R.id.input_stock);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);
        com.google.android.material.textfield.TextInputEditText inputUnit = dialogView.findViewById(R.id.input_unit);
        android.widget.TextView textExpirationDate = dialogView.findViewById(R.id.text_expiration_date);
        com.google.android.material.button.MaterialButton btnPickDate = dialogView.findViewById(R.id.btn_pick_date);
        CheckBox checkEco = dialogView.findViewById(R.id.check_eco);
        com.google.android.material.button.MaterialButton btnPick = dialogView.findViewById(R.id.btn_pick_image);
        ImageView imagePreview = dialogView.findViewById(R.id.image_preview);
        com.google.android.material.button.MaterialButton btnRemoveImage = dialogView.findViewById(R.id.btn_remove_image);

        // Configurar spinner de categorías
        setupCategorySpinner(categorySpinner);
        
        // Configurar DatePicker
        btnPickDate.setOnClickListener(v -> showDatePickerDialog(textExpirationDate));
        
        // Establecer referencias actuales para el preview
        currentImagePreview = imagePreview;
        currentBtnRemoveImage = btnRemoveImage;
        
        btnPick.setOnClickListener(v -> pickImage());
        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            updateImagePreview(imagePreview, btnRemoveImage, null);
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Publicar", (d, which) -> {
                    publishProduct(
                            inputName.getText().toString(),
                            inputDesc.getText().toString(),
                            inputPrice.getText().toString(),
                            inputStock.getText().toString(),
                            selectedCategory,
                            inputUnit.getText().toString(),
                            selectedExpirationDate,
                            checkEco.isChecked()
                    );
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }

    private void openEditProductDialog(Product product) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        
        // Establecer el título del diálogo
        android.widget.TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText("Editar Producto");
        
        // Obtener referencias a los campos del formulario
        com.google.android.material.textfield.TextInputEditText inputName = dialogView.findViewById(R.id.input_name);
        com.google.android.material.textfield.TextInputEditText inputDesc = dialogView.findViewById(R.id.input_description);
        com.google.android.material.textfield.TextInputEditText inputPrice = dialogView.findViewById(R.id.input_price);
        com.google.android.material.textfield.TextInputEditText inputStock = dialogView.findViewById(R.id.input_stock);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);
        com.google.android.material.textfield.TextInputEditText inputUnit = dialogView.findViewById(R.id.input_unit);
        android.widget.TextView textExpirationDate = dialogView.findViewById(R.id.text_expiration_date);
        com.google.android.material.button.MaterialButton btnPickDate = dialogView.findViewById(R.id.btn_pick_date);
        CheckBox checkEco = dialogView.findViewById(R.id.check_eco);
        com.google.android.material.button.MaterialButton btnPick = dialogView.findViewById(R.id.btn_pick_image);
        ImageView imagePreview = dialogView.findViewById(R.id.image_preview);
        com.google.android.material.button.MaterialButton btnRemoveImage = dialogView.findViewById(R.id.btn_remove_image);
        
        // Rellenar campos con datos actuales del producto
        inputName.setText(product.getName());
        inputDesc.setText(product.getDescription() != null ? product.getDescription() : "");
        inputPrice.setText(String.valueOf(product.getPrice()));
        
        // Usar stock_available si está disponible, sino usar stock
        double stockValue = 0.0;
        if (product.getStockAvailable() != null) {
            stockValue = product.getStockAvailable();
        } else {
            stockValue = product.getStock();
        }
        inputStock.setText(String.valueOf(stockValue));
        
        // Configurar spinner de categorías y seleccionar la categoría actual
        setupCategorySpinner(categorySpinner);
        if (product.getCategory() != null && !product.getCategory().isEmpty()) {
            int categoryPosition = categoryAdapter.getPosition(product.getCategory());
            if (categoryPosition > 0) {
                categorySpinner.setSelection(categoryPosition);
                selectedCategory = product.getCategory();
            }
        }
        
        inputUnit.setText(""); // El modelo Product no tiene campo unit
        
        // Variable local para la fecha de este diálogo específico
        String[] localExpirationDate = {""};
        
        // Formatear fecha de expiración - usar expiration_date si está disponible
        if (product.getExpirationDate() != null) {
            java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.text.SimpleDateFormat backendFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            textExpirationDate.setText(displayFormat.format(product.getExpirationDate()));
            textExpirationDate.setTextColor(getContext().getColor(R.color.text_primary));
            localExpirationDate[0] = backendFormat.format(product.getExpirationDate());
        } else if (product.getHarvestDate() != null) {
            java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.text.SimpleDateFormat backendFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            textExpirationDate.setText(displayFormat.format(product.getHarvestDate()));
            textExpirationDate.setTextColor(getContext().getColor(R.color.text_primary));
            localExpirationDate[0] = backendFormat.format(product.getHarvestDate());
        } else {
            localExpirationDate[0] = "";
        }
        
        // Configurar DatePicker y mostrar fecha actual si existe
        btnPickDate.setOnClickListener(v -> showDatePickerDialogForEdit(textExpirationDate, localExpirationDate));
        
        // Usar isEco si está disponible, sino usar isSustainable
        boolean ecoValue = false;
        if (product.getIsEco() != null) {
            ecoValue = product.getIsEco();
        } else {
            ecoValue = product.isSustainable();
        }
        checkEco.setChecked(ecoValue);

        // Mostrar imagen actual del producto si existe
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // Construir URL completa si es relativa
            String imageUrl = product.getImageUrl();
            if (imageUrl.startsWith("/")) {
                // Si es una ruta relativa, construir URL completa usando la URL base del servidor
                String baseUrl = ApiClient.getBaseUrl();
                // Remover la barra final de la URL base si existe
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                }
                imageUrl = baseUrl + imageUrl;
            }
            
            // Cargar imagen actual del producto usando Glide
            Glide.with(getContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_product_placeholder)
                .error(R.drawable.ic_product_placeholder)
                .into(imagePreview);
            
            imagePreview.setVisibility(View.VISIBLE);
            btnRemoveImage.setVisibility(View.VISIBLE);
        } else {
            imagePreview.setVisibility(View.GONE);
            btnRemoveImage.setVisibility(View.GONE);
        }

        // Establecer referencias actuales para el preview
        currentImagePreview = imagePreview;
        currentBtnRemoveImage = btnRemoveImage;
        
        btnPick.setOnClickListener(v -> pickImage());
        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            updateImagePreview(imagePreview, btnRemoveImage, null);
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", (d, which) -> {
                    updateProduct(product,
                            inputName.getText().toString(),
                            inputDesc.getText().toString(),
                            inputPrice.getText().toString(),
                            inputStock.getText().toString(),
                            selectedCategory,
                            inputUnit.getText().toString(),
                            localExpirationDate[0],
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

    private void updateImagePreview(ImageView imagePreview, com.google.android.material.button.MaterialButton btnRemoveImage, Uri imageUri) {
        if (imageUri != null) {
            imagePreview.setImageURI(imageUri);
            imagePreview.setVisibility(View.VISIBLE);
            btnRemoveImage.setVisibility(View.VISIBLE);
        } else {
            imagePreview.setVisibility(View.GONE);
            btnRemoveImage.setVisibility(View.GONE);
        }
    }

    private void updateCurrentImagePreview() {
        // Actualizar el preview de imagen actual si hay referencias disponibles
        if (selectedImageUri != null && currentImagePreview != null && currentBtnRemoveImage != null) {
            updateImagePreview(currentImagePreview, currentBtnRemoveImage, selectedImageUri);
        }
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

    private void updateProduct(Product originalProduct, String name, String desc, String priceStr, String stockStr,
                              String category, String unit, String expiration, boolean isEco) {
        ApiService api = ApiClient.getClient().create(ApiService.class);

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

        // Crear objeto ProductUpdate solo con los campos que tienen valores
        com.example.frontend.model.ProductUpdate productUpdate = new com.example.frontend.model.ProductUpdate();
        
        // Solo establecer campos que no estén vacíos
        if (name != null && !name.trim().isEmpty()) {
            productUpdate.setName(name.trim());
        }
        if (desc != null && !desc.trim().isEmpty()) {
            productUpdate.setDescription(desc.trim());
        }
        if (priceStr != null && !priceStr.trim().isEmpty()) {
            productUpdate.setPrice(Double.parseDouble(priceStr.trim()));
        }
        if (stockStr != null && !stockStr.trim().isEmpty()) {
            productUpdate.setStockAvailable(Double.parseDouble(stockStr.trim()));
        }
        if (category != null && !category.trim().isEmpty()) {
            productUpdate.setCategory(category.trim());
        }
        if (unit != null && !unit.trim().isEmpty()) {
            productUpdate.setUnit(unit.trim());
        }
        productUpdate.setIsEco(isEco);
        
        // Manejar fecha de expiración
        if (validatedExpiration != null && !validatedExpiration.isEmpty()) {
            productUpdate.setExpirationDateString(validatedExpiration);
        } else {
            productUpdate.setExpirationDateString(null);
        }
        

        // Actualizar producto usando el endpoint normal
        Call<Product> call = api.updateProduct(Integer.parseInt(originalProduct.getId()), productUpdate);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    // Si se seleccionó una nueva imagen, actualizarla también
                    if (selectedImageUri != null) {
                        updateProductImage(Integer.parseInt(originalProduct.getId()));
                    } else {
                        Toast.makeText(getContext(), "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        loadFarmerProducts(); // Recargar lista para actualizar la UI
                    }
                } else {
                    Toast.makeText(getContext(), "Error al actualizar producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductImage(int productId) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        
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
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", fileName, req);

                Call<Product> call = api.updateProductImage(productId, imagePart);
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Producto e imagen actualizados exitosamente", Toast.LENGTH_SHORT).show();
                            loadFarmerProducts(); // Recargar lista para actualizar la UI
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar imagen del producto", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de red al actualizar imagen: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onToggleHidden(Product product) {
        // Mostrar diálogo de confirmación más claro
        String currentStatus = product.isHidden() ? "oculto" : "visible";
        String newStatus = product.isHidden() ? "visible" : "oculto";
        String action = product.isHidden() ? "mostrar" : "ocultar";
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Cambiar visibilidad del producto")
                .setMessage(String.format("¿Quieres %s este producto?\n\n" +
                        "• Estado actual: %s para usuarios\n" +
                        "• Nuevo estado: %s para usuarios\n\n" +
                        "Los usuarios %s podrán ver y comprar este producto.",
                        action, currentStatus, newStatus, 
                        product.isHidden() ? "SÍ" : "NO"))
                .setPositiveButton("Sí, " + action, (dialog, which) -> {
                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    Call<Map<String, Object>> call = api.toggleProductHidden(Integer.parseInt(product.getId()));
                    call.enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            if (response.isSuccessful()) {
                                String message = product.isHidden() ? 
                                    "Producto VISIBLE para usuarios" :
                                    "Producto OCULTO para usuarios";
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                loadFarmerProducts(); // Recargar lista
                            } else {
                                Toast.makeText(getContext(), "❌ Error al cambiar visibilidad", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            Toast.makeText(getContext(), "❌ Error de conexión al cambiar visibilidad", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onEdit(Product product) {
        openEditProductDialog(product);
    }

    @Override
    public void onDelete(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar este producto?\n\nEsta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    Call<Void> call = api.deleteProduct(Integer.parseInt(product.getId()));
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
                                loadFarmerProducts(); // Recargar lista
                            } else {
                                Toast.makeText(getContext(), "Error al eliminar producto", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error de conexión al eliminar producto", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupCategorySpinner(Spinner spinner) {
        // Lista de categorías predefinidas
        List<String> categories = new ArrayList<>();
        categories.add("Seleccionar categoría");
        categories.add("verduras");
        categories.add("frutas");
        categories.add("cereales");
        categories.add("legumbres");
        categories.add("frutos_secos");
        categories.add("lacteos");
        categories.add("carnes");
        categories.add("pescados");
        categories.add("huevos");
        categories.add("hierbas");
        categories.add("especias");
        categories.add("otros");

        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categoryAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // No seleccionar el primer elemento que es solo texto
                    selectedCategory = categories.get(position);
                } else {
                    selectedCategory = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });
    }

    private void showDatePickerDialog(android.widget.TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    
                    // Formatear fecha para mostrar al usuario
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    textView.setText(displayFormat.format(selectedDate.getTime()));
                    textView.setTextColor(getContext().getColor(R.color.text_primary));
                    
                    // Guardar fecha en formato para el backend (YYYY-MM-DD)
                    SimpleDateFormat backendFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedExpirationDate = backendFormat.format(selectedDate.getTime());
                }, year, month, day);

        // Establecer fecha mínima como hoy
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showDatePickerDialogForEdit(android.widget.TextView textView, String[] localDateArray) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    
                    // Formatear fecha para mostrar al usuario
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    textView.setText(displayFormat.format(selectedDate.getTime()));
                    textView.setTextColor(getContext().getColor(R.color.text_primary));
                    
                    // Guardar fecha en formato para el backend (YYYY-MM-DD)
                    SimpleDateFormat backendFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    localDateArray[0] = backendFormat.format(selectedDate.getTime());
                }, year, month, day);

        // Establecer fecha mínima como hoy
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

}
