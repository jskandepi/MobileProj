package com.example.mobileproj;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class homepage extends AppCompatActivity {

    private static final String TAG = "homepage";
    int AUTHUI_REQUESTCODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(this,MainActivity.class));
            this.finish();
        }
    }

    public void logreg(View view){

        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        Intent intent = AuthUI.getInstance().
                createSignInIntentBuilder().
                setAvailableProviders(provider).
                setLogo(R.drawable.download).
                build();

        startActivityForResult(intent, AUTHUI_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTHUI_REQUESTCODE){
            if(resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG,"onActivityResult: "+user.getEmail());
                if(user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()){
                    Toast.makeText(this,"Welcome new User",Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(this, "Welcome Back",Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this,MainActivity.class));
                this.finish();
            }
            else{
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if(response == null){
                    Log.d(TAG,"User has cancelled Sign-in request");
                }
                else Log.e(TAG,"Error:",response.getError());
            }
        }
    }
}
