package com.example.tictactoegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JoiningActivity extends AppCompatActivity {
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private EditText codeEdit;
    private DatabaseReference mref;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining);
        codeEdit = findViewById(R.id.codeEdit);
        mref = db.getReference();
        currentUser = auth.getCurrentUser();

    }

    public void create(View view) {

        String uid = currentUser.getUid();
        String name = currentUser.getDisplayName();
        String code = GenerateCode.getCode(uid);
        Log.d("chris_auth",code);
        Map<String,Object> map = new HashMap<>();
        map.put("player1",name);
        map.put("player1Score",0);
        map.put("player2Score",0);
        mref.child(code).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getApplicationContext(),OnlineActivity.class);
                intent.putExtra("code",code);
                intent.putExtra("created",true);
                startActivity(intent);
                finish();
            }
        });

    }

    public void join(View view) {
        String code = codeEdit.getText().toString().trim();
        String name = currentUser.getDisplayName();
        if(code.length()==8){
            try{
                mref.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Map map = (Map) snapshot.getValue();
                            Log.d("chris",map.toString());
                            if(map.containsKey("player2")){
                                codeEdit.setError("incorrect code");
                            }
                            else{

                                Map<String,Object> m = new HashMap<>();
                                m.put("player2",name);
                                mref.child(code).updateChildren(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(getApplicationContext(),OnlineActivity.class);
                                        intent.putExtra("code",code);
                                        intent.putExtra("created",false);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                }
                        }catch(Exception e){
                            codeEdit.setError("incorrect code");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }catch(Exception e){
                codeEdit.setError("incorrect code");
            }
        }
        else{
            codeEdit.setError("incorrect code");
        }

    }
}