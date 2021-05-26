package com.example.evotingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.goebl.david.Webb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class VoteActivity extends AppCompatActivity {
    private String state,legislative,district,phoneNumber;
    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voteactivity);
        Intent intent = getIntent();
        phoneNumber=intent.getStringExtra("phoneNumber");
        state=intent.getStringExtra("state");
        district=intent.getStringExtra("district");
        legislative=intent.getStringExtra("legislative");
        Runnable r = new CandidateRetriever();
        new Thread(r).start();
    }
    private class CandidateRetriever implements Runnable {
        ArrayList<String> candidateName;
        ArrayList<String> candidateImage;
        ArrayList<String> candidateSymbol;
        @Override
        public void run() {
            Webb webb = Webb.create();
            JSONObject jsonObject = webb.get("http://192.168.1.5:8000/viewactivepoll")
                    .useCaches(false)
                    .ensureSuccess()
                    .asJsonObject()
                    .getBody();
            try{
                JSONObject reader = new JSONObject((jsonObject.toString()));
                JSONObject k=reader.getJSONObject("k");
                JSONObject sta=k.getJSONObject(state);
                JSONObject dis=sta.getJSONObject(district);
                JSONObject le=dis.getJSONObject(legislative);

                candidateName =  new ArrayList<>();
                for (Iterator<String> it = le.keys(); it.hasNext(); ) {
                    candidateName.add(it.next());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Webb candidateProfile = Webb.create();
            JSONObject candidateProfileobject=candidateProfile.post("http://192.168.1.5:8000/availability_checker")
                    .useCaches(false)
                    .param("state",state)
                    .param("district",district)
                    .param("legislative",legislative)
                    .ensureSuccess()
                    .asJsonObject()
                    .getBody();
            try {
                candidateImage = new ArrayList<>();
                candidateSymbol=new ArrayList<>();
                JSONObject candidateProfileJson= new JSONObject(candidateProfileobject.toString());
                for(int i=0;i<candidateProfileJson.length()-3;i++){

                        JSONObject details = candidateProfileJson.getJSONObject(candidateName.get(i));
                        String candidateImageUrl= details.getString("candidate_image_url");
                        String candidateSymbolUrl = details.getString("candidate_symbol_url");
                        candidateImage.add(candidateImageUrl);
                        candidateSymbol.add(candidateSymbolUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListView listView = findViewById(R.id.candidate_list);

                    listView.setAdapter(new AdapterClass(candidateName,candidateImage,candidateSymbol,getBaseContext(),VoteActivity.this,state,district,legislative,phoneNumber));
                }
            });
 }
    }
}
