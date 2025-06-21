package com.example.frontend.ui.supermarket;

import android.util.Log;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;

public class SupermarketDashboardFragment extends Fragment {
    private static final String TAG = "Curr.ERROR SupermarketSuppliersFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando la aplicación");
        View view = inflater.inflate(R.layout.fragment_supermarket_dashboard, container, false);

        // Simulamos algunos valores
        TextView kpiPedidos = view.findViewById(R.id.kpi_pedidos);
        TextView kpiProveedores = view.findViewById(R.id.kpi_proveedores);
        TextView proximoPedido = view.findViewById(R.id.proximo_pedido);
        TextView avisoEntrega = view.findViewById(R.id.aviso_entrega);

        kpiPedidos.setText("15 pedidos activos");
        kpiProveedores.setText("8 proveedores activos");
        proximoPedido.setText("Próximo pedido: 24 junio");
        avisoEntrega.setText("Entrega pendiente: Lechugas (Mañana)");

        return view;
    }
}
