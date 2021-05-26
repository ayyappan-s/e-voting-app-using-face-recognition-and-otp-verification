package com.example.evotingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Spinner stateSpinner;
    private Spinner districtSpinner;
    private Spinner legislativeSpinner;
    private String spinnerState;
    private String spinnerDistrict;
    private String spinnerLegislative;
    private TextView responeText;
    private Button sendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stateSpinner=findViewById(R.id.stateSpinner);
        districtSpinner=findViewById(R.id.districtSpinner);
        legislativeSpinner=findViewById(R.id.legislativeSpinner);
        responeText=findViewById(R.id.textView5);
        sendOTP=findViewById(R.id.sendOTP);
        sendOTP.setBackgroundColor(Color.GRAY);
        editText = findViewById(R.id.editTextPhone);
        sendOTP.setEnabled(false);
        legislativeSpinner.setEnabled(false);
        districtSpinner.setEnabled(false);
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String district=parent.getItemAtPosition(position).toString();
                spinnerDistrict=district;
                if(district.contains("Select District")){
                    Toast.makeText(getApplicationContext(),district,Toast.LENGTH_LONG).show();
                }
                else{

                    legislativeSpinner.setEnabled(true);
                    Runnable r = new SpinnerAdder(district,"district");
                    new Thread(r).start();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        stateSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String state=parent.getItemAtPosition(position).toString();
                        spinnerState=state;
                        if(state.contains("Select State")){
                            Toast.makeText(getApplicationContext(),state,Toast.LENGTH_LONG).show();
                        }
                        else{


                            Runnable r = new SpinnerAdder(state,"state");
                            districtSpinner.setEnabled(true);
                            new Thread(r).start();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
        legislativeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String val=parent.getItemAtPosition(position).toString();
                spinnerLegislative=val;
                if(val=="Select Constituency"){
                    Toast.makeText(getApplicationContext(),val,Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length=editText.length();
                if(length>10 || length<10){
                    sendOTP.setEnabled(false);
                    sendOTP.setBackgroundColor(Color.GRAY);
                }
                if(length==10){
                    sendOTP.setEnabled(true);
                    sendOTP.setBackgroundColor(Color.BLACK);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void SendOTP(View view) {
    Runnable runnable = new VoterChecker(spinnerState,spinnerDistrict,spinnerLegislative);
    new Thread(runnable).start();
        Runnable runnable2 = new VoterConfirmation();
        new Thread(runnable2).start();
    }
    class SpinnerAdder implements Runnable {
        String state;
        String val;
    public SpinnerAdder(Object parameter, String type){
           this.state =parameter.toString();
          val=type;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        if(val=="state"){
            Webb webb = Webb.create();
            JSONObject result = webb.post("http://192.168.1.5:8000/state_submission")
                    .useCaches(false)
                    .param("state",state)
                    .ensureSuccess()
                    .asJsonObject()
                    .getBody();
            try {
                JSONObject reader= new JSONObject(result.toString());
                JSONArray array = reader.getJSONArray("district");
                Spinner spinner=findViewById(R.id.districtSpinner);
                ArrayList arrayList=new ArrayList();
                arrayList.add("Select District");
                for(int i=0;i<array.length();i++){
                    arrayList.add(array.get(i));
                }

                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,arrayList);
                runOnUiThread(() -> {
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    spinner.setAdapter(spinnerArrayAdapter);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (val=="district"){
            JSONObject msg;

            Webb webb = Webb.create();
            JSONObject result = webb.post("http://192.168.1.5:8000/district_submission")
                    .useCaches(false)
                    .param("district",state)
                    .ensureSuccess()
                    .asJsonObject()
                    .getBody();
            try {
                JSONObject reader= new JSONObject(result.toString());
                JSONArray array = reader.getJSONArray("legislative");
                Spinner spinner=findViewById(R.id.legislativeSpinner);
                ArrayList arrayList=new ArrayList();
                arrayList.add("Select Constituency");
                for(int i=0;i<array.length();i++){
                    arrayList.add(array.get(i));
                }
                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,arrayList);
                runOnUiThread(() -> {
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    spinner.setAdapter(spinnerArrayAdapter);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
class VoterChecker implements Runnable{
    String state,district,legislative;
    public VoterChecker(String state,String district,String legislative){
        this.state=state;
        this.district=district;
        this.legislative=legislative;
    }
    @Override
    public void run() {
        Webb webb = Webb.create();
        JSONObject result = webb.post("http://192.168.1.5:8000/check_user")
                .useCaches(false)
                .param("state",spinnerState)
                .param("district",spinnerDistrict)
                .param("legislative",spinnerLegislative)
                .param("phoneNumber",editText.getText())
                .ensureSuccess()
                .asJsonObject()
                .getBody();
        try {

            if (!result.get("res").toString().contentEquals("FOUND")){
                runOnUiThread(() -> responeText.setText("Voter Does Not Exist! \nContact Admin For More Details"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
    private class VoterConfirmation implements Runnable {
        @Override
        public void run() {
            Webb webb =Webb.create();
            JSONObject jsonObject = webb.post("http://192.168.1.5:8000/voter_confirmation")
                    .useCaches(false)
                    .ensureSuccess()
                    .param("state",spinnerState)
                    .param("district",spinnerDistrict)
                    .param("legislative",spinnerLegislative)
                    .param("phoneNumber",editText.getText())
                    .asJsonObject()
                    .getBody();
            try {
                if(jsonObject.get("res").toString().contains("NOT FOUND")){
                   runOnUiThread(() -> responeText.setText("Voter Does Not Exist! \nContact Admin For More Details"));
                }
                else if(jsonObject.get("res").toString().contains("NOTVALID")){
                    runOnUiThread(() -> responeText.setText("You Have Already Voted"));
                }
                else if(jsonObject.get("res").toString().contains("VALID")){
                    Webb webb1 = Webb.create();
                    JSONObject jsonObject1 = webb1.post("http://192.168.1.5:8000/check_poll_status")
                            .useCaches(false)
                            .ensureSuccess()
                            .param("state",spinnerState)
                            .param("district",spinnerDistrict)
                            .param("legislative",spinnerLegislative)
                            .param("phoneNumber",editText.getText())
                            .asJsonObject()
                            .getBody();
                    if(jsonObject1.get("msg").toString().contains("notrunning")){
                        responeText.setText("Poll is Not Available For this Constituency");
                    }else{
                        runOnUiThread(() -> {
                            Intent intent = new Intent(MainActivity.this,OtpVerificationActivity.class);
                            intent.putExtra("phoneNumber",editText.getText().toString());
                            intent.putExtra("state",spinnerState);
                            intent.putExtra("district",spinnerDistrict);
                            intent.putExtra("legislative",spinnerLegislative);
                            startActivity(intent);
                            finish();
                        });
                    }
                }
                else if(jsonObject.get("res").toString().contains("Error")){
                    runOnUiThread(() -> responeText.setText("Error,Please Try Later"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}