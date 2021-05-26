package com.example.evotingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String state;
    private String district;
    private String legislative;
    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otpverification);
        Intent intent=getIntent();
        phoneNumber=intent.getStringExtra("phoneNumber");
        state=intent.getStringExtra("state");
        district=intent.getStringExtra("district");
        legislative=intent.getStringExtra("legislative");
        mAuth = FirebaseAuth.getInstance();
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull  PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                if(code!=null){
                    EditText editText=findViewById(R.id.otpText);
                    editText.setText(code);
                }
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
               Toast.makeText(getApplicationContext(),"Verification Failed",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(getApplicationContext(),"Code Sent",Toast.LENGTH_LONG).show();

                mVerificationId=verificationId;
            }
        };
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91"+phoneNumber)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void verifyVerificationCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(OtpVerificationActivity.this,FaceRecognitionActivity.class);
                            intent.putExtra("state",state);
                            intent.putExtra("district",district);
                            intent.putExtra("legislative",legislative);
                            intent.putExtra("phoneNumber",phoneNumber);
                            startActivity(intent);
                        }
                        else{
                            TextView textView=findViewById(R.id.textView8);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                textView.setText("Invalid code entered");
                            }else {
                                textView.setText("Error Occured! Please Try Again Later");
                            }
                        }
                    }
                });
    }
    public void verifyOTP(View view) {
        EditText otpText=findViewById(R.id.otpText);
        String code=otpText.getText().toString();
        verifyVerificationCode(code);
    }
}
