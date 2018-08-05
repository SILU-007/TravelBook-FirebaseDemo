package com.example.travelbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InsertActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle;
    private EditText txtDescription;
    private EditText txtPrice;
    private Button mButton;
    private TextView mTextView;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
       FirebaseUtil.openFBReference("traveldeals");

        mFirebaseDatabase =  FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        mTextView=findViewById(R.id.textData);
        mButton = findViewById(R.id.buttonSave);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                SaveData();
            }
        });
        dataLoader();

//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                retrivalCode(dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
//        mDatabaseReference.addValueEventListener(postListener);

    }

    private void dataLoader() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot pDataSnapshot, @Nullable String pS) {


                retrivalCode(pDataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot pDataSnapshot, @Nullable String pS) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot pDataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot pDataSnapshot, @Nullable String pS) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError pDatabaseError) {

            }
        };
    }

    private void retrivalCode( DataSnapshot pDataSnapshot) {
        if(pDataSnapshot!=null) {
            TravelDeal localDeal = pDataSnapshot.getValue(TravelDeal.class);
            if(localDeal!=null) {
                String localTitle = mTextView.getText().toString();
                if(!localTitle.equals("null")) {
                    String show = localTitle + "\n" + localDeal.getTitle();
                    mTextView.setText(show);
                }
            }
        }
        else
        {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    private void clean() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.requestFocus();
    }

    private boolean EmptyString(View v) {
        EditText localEditText = (EditText) v;
        if (localEditText.getText().toString().equals("") || localEditText.getText().toString().equals("null")) {
            localEditText.setError("Field cannot be Empty");
            return false;
        }
        return true;
    }

    private void SaveData() {

        String title = txtTitle.getText().toString();

        String des = txtDescription.getText().toString();
        String price = txtPrice.getText().toString();
        if (EmptyString(txtTitle) && EmptyString(txtDescription) && EmptyString(txtPrice)) {
            TravelDeal localTravelDeal = new TravelDeal(title, des, price, "");
            mDatabaseReference.push().setValue(localTravelDeal);
            Toast.makeText(InsertActivity.this, "Uploaded to Firebase", Toast.LENGTH_LONG).show();
            clean();

        }
    }
}
