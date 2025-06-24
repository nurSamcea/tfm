package com.example.frontend.ui.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.FarmerOrder;
import com.example.frontend.ui.adapters.FarmerOrderAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FarmerOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private FarmerOrderAdapter adapter;
    private List<FarmerOrder> orderList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_orders, container, false);

        recyclerView = view.findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderList.add(new FarmerOrder("Supermercado Día", Arrays.asList("Tomate Cherry (6kg)", "Lechuga (3 uds)"), "24/06", "21,50 €", "🔴 Pendiente"));
        orderList.add(new FarmerOrder("Marta Pérez", Arrays.asList("Calabacín (4kg)"), "25/06", "7,80 €", "🟡 En camino"));

        adapter = new FarmerOrderAdapter(orderList, this::showOrderDetails);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void showOrderDetails(FarmerOrder order) {
        new FarmerOrderDetailsDialogFragment(order).show(getParentFragmentManager(), "details");
    }
}
