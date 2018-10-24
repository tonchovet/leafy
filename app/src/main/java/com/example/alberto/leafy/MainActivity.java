package com.example.alberto.leafy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout ProfileSection;
    private Button LogOut;
    private SignInButton SignInButton;
    private TextView Name, Email;
    private ImageView ProfilePic;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProfileSection = (LinearLayout) findViewById(R.id.prof_section);
        LogOut = (Button) findViewById(R.id.bn_logout);
        SignInButton = (SignInButton) findViewById(R.id.bn_login);
        Name = (TextView) findViewById(R.id.name);
        Email = (TextView) findViewById(R.id.email);
        ProfilePic = (ImageView) findViewById(R.id.prof_pic);

        SignInButton.setOnClickListener(this);
        LogOut.setOnClickListener(this);

        ProfileSection.setVisibility(View.GONE);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bn_login:
                signIn();
                break;
            case R.id.bn_logout:
                signOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn(){

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        startActivityForResult(intent, REQ_CODE);

    }

    private void signOut(){

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });

    }

    private void handleResult(GoogleSignInResult googleSignInResult){

        if(googleSignInResult.isSuccess()){
            GoogleSignInAccount account = googleSignInResult.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String imgUrl = account.getPhotoUrl().toString();

            Name.setText(name);
            Email.setText(email);

            Glide.with(this).load(imgUrl).into(ProfilePic);

            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    private void updateUI(Boolean isLogIn){

        if (isLogIn){
            ProfileSection.setVisibility(View.VISIBLE);
            SignInButton.setVisibility(View.GONE);
        } else {
            ProfileSection.setVisibility(View.GONE);
            SignInButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}
