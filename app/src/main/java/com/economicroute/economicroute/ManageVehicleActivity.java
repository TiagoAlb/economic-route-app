package com.economicroute.economicroute;

import android.companion.CompanionDeviceManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ManageVehicleActivity extends AppCompatActivity {

    private String [] typeVehicle = new String [] {
            "Carro",
            "Moto",
            "Caminhão"
    };

    private int [] idsVehiclesBrand;
    private int [] idsVehiclesYear;
    private int [] idsVehiclesName;

    private Spinner spinner_type_vehicle;
    private Spinner spinner_brand_vehicle;
    private Spinner spinner_name_vehicle;
    private Spinner spinner_year_vehicle;
    private String BASE_URL = "http://fipeapi.appspot.com/api/1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vehicle);
        Bundle bundleTitle = getIntent().getExtras();
        setTitle(bundleTitle.getString("intentName"));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typeVehicle);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_type_vehicle = findViewById(R.id.spinner_type_vehicle);
        spinner_brand_vehicle = findViewById(R.id.spinner_brand_vehicle);
        spinner_name_vehicle = findViewById(R.id.spinner_name_vehicle);
        spinner_year_vehicle = findViewById(R.id.spinner_year_vehicle);

        spinner_type_vehicle.setAdapter(adapter);
        //getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
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
    }

    public void changeRequest(String typeRequest) {
        String vehicle= "";
        if (spinner_type_vehicle.getSelectedItemPosition()!=2)
            vehicle = spinner_type_vehicle.getSelectedItem() + "s";
        else
            vehicle = "Caminhoes";

        if (typeRequest == "brand") {
            Request request = new Request.Builder()
                    .url(BASE_URL+vehicle+"/marcas.json")
                    .build();
            getResultFipeApi(request, typeRequest);
        } else if (typeRequest == "name") {
            Request request = new Request.Builder()
                    .url(BASE_URL+vehicle+"/veiculos/"+idsVehiclesBrand[spinner_brand_vehicle.getSelectedItemPosition()]+".json")
                    .build();
            getResultFipeApi(request, typeRequest);
        } else {
            Request request = new Request.Builder()
                    .url(BASE_URL+vehicle+"/veiculos/"+idsVehiclesBrand[spinner_brand_vehicle.getSelectedItemPosition()]+"/"+idsVehiclesName[spinner_name_vehicle.getSelectedItemPosition()]+".json")
                    .build();
            getResultFipeApi(request, typeRequest);
        }
    }

    public void setVehicleBrand(JSONArray vehicleBrand) throws JSONException {
        String [] brands = new String [vehicleBrand.length()];
        idsVehiclesBrand = new int [vehicleBrand.length()];
        for (int i=0; i < vehicleBrand.length(); i++) {
            JSONObject jsonObject = vehicleBrand.getJSONObject(i);
            brands[i] = jsonObject.getString("name");
            idsVehiclesBrand[i] = jsonObject.getInt("id");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, brands);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_brand_vehicle.setAdapter(adapter);
    }

    public void setVehicleName(JSONArray vehicleName) throws JSONException {
        String [] names = new String [vehicleName.length()];
        idsVehiclesName = new int [vehicleName.length()];
        for (int i=0; i < vehicleName.length(); i++) {
            JSONObject jsonObject = vehicleName.getJSONObject(i);
            names[i] = jsonObject.getString("name");
            idsVehiclesName[i] = jsonObject.getInt("id");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_name_vehicle.setAdapter(adapter);
    }

    public void setVehicleYear(JSONArray vehicleYear) throws JSONException {
        String [] years = new String [vehicleYear.length()];
        idsVehiclesYear = new int [vehicleYear.length()];
        for (int i=0; i < vehicleYear.length(); i++) {
            JSONObject jsonObject = vehicleYear.getJSONObject(i);

            /*
            if (jsonObject.getString("name").length()>15)
                years[i] = jsonObject.getString("name").substring(1, 7);
            else
              years[i] = jsonObject.getString("name").substring(1, 4);
*/

            years[i] = jsonObject.getString("name");
            idsVehiclesYear[i] = jsonObject.getInt("id");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year_vehicle.setAdapter(adapter);
    }

    public void getResultFipeApi(Request request, String typeRequest) {
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback()  {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();

                    ManageVehicleActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray arrayJson = new JSONArray(myResponse);
                                if (typeRequest == "brand")
                                    setVehicleBrand(arrayJson);
                                else if (typeRequest == "name")
                                    setVehicleName(arrayJson);
                                else
                                    setVehicleYear(arrayJson);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}

