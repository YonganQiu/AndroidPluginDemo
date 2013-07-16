/*
 * Copyright 2013 Elvis Hew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import com.elvishew.androidplugindemo.host.plugin.PluginEntry;
import com.elvishew.androidplugindemo.host.plugin.PluginManager;
import com.elvishew.androidplugindemo.host.plugin.PluginManager.OnPluginChangedListener;

/**
 * A simple plugin chooser provided to you to choose plugin which you would like
 * to used.
 */
public class PluginChooseActivity extends Activity implements OnItemClickListener,
        OnPluginChangedListener {

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

        mPluginManager.registerOnPluginChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPluginManager.unRegisterOnPluginChangedListener(this);
    }

    @Override
    public void onPluginChanged(PluginEntry entry, boolean added) {
        if (added) {
            mPlugins.add(entry);
        } else {
            for (PluginEntry plugin : mPlugins) {
                if (entry.pluginClass.equals(plugin.pluginClass)) {
                    mPlugins.remove(plugin);
                    break;
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // Set clicked plugin to be the current plugin.
        PluginEntry plugin = mAdapter.getItem(arg2);
        mPluginManager.setCurrentPlugin(plugin.pluginClass);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Simple plugin list adapter, show plugins with icons and labels.
     */
    private class PluginAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

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

            // Show current plugin with different background color.
            if (plugin.pluginClass.equals(currentPluginClass)) {
                convertView.setBackgroundColor(Color.MAGENTA);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            // Setup icon field.
            ImageView iconField = (ImageView) convertView.findViewById(R.id.icon);
            iconField.setImageDrawable(plugin.icon);

            // Setup label field.
            TextView labelField = (TextView) convertView.findViewById(R.id.label);
            labelField.setText(plugin.label);

            return convertView;
        }
        
    }
}
