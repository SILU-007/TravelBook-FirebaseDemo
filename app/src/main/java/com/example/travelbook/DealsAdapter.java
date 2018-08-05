package com.example.travelbook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealAdapter> {

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mDatabaseReference;
    private ArrayList<TravelDeal> mDeals;
    private ChildEventListener mChildEventListener;
    private ArrayList<TravelDeal> passedDeals;

    public DealsAdapter() {

        mFirebaseDatabase =  FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        mDeals=FirebaseUtil.sTravelDealList;
        passedDeals=new ArrayList<TravelDeal>();
        dataLoader();
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    public DealAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        Context localContext= parent.getContext();
        View localView=LayoutInflater.from(localContext).inflate(R.layout.rv_row,parent,false);
        return new DealAdapter(localView);
    }

    @Override
    public void onBindViewHolder(DealAdapter holder, int position) {
        TravelDeal localTravelDeal=passedDeals.get(position);
        Log.e("Deals","BINDER: "+localTravelDeal.getTitle()+position);
//        holder.mTextView.setText(localTravelDeal.getTitle());
        holder.bind(localTravelDeal);
    }

    @Override
    public int getItemCount() {
        return passedDeals.size();
    }


    private void dataLoader() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot pDataSnapshot, @Nullable String pS) {


                retrivalCode(pDataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot pDataSnapshot, @Nullable String pS) {
                    retrivalCode(pDataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot pDataSnapshot) {
                retrivalCode(pDataSnapshot);
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
            if (localDeal != null) {
               // Log.d("Deals", "retrivalCode:  " + localDeal.getTitle()+mDeals.size());
                localDeal.setId(pDataSnapshot.getKey());
                mDeals.add(localDeal);
               // Log.d("Deals", "LIST SIZE:" + mDeals.get(0).getTitle());
               updateRecyclerView();


            }
        }
    }
    private void updateRecyclerView()
    {
        passedDeals.clear();
        passedDeals.addAll(mDeals);
        notifyDataSetChanged();

    }

    public class DealAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;
        private TextView mDescription;
        private TextView mPrice;
        private onClickListenerInterface mListenerInterface;

        public DealAdapter(View itemView) {
            super(itemView);
            mTitle= itemView.findViewById(R.id.textTitle);
            mDescription=itemView.findViewById(R.id.textDescription);
            mPrice=itemView.findViewById(R.id.textPrice);
            itemView.setOnClickListener(this);

        }
        public void bind(TravelDeal deal)
        {
            mTitle.setText(deal.getTitle());
            mDescription.setText(deal.getDescription());
            String price="Rs."+deal.getPrice();
            mPrice.setText(price);
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            //Toast.makeText(v.getContext(), "v"+getAdapterPosition(), Toast.LENGTH_SHORT).show();
            Log.d("Deals","position:"+String.valueOf(position));
            TravelDeal clickedDeal=passedDeals.get(position);
            Intent localIntent=new Intent(v.getContext(),TravelDealInsertActivity.class);
            localIntent.putExtra("clickedDeal",clickedDeal);
            v.getContext().startActivity(localIntent);

        }


    }
}
