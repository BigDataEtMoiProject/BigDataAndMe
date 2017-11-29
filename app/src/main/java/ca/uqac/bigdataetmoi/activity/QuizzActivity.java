package ca.uqac.bigdataetmoi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class QuizzActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        // Récupérer les vues du Layout
        ListView myListView = (ListView) findViewById(R.id.quizzListView);

        // On peuple le listView
        String[] fonctions = new String[] {
                "Quizz de Personalité (Argent/Temps/Securitée",
                "Quizz Facile",
                "Quizz Moyen",
                "Quizz Difficile",
                };

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
