package com.example.travelbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ListActivity extends AppCompatActivity {

    private  RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initMethod();
    }

    private void initMethod() {
        FirebaseUtil.openFBReference("traveldeals",this);
         mRecyclerView=findViewById(R.id.RecyclerViewDeal);
        RecyclerView.LayoutManager localManagerl=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        DealsAdapter localAdapter=new DealsAdapter();
        mRecyclerView.setLayoutManager(localManagerl);
        mRecyclerView.setAdapter(localAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater localMenuInflater=getMenuInflater();
        localMenuInflater.inflate(R.menu.home_list_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.next:startActivity(new Intent(ListActivity.this,TravelDealInsertActivity.class));
                break;
           case R.id.logout:
               AuthUI.getInstance()
                       .signOut(this)
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           public void onComplete(@NonNull Task<Void> task) {
                               //FirebaseUtil.attachAuthListener();
                           }
                       });
               break;
           case R.id.deleteId:
               AuthUI.getInstance()
                       .delete(this)
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               // FirebaseUtil.attachAuthListener();
                               Toast.makeText(ListActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                           }
                       });
               break;
                default: return super.onOptionsItemSelected(item);
       }
       return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachAuthListener();
       initMethod();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUtil.detachAuthListener();
    }
}
