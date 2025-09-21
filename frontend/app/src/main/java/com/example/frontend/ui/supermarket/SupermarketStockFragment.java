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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.frontend.model.InventoryItem;
import com.example.frontend.model.Product;
import com.example.frontend.ui.adapters.FarmerStockAdapter;
import com.example.frontend.api.ApiService;
import com.example.frontend.api.ApiClient;
import com.example.frontend.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupermarketStockFragment extends Fragment implements FarmerStockAdapter.OnProductActionListener {
    private static final String TAG = "SupermarketStockFragment";

    private RecyclerView recyclerView;
    private FarmerStockAdapter adapter;
    private List<Product> productList; // Cambiado a Product para productos propios
    private List<InventoryItem> inventoryList; // Para productos de proveedores
    private SessionManager sessionManager;
    private ImageButton addProductButton;
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
        setupAddProductButton(view);
        setupImagePicker();
        loadProducts();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_stock);
        sessionManager = new SessionManager(requireContext());
        productList = new ArrayList<>();
        inventoryList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        if (recyclerView == null) {
            Log.e(TAG, "setupRecyclerView: RecyclerView es null");
            return;
        }
        
        // Usar el adaptador de FarmerStockAdapter para productos propios
        adapter = new FarmerStockAdapter(productList);
        adapter.setOnProductActionListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupAddProductButton(View view) {
        addProductButton = view.findViewById(R.id.add_product_button);
        if (addProductButton != null) {
            addProductButton.setOnClickListener(v -> openAddProductDialog());
        }
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    updateCurrentImagePreview();
                }
            }
        );
    }

    private void loadProducts() {
        Log.d(TAG, "loadProducts: Cargando productos del supermercado");
        
        // Obtener ID del supermercado desde la sesión
        Integer supermarketId = sessionManager.getUserId();
        if (supermarketId == null) {
            Log.e(TAG, "No se pudo obtener el ID del supermercado");
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del supermercado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cargar productos propios del supermercado
        loadSupermarketOwnProducts(supermarketId);
        
        // Cargar inventario de proveedores
        loadInventoryFromSuppliers(supermarketId);
    }

    private void loadSupermarketOwnProducts(Integer supermarketId) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<Product>> call = api.getFarmerProducts(supermarketId);
        
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "Productos propios del supermercado cargados: " + productList.size() + " productos");
                } else {
                    Log.e(TAG, "Error al cargar productos propios: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al cargar productos propios: " + t.getMessage());
            }
        });
    }

    private void loadInventoryFromSuppliers(Integer supermarketId) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<InventoryItem>> call = api.getInventory(String.valueOf(supermarketId));
        
        call.enqueue(new Callback<List<InventoryItem>>() {
            @Override
            public void onResponse(Call<List<InventoryItem>> call, Response<List<InventoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    inventoryList.clear();
                    inventoryList.addAll(response.body());
                    Log.d(TAG, "Inventario de proveedores cargado: " + inventoryList.size() + " items");
                } else {
                    Log.e(TAG, "Error al cargar inventario de proveedores: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<InventoryItem>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al cargar inventario: " + t.getMessage());
            }
        });
    }

    private void openAddProductDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        
        // Establecer el título del diálogo
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText("Nuevo Producto Propio");
        
        // Obtener referencias a los campos del formulario
        com.google.android.material.textfield.TextInputEditText inputName = dialogView.findViewById(R.id.input_name);
        com.google.android.material.textfield.TextInputEditText inputDesc = dialogView.findViewById(R.id.input_description);
        com.google.android.material.textfield.TextInputEditText inputPrice = dialogView.findViewById(R.id.input_price);
        com.google.android.material.textfield.TextInputEditText inputStock = dialogView.findViewById(R.id.input_stock);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category);
        com.google.android.material.textfield.TextInputEditText inputUnit = dialogView.findViewById(R.id.input_unit);
        TextView textExpirationDate = dialogView.findViewById(R.id.text_expiration_date);
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

    private void setupCategorySpinner(Spinner spinner) {
        List<String> categories = new ArrayList<>();
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

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedCategory = categories.get(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedCategory = "";
            }
        });
    }

    private void showDatePickerDialog(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedExpirationDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    textView.setText(selectedExpirationDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void updateCurrentImagePreview() {
        if (currentImagePreview != null && currentBtnRemoveImage != null) {
            updateImagePreview(currentImagePreview, currentBtnRemoveImage, selectedImageUri);
        }
    }

    private void updateImagePreview(ImageView imageView, com.google.android.material.button.MaterialButton removeButton, Uri imageUri) {
        if (imageUri != null) {
            imageView.setImageURI(imageUri);
            imageView.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            removeButton.setVisibility(View.GONE);
        }
    }

    private void publishProduct(String name, String desc, String priceStr, String stockStr, 
                               String category, String unit, String expirationDate, boolean isEco) {
        if (name.trim().isEmpty()) {
            Toast.makeText(getContext(), "El nombre del producto es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer supermarketId = sessionManager.getUserId();
        if (supermarketId == null) {
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del supermercado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar fecha de expiración
        String validatedExpiration = null;
        if (expirationDate != null && !expirationDate.trim().isEmpty() && !expirationDate.equals("Seleccionar fecha")) {
            validatedExpiration = expirationDate.trim();
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);

        // Preparar RequestBody para cada campo
        RequestBody rbName = RequestBody.create(MediaType.parse("text/plain"), name.trim());
        RequestBody rbProvider = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(supermarketId));
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
                if (response.isSuccessful() && response.body() != null) {
                    Product newProduct = response.body();
                    productList.add(newProduct);
                    if (adapter != null) {
                        adapter.notifyItemInserted(productList.size() - 1);
                    }
                    Toast.makeText(getContext(), "Producto añadido correctamente", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Producto creado: " + newProduct.getName());
                } else {
                    Log.e(TAG, "Error al crear producto: " + response.code());
                    Toast.makeText(getContext(), "Error al crear producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEdit(Product product) {
        String productName = product.getName();
        Log.d(TAG, "onEdit: Editando producto: " + productName);
        // TODO: Implementar edición de producto
        Toast.makeText(getContext(), "Editar producto: " + productName, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onToggleHidden(Product product) {
        String productName = product.getName();
        Log.d(TAG, "onToggleHidden: Cambiando visibilidad del producto: " + productName);
        // TODO: Implementar cambio de visibilidad
        Toast.makeText(getContext(), "Cambiar visibilidad: " + productName, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDelete(Product product) {
        String productName = product.getName();
        Log.d(TAG, "onDelete: Eliminando producto: " + productName);
        // TODO: Implementar eliminación de producto
        Toast.makeText(getContext(), "Eliminar producto: " + productName, Toast.LENGTH_SHORT).show();
    }
}