package com.serenity.wlviewutils;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.serenity.viewinject.annotation.LayoutInject;
import com.serenity.viewinject.annotation.OnClickInject;
import com.serenity.viewinject.annotation.ViewInject;


@LayoutInject(R.layout.activity_main)
public class MainActivity extends BaseActivity {


    @ViewInject(R.id.bt_button)
    private Button button ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "------->"+button.toString(), Toast.LENGTH_SHORT).show();


    }

    @OnClickInject({R.id.bt_button})
    public void onclickTest(View view) {
        Toast.makeText(this, "-------点击了------", Toast.LENGTH_SHORT).show();
    }
}
