package com.economicroute.economicroute;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.economicroute.economicroute.adapter.ConfigAdapter;
import com.economicroute.economicroute.adapter.ManageVehicleAdapter;
import com.economicroute.economicroute.model.Config;
import com.economicroute.economicroute.model.Vehicle;

import io.realm.Realm;
import io.realm.RealmResults;

public class ConfigActivity extends AppCompatActivity {

    private TextView toolbar_title;
    private ListView config_list;
    private Realm realm;
    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Bundle bundleTitle = getIntent().getExtras();
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(bundleTitle.getString("intentName"));

        realm = Realm.getDefaultInstance();

        RealmResults<Config> configs = realm.where(Config.class).findAll();

        final ConfigAdapter adapter = new ConfigAdapter(this, configs);

        config_list = findViewById(R.id.config_list);
        config_list.setAdapter(adapter);
    }

    public void changeConfigSelected(final int configId) {
        Config config = new Config();
        config = realm.where(Config.class).equalTo("active", true).findFirst();

        try {
        if ((config != null) && config.isValid() && (config.getId() != configId)) {
            realm.beginTransaction();
            config.setActive(false);
            realm.copyToRealmOrUpdate(config);
            realm.commitTransaction();
            finish();
        }
            config = realm.where(Config.class).equalTo("id", configId).findFirst();
            realm.beginTransaction();
            config.setActive(!config.isActive());
            realm.copyToRealmOrUpdate(config);
            realm.commitTransaction();
            finish();

            Toast.makeText(ConfigActivity.this, "Configuração alterada!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ConfigActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
