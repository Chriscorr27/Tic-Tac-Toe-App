package com.example.tictactoegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class OnlineActivity extends AppCompatActivity {
    int chance=1;
    int wait=0;
    int played =0;
    int scoreX=0,scoreO=0;
    int playerSymbol=1;
    boolean gameActive = true;
    int activePlayer = 1;
    int[] gameState ={2,2,2,2,2,2,2,2,2,};
    int[][] winPosn = { {0,1,2} , {3,4,5} , {6,7,8},
            {0,3,6} , {1,4,7} , {2,5,8},
            {0,4,8} , {2,4,6} };

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference mref,mref1;
    TextView player1Name,player2Name,statusJoin,player1,player2,codeView;
    private ProgressBar pb;
    String name1,name2;
    //game

    ImageView[] gridPosn = new ImageView[9];
    TextView p1Name,p2Name,score,p1Score,p2Score,status;
    ImageView gridImage;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        String code;
        Bundle bundle = getIntent().getExtras();
        code = bundle.getString("code");
        Boolean created = bundle.getBoolean("created");

        pb=findViewById(R.id.pb);

        player1=findViewById(R.id.player1);
        player2=findViewById(R.id.player2);
        player1Name = findViewById(R.id.player1Name);
        player2Name = findViewById(R.id.player2Name);
        statusJoin = findViewById(R.id.statusJoin);
        codeView = findViewById(R.id.codeView);

        p1Name = findViewById(R.id.yourName);
        p1Score = findViewById(R.id.yourScore);
        p2Name= findViewById(R.id.opponent);
        p2Score = findViewById(R.id.opponentScore);
        score = findViewById(R.id.score);
        status = findViewById(R.id.status);
        gridImage = findViewById(R.id.imageView);
        mainLayout = findViewById(R.id.linearLayout);

        gridPosn[0]=findViewById(R.id.img0);
        gridPosn[1]=findViewById(R.id.img1);
        gridPosn[2]=findViewById(R.id.img2);
        gridPosn[3]=findViewById(R.id.img3);
        gridPosn[4]=findViewById(R.id.img4);
        gridPosn[5]=findViewById(R.id.img5);
        gridPosn[6]=findViewById(R.id.img6);
        gridPosn[7]=findViewById(R.id.img7);
        gridPosn[8]=findViewById(R.id.img8);

        mref = db.getReference(code);
        if(created){
            playerSymbol=1;
            pb.setVisibility(View.GONE);
            codeView.setText(code);
            player1.setVisibility(View.VISIBLE);
            player1Name.setVisibility(View.VISIBLE);
            player2.setVisibility(View.VISIBLE);
            player2Name.setVisibility(View.VISIBLE);
            statusJoin.setVisibility(View.VISIBLE);
            codeView.setVisibility(View.VISIBLE);

        }else{
            playerSymbol = 0;
        }
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                @SuppressWarnings("rawtypes") Map map = (Map) snapshot.getValue();
                if(map.get("player1").toString()!=null ){

                    name1=map.get("player1").toString();
                    player1Name.setText(map.get("player1").toString());
                    p1Name.setText(map.get("player1").toString());
                    String s=name1+" chance";
                    status.setText(s);
                    status.setTextColor(getResources().getColor(R.color.ligth_grey));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    if (snapshot.getKey().equals("player2")) {
                        if (snapshot.getValue().toString() != null) {
                            name2=snapshot.getValue().toString();
                            player2Name.setText(snapshot.getValue().toString());
                            p2Name.setText(snapshot.getValue().toString());
                            statusJoin.setText("Apponent joined");
                            changeVisiblity();
                        }
                    } else if (!snapshot.getKey().equals("player1")) {
                        Log.d("chris", snapshot.toString());
                        int posn = Integer.parseInt(snapshot.getKey());
                        ImageView img = gridPosn[posn];
                        img.setTranslationY(-1000f);
                        String str = snapshot.getValue().toString();
                        if (str.equals("0")) {
                            gameState[posn] = 0;
                            gridPosn[posn].setImageResource(R.drawable.o);
                        } else if (str.equals("1")) {
                            gameState[posn] = 1;
                            gridPosn[posn].setImageResource(R.drawable.x);
                        }
                        img.animate().translationYBy(1000f).setDuration(300);
                        played++;
                        chance=(chance+1)%2;
                        String s;
                        if(chance==0){
                            s=name2+" chance";
                            status.setText(s);
                            status.setTextColor(getResources().getColor(R.color.ligth_grey));
                        }
                        else if(chance==1){
                            s=name1+" chance";
                            status.setText(s);
                            status.setTextColor(getResources().getColor(R.color.ligth_grey));
                        }
                        checkMatch();
                    }
                }
                catch (Exception ignored){

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                resetGridPosn();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if(snapshot.getKey().equals("player1") || snapshot.getKey().equals("player2")){
                    Map<String,Object> map = new HashMap<>();
                    map.put(code,null);
                    mref1 = db.getReference();
                    mref1.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showDialog();
                        }
                    });

                }
                else{
                    resetGridPosn();
                    String s;
                    if(chance==0){
                        s=name2+" chance";
                        status.setText(s);
                        status.setTextColor(getResources().getColor(R.color.ligth_grey));
                    }
                    else if(chance==1){
                        s=name1+" chance";
                        status.setText(s);
                        status.setTextColor(getResources().getColor(R.color.ligth_grey));
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tic Tac Toe")
                .setMessage("your opponent has left the match")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), Menu.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.setCancelable(false);
        builder.show();

    }

    private void resetGridPosn() {
        for(int i=0;i<9;i++){
            gameState[i]=2;
            gridPosn[i].setImageResource(0);
        }
        played=0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Map<String,Object> map = new HashMap<>();
        if(playerSymbol==1){
            map.put("player1",null);
        }
        else if(playerSymbol==0){
            map.put("player2",null);
        }
        mref.updateChildren(map);
    }

    public void quitClicked(View view) {
        showQuitDialog();
    }

    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tic Tac Toe")
                .setMessage("Do you want to quit?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String,Object> map = new HashMap<>();
                        if(playerSymbol==1){
                            map.put("player1",null);
                        }
                        else if(playerSymbol==0){
                            map.put("player2",null);
                        }
                        mref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(getApplicationContext(),Menu.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                })
        .setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void playerTap(View view) {
        ImageView img = (ImageView)view;
        int tappedImage = Integer.parseInt(img.getTag().toString());
        HashMap<String,Object> map =new HashMap<>();
        if(!gameActive){


            map.put("player1Score",Integer.parseInt(p1Score.getText().toString()));
            map.put("player2Score",Integer.parseInt(p2Score.getText().toString()));
            for(int i=0;i<9;i++){
                if(gameState[i]!=2){
                    String posn = Integer.toString(i);
                    map.put(posn,null);
                }
            }
            wait=1;
            String s = "wait";
            status.setText(s);
            status.setTextColor(getResources().getColor(R.color.draw));
            mref.updateChildren(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    showResultDialog();
                }
            });
        }
        else{
                if(gameState[tappedImage]==2 && wait!=1 && chance==playerSymbol){

                    map.put(img.getTag().toString(),playerSymbol);
                    mref.updateChildren(map);

                }



        }
    }

    private void showResultDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tic Tac Toe")
                .setMessage("Do you want to play?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameActive=true;
                        wait=0;
                        String s;
                        if(chance==0){
                            s=name2+" chance";
                            status.setText(s);
                            status.setTextColor(getResources().getColor(R.color.ligth_grey));
                        }
                        else if(chance==1){
                            s=name1+" chance";
                            status.setText(s);
                            status.setTextColor(getResources().getColor(R.color.ligth_grey));
                        }
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String,Object> map = new HashMap<>();
                        if(playerSymbol==1){
                            map.put("player1",null);
                        }
                        else if(playerSymbol==0){
                            map.put("player2",null);
                        }
                        mref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(getApplicationContext(),Menu.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    public void changeVisiblity(){
        pb.setVisibility(View.GONE);
        player1.setVisibility(View.GONE);
        player1Name.setVisibility(View.GONE);
        player2.setVisibility(View.GONE);
        player2Name.setVisibility(View.GONE);
        statusJoin.setVisibility(View.GONE);
        codeView.setVisibility(View.GONE);

        status.setVisibility(View.VISIBLE);
        score.setVisibility(View.VISIBLE);
        p1Name.setVisibility(View.VISIBLE);
        p1Score.setVisibility(View.VISIBLE);
        p2Name.setVisibility(View.VISIBLE);
        p2Score.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
        gridImage.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public void checkMatch(){
        if(played==9){
            String draw = "Draw";
            status.setText(draw);
            status.setTextColor(getResources().getColor(R.color.draw));
            gameActive=false;
            return;
        }
        // check if any player wins
        for(int[] winPos : winPosn){
            if(gameState[winPos[0]]==gameState[winPos[1]]){
                if(gameState[winPos[1]]==gameState[winPos[2]]){
                    if(gameState[winPos[0]]==0){
                        status.setText(R.string.o_win);
                        status.setTextColor(getResources().getColor(R.color.green));
                        scoreO++;
                        p2Score.setText(Integer.toString(scoreO));
                        gameActive=false;
                        break;
                    }
                    else if(gameState[winPos[0]]==1){
                        status.setText(R.string.x_win);
                        status.setTextColor(getResources().getColor(R.color.green));
                        scoreX++;
                        p1Score.setText(Integer.toString(scoreX));
                        gameActive=false;
                        break;
                    }
                }
            }
        }
    }
}