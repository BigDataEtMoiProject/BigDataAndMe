package ca.uqac.bigdataetmoi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ca.uqac.bigdataetmoi.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }

    public void returnToMainActivity(View view){
        finish();
    }
}
