package com.shashwat.alzobot.luis;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Contacts extends AppCompatActivity {
    EditText t1;
    EditText t2;
    Button b;
    String n1;
    String txt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        t1 = (EditText)findViewById(R.id.editText);
        t2 = (EditText)findViewById(R.id.editText2);
        b = (Button)findViewById(R.id.button2);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n1=t1.getText().toString();
                txt1=t2.getText().toString();
                SharedPreferences prefs = getSharedPreferences("tasklist", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("n1", n1);
                editor.putString("txt1", txt1);
                editor.apply();
            }
        });

        SharedPreferences prefs = getSharedPreferences("tasklist", MODE_PRIVATE);
        String n1 = prefs.getString("n1", "");
        String txt1 = prefs.getString("txt1", "");

        t1.setText(n1);
        t2.setText(txt1);
    }
}
