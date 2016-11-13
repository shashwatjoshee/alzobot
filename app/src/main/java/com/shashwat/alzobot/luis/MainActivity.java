package com.shashwat.alzobot.luis;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.microsoft.cognitiveservices.luis.clientlibrary.LUISClient;
import com.microsoft.cognitiveservices.luis.clientlibrary.LUISDialog;
import com.microsoft.cognitiveservices.luis.clientlibrary.LUISEntity;
import com.microsoft.cognitiveservices.luis.clientlibrary.LUISIntent;
import com.microsoft.cognitiveservices.luis.clientlibrary.LUISResponse;
import com.microsoft.cognitiveservices.luis.clientlibrary.LUISResponseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LUISResponse previousResponse = null;
    String chatText = "";
    String result = "";
    ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String textPredict = "";
    TextToSpeech t1;
    int count = 0;
    int cin = 0;

    String Country;
    double latitude;
    double longitude;
    String addressString = "";
//    TextView txt;
    Address address;
    String strAddress;
    SharedPreferences prefs;
    String number;
    String phrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSpeak = (ImageButton) findViewById(R.id.imageButton);
        prefs = getSharedPreferences("tasklist", MODE_PRIVATE);
        number = prefs.getString("n1", "");
        phrase = prefs.getString("txt1", "");
//        final EditText editTextPredict = (EditText) findViewById(R.id.editTextPredict);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

//        txt = (TextView) findViewById(R.id.textView2);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider, 20000, 0, this);
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private String getMyPosAddress(double dbLat, double dbLon) throws IOException {
        Geocoder gc = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = gc.getFromLocation(dbLat, dbLon, 1);

            if (addresses.size() > 0) {
                address = addresses.get(0);
                Country = address.getCountryName();

                StringBuilder str = new StringBuilder();
                str.append("Lane: " + address.getAddressLine(0) + "\n");
                str.append("Locality: " + address.getAddressLine(1) + "\n");
                str.append("Admin Area:: " + address.getAddressLine(2) + "\n");
                str.append("Country: " + address.getAddressLine(3) + "\n");
                str.append("Country Code: " + address.getCountryCode() + "\n");

                String strAddress = str.toString();
//                txt.setText(strAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressString;
    }

    private void call() throws IOException {
        SharedPreferences prefs = getSharedPreferences("tasklist", MODE_PRIVATE);
        String n1 = prefs.getString("n1", "");
        if (n1.equals("")){
            n1=n1.replace("","0");
        }

        if (haveNetworkConnection()) {
            StringBuilder str2 = new StringBuilder();
            str2.append(address.getAddressLine(0) + "\n");
            str2.append(address.getAddressLine(1) + "\n");
            str2.append(address.getAddressLine(2) + "\n");

            strAddress = str2.toString();
        } else {
            strAddress=":";
        }
        SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, "I am in a panic. I cannot remember anything. "+"My Location is "+strAddress+"("+"http://maps.google.com/?q="+latitude+","+longitude+")", null, null);
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d("Lat", String.valueOf(latitude));
        Log.d("Lon", String.valueOf(longitude));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(this, task.class);
                startActivity(intent);
                break;
            case R.id.item2:
                Intent in = new Intent(this, Contacts.class);
                startActivity(in);
                break;
        }
        return false;
    }

    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask your question");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn\\'t support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    editTextPredict.setText(result.get(0));
                    textPredict=result.get(0);
                    /*if (textPredict.equalsIgnoreCase("yes")){
                        //TO DO Message to family
                    }
                    if (textPredict.equalsIgnoreCase("no")){
                        speakloc();
                    }
                    else{
                        speakloc();
                    }*/
                }
                break;
            }
        }
        convert();
    }

    public void convert(){
        String appId = "b5477dd3-198e-4434-aa3c-40970f5c13dd";
        String appKey = "3ccbd944338843a284752b6a8e0f064a";

//        EditText editTextPredict = (EditText) findViewById(R.id.editTextPredict);

                    if (textPredict.equalsIgnoreCase("yes")){
                        //TO DO Message to family
                    }
                    if (textPredict.equalsIgnoreCase("no")){
                        speakloc(cin);
                    }
                    if (textPredict.equalsIgnoreCase(phrase)){
                        Toast.makeText(getApplicationContext(), "Lost!!", Toast.LENGTH_LONG).show();
                        try {
                            if (haveNetworkConnection()) {
                                getMyPosAddress(latitude, longitude);
                            }
                            call();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        SharedPreferences prefs = getSharedPreferences("tasklist", MODE_PRIVATE);

                        try {
                            LUISClient client = new LUISClient(appId, appKey, true, true);
                            client.predict(textPredict, new LUISResponseHandler() {
                                @Override
                                public void onSuccess(LUISResponse response) {
                                    processResponse(response);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    printToResponse(e.getMessage());
                                }
                            });
                        } catch (Exception e) {
                            printToResponse(e.getMessage());
                        }
//        editTextPredict.setText(prefs.getString("location",""));
                    }
    }

    public void processResponse(LUISResponse response) {
//        printToResponse("-------------------");
        previousResponse = response;
//        printToResponse(response.getQuery());
        LUISIntent topIntent = response.getTopIntent();
//        printToResponse("Top Intent: " + topIntent.getName());
//        printToResponse("Entities:");
        List<LUISEntity> entities = response.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            printToResponse(entities.get(i).getName());
//            printToResponse(String.valueOf(i+1)+ " - " + entities.get(i).getName());
//            textPredict=entities.get(i).getName();
        }
        LUISDialog dialog = response.getDialog();
        if (dialog != null) {
//            printToResponse("Dialog Status: " + dialog.getStatus());
            if (!dialog.isFinished()) {
//                printToResponse("Dialog prompt: " + dialog.getPrompt());
            }
        }
    }

    public void printToResponse(String text) {
//        TextView textViewResponse = (TextView) findViewById(R.id.textViewResponse);
        result = text;
        chatText += "\n" + text;
//        textViewResponse.setText(chatText);
        SharedPreferences prefs = getSharedPreferences("tasklist", MODE_PRIVATE);
        String[] col = (prefs.getString("task", "")).split(":");
        for (int i=0;i<col.length;i++) {
            if (result.equals(col[i].toLowerCase())) {
                cin=i;
                speakloc(i);
            }
//            else{
//                t1.speak("Sorry I didn't understand you", TextToSpeech.QUEUE_FLUSH, null);
//            }
        }
        /*if (result.equalsIgnoreCase(prefs.getString("task", ""))) {
            speakloc();
        }*/

    }

    public void speakloc(int in){
        count++;
        SharedPreferences prefs = getSharedPreferences("tasklist", MODE_PRIVATE);
        String[] col = (prefs.getString("location", "")).split(":");
//        t1.speak(prefs.getString("location", ""), TextToSpeech.QUEUE_FLUSH, null);
        t1.speak(col[in], TextToSpeech.QUEUE_FLUSH, null);
        conf();
    }

    public void conf(){
        if (count<3) {
            t1.speak("Is your query resolved", TextToSpeech.QUEUE_ADD, null);
            promptSpeechInput();
        }
        else{
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            callIntent.setData(Uri.parse("tel:" + number));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(callIntent);
        }
    }
}
