package com.economicroute.economicroute.application;

import android.app.Application;
import android.widget.Toast;

import com.economicroute.economicroute.NewVehicleActivity;
import com.economicroute.economicroute.model.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("economic_route_database.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        Realm realm;
        realm = Realm.getDefaultInstance();

        Config [] initial_configs = new Config [3];

        //Config 1
        initial_configs[0] = new Config();
        initial_configs[0].setId(1);
        initial_configs[0].setGroup(1);
        initial_configs[0].setTitle("Considerar Postos");
        initial_configs[0].setDescription("Quando marcada, gerencia as rotas do mapa de" +
                " acordo com o preço dos postos na localidade.");
        initial_configs[0].setActive(true);
        //Config 2
        initial_configs[1] = new Config();
        initial_configs[1].setId(2);
        initial_configs[1].setGroup(1);
        initial_configs[1].setTitle("Considerar Tempo");
        initial_configs[1].setDescription("Quando marcada, gerencia as rotas do mapa de" +
                " acordo o tempo médio para chegada ao local de destino.");
        initial_configs[1].setActive(false);
        //Config 3
        initial_configs[2] = new Config();
        initial_configs[2].setId(3);
        initial_configs[2].setGroup(1);
        initial_configs[2].setTitle("Considerar Distância");
        initial_configs[2].setDescription("Quando marcada, gerencia as rotas do mapa de" +
                " acordo com a menor distância até a chegada ao destino.");
        initial_configs[2].setActive(false);

        for(int i=0;i<initial_configs.length;i++) {
            try {
                realm.beginTransaction();

                realm.copyToRealmOrUpdate(initial_configs[i]);
                realm.commitTransaction();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
