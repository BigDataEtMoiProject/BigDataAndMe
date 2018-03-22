package ca.uqac.bigdataetmoi.activity.quizz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.activity.BaseActivity;


@SuppressWarnings("HardCodedStringLiteral")
public class QuizzPersonaliteActivity extends BaseActivity {
    private QuizzLibrary QuestionLibrary = new QuizzLibrary();

    private TextView mScoreView;

    private TextView QuestionView;
    private Button ButtonChoice1;
    private Button ButtonChoice2;
    private Button ButtonChoice3;
    private String MoneyAnswer;
    private String TimeAnswer;
    private String SecurityAnswer;

    private int MoneyScore = 0;
    private int TimeScore = 0;
    private int SecurityScore = 0;
    private int QuestionNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        QuestionNumber=0;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quizzpersonalite);

        QuestionView = findViewById(R.id.question);
        ButtonChoice1 = findViewById(R.id.button);
        ButtonChoice2 = findViewById(R.id.button2);
        ButtonChoice3 = findViewById(R.id.button3);

        updateQuestion();

        //Start of Button Listener for Button1
        ButtonChoice1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //My logic for Button goes in here

                if (ButtonChoice1.getText() == MoneyAnswer){
                    MoneyScore = MoneyScore + 7;
                    TimeScore = TimeScore + 3;
                    updateQuestion();
                }
                else if (ButtonChoice1.getText() == TimeAnswer){
                    MoneyScore = MoneyScore + 3;
                    TimeScore = TimeScore + 7;
                    updateQuestion();
                }
                else if (ButtonChoice1.getText() == SecurityAnswer){
                    SecurityScore = SecurityScore + 10;

                    updateQuestion();
                }
            }
        });

        //End of Button Listener for Button1

        //Start of Button Listener for Button2
        ButtonChoice2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //My logic for Button goes in here

                if (ButtonChoice2.getText() == MoneyAnswer){
                    MoneyScore = MoneyScore + 7;
                    TimeScore = TimeScore + 3;
                    updateQuestion();
                }
                else if (ButtonChoice2.getText() == TimeAnswer){
                    MoneyScore = MoneyScore + 3;
                    TimeScore = TimeScore + 7;
                    updateQuestion();
                }
                else if (ButtonChoice2.getText() == SecurityAnswer){
                    SecurityScore = SecurityScore + 10;

                    updateQuestion();
                }
            }
        });

        //End of Button Listener for Button2


        //Start of Button Listener for Button3
        ButtonChoice3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //My logic for Button goes in here

                if (ButtonChoice3.getText() == MoneyAnswer){
                    MoneyScore = MoneyScore + 7;
                    TimeScore = TimeScore + 3;
                    updateQuestion();
                }
                else if (ButtonChoice3.getText() == TimeAnswer){
                    MoneyScore = MoneyScore + 3;
                    TimeScore = TimeScore + 7;
                    updateQuestion();
                }
                else if (ButtonChoice3.getText() == SecurityAnswer){
                    SecurityScore = SecurityScore + 10;

                    updateQuestion();
                }

            }
        });

        //End of Button Listener for Button3





    }

    @SuppressLint("SetTextI18n")
    private void updateQuestion(){
        if (QuestionNumber<QuestionLibrary.PersoQuestions.length){
            QuestionView.setText(QuestionLibrary.getPersoQuestion(QuestionNumber));
            ButtonChoice1.setText(QuestionLibrary.getPersoChoice1(QuestionNumber));
            ButtonChoice2.setText(QuestionLibrary.getPersoChoice2(QuestionNumber));
            ButtonChoice3.setText(QuestionLibrary.getPersoChoice3(QuestionNumber));

            MoneyAnswer = QuestionLibrary.getMoneyAnswer(QuestionNumber);
            TimeAnswer = QuestionLibrary.getTimeAnswer(QuestionNumber);
            SecurityAnswer = QuestionLibrary.getSecurityAnswer(QuestionNumber);
        }
        else {
            QuestionView.setText("Résultat");
            ButtonChoice1.setText("MoneyScore = "+MoneyScore);
            ButtonChoice2.setText("TimeScore = "+TimeScore);
            ButtonChoice3.setText("Security = "+ SecurityScore);

        }

        QuestionNumber+=1;
    }
}
