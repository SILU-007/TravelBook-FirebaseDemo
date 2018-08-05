package com.example.travelbook;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    public static ArrayList<TravelDeal> sTravelDealList;
    private static FirebaseUtil sFirebaseUtil;
    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    private static Activity sCallerActivity;

    public static boolean isAdminUser;
    private static final int RC_SIGN_IN = 123;

    private FirebaseUtil(){}

    public static void  openFBReference(String ref,Activity callerActivity){
        if(sFirebaseUtil==null) {
            sFirebaseUtil=new FirebaseUtil();
            sFirebaseDatabase = FirebaseDatabase.getInstance();
            sFirebaseAuth=FirebaseAuth.getInstance();
            sCallerActivity=callerActivity;
            sAuthStateListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth pFirebaseAuth) {
                        if(pFirebaseAuth.getCurrentUser()==null)
                        {
                            SignIn();
                        }
                        else
                        {
                            String UserId= sFirebaseAuth.getUid();
                        }
                }
            };
        }
        sTravelDealList=new ArrayList<TravelDeal>();
        sDatabaseReference=sFirebaseDatabase.getReference().child(ref);

    }
    private static void SignIn()
    {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
               // new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
                //new AuthUI.IdpConfig.FacebookBuilder().build(),
                //new AuthUI.IdpConfig.TwitterBuilder().build()
                );

// Create and launch sign-in intent
        sCallerActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachAuthListener()
    {
      sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }
    public static void detachAuthListener()
    {
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }
}
