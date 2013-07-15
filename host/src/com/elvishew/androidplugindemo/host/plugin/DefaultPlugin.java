package com.elvishew.androidplugindemo.host.plugin;

import com.elvishew.androidplugindemo.plugin.Plugin;

import android.content.Context;
import android.widget.Toast;

public class DefaultPlugin implements Plugin {

    private Context mContext;

    public DefaultPlugin(Context context) {
        mContext = context;
    }

    @Override
    public String name() {
        return "Default";
    }

    @Override
    public void f1() {
        Toast.makeText(mContext, name(), Toast.LENGTH_SHORT).show();
    }

}
