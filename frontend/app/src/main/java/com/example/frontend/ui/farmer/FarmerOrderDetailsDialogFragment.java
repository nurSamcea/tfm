package com.example.frontend.ui.farmer;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.frontend.R;
import com.example.frontend.model.FarmerOrder;

public class FarmerOrderDetailsDialogFragment extends DialogFragment {

    private final FarmerOrder order;

    public FarmerOrderDetailsDialogFragment(FarmerOrder order) {
        this.order = order;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.farmer_dialog_order_details, null);

        TextView title = view.findViewById(R.id.detail_title);
        TextView items = view.findViewById(R.id.detail_items);
        TextView date = view.findViewById(R.id.detail_date);
        TextView total = view.findViewById(R.id.detail_total);
        TextView status = view.findViewById(R.id.detail_status);

        title.setText("Pedido de " + order.getClientOrMarket());
        items.setText("Productos:\n- " + TextUtils.join("\n- ", order.getProducts()));
        date.setText("Entrega: " + order.getDeliveryDate());
        total.setText("Total: " + order.getTotal());
        status.setText("Estado: " + order.getStatus());

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Aceptar", null)
                .create();
    }
}
