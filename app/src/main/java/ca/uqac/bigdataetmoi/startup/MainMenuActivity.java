package ca.uqac.bigdataetmoi.startup;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.activity.CompteurDePasActivity;
import ca.uqac.bigdataetmoi.activity.PermissionManagerActivity;
import ca.uqac.bigdataetmoi.application_usage.UsageActivity;
import ca.uqac.bigdataetmoi.sommeil.SommeilActivity;
import ca.uqac.bigdataetmoi.activity.contact_sms.TelephoneSmsActivity;
import ca.uqac.bigdataetmoi.authentification.LoginActivity;
import ca.uqac.bigdataetmoi.menu.AboutActivity;
import ca.uqac.bigdataetmoi.service.BigDataService;

public class MainMenuActivity extends BaseActivity {

    private MainMenuPresenter mainMenuPresenter;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Add the nav drawer button to the Action Bar
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        // Initialize nav drawer features
        setupNavigationDrawerFeatures();

        // On assigne l'activite courante dans le Fetcher
        ActivityFetcherActivity.setCurrentActivity(this);

        // On met l'identifieur du téléphone dans la classe ActivityFetcherActivity
        ActivityFetcherActivity.setUserID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        // L'activity créer le gfragment et le lie avece le presenter
        MainMenuFragment frag = (MainMenuFragment) getFragmentManager().findFragmentById(R.layout.fragment_main_menu);
        if (frag == null) {
            frag = new MainMenuFragment();
            // On l'ajoute a l'activite a l'aide des FragmentTransactions
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_menu_frame, frag);
            transaction.commit();
        }

        mainMenuPresenter = new MainMenuPresenter(frag);

        BigDataService.startRecurrence(getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                // Open or close navigation menu
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Close Navigation drawer when Back button is pressed and Drawer is open
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    // Setup Navigation Drawer features
    private void setupNavigationDrawerFeatures() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // Close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()) {
                            case R.id.nav_accueil :
                                // Don't start MainMenuActivity if we already are in
                                // startActivity(new Intent(MainMenuActivity.this, MainMenuActivity.class));
                                break;
                            case R.id.nav_profile :
                                Toast.makeText(MainMenuActivity.this, "Not yet implemented", Toast.LENGTH_SHORT).show();
                                // TODO: nav profile

                                if (ActivityFetcherActivity.getUserId() != null) {
                                    /*
                                        Afficher le profil utilisateur?
                                     */
                                }
                                break;
                            case R.id.nav_parametre :
                                startActivity(new Intent(MainMenuActivity.this, PermissionManagerActivity.class));
                                break;
                            case R.id.nav_propos :
                                startActivity(new Intent(MainMenuActivity.this, AboutActivity.class));
                                break;
                            case R.id.nav_deconnection :
                                ActivityFetcherActivity.setUserID(null);
                                startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
                                break;
                        }
                        return true;
                    }
                });
    }

    public void launchSleepActivity(View view) {
        startActivity(new Intent(this, SommeilActivity.class));
    }

    public void launchCommunicationActivity(View view) {
        startActivity(new Intent(this, TelephoneSmsActivity.class));
    }

    public void launchStepsActivity(View view) {
        startActivity(new Intent(this, CompteurDePasActivity.class));
    }

    public void launchApplicationDetailsActivity(View view) {
        startActivity(new Intent(this, UsageActivity.class));
    }
}
