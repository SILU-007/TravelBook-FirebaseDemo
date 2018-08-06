package com.example.travelbook;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DecimalFormat;


public class TravelDealInsertActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle;
    private EditText txtDescription;
    private EditText txtPrice;
    private Button mButton;
    private TextView mViewUpload;
    private ProgressBar mProgressBar;
    private static int REQ_CODE = 42;

    private TravelDeal currentDeal;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_deal);
        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        mViewUpload=findViewById(R.id.txtUpload);
        mProgressBar=findViewById(R.id.progressBarUpload);

        Intent localIntent = getIntent();
        currentDeal = (TravelDeal) localIntent.getSerializableExtra("clickedDeal");
        if (currentDeal == null) {
            currentDeal = new TravelDeal();
        }
        txtTitle.setText(currentDeal.getTitle());
        txtDescription.setText(currentDeal.getDescription());
        txtPrice.setText(currentDeal.getPrice());

        Button btnImage = findViewById(R.id.btnImage);
        getUploadImage(btnImage);

    }

    private void getUploadImage(Button pBtnImage) {
        pBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localIntent1 = new Intent(Intent.ACTION_GET_CONTENT);
                localIntent1.setType("image/jpeg");
                localIntent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(localIntent1, "UPLOAD PICTURE"), REQ_CODE);
            }
        });
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
        MenuInflater localMenuInflater = getMenuInflater();
        localMenuInflater.inflate(R.menu.insert_deal_menu, menu);
        if (FirebaseUtil.isAdminUser) {
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem(R.id.save).setVisible(true);
            enabledEdit(true);
        } else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.save).setVisible(false);
            enabledEdit(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                SaveData();

                break;
            case R.id.delete:
                DeleteDeal();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    public void SaveData() {

        currentDeal.setTitle(txtTitle.getText().toString());
        currentDeal.setDescription(txtDescription.getText().toString());
        currentDeal.setPrice(txtPrice.getText().toString());

        if (EmptyString(txtTitle) && EmptyString(txtDescription) && EmptyString(txtPrice)) {

//            TravelDeal localTravelDeal = new TravelDeal(title, des, price, "");
            if (currentDeal.getId() == null) {
                mDatabaseReference.push().setValue(currentDeal);
            } else {
                mDatabaseReference.child(currentDeal.getId()).setValue(currentDeal);
            }
            Toast.makeText(TravelDealInsertActivity.this, "Uploaded to Firebase", Toast.LENGTH_LONG).show();
            clean();
            backToList();
        }
    }

    public void DeleteDeal() {
        if (currentDeal.getId() == null) {
            Toast.makeText(this, "Please Save Deal first", Toast.LENGTH_SHORT).show();
        } else {
            mDatabaseReference.child(currentDeal.getId()).removeValue();
        }
        Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
        backToList();
    }

    private void backToList() {
        startActivity(new Intent(TravelDealInsertActivity.this, ListActivity.class));
    }

    private void enabledEdit(boolean isEnabled) {
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtTitle.setEnabled(isEnabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            //  Log.d("Deals", image.getName());
            StorageReference ref = FirebaseUtil.sStorageReference.child("images/" + imageUri.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(imageUri);
            uploadTask.addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot pTaskSnapshot) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0 * pTaskSnapshot.getBytesTransferred()) / pTaskSnapshot.getTotalByteCount();
                    String s=new DecimalFormat("##").format(progress);
                    mViewUpload.setText( " "+s+"%");
                }
            }).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot pTaskSnapshot) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mViewUpload.setVisibility(View.INVISIBLE);
                }
            });

        }

    }


}
