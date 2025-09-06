package com.example.frontend.ui.supermarket;

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
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.model.InventoryItem;
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

public class SupermarketStockFragment extends Fragment implements FarmerStockAdapter.OnProductActionListener {
    private static final String TAG = "SupermarketStockFragment";

    private RecyclerView recyclerView;
    private FarmerStockAdapter adapter;
    private List<Product> productList;
    private EditText searchEditText;
    private ImageButton filterButton;
    private com.google.android.material.floatingactionbutton.FloatingActionButton addProductButton;
    private SessionManager sessionManager;
    private ApiService apiService;
    
    // Variables para crear/editar productos
    private Uri selectedImageUri = null;
    private String selectedCategory = "";
    private String selectedExpirationDate = "";
    private Spinner categorySpinner;
    private ArrayAdapter<String> categoryAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView currentImagePreview = null;
    private com.google.android.material.button.MaterialButton currentBtnRemoveImage = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando SupermarketStockFragment");
        View view = inflater.inflate(R.layout.fragment_farmer_stock, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupSearchAndFilters();
        loadSupermarketStock();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_stock);
        searchEditText = view.findViewById(R.id.search_stock);
        filterButton = view.findViewById(R.id.filter_button);
        addProductButton = view.findViewById(R.id.add_product_button);
        
        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getClient().create(ApiService.class);
        
        productList = new ArrayList<>();
        
