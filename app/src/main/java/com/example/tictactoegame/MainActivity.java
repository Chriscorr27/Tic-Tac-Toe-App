package com.example.tictactoegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // 0- O
    // 1 - X
    int played =0;
    int scoreX=0,scoreO=0;
    boolean gameActive = true;
    int activePlayer = 1;
    int[] gameState ={2,2,2,2,2,2,2,2,2,};
    int[][] winPosn = { {0,1,2} , {3,4,5} , {6,7,8},
            {0,3,6} , {1,4,7} , {2,5,8},
            {0,4,8} , {2,4,6} };
    // 0 - O
    // 1 - X
    // 2 - null
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void playerTap(View view) {
        if(!gameActive)
        {
            gameReset();
            return;
        }
        ImageView img = (ImageView) view;
        TextView status = findViewById(R.id.status);
        TextView score_X=findViewById(R.id.playerXScore);
        TextView score_O=findViewById(R.id.playerOScore);
        int tappedImage = Integer.parseInt(img.getTag().toString());
        if(gameState[tappedImage]==2){
            img.setTranslationY(-1000f);
            gameState[tappedImage]= activePlayer;
            if(activePlayer==0){
                img.setImageResource(R.drawable.o);
                activePlayer=1;
                status.setText(R.string.x_s_turn_tap_to_play);
            }
            else{
                img.setImageResource(R.drawable.x);
                activePlayer=0;
                status.setText(R.string.o_s_turn_tap_to_play);
            }
            img.animate().translationYBy(1000f).setDuration(300);
            played++;
        }
        else{
            Toast.makeText(this,"Already clicked",Toast.LENGTH_SHORT).show();
        }
        if(played==9){
            status.setText(R.string.draw);
            status.setTextColor(getResources().getColor(R.color.draw));
            gameActive=false;
        }
        // check if any player wins
        for(int[] winPos : winPosn){
            if(gameState[winPos[0]]==gameState[winPos[1]]){
                if(gameState[winPos[1]]==gameState[winPos[2]]){
                    if(gameState[winPos[0]]==0){
                        status.setText(R.string.o_win);
                        status.setTextColor(getResources().getColor(R.color.green));
                        scoreO++;
                        score_O.setText(Integer.toString(scoreO));
                        gameActive=false;
                        break;
                    }
                    else if(gameState[winPos[0]]==1){
                        status.setText(R.string.x_win);
                        status.setTextColor(getResources().getColor(R.color.green));
                        scoreX++;
                        score_X.setText(Integer.toString(scoreX));
                        gameActive=false;
                        break;
                    }
                }
            }
        }

    }

    public void gameReset(){
        gameActive=true;

        played=0;
        gameState = new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2,};
        ((ImageView)findViewById(R.id.img0)).setImageResource(0);
        ((ImageView)findViewById(R.id.img1)).setImageResource(0);
        ((ImageView)findViewById(R.id.img2)).setImageResource(0);
        ((ImageView)findViewById(R.id.img3)).setImageResource(0);
        ((ImageView)findViewById(R.id.img4)).setImageResource(0);
        ((ImageView)findViewById(R.id.img5)).setImageResource(0);
        ((ImageView)findViewById(R.id.img6)).setImageResource(0);
        ((ImageView)findViewById(R.id.img7)).setImageResource(0);
        ((ImageView)findViewById(R.id.img8)).setImageResource(0);
        ((TextView)findViewById(R.id.status)).setTextColor(getResources().getColor(R.color.ligth_grey));
        if(activePlayer==0){
            ((TextView)findViewById(R.id.status)).setText(R.string.o_s_turn_tap_to_play);
        }
        else
            ((TextView)findViewById(R.id.status)).setText(R.string.x_s_turn_tap_to_play);
    }


    public void quitClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Tic Tac Toe");
        builder.setMessage("Do you want to quit?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showResultDialog();
            }
        })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void showResultDialog() {
        String res = getResutl();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tic Tac Toe")
                .setMessage(res)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),Menu.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    private String getResutl() {
        String ans="";
        if(scoreO>scoreX){
            ans = "Player O wins this game";
        }
        else if(scoreO<scoreX){
            ans = "Player X wins this game";
        }else{
            ans = "game draw";
        }
        return ans;
    }

}