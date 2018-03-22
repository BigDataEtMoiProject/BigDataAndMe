package ca.uqac.bigdataetmoi.activity.quizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.activity.BaseActivity;


public class QuizzActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        // Récupérer les vues du Layout
        ListView myListView = findViewById(R.id.quizzListView);

        // On peuple le listView
        String[] fonctions = getResources().getStringArray(R.array.quizz_menu);

        ArrayList<String> fonctionList = new ArrayList<String>();
        fonctionList.addAll(Arrays.asList(fonctions));

        ListAdapter myListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fonctionList);

        myListView.setAdapter(myListAdapter);

        // On définie les actions lors d'un clic sur un item
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i)
                {
                    case 0:
                        startActivity(new Intent(QuizzActivity.this, QuizzPersonaliteActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(QuizzActivity.this, QuizzFacileActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(QuizzActivity.this, QuizzMoyenActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(QuizzActivity.this, QuizzDifficileActivity.class));
                        break;
                }
            }
        });
    }


}
