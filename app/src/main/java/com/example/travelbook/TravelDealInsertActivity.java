package com.example.travelbook;

import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

import retrofit2.http.Url;


public class TravelDealInsertActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtTitle;
    private EditText txtDescription;
    private EditText txtPrice;
    private ImageView mImageView;

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
        mImageView = findViewById(R.id.image);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);

        mProgressBar = findViewById(R.id.progressBarUpload);

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
        if(currentDeal.getImageUrl()!=null)
        showImage(currentDeal.getImageUrl());

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
            final Uri imageUri = data.getData();

            try {
                Upload_DownloadUrl(imageUri);
            } catch (Exception pE) {
                pE.printStackTrace();
            }

        }

    }

    private void Upload_DownloadUrl(Uri pImageUri) throws Exception {
        Uri image = null;
        if (pImageUri != null) {
            showImage(pImageUri.toString());
            image = FilePathNameExtractor(pImageUri);
        }
        final StorageReference ref = FirebaseUtil.sStorageReference.child("images/" + image);

        ref.putFile(pImageUri).
                addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot pTaskSnapshot) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        double progress = (100.0 * pTaskSnapshot.getBytesTransferred()) / pTaskSnapshot.getTotalByteCount();
                        mProgressBar.setProgress((int) progress);
//                        String s = new DecimalFormat("##").format(progress);

                    }
                })
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        // Forward any exceptions
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return ref.getDownloadUrl();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri downloadUri) {

                        currentDeal.setImageUrl(downloadUri.toString());
                            mProgressBar.setVisibility(View.GONE);
                        Log.d("Firebase url", "" + downloadUri.toString());
                        showImage(downloadUri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
    }

    private Uri FilePathNameExtractor(Uri pImageUri) {
        String path = pImageUri.getLastPathSegment();
        String filename = path.substring(path.lastIndexOf("/") + 1);
        String file;
//        if (filename.indexOf(".") > 0) {
//            file = filename.substring(0, filename.lastIndexOf("."));
//        } else {
//            file = filename;
//        }

        return Uri.parse(filename);
        //   Log.d("DealsPath", filename);
    }

    public void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width,(width*9)/10)
                    .centerCrop()
                    .into(mImageView);
            // Glide.with(this).load(url).into(mImageView);
        }
    }


}
