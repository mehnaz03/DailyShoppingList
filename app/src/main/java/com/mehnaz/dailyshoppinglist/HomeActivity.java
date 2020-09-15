package com.mehnaz.dailyshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mehnaz.dailyshoppinglist.Model.Data;

import java.text.DateFormat;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab_button;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView homeRecycler;
    FirebaseRecyclerAdapter<Data,myViewHolder>adapter;
    String uId;

    private TextView totalSumResult;

    private String type;
    private int amount;
    private  String note;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Shopping List");

        totalSumResult = findViewById(R.id.tvTotalAmount);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
            uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);
        mDatabase.keepSynced(true);

        homeRecycler = findViewById(R.id.home_recycler);
        LinearLayoutManager layoutManager =  new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

//       homeRecycler.setHasFixedSize(true);
        homeRecycler.setLayoutManager(layoutManager);
        fetch();
        homeRecycler.setAdapter(adapter);

        // Total sum number
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalammount = 0;

                for(DataSnapshot snap:snapshot.getChildren()){

                    Data data = snap.getValue(Data.class);

                    totalammount+= data.getAmount();

                    String sttotal = String.valueOf(totalammount + " .00 ");
                    totalSumResult.setText(sttotal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void customDialog() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_data,null);

        AlertDialog dialog = mydialog.create();

        dialog.setView(myview);

        EditText type = myview.findViewById(R.id.etType);
        EditText amount =myview.findViewById(R.id.etAmount);
        EditText note =myview.findViewById(R.id.etNote);
        Button save =myview.findViewById(R.id.btnSave);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Options"," data : ki hoy ");
                String mType= type.getText().toString().trim();
                String mAmount= amount.getText().toString().trim();
                String mNote= note.getText().toString().trim();

                int ammount = Integer.parseInt(mAmount);

                if(TextUtils.isEmpty(mType)){
                    type.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(mAmount)){
                    amount.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(mNote)){
                    note.setError("Required Field...");
                    return;
                }
                Log.d("Options"," data : ki hoy wstfws");
                String id = mDatabase.push().getKey();
                String  date = DateFormat.getDateInstance().format(new Date());

                Data data = new  Data (mType,ammount,mNote,date,id);

                mDatabase.child(id).setValue(data);
                Toast.makeText(getApplicationContext(),"Data Add",Toast.LENGTH_SHORT).show();


                dialog.dismiss();
            }
        });
         dialog.show();

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//
//
//
//    }
    private void fetch() {
        fab_button = findViewById(R.id.fab);

        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });
        Query query1 =  FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query1, Data.class)
                        .build();
        Log.d("Options"," data : "+ options);

        adapter = new FirebaseRecyclerAdapter<Data, myViewHolder>(options) {

            @Override public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_data, parent, false);
                return new myViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Data model) {
                holder.setDate(model.getDate());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setAmount(model.getAmount());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();
                        type = model.getType();
                        amount = model.getAmount();
                        note = model.getNote();

                        updateData();
                    }
                });

            }

        };


    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    public static class myViewHolder extends RecyclerView.ViewHolder{



        public myViewHolder(View itemView){
            super(itemView);

        }

        public void setType(String type){
            TextView mType = itemView.findViewById(R.id.tvType);
            mType.setText(type);
        }
        public void setNote(String note){
            TextView mNote = itemView.findViewById(R.id.tvNote);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate = itemView.findViewById(R.id.tvDate);
            mDate.setText(date);
        }

        public void setAmount(int amount){
            TextView mAmount = itemView.findViewById(R.id.tvAmount);

            String ammount = String.valueOf(amount);
            mAmount.setText(ammount);
        }

    }


    public void updateData(){
        AlertDialog.Builder mydialog2 = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview2 = inflater.inflate(R.layout.update_input_field,null);

        AlertDialog dialog2 = mydialog2.create();

        dialog2.setView(myview2);

        final EditText edt_type = myview2.findViewById(R.id.etTypeUpdate);
        final EditText edt_ammount = myview2.findViewById(R.id.etAmountUpdate);
        final EditText edt_note = myview2.findViewById(R.id.etNoteUpdate);
        Button btn_update = myview2.findViewById(R.id.btnUpdate);
        Button btn_dlt = myview2.findViewById(R.id.btnDelete);

        edt_type.setText(type);
        edt_type.setSelection(type.length());

        edt_ammount.setText(String.valueOf(amount));
        edt_ammount.setSelection(String.valueOf(amount).length());

        edt_note.setText(note);
        edt_note.setSelection(note.length());



        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mType= edt_type.getText().toString().trim();
                String mAmmount  = edt_ammount.getText().toString().trim();
                String  mNote = edt_note.getText().toString().trim();

                int intAmount = Integer.parseInt(mAmmount);
                String date=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(type,intAmount,note,date,post_key);

                mDatabase.child(post_key).setValue(data);


                dialog2.dismiss();


            }
        });

        btn_dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child(post_key).removeValue();

                dialog2.dismiss();

            }
        });


        dialog2.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.log_out:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
