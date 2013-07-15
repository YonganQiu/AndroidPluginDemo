
package com.elvishew.androidplugindemo.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.elvishew.androidplugindemo.plugin.Plugin;
import com.elvishew.androidplugindemo.plugin.PluginManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.button);
        final PluginManager pluginManager = (PluginManager) getApplicationContext()
                .getSystemService(PluginManager.PLUGIN);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Plugin plugin = pluginManager.createPlugin(MainActivity.this);
                Toast.makeText(MainActivity.this, plugin.name(), Toast.LENGTH_SHORT).show();
            }
        });
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
