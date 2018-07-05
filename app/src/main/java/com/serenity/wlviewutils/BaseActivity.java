package com.serenity.wlviewutils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.serenity.viewinject.InjectUtils;

/**
 * Created by serenitynanian on 2018/7/5.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectUtils.setDebug(true);

        //布局注入
        InjectUtils.layoutInject(this);
        //view注入
        InjectUtils.viewInject(this);
        //监听注入
        InjectUtils.listenerInject(this);

    }
}
