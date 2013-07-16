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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.elvishew.androidplugindemo.host.plugin.PluginManager;
import com.elvishew.androidplugindemo.plugin.Plugin;

/**
 * You can choose which plugin to used, and show simple functions of current
 * plugin.
 * <p>
 * TODO: Notice that third-party plugins is not promised to be safe, so before
 * you call a plugin's method, you should check whether this plugin has all
 * necessary methods.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PluginManager pluginManager = (PluginManager) getApplicationContext()
                .getSystemService(PluginManager.PLUGIN);

        // Plugin function 1.
        Button fun1 = (Button) findViewById(R.id.plugin_fun_1);
        fun1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Plugin plugin = pluginManager.createPlugin(MainActivity.this);
                Toast.makeText(MainActivity.this, plugin.function1(), Toast.LENGTH_SHORT).show();
            }
        });

        // Plugin function 2.
        Button fun2 = (Button) findViewById(R.id.plugin_fun_2);
        fun2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Plugin plugin = pluginManager.createPlugin(MainActivity.this);
                Toast.makeText(MainActivity.this, plugin.function2(), Toast.LENGTH_SHORT).show();
            }
        });

        // Start plugin chooser.
        Button choose = (Button) findViewById(R.id.choose_plugin);
        choose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PluginChooseActivity.class);
                startActivity(intent);
            }
        });
    }
}