        // Configurar botón de añadir producto
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
    }

    private void setupRecyclerView() {
        adapter = new FarmerStockAdapter(productList);
        adapter.setOnProductActionListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchAndFilters() {
        searchEditText.setHint("Buscar en stock...");
        
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchEditText.setHint("");
            } else {
                searchEditText.setHint("Buscar en stock...");
            }
        });

        filterButton.setOnClickListener(v -> {
            // Implementar filtros para el stock del supermercado
            Toast.makeText(getContext(), "Filtros de stock (no implementado)", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSupermarketStock() {
        Log.d(TAG, "loadSupermarketStock: Cargando productos del supermercado");
        
        int supermarketId = sessionManager.getUserId();
        if (supermarketId == -1) {
            Log.e(TAG, "loadSupermarketStock: No hay usuario logueado");
            Toast.makeText(getContext(), "Error: Usuario no logueado", Toast.LENGTH_SHORT).show();
            loadMockStock(); // Cargar datos de ejemplo como fallback
            return;
        }
        
        Log.d(TAG, "loadSupermarketStock: Cargando productos para supermercado ID: " + supermarketId);
        
        // Usar el mismo endpoint que farmer pero con el ID del supermercado
        Call<List<Product>> call = apiService.getFarmerProducts(supermarketId);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "loadSupermarketStock: Productos cargados exitosamente");
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.updateProducts(productList);
                    Log.d(TAG, "loadSupermarketStock: " + productList.size() + " productos cargados");
                } else {
                    Log.e(TAG, "loadSupermarketStock: Error en respuesta - " + response.code());
                    Toast.makeText(getContext(), "Error cargando productos: " + response.code(), Toast.LENGTH_SHORT).show();
                    loadMockStock(); // Cargar datos de ejemplo como fallback
                }
            }
            
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "loadSupermarketStock: Error en llamada API", t);
                Toast.makeText(getContext(), "Error de conexión. Cargando datos de ejemplo...", Toast.LENGTH_SHORT).show();
                loadMockStock(); // Cargar datos de ejemplo como fallback
            }
        });
    }


    private void loadMockStock() {
        Log.d(TAG, "loadMockStock: Cargando datos de ejemplo");
        
        // Datos de ejemplo para el stock del supermercado
        productList.clear();
        
        Product product1 = new Product();
        product1.setId("1");
        product1.setName("Tomates Ecológicos");
        product1.setPrice(2.50);
        product1.setStock(150);
        product1.setCategory("Verduras");
        
        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Lechuga Local");
        product2.setPrice(1.80);
        product2.setStock(80);
        product2.setCategory("Verduras");
        
        Product product3 = new Product();
        product3.setId("3");
        product3.setName("Zanahorias Orgánicas");
        product3.setPrice(3.20);
        product3.setStock(120);
        product3.setCategory("Verduras");
        
        Product product4 = new Product();
        product4.setId("4");
        product4.setName("Manzanas Rojas");
        product4.setPrice(2.80);
        product4.setStock(200);
        product4.setCategory("Frutas");
        
        Product product5 = new Product();
        product5.setId("5");
        product5.setName("Plátanos");
        product5.setPrice(1.50);
        product5.setStock(180);
        product5.setCategory("Frutas");
        
        productList.add(product1);
        productList.add(product2);
        productList.add(product3);
        productList.add(product4);
        productList.add(product5);
        
        adapter.updateProducts(productList);
        
        Log.d(TAG, "loadMockStock: " + productList.size() + " productos de ejemplo cargados");
    }

    // Implementación de OnProductActionListener
    @Override
    public void onEdit(Product product) {
        openEditProductDialog(product);
    }

    @Override
    public void onToggleHidden(Product product) {
        toggleProductVisibility(product);
    }

    @Override
    public void onDelete(Product product) {
        showDeleteConfirmationDialog(product);
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
        String[] categories = {"Verduras", "Frutas", "Carnes", "Lácteos", "Cereales", "Otros"};
        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        
        // Configurar selección de categoría
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });
        
        // Configurar selección de fecha
        btnPickDate.setOnClickListener(v -> showDatePickerDialog(textExpirationDate));
        
        // Configurar selección de imagen
        btnPick.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
            currentImagePreview = imagePreview;
            currentBtnRemoveImage = btnRemoveImage;
        });
        
        // Configurar botón de eliminar imagen
        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            imagePreview.setImageResource(android.R.color.transparent);
            btnRemoveImage.setVisibility(View.GONE);
        });
        
        // Crear y mostrar el diálogo
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Crear", (dialogInterface, which) -> {
                    // Validar campos
                    String name = inputName.getText().toString().trim();
                    String description = inputDesc.getText().toString().trim();
                    String priceStr = inputPrice.getText().toString().trim();
                    String stockStr = inputStock.getText().toString().trim();
                    String unit = inputUnit.getText().toString().trim();
                    
                    if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || selectedCategory.isEmpty()) {
                        Toast.makeText(getContext(), "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    try {
                        double price = Double.parseDouble(priceStr);
                        int stock = Integer.parseInt(stockStr);
                        
                        // Crear producto
                        createProduct(name, description, price, stock, unit, selectedCategory, selectedExpirationDate, checkEco.isChecked());
                        
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Por favor, ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show();
                    }
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
        
        // Pre-llenar campos con datos del producto
        inputName.setText(product.getName());
        inputDesc.setText(product.getDescription());
        inputPrice.setText(String.valueOf(product.getPrice()));
        inputStock.setText(String.valueOf(product.getStock()));
        inputUnit.setText(product.getCategory()); // Usar category como unit por ahora
        checkEco.setChecked(product.getIsEco() != null ? product.getIsEco() : false);
        
        // Configurar spinner de categorías
        String[] categories = {"Verduras", "Frutas", "Carnes", "Lácteos", "Cereales", "Otros"};
        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        
        // Seleccionar la categoría actual del producto
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(product.getCategory())) {
                categorySpinner.setSelection(i);
                selectedCategory = categories[i];
                break;
            }
        }
        
        // Configurar selección de categoría
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });
        
        // Configurar selección de fecha
        btnPickDate.setOnClickListener(v -> showDatePickerDialog(textExpirationDate));
        
        // Configurar selección de imagen
        btnPick.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
            currentImagePreview = imagePreview;
            currentBtnRemoveImage = btnRemoveImage;
        });
        
        // Configurar botón de eliminar imagen
        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            imagePreview.setImageResource(android.R.color.transparent);
            btnRemoveImage.setVisibility(View.GONE);
        });
        
        // Crear y mostrar el diálogo
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialogInterface, which) -> {
                    // Validar campos
                    String name = inputName.getText().toString().trim();
                    String description = inputDesc.getText().toString().trim();
                    String priceStr = inputPrice.getText().toString().trim();
                    String stockStr = inputStock.getText().toString().trim();
                    String unit = inputUnit.getText().toString().trim();
                    
                    if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || selectedCategory.isEmpty()) {
                        Toast.makeText(getContext(), "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    try {
                        double price = Double.parseDouble(priceStr);
                        int stock = Integer.parseInt(stockStr);
                        
                        // Actualizar producto
                        updateProductWithImage(product, name, description, price, stock, unit, selectedCategory, selectedExpirationDate, checkEco.isChecked());
                        
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Por favor, ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        
        dialog.show();
    }

    private void toggleProductVisibility(Product product) {
        // Llamar a la API para alternar la visibilidad del producto
        Call<Map<String, Object>> call = apiService.toggleProductHidden(Integer.parseInt(product.getId()));
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    String status = product.isHidden() ? "visible" : "oculto";
                    Toast.makeText(getContext(), "Producto " + status + " exitosamente", Toast.LENGTH_SHORT).show();
                    loadSupermarketStock(); // Recargar la lista
                } else {
                    Toast.makeText(getContext(), "Error al cambiar visibilidad: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(Product product) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Producto")
                .setMessage("¿Está seguro de que desea eliminar el producto '" + product.getName() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void createProduct(String name, String description, double price, int stock, String unit, String category, String expirationDate, boolean isEco) {
        int supermarketId = sessionManager.getUserId();
        
        if (selectedImageUri != null) {
            // Crear producto con imagen
            createProductWithImage(name, description, price, stock, unit, category, expirationDate, isEco, supermarketId);
        } else {
            // Crear producto sin imagen
            createProductWithoutImage(name, description, price, stock, unit, category, expirationDate, isEco, supermarketId);
        }
    }

    private void createProductWithImage(String name, String description, double price, int stock, String unit, String category, String expirationDate, boolean isEco, int supermarketId) {
        try {
            // Preparar los datos del formulario
            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody providerIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(supermarketId));
            RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
            RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price));
            RequestBody currencyBody = RequestBody.create(MediaType.parse("text/plain"), "EUR");
            RequestBody unitBody = RequestBody.create(MediaType.parse("text/plain"), unit);
            RequestBody categoryBody = RequestBody.create(MediaType.parse("text/plain"), category);
            RequestBody stockBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stock));
            RequestBody expirationBody = RequestBody.create(MediaType.parse("text/plain"), expirationDate);
            RequestBody isEcoBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(isEco));
            
            // Preparar la imagen
            byte[] imageBytes = null;
            try {
                java.io.InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                imageBytes = byteArrayOutputStream.toByteArray();
                inputStream.close();
                byteArrayOutputStream.close();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "product_image.jpg", imageRequestBody);
            
            // Llamar a la API
            Call<Product> call = apiService.createProductWithImage(
                    nameBody, providerIdBody, descriptionBody, priceBody, currencyBody,
                    unitBody, categoryBody, stockBody, expirationBody, isEcoBody, imagePart
            );
            
            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                        loadSupermarketStock(); // Recargar la lista
                    } else {
                        Toast.makeText(getContext(), "Error al crear producto: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createProductWithoutImage(String name, String description, double price, int stock, String unit, String category, String expirationDate, boolean isEco, int supermarketId) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        product.setIsEco(isEco);
        
        Call<Product> call = apiService.addProduct(String.valueOf(supermarketId), product);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                    loadSupermarketStock(); // Recargar la lista
                } else {
                    Toast.makeText(getContext(), "Error al crear producto: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductWithImage(Product product, String name, String description, double price, int stock, String unit, String category, String expirationDate, boolean isEco) {
        if (selectedImageUri != null) {
            // Si hay una nueva imagen, actualizar primero la imagen y luego los datos
            updateProductImage(product, name, description, price, stock, unit, category, expirationDate, isEco);
        } else {
            // Si no hay nueva imagen, solo actualizar los datos
            updateProduct(product, name, description, price, stock, unit, category, expirationDate, isEco);
        }
    }

    private void updateProductImage(Product product, String name, String description, double price, int stock, String unit, String category, String expirationDate, boolean isEco) {
        try {
            // Preparar la imagen
            byte[] imageBytes = null;
            try {
                java.io.InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                imageBytes = byteArrayOutputStream.toByteArray();
                inputStream.close();
                byteArrayOutputStream.close();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "product_image.jpg", imageRequestBody);
            
            // Actualizar la imagen primero
            Call<Product> imageCall = apiService.updateProductImage(Integer.parseInt(product.getId()), imagePart);
            imageCall.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful()) {
                        // Si la imagen se actualizó correctamente, actualizar los datos del producto
                        updateProduct(product, name, description, price, stock, unit, category, expirationDate, isEco);
                        // Resetear la imagen seleccionada
                        selectedImageUri = null;
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar imagen: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión al actualizar imagen: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProduct(Product product, String name, String description, double price, int stock, String unit, String category, String expirationDate, boolean isEco) {
        // Crear objeto ProductUpdate con los nuevos datos
        com.example.frontend.model.ProductUpdate productUpdate = new com.example.frontend.model.ProductUpdate();
        productUpdate.setName(name);
        productUpdate.setDescription(description);
        productUpdate.setPrice(price);
        productUpdate.setStockAvailable((double) stock);
        productUpdate.setCategory(category);
        productUpdate.setIsEco(isEco);
        productUpdate.setUnit(unit);
        if (expirationDate != null && !expirationDate.isEmpty()) {
            productUpdate.setExpirationDateString(expirationDate);
        }
        
        // Llamar a la API para actualizar el producto
        Call<Product> call = apiService.updateProduct(Integer.parseInt(product.getId()), productUpdate);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show();
                    loadSupermarketStock(); // Recargar la lista
                    // Resetear la imagen seleccionada
                    selectedImageUri = null;
                } else {
                    Toast.makeText(getContext(), "Error al actualizar producto: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProduct(Product product) {
        // Llamar a la API para eliminar el producto
        Call<Void> call = apiService.deleteProduct(Integer.parseInt(product.getId()));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show();
                    loadSupermarketStock(); // Recargar la lista
                } else {
                    Toast.makeText(getContext(), "Error al eliminar producto: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    selectedExpirationDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    textView.setText(selectedExpirationDate);
                }, year, month, day);
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateCurrentImagePreview() {
        if (currentImagePreview != null && selectedImageUri != null) {
            Glide.with(this)
                    .load(selectedImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(currentImagePreview);
            
            if (currentBtnRemoveImage != null) {
                currentBtnRemoveImage.setVisibility(View.VISIBLE);
            }
        }
    }
}