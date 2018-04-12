package ca.uqac.bigdataetmoi.startup;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.activity.PermissionManagerActivity;
import ca.uqac.bigdataetmoi.authentification.LoginActivity;
import ca.uqac.bigdataetmoi.menu.AboutActivity;
import ca.uqac.bigdataetmoi.service.BigDataService;

public class MainMenuActivity extends BaseActivity{

    private MainMenuPresenter mainMenuPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //On assigne l'activite courante dans le Fetcher
        ActivityFetcherActivity.setCurrentActivity(this);

        // On met l'identifieur du téléphone dans la classe ActivityFetcherActivity
        ActivityFetcherActivity.setUserID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        MainMenuFragment frag = (MainMenuFragment) getFragmentManager().findFragmentById(R.layout.fragment_main_menu);
        if(frag == null) {
            //On cree le fragment
            frag = MainMenuFragment.init();
            //On l'ajoute a l'activite a l'aide des FragmentTransactions
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.main_menu_frame, frag);
            transaction.commit();
        }

        mainMenuPresenter = new MainMenuPresenter(frag);

        BigDataService.startRecurrence(getApplicationContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /*
        Cette methode est appellee quand un utilisateur appuit sur un bouton
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_profile:
                if(ActivityFetcherActivity.getUserId() != null) {
                    /*
                        Afficher le profil utilisateur?
                     */
                }
                return true;
            case R.id.nav_deconnection:
                Log.d("BDEM", "nav_deconnection");
                ActivityFetcherActivity.setUserID(null);
                startActivity(new Intent(this, LoginActivity.class));

            case R.id.nav_accueil:
                Log.d("BDEM", "nav_accueil");
                startActivity(new Intent(this, MainMenuFragment.class));

            case R.id.nav_parametre:
                Log.d("BDEM", "nav_parametre");
                startActivity(new Intent(this, PermissionManagerActivity.class));

            case R.id.nav_propos:
                Log.d("BDEM", "nav_apropos");
                startActivity(new Intent(this, AboutActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
