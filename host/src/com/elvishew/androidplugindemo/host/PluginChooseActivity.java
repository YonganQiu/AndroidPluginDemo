package com.elvishew.androidplugindemo.host;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.elvishew.androidplugindemo.plugin.PluginEntry;
import com.elvishew.androidplugindemo.plugin.PluginManager;

public class PluginChooseActivity extends Activity implements OnItemClickListener {

    private PluginManager mPluginManager;
    private List<PluginEntry> mPlugins;
    private ListView mListView;
    private PluginAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_choose);
        mPluginManager = (PluginManager) getApplicationContext().getSystemService(
                PluginManager.PLUGIN);
        mPlugins = mPluginManager.getPlugins();
        mListView = (ListView) findViewById(android.R.id.list);
        mAdapter = new PluginAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        PluginEntry plugin = mAdapter.getItem(arg2);
        mPluginManager.setCurrentPlugin(plugin.pluginClass);
        mAdapter.notifyDataSetChanged();
    }

    private class PluginAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        public PluginAdapter() {
            mInflater = LayoutInflater.from(PluginChooseActivity.this);
        }

        @Override
        public int getCount() {
            return mPlugins.size();
        }

        @Override
        public PluginEntry getItem(int position) {
            return mPlugins.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.plugin_item, parent, false);
            }
            String currentPluginClass = mPluginManager.getCurrentPlugin();
            PluginEntry plugin = getItem(position);
            if (plugin.pluginClass.equals(currentPluginClass)) {
                convertView.setBackgroundColor(Color.BLUE);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            ImageView iconField = (ImageView) convertView.findViewById(R.id.icon);
            iconField.setImageDrawable(plugin.icon);

            TextView labelField = (TextView) convertView.findViewById(R.id.label);
            labelField.setText(plugin.label);

            return convertView;
        }
        
    }
}
