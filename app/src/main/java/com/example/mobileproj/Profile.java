package com.example.mobileproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    CircleImageView profileImage;
    TextView profileName;
    TextView Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImage = findViewById(R.id.circleImageView);
        profileName = findViewById(R.id.textView2);
        Email = findViewById(R.id.textView3);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            if(user.getDisplayName() != null)profileName.setText(user.getDisplayName());
            else profileName.setText("Please update display name.");

            if(user.getEmail() != null)Email.setText(user.getEmail());
            else Email.setText("No E-mail address attached to this account.");
        }


        if(user.getPhotoUrl()!=null){
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profileImage);
        }
    }

    public void edit(View view){
        startActivity(new Intent(Profile.this,editdetails.class));
    }
}
