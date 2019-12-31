package com.urraan.hamzakhan.gitpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Hello!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void dnka(View view) {
        Toast.makeText(this, "Here is the New Changes.....", Toast.LENGTH_SHORT).show();
    }
}
