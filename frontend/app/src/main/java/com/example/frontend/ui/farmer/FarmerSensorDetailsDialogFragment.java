package com.example.frontend.ui.farmer;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.frontend.R;
import com.example.frontend.model.FarmerSensorReading;

public class FarmerSensorDetailsDialogFragment extends DialogFragment {

    private final FarmerSensorReading sensor;

    public FarmerSensorDetailsDialogFragment(FarmerSensorReading sensor) {
        this.sensor = sensor;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.farmer_dialog_sensor_details, null);

        TextView title = view.findViewById(R.id.sensor_detail_title);
        TextView value = view.findViewById(R.id.sensor_detail_value);
        TextView info = view.findViewById(R.id.sensor_detail_extra);

        title.setText(sensor.getName());
        value.setText("Valor: " + sensor.getValue());
        info.setText(sensor.getExtraInfo());

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Cerrar", null)
                .create();
    }
}
