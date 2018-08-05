package com.example.travelbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TravelDealInsertActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle;
    private EditText txtDescription;
    private EditText txtPrice;
    private Button mButton;

    private TravelDeal currentDeal;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        FirebaseUtil.openFBReference("traveldeals",this);
        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);

       // mButton = findViewById(R.id.buttonSave);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View pView) {
//                SaveData();
//            }
//        });
        Intent localIntent = getIntent();
        currentDeal = (TravelDeal) localIntent.getSerializableExtra("clickedDeal");
        if (currentDeal == null) {
            currentDeal = new TravelDeal();
        }
        txtTitle.setText(currentDeal.getTitle());
        txtDescription.setText(currentDeal.getDescription());
        txtPrice.setText(currentDeal.getPrice());


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

    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater localMenuInflater=getMenuInflater();
        localMenuInflater.inflate(R.menu.insert_deal_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.save: SaveData();

            break;
            case R.id.delete:DeleteDeal();
            break;
            default: return super.onOptionsItemSelected(item);
        }
       return true;

    }

    public   void SaveData() {

        currentDeal.setTitle(txtTitle.getText().toString());
        currentDeal.setDescription(txtDescription.getText().toString());
        currentDeal.setPrice(txtPrice.getText().toString());

        if (EmptyString(txtTitle) && EmptyString(txtDescription) && EmptyString(txtPrice)) {

//            TravelDeal localTravelDeal = new TravelDeal(title, des, price, "");
            if(currentDeal.getId()==null){
                mDatabaseReference.push().setValue(currentDeal);
            }
            else{
                mDatabaseReference.child(currentDeal.getId()).setValue(currentDeal);
            }
            Toast.makeText(TravelDealInsertActivity.this, "Uploaded to Firebase", Toast.LENGTH_LONG).show();
            clean();
            backToList();
        }
    }
    public void DeleteDeal()
    {
        if(currentDeal.getId()==null){
            Toast.makeText(this, "Please Save Deal first", Toast.LENGTH_SHORT).show();
        }
        else{
            mDatabaseReference.child(currentDeal.getId()).removeValue();
        }
        Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
        backToList();
    }
    private void backToList(){
        startActivity(new Intent(TravelDealInsertActivity.this, ListActivity.class));
    }
}
