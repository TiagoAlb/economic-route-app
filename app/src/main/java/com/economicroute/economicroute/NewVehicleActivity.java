package com.economicroute.economicroute;

import android.companion.CompanionDeviceManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.economicroute.economicroute.model.Fuel;
import com.economicroute.economicroute.model.Vehicle;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NewVehicleActivity extends AppCompatActivity {

    private Realm realm;
    private RealmResults<Vehicle> vehicles;

    private String[] typeVehicle = new String[]{
            "Carro",
            "Moto",
            "Caminhão"
    };

    private int[] idsVehiclesBrand;
    private String[] idsVehiclesYear;
    private int[] idsVehiclesName;

    private Spinner spinner_type_vehicle;
    private Spinner spinner_brand_vehicle;
    private Spinner spinner_name_vehicle;
    private Spinner spinner_year_vehicle;
    private Spinner spinner_fuel_vehicle;
    private String BASE_URL = "http://fipeapi.appspot.com/api/1/";

    private EditText input_tank_vehicle;
    private EditText input_consumption_vehicle;
    private EditText input_plate_vehicle;
    private Button register_vehicle;

    private TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
        Bundle bundleTitle = getIntent().getExtras();
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(bundleTitle.getString("intentName"));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typeVehicle);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_type_vehicle = findViewById(R.id.spinner_type_vehicle);
        spinner_brand_vehicle = findViewById(R.id.spinner_brand_vehicle);
        spinner_name_vehicle = findViewById(R.id.spinner_name_vehicle);
        spinner_year_vehicle = findViewById(R.id.spinner_year_vehicle);
        spinner_fuel_vehicle = findViewById(R.id.spinner_fuel_vehicle);
        input_tank_vehicle = findViewById(R.id.input_tank_vehicle);
        input_consumption_vehicle = findViewById(R.id.input_consumption_vehicle);
        input_plate_vehicle = findViewById(R.id.input_plate_vehicle);
        register_vehicle = findViewById(R.id.register_vehicle);

        spinner_type_vehicle.setAdapter(adapter);
        realm = Realm.getDefaultInstance();

        onButtonClick();
    }

    public void onButtonClick() {
        spinner_type_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeRequest("brand");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_brand_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeRequest("name");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_name_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeRequest("year");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_year_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeRequest("fuel");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        input_tank_vehicle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String tankText = editable.toString();
                String consumptionText = input_consumption_vehicle.getText().toString();

                if (((!tankText.isEmpty()) && (!tankText.equals(null)) && (!tankText.equals("0")))
                        && ((!consumptionText.isEmpty()) && (!consumptionText.equals(null)) && (!consumptionText.equals("0")))) {
                    register_vehicle.setEnabled(true);
                    register_vehicle.setVisibility(View.VISIBLE);
                } else {
                    register_vehicle.setEnabled(false);
                    register_vehicle.setVisibility(View.GONE);
                }
            }
        });
        input_consumption_vehicle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String consumptionText = editable.toString();
                String tankText = input_tank_vehicle.getText().toString();

                if (((!tankText.isEmpty()) && (!tankText.equals(null)) && (!tankText.equals("0")))
                        && ((!consumptionText.isEmpty()) && (!consumptionText.equals(null)) && (!consumptionText.equals("0")))) {
                    register_vehicle.setEnabled(true);
                    register_vehicle.setVisibility(View.VISIBLE);
                } else {
                    register_vehicle.setEnabled(false);
                    register_vehicle.setVisibility(View.GONE);
                }
            }
        });
        register_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerVehicle(view);
            }
        });
    }

    public void registerVehicle(View view) {
        Vehicle vehicle = new Vehicle();

        vehicle.setId(realm.where(Vehicle.class).findAll().size()+1);
        vehicle.setType(spinner_type_vehicle.getSelectedItem().toString());
        vehicle.setName(spinner_name_vehicle.getSelectedItem().toString());
        vehicle.setBrand(spinner_brand_vehicle.getSelectedItem().toString());
        vehicle.setYear(spinner_year_vehicle.getSelectedItem().toString());
        vehicle.setFuel_name(spinner_fuel_vehicle.getSelectedItem().toString());
        vehicle.setPlate(input_plate_vehicle.getText().toString());
        vehicle.setTank(Double.parseDouble(input_tank_vehicle.getText().toString()));
        vehicle.setConsumption(Double.parseDouble(input_consumption_vehicle.getText().toString()));

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(vehicle);
            realm.commitTransaction();

            Toast.makeText(NewVehicleActivity.this, "Sucesso! Veículo "+vehicle.getName()+" adicionado.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(NewVehicleActivity.this, "Erro! "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void changeRequest(String typeRequest) {
        String vehicle = "";
        if (spinner_type_vehicle.getSelectedItemPosition() != 2)
            vehicle = spinner_type_vehicle.getSelectedItem() + "s";
        else
            vehicle = "Caminhoes";

        vehicle = vehicle.toLowerCase();

        if (typeRequest == "brand") {
            Request request = new Request.Builder()
                    .url(BASE_URL + vehicle + "/marcas.json")
                    .build();
            System.out.println(request.url());
            getResultFipeApi(request, typeRequest);
        } else if (typeRequest == "name") {
            Request request = new Request.Builder()
                    .url(BASE_URL + vehicle + "/veiculos/" + idsVehiclesBrand[spinner_brand_vehicle.getSelectedItemPosition()] + ".json")
                    .build();
            getResultFipeApi(request, typeRequest);
        } else if (typeRequest == "year") {
            Request request = new Request.Builder()
                    .url(BASE_URL + vehicle + "/veiculo/" + idsVehiclesBrand[spinner_brand_vehicle.getSelectedItemPosition()] + "/" + idsVehiclesName[spinner_name_vehicle.getSelectedItemPosition()] + ".json")
                    .build();
            getResultFipeApi(request, typeRequest);
        } else {
            Request request = new Request.Builder()
                    .url(BASE_URL + vehicle + "/veiculo/" + idsVehiclesBrand[spinner_brand_vehicle.getSelectedItemPosition()] + "/" + idsVehiclesName[spinner_name_vehicle.getSelectedItemPosition()] + "/" + idsVehiclesYear[spinner_year_vehicle.getSelectedItemPosition()] + ".json")
                    .build();
            getResultFipeApi(request, typeRequest);
        }
    }

    public void setVehicleBrand(JSONArray vehicleBrand) throws JSONException {
        String[] brands = new String[vehicleBrand.length()];
        idsVehiclesBrand = new int[vehicleBrand.length()];
        for (int i = 0; i < vehicleBrand.length(); i++) {
            JSONObject jsonObject = vehicleBrand.getJSONObject(i);
            brands[i] = jsonObject.getString("name");
            idsVehiclesBrand[i] = jsonObject.getInt("id");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, brands);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_brand_vehicle.setAdapter(adapter);
    }

    public void setVehicleName(JSONArray vehicleName) throws JSONException {
        String[] names = new String[vehicleName.length()];
        idsVehiclesName = new int[vehicleName.length()];
        for (int i = 0; i < vehicleName.length(); i++) {
            JSONObject jsonObject = vehicleName.getJSONObject(i);
            names[i] = jsonObject.getString("name");
            idsVehiclesName[i] = jsonObject.getInt("id");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_name_vehicle.setAdapter(adapter);
    }

    public void setVehicleYear(JSONArray vehicleYear) throws JSONException {
        String[] years = new String[vehicleYear.length()];
        idsVehiclesYear = new String[vehicleYear.length()];
        for (int i = 0; i < vehicleYear.length(); i++) {
            JSONObject jsonObject = vehicleYear.getJSONObject(i);

            if (jsonObject.getString("name").length() > 15)
                years[i] = jsonObject.getString("name").substring(0, 7);
            else
                years[i] = jsonObject.getString("name").substring(0, 4);

            idsVehiclesYear[i] = jsonObject.getString("id");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year_vehicle.setAdapter(adapter);
    }

    public void setVehicleFuel(JSONObject vehicleFuel) throws JSONException {
        String[] fuels = new String[1];
        fuels[0] = vehicleFuel.getString("combustivel");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fuels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_fuel_vehicle.setAdapter(adapter);
    }

    public void getResultFipeApi(Request request, String typeRequest) {
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();

                    NewVehicleActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray arrayJson = null;
                                if (typeRequest != "fuel")
                                    arrayJson = new JSONArray(myResponse);
                                if (typeRequest == "brand")
                                    setVehicleBrand(arrayJson);
                                else if (typeRequest == "name")
                                    setVehicleName(arrayJson);
                                else if (typeRequest == "year")
                                    setVehicleYear(arrayJson);
                                else {
                                    setVehicleFuel(new JSONObject(myResponse));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

