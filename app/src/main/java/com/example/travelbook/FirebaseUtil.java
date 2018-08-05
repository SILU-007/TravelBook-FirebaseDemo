package com.example.travelbook;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil {
    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    public static ArrayList<TravelDeal> sTravelDealList;
    private static FirebaseUtil sFirebaseUtil;
    private FirebaseUtil(){}

    public static void  openFBReference(String ref){
        if(sFirebaseUtil==null) {
            sFirebaseUtil=new FirebaseUtil();
            sFirebaseDatabase = FirebaseDatabase.getInstance();
            sTravelDealList=new ArrayList<TravelDeal>();
        }
        sDatabaseReference=sFirebaseDatabase.getReference().child(ref);

    }
}
