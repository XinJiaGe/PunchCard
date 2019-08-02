package com.jiage.punchcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jiage.punchcard.service.TimeService;
import com.jiage.punchcard.sharedPreference.SharedPreference;
import com.jiage.punchcard.sharedPreference.SharedPreferenceConfig;

public class MainActivity extends AppCompatActivity {
    private TimePicker topTP;
    private TimePicker botTP;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topTP = findViewById(R.id.main_top);
        botTP = findViewById(R.id.main_bot);
        save = findViewById(R.id.main_save);

        SharedPreferenceConfig sp = SharedPreference.getSharedPreference(this);
        String top = sp.getTOP();
        String bot = sp.getBOT();
        //早上
        String[] toptimes = top.split("-");
        String tophh = toptimes[0];
        String topmm = toptimes[1];
        //晚上
        String[] bottimes = bot.split("-");
        String bothh = bottimes[0];
        String botmm = bottimes[1];

        topTP.setHour(Integer.parseInt(tophh));
        topTP.setMinute(Integer.parseInt(topmm));
        botTP.setHour(Integer.parseInt(bothh));
        botTP.setMinute(Integer.parseInt(botmm));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreference.getSharedPreference(MainActivity.this).setTOP(topTP.getHour()+"-"+topTP.getMinute());
                SharedPreference.getSharedPreference(MainActivity.this).setBOT(botTP.getHour()+"-"+botTP.getMinute());
                Toast.makeText(MainActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
            }
        });

        startForegroundService(new Intent(this, TimeService.class));
    }
}
