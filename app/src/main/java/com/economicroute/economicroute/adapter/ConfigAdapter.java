package com.economicroute.economicroute.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.economicroute.economicroute.ConfigActivity;
import com.economicroute.economicroute.ManageVehicleActivity;
import com.economicroute.economicroute.R;
import com.economicroute.economicroute.model.Config;
import com.economicroute.economicroute.model.Vehicle;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class ConfigAdapter extends RealmBaseAdapter<Config> implements ListAdapter {
    private ConfigActivity activity;

    private static class ViewHolder {
        private TextView config_title;
        private TextView config_description;
        private Switch config_active;
    }

    public ConfigAdapter(ConfigActivity activity, OrderedRealmCollection<Config> data) {
        super(data);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ConfigAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.config_list_row, parent, false);
            viewHolder = new ConfigAdapter.ViewHolder();
            viewHolder.config_title = (TextView) convertView.findViewById(R.id.config_title);
            viewHolder.config_description = (TextView) convertView.findViewById(R.id.config_description);
            viewHolder.config_active = (Switch) convertView.findViewById(R.id.config_active);
            viewHolder.config_active.setOnClickListener(listener);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ConfigAdapter.ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            Config config = adapterData.get(position);
            viewHolder.config_title.setText(config.getTitle());
            viewHolder.config_description.setText(config.getDescription());
            viewHolder.config_active.setChecked(config.isActive());
            viewHolder.config_active.setTag(position);
        }

        return convertView;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (Integer) view.getTag();
            if (adapterData != null) {
                Config config = adapterData.get(position);
                activity.changeConfigSelected(config.getId());
            }
        }
    };
}