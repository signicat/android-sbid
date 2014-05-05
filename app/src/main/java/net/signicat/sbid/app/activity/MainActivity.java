package net.signicat.sbid.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.signicat.sbid.app.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAuthActivity(View target){
        startActivity(new Intent(this, SbidAuthActivity.class));
    }
}
