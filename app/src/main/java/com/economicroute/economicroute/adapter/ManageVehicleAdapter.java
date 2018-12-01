package com.economicroute.economicroute.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.economicroute.economicroute.ManageVehicleActivity;
import com.economicroute.economicroute.R;
import com.economicroute.economicroute.model.Vehicle;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class ManageVehicleAdapter extends RealmBaseAdapter<Vehicle> implements ListAdapter {

    private ManageVehicleActivity activity;

    private static class ViewHolder {
        private TextView vehicle_name;
        private TextView vehicle_brand;
        private TextView vehicle_fuel_name;
        private TextView vehicle_fuel_quantity;
        private CheckBox choice_vehicle;
    }

    public ManageVehicleAdapter(ManageVehicleActivity activity, OrderedRealmCollection<Vehicle> data) {
        super(data);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vehicle_list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.vehicle_name = (TextView) convertView.findViewById(R.id.vehicle_name);
            viewHolder.vehicle_brand = (TextView) convertView.findViewById(R.id.vehicle_brand);
            viewHolder.vehicle_fuel_name = (TextView) convertView.findViewById(R.id.vehicle_fuel_name);
            viewHolder.vehicle_fuel_quantity = (TextView) convertView.findViewById(R.id.vehicle_fuel_quantity);
            viewHolder.choice_vehicle = (CheckBox) convertView.findViewById(R.id.choice_vehicle);
            viewHolder.choice_vehicle.setOnClickListener(listener);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            Vehicle vehicle = adapterData.get(position);
            viewHolder.vehicle_name.setText(vehicle.getName());
            viewHolder.vehicle_brand.setText(vehicle.getBrand()+" - "+vehicle.getType());
            viewHolder.vehicle_fuel_name.setText(vehicle.getFuel_name());
            viewHolder.vehicle_fuel_quantity.setText(vehicle.getFuel_quantity()+" / "+vehicle.getTank());
            viewHolder.choice_vehicle.setChecked(vehicle.isBeingUsed());
            viewHolder.choice_vehicle.setTag(position);
        }

        return convertView;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (Integer) view.getTag();
            if (adapterData != null) {
                Vehicle vehicle = adapterData.get(position);
                activity.changeVehicleSelected(vehicle.getId());
            }
        }
    };
}