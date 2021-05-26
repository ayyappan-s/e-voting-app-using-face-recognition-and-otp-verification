package com.example.evotingapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FaceRecognitionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private String imagePath,state,district,legislative,phoneNumber;
    ProgressDialog progressDialog;
    private Button verify;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent=getIntent();
        state=intent.getStringExtra("state");
        legislative=intent.getStringExtra("legislative");
        district=intent.getStringExtra("district");
        phoneNumber=intent.getStringExtra("phoneNumber");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facerecognition);
    }
    public void uploadImage(View view) {
        String[] perms = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(this,perms)){
                captureImage();
        }else{
            EasyPermissions.requestPermissions(this,"Allow Access to Upload Image",123,perms);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull  List<String> perms) {

    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getApplicationContext(),"Grant Permission To Upload Photo For Face Recognition",Toast.LENGTH_LONG);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==123){
            captureImage();
        }
        if (requestCode==215){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri uri = getImageUri(getApplicationContext(),photo);
            imagePath=getRealPathFromURI(uri);
            File file= new File(getRealPathFromURI(uri));
            ImageView capturedImage=findViewById(R.id.capturedImage);
            capturedImage.setImageBitmap(photo);
            Runnable runnable = new FaceMatcher(photo);
            new Thread(runnable).start();
        }
    }
    public int gen() {
        Random r = new Random( System.currentTimeMillis() );
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }
    private Uri getImageUri(Context icontext, Bitmap photo) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(icontext.getContentResolver(),photo,"user"+gen(),null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
    @AfterPermissionGranted(123)
    public void captureImage(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,215);
    }
    private class FaceMatcher implements Runnable {
        Bitmap photo;
        public FaceMatcher(Bitmap photo) {
            this.photo=photo;

        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
           runOnUiThread(() -> {
               verify = findViewById(R.id.verify);
               verify.setEnabled(false);
               progressDialog=new ProgressDialog(FaceRecognitionActivity.this);
               progressDialog.setMessage("Verifying...");
               progressDialog.setTitle("Recognizing Face");
               progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               progressDialog.show();
               progressDialog.setCancelable(false);
           });
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://192.168.1.5:8000/face_recognizer");
                MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                if (imagePath != null) {
                    File file = new File(imagePath);
                    Log.d("EDIT USER PROFILE", "UPLOAD: file length = " + file.length());
                    Log.d("EDIT USER PROFILE", "UPLOAD: file exist = " + file.exists());
                    mpEntity.addPart("voter_image", new FileBody(file, "application/octet"));
                    mpEntity.addPart("state", new StringBody(state));
                    mpEntity.addPart("district",new StringBody(district));
                    mpEntity.addPart("legislative",new StringBody(legislative));
                    mpEntity.addPart("phoneNumber",new StringBody(phoneNumber));

                    httppost.setEntity(mpEntity);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    Header header = entity.getContentEncoding();
                    Charset encoding = header ==null? StandardCharsets.UTF_8: Charsets.toCharset(header.getValue());
                    String json = EntityUtils.toString(entity,StandardCharsets.UTF_8);
                    JSONObject o = new JSONObject(json);
                    String val = o.getString("Found");
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        verify.setEnabled(true);
                        if (val.contains("TRUE")){
                        Intent intent = new Intent(FaceRecognitionActivity.this,VoteActivity.class);
                        intent.putExtra("phoneNumber",phoneNumber);
                        intent.putExtra("state",state);
                        intent.putExtra("district",district);
                        intent.putExtra("legislative",legislative);
                        startActivity(intent);
                        finish();
                        }else{
                            TextView facerec_err=findViewById(R.id.facerec_err);
                            facerec_err.setText("Face didn't Match or Image contain So many Faces");
                        }
                    });
                }
            }catch (Exception e){
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(),"Error Occured! Please Try Again",Toast.LENGTH_LONG).show();
                    verify.setEnabled(true);
                    progressDialog.dismiss();
                });
            }
        }
    }
}
