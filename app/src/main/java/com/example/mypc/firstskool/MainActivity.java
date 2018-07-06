package com.example.mypc.firstskool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import static com.example.mypc.firstskool.R.drawable.ic_perm_identity_black_24dp;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener  {

    private TextView txtUserEmailId,txtuserName;
    private LinearLayout prof_section;
    private SignInButton btnLogin;
    private Button btn_logout;
    private ImageView imgViewPhoto;

    private SharedPreferences userDetails;

    private GoogleApiClient googleApiClient;

    private static final int REQ_CODE= 9001;

    private String name,email,imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        
        
        imgViewPhoto=findViewById(R.id.imgViewPhoto);

        prof_section=findViewById(R.id.prof_section);

        btn_logout= findViewById(R.id.btn_logout);
        btnLogin=findViewById(R.id.btnLogin);

        txtUserEmailId=findViewById(R.id.txtUserEmailId);
        txtuserName=findViewById(R.id.txtuserName);
        prof_section.setVisibility(View.GONE);

        btnLogin.setOnClickListener(MainActivity.this);
        btn_logout.setOnClickListener(MainActivity.this);

        GoogleSignInOptions signInOptions= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();


        userDetails = getSharedPreferences("userGmailLogin", MODE_PRIVATE);

        //getting the information if the user is already login
        if(userDetails.getBoolean("userLogin",false))
        {
            name = userDetails.getString("userName","");
            email = userDetails.getString("userGmailId","");
          //  imgUrl = userDetails.getString("userImageUrl","");
            updateUi(true);
        }



    }
    
    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnLogin:

            {
                Log.d("LOG","LOGIN");
                signIn();
            }
                break;

            case R.id.btn_logout:
                signOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn()
    {
        Intent intent =Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);

    }

    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUi(false);
            }
        });
    }

    private void handelResult(GoogleSignInResult result)
    {
        if(result.isSuccess())
        {
            //get user information

            GoogleSignInAccount account= result.getSignInAccount();
            name= account.getDisplayName();
            email= account.getEmail();

            //imgUrl=""+account.getPhotoUrl().toString();

            //Filling the SharedPrefrences to be used in futur
            SharedPreferences.Editor editor = userDetails.edit();
            editor.putBoolean("userLogin",true);
            editor.putString("userName",name);
            editor.putString("userGmailId",email);
            //editor.putString("userImageUrl",imgUrl);
            editor.commit();

            updateUi(true);

        }
        else {
            updateUi(false);
        }
    }

    private void updateUi(boolean isLoginFlag)
    {
        if(isLoginFlag)
        {
            //Setting up the textView
            txtUserEmailId.setText(email);
            txtuserName.setText(name);

           /* if(!imgUrl.equals(""))
            //Loadin the image from url using Glide library
            {
                Glide.with(this).load(imgUrl).into(imgViewPhoto);
            }

            else {
                imgViewPhoto.setImageResource(R.drawable.ic_perm_identity_black_24dp);
            }*/

            prof_section.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
        }
        else
        {
            prof_section.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE)
        {
            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handelResult(result);
        }
    }

}
