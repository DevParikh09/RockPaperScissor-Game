package com.example.rockpaperscissor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.example.rockpaperscissor.databinding.FragmentSecondBinding;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    int scoreThreshold = 5;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);    //Initialising the Contents of the fragment
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.exitGame.setOnClickListener(new View.OnClickListener() {    // Defining On-click event for exitBtn
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        binding.restartGame.setOnClickListener(new View.OnClickListener() { // Defining On-click event for restartBtn
            @Override
            public void onClick(View view) {
                restart();
            }
        });

        binding.compScore.setText("0");     // Initialising the scores
        binding.playerScore.setText("0");
        binding.compStatus.setText("READY");
        binding.compChoiceImg.setImageResource(R.drawable.question_mark);

        binding.rockBtn.setOnClickListener(new View.OnClickListener() {     // Defining On-click event for rockBtn
            @Override
            public void onClick(View view){
                afterChoosing(1);
            }
        });

        binding.paperBtn.setOnClickListener(new View.OnClickListener() {    // Defining On-click event for epaperBtn
            @Override
            public void onClick(View view){
                afterChoosing(2);
            }
        });

        binding.scissorBtn.setOnClickListener(new View.OnClickListener() {  // Defining On-click event for scissorBtn
            @Override
            public void onClick(View view){
                afterChoosing(3);
            };
        });
    }


    public void showAlert(String winMsg) {      // Alert used after each round of play
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        //alertDialogBuilder.setTitle(winMsg);
        alertDialogBuilder.setMessage(winMsg);

        AlertDialog alertDialog = alertDialogBuilder.create();

        // Setting the position and style for Alert Dialog
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.screenBackground1);
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        alertDialog.show();

        // Formatting the text of the Alert dialog
        TextView textView = ((TextView) alertDialog.findViewById(android.R.id.message));
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);

        // // Defining On-cancel event for Dialog
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
                binding.compChoiceImg.setImageResource(R.drawable.question_mark);
            }
        });
    }

    // Defining the actions to be taken after player plays his choice
    private void afterChoosing(int playerChoice){
        MediaPlayer clickSound = MediaPlayer.create(getActivity(), R.raw.click_sound);
        clickSound.start();
        disableEnableBtns(false);

        boolean playerWins = true;
        String result = "";

        //Generating a random no. b/w 1 & 3 (included) which decides computers play
        // 1 means rock, 2 means paper, 3 means scissor
        int min = 1;
        int max = 3;
        int compChoice = (int)Math.floor(Math.random()*(max-min+1)+min);

        binding.compStatus.setText("READY");

        //Setting the image depending on computer's play
        if (compChoice==1){
            binding.compChoiceImg.setImageResource(R.drawable.rock_modified);
        }
        else if(compChoice==2){
            binding.compChoiceImg.setImageResource(R.drawable.paper_modified);
        }
        else{
            binding.compChoiceImg.setImageResource(R.drawable.scissor_modified);
        }

        // Checking who is the winner according to conventional rules of game
        if(playerChoice==compChoice){
            result = "Draw!";
        }
        else if(playerChoice==1){
            if(compChoice == 3){result = "You Win!"; }
            else{result = "Computer Wins!"; playerWins = false;}
        }
        else if(playerChoice==2){
            if(compChoice == 1){result = "You Win!"; }
            else{result = "Computer Wins!"; playerWins = false;}
        }
        else if(playerChoice==3){
            if(compChoice == 2){result = "You Win!"; }
            else{result = "Computer Wins!"; playerWins = false;}
        }

        // Increasing the Winner's score depending on the result of calculations done above
        // and checking whether threshold is reached(here its 5 points)
        if(playerWins && (!result.equals("Draw!"))){
            Integer newPlayerScore = Integer.parseInt(binding.playerScore.getText().toString()) + 1;
            binding.playerScore.setText(newPlayerScore.toString());
            if(newPlayerScore == scoreThreshold){
                gameOverAlert(result);
        }}
        else if(!playerWins){
            Integer newCompScore = Integer.parseInt(binding.compScore.getText().toString()) + 1;
            binding.compScore.setText(newCompScore.toString());
            if(newCompScore == scoreThreshold){
                gameOverAlert(result);
            }
        }

        showAlert(result);
        disableEnableBtns(true);    //Disabling buttons until focus is restored to play screen

    }

    public void disableEnableBtns(boolean enable){
        binding.rockBtn.setClickable(enable);
        binding.paperBtn.setClickable(enable);
        binding.scissorBtn.setClickable(enable);
    }

    //Alert used once one of the player reaches 5 points
    public void gameOverAlert(String title){
        if (title.contains("You")){
            MediaPlayer winningSound = MediaPlayer.create(getActivity(), R.raw.game_won);
            winningSound.start();
        }
        else{
            MediaPlayer winningSound = MediaPlayer.create(getActivity(), R.raw.game_lost);
            winningSound.start();
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Game Over!");
        alertDialogBuilder.setMessage(title.substring(0,title.length()-1));
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Restart", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int btn) {
                restart();
            }
        });

        alertDialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int btn) {
                exit();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.screenBackground1);

        alertDialog.show();

        TextView textView = ((TextView) alertDialog.findViewById(android.R.id.message));
        textView.setTextSize(40);
        textView.setGravity(Gravity.CENTER);

    }


    public void exit(){
        System.exit(0);      // Closes the app

        // Below Code can be used to exit to home screen

        /*NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment);*/
    }

    public void restart(){
        MediaPlayer gameStartSound = MediaPlayer.create(getActivity(), R.raw.game_start_sound_new);
        gameStartSound.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getFragmentManager().beginTransaction().detach(this).commitNow();
            getFragmentManager().beginTransaction().attach(this).commitNow();
        } else {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
        // I have still used getFragmentManager since I have minimal requirement for it.
        // It vulnerability will not harm this app in any way

    }


}