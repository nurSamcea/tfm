package com.example.frontend.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.ui.dialogs.ProductTraceabilityDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDetailFragment extends Fragment {
    
    private static final String TAG = "ProductDetailFragment";
    private static final String ARG_PRODUCT = "product";
    
    private Product product;
    
    // UI Components
    private ImageView productImage;
    private TextView productName;
    private TextView productCategory;
    private TextView productPrice;
    private TextView productDescription;
    private TextView farmerName;
    private TextView farmerLocation;
    private TextView sustainabilityScore;
    private TextView stockAvailable;
    private TextView expirationDate;
    private Button btnViewTraceability;
    private Button btnAddToCart;
    private Button btnContactFarmer;
    
    public static ProductDetailFragment newInstance(Product product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate - Inicializando ProductDetailFragment");
        
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PRODUCT);
            if (product != null) {
                Log.d(TAG, "Producto recibido: " + product.getName() + " (ID: " + product.getId() + ")");
            } else {
                Log.e(TAG, "Producto es null en getArguments");
            }
        } else {
            Log.e(TAG, "getArguments es null");
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        
        initializeViews(view);
        setupClickListeners();
        
        if (product != null) {
            populateProductData();
        } else {
            Log.e(TAG, "No se puede mostrar el producto porque es null");
            showError("Error al cargar la información del producto");
        }
        
        return view;
    }
    
    private void initializeViews(View view) {
        productImage = view.findViewById(R.id.product_image);
        productName = view.findViewById(R.id.product_name);
        productCategory = view.findViewById(R.id.product_category);
        productPrice = view.findViewById(R.id.product_price);
        productDescription = view.findViewById(R.id.product_description);
        farmerName = view.findViewById(R.id.farmer_name);
        farmerLocation = view.findViewById(R.id.farmer_location);
        sustainabilityScore = view.findViewById(R.id.sustainability_score);
        stockAvailable = view.findViewById(R.id.stock_available);
        expirationDate = view.findViewById(R.id.expiration_date);
        btnViewTraceability = view.findViewById(R.id.btn_view_traceability);
        btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        btnContactFarmer = view.findViewById(R.id.btn_contact_farmer);
        
        Log.d(TAG, "Vistas inicializadas correctamente");
    }
    
    private void setupClickListeners() {
        btnViewTraceability.setOnClickListener(v -> openTraceabilityDialog());
        btnAddToCart.setOnClickListener(v -> addToCart());
        btnContactFarmer.setOnClickListener(v -> contactFarmer());
        
        Log.d(TAG, "Click listeners configurados");
    }
    
    private void populateProductData() {
        try {
            // Información básica del producto
            productName.setText(product.getName() != null ? product.getName() : "Nombre no disponible");
            productCategory.setText(product.getCategory() != null ? product.getCategory() : "Categoría no disponible");
            productPrice.setText(String.format("€%.2f", product.getPrice()));
            productDescription.setText(product.getDescription() != null ? product.getDescription() : "Sin descripción disponible");
            
            // Información del productor
            if (product.getProviderName() != null) {
                farmerName.setText(product.getProviderName());
            } else {
                farmerName.setText("Productor no disponible");
            }
            
            // Ubicación del productor (usando coordenadas si están disponibles)
            if (product.getProviderLat() != null && product.getProviderLon() != null) {
                farmerLocation.setText(String.format("Ubicación: %.4f, %.4f", 
                    product.getProviderLat(), product.getProviderLon()));
            } else {
                farmerLocation.setText("Ubicación no disponible");
            }
            
            // Puntuación de sostenibilidad
            if (product.getScore() != null) {
                sustainabilityScore.setText(String.format("Puntuación de Sostenibilidad: %.1f/10", 
                    product.getScore()));
            } else {
                sustainabilityScore.setText("Puntuación de Sostenibilidad: No disponible");
            }
            
            // Información de stock
            stockAvailable.setText(String.format("%.1f %s", 
                product.getStockAvailable(), 
                product.getUnit() != null ? product.getUnit() : "unidades"));
            
            if (product.getExpirationDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                expirationDate.setText(sdf.format(product.getExpirationDate()));
            } else {
                expirationDate.setText("No disponible");
            }
            
            // Cargar imagen del producto
            loadProductImage();
            
            Log.d(TAG, "Datos del producto cargados correctamente");
            
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar los datos del producto", e);
            showError("Error al cargar la información del producto");
        }
    }
    
    private void loadProductImage() {
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_products)
                .error(R.drawable.ic_products)
                .into(productImage);
            Log.d(TAG, "Imagen del producto cargada: " + product.getImageUrl());
        } else {
            productImage.setImageResource(R.drawable.ic_products);
            Log.d(TAG, "Usando imagen por defecto para el producto");
        }
    }
    
    private void openTraceabilityDialog() {
        Log.d(TAG, "Abriendo diálogo de trazabilidad");
        
        if (product != null) {
            ProductTraceabilityDialog dialog = ProductTraceabilityDialog.newInstance(product);
            dialog.show(getParentFragmentManager(), "ProductTraceabilityDialog");
        } else {
            Log.e(TAG, "No se puede abrir el diálogo porque el producto es null");
            showError("Error al abrir la trazabilidad del producto");
        }
    }
    
    private void addToCart() {
        Log.d(TAG, "Añadiendo producto al carrito");
        Toast.makeText(getContext(), "Producto añadido al carrito", Toast.LENGTH_SHORT).show();
        // TODO: Implementar lógica para añadir al carrito
    }
    
    private void contactFarmer() {
        Log.d(TAG, "Contactando con el agricultor");
        Toast.makeText(getContext(), "Funcionalidad de contacto próximamente", Toast.LENGTH_SHORT).show();
        // TODO: Implementar lógica para contactar con el agricultor
    }
    
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error mostrado al usuario: " + message);
    }
}
