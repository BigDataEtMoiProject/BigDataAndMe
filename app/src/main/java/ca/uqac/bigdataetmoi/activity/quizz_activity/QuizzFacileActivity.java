package ca.uqac.bigdataetmoi.activity.quizz_activity;

import java.util.Random;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ca.uqac.bigdataetmoi.R;

public class QuizzFacileActivity extends AppCompatActivity {

    private QuizzLibrary QuestionLibrary = new QuizzLibrary();

    private TextView mScoreView;
    private TextView mQuestionView;
    private Button mButtonChoice1;
    private Button mButtonChoice2;
    private Button mButtonChoice3;

    private String mAnswer;
    private int mScore = 0;
    private int mQuestionNumber = 0;
    private int i = 0;
    private Random randomGenerator = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzfacile);

        mScoreView = (TextView)findViewById(R.id.facilescore);
        mQuestionView = (TextView)findViewById(R.id.question);
        mButtonChoice1 = (Button)findViewById(R.id.facilechoice1);
        mButtonChoice2 = (Button)findViewById(R.id.facilechoice2);
        mButtonChoice3 = (Button)findViewById(R.id.facilechoice3);

        updateQuestion();

        //Start of Button Listener for Button1
        mButtonChoice1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //My logic for Button goes in here

                if (mButtonChoice1.getText() == mAnswer){
                    mScore = mScore + 1;
                    updateScore(mScore);
                    updateQuestion();
                    //This line of code is optiona
                    Toast.makeText(QuizzFacileActivity.this, "correct", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(QuizzFacileActivity.this, "wrong", Toast.LENGTH_SHORT).show();
                    updateQuestion();
                }
            }
        });

        //End of Button Listener for Button1

        //Start of Button Listener for Button2
        mButtonChoice2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //My logic for Button goes in here

                if (mButtonChoice2.getText() == mAnswer){
                    mScore = mScore + 1;
                    updateScore(mScore);
                    updateQuestion();
                    //This line of code is optiona
                    Toast.makeText(QuizzFacileActivity.this, "correct", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(QuizzFacileActivity.this, "wrong", Toast.LENGTH_SHORT).show();
                    updateQuestion();
                }
            }
        });

        //End of Button Listener for Button2


        //Start of Button Listener for Button3
        mButtonChoice3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //My logic for Button goes in here

                if (mButtonChoice3.getText() == mAnswer){
                    mScore = mScore + 1;
                    updateScore(mScore);
                    updateQuestion();
                    //This line of code is optiona
                    Toast.makeText(QuizzFacileActivity.this, "correct", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(QuizzFacileActivity.this, "wrong", Toast.LENGTH_SHORT).show();
                    updateQuestion();
                }
            }
        });

        //End of Button Listener for Button3





    }

    private void updateQuestion(){


        if(i<10){

            mQuestionNumber+= randomGenerator.nextInt(5);
            mQuestionNumber+= 1;
            mQuestionView.setText(QuizzLibrary.getFacileQuestion(mQuestionNumber));
            mButtonChoice1.setText(QuizzLibrary.getFacileChoice1(mQuestionNumber));
            mButtonChoice2.setText(QuizzLibrary.getFacileChoice2(mQuestionNumber));
            mButtonChoice3.setText(QuizzLibrary.getFacileChoice3(mQuestionNumber));

            mAnswer = QuizzLibrary.getFacileCorrectAnswer(mQuestionNumber);
            i++;
        }
        else{
            mQuestionView.setText("");
            mButtonChoice1.setText("");
            mButtonChoice2.setText("");
            mButtonChoice3.setText("");
        }

    }


    private void updateScore(int point) {
        mScoreView.setText("" + mScore);
    }
}
