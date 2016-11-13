package com.shashwat.alzobot.luis;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class task extends AppCompatActivity {

//    private TextView txtSpeechInput;
//    private ImageButton btnSpeak;
//    private final int REQ_CODE_SPEECH_INPUT = 100;
    Button button;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        final EditText t = (EditText)findViewById(R.id.editText);
        final EditText loc = (EditText)findViewById(R.id.editText2);

//        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
//        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        button = (Button)findViewById(R.id.button);

        prefs = getSharedPreferences("tasklist", MODE_PRIVATE);

        /*btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });*/

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                prefs = getSharedPreferences("tasklist", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("task", prefs.getString("task", "")+(t.getText().toString())+":");
                editor.putString("location", prefs.getString("location", "")+(loc.getText().toString())+":");
                editor.apply();
            }
        });
    }

    /*private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the exact location for the object including room, place in room");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
    }*/
}
