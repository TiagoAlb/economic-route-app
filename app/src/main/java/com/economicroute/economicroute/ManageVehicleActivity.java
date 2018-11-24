package com.economicroute.economicroute;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.economicroute.economicroute.adapter.ManageVehicleAdapter;
import com.economicroute.economicroute.model.Fuel;
import com.economicroute.economicroute.model.Vehicle;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class ManageVehicleActivity extends AppCompatActivity {
    private Realm realm;
    private Vehicle vehicle;

    TextView vehicle_use_name;
    TextView vehicle_use_brand;
    TextView vehicle_use_fuel;
    TextView vehicle_use_fuel_quantity;
    LinearLayout vehicle_use;
    TextView toolbar_title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vehicle);
        Bundle bundleTitle = getIntent().getExtras();
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(bundleTitle.getString("intentName"));
        realm = Realm.getDefaultInstance();

        // RealmResults are "live" views, that are automatically kept up to date, even when changes happen
        // on a background thread. The RealmBaseAdapter will automatically keep track of changes and will
        // automatically refresh when a change is detected.
        RealmResults<Vehicle> vehicles = realm.where(Vehicle.class).findAll();

        vehicle_use_name = findViewById(R.id.vehicle_use_name);
        vehicle_use_brand = findViewById(R.id.vehicle_use_brand);
        vehicle_use_fuel = findViewById(R.id.vehicle_use_fuel);
        vehicle_use_fuel_quantity = findViewById(R.id.vehicle_use_fuel_quantity);
        vehicle_use = findViewById(R.id.vehicle_use);

        final ManageVehicleAdapter adapter = new ManageVehicleAdapter(this, vehicles);

        ListView listView = findViewById(R.id.vehicle_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Vehicle vehicle = (Vehicle) adapterView.getAdapter().getItem(i);
                final EditText vehicleEditText = new EditText(ManageVehicleActivity.this);
                vehicleEditText.setText(vehicle.getName());
            }
        });

        Button new_vehicle = findViewById(R.id.new_vehicle);
        new_vehicle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ManageVehicleActivity.this, NewVehicleActivity.class);
                intent.putExtra("intentName", "Cadastre um veículo");
                startActivity(intent);
            }
        });

        selectIsBeingUsedVehicle(-1);
    }

    public void selectIsBeingUsedVehicle(final int vehicleId) {
        vehicle = new Vehicle();
        vehicle = realm.where(Vehicle.class).equalTo("id", vehicleId).findFirst();
        if(vehicle!=null) {
            if (vehicle.isBeingUsed() && vehicleId != -1) {
                vehicle_use_name.setText(vehicle.getName());
                vehicle_use_brand.setText(vehicle.getBrand() + " - " + vehicle.getType());
                vehicle_use_fuel.setText(vehicle.getFuel().get(0).getName());
                vehicle_use_fuel_quantity.setText(vehicle.getFuel_quantity() + " / " + vehicle.getTank());
                vehicle_use.setVisibility(View.VISIBLE);
            } else {
                vehicle_use.setVisibility(View.GONE);
            }
        }
    }

    public void changeVehicleSelected(final int vehicleId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                vehicle = new Vehicle();
                vehicle = realm.where(Vehicle.class).equalTo("isBeingUsed", true).findFirst();
                if((vehicle!=null)&&vehicle.isValid()&&(vehicle.getId()!=vehicleId)) {
                    vehicle.setBeingUsed(false);

                    try {
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(vehicle);
                        realm.commitTransaction();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                vehicle = realm.where(Vehicle.class).equalTo("id", vehicleId).findFirst();
                vehicle.setBeingUsed(!vehicle.isBeingUsed());

                try {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(vehicle);
                    realm.commitTransaction();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        selectIsBeingUsedVehicle(vehicleId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

