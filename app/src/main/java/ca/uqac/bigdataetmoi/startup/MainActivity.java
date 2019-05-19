package ca.uqac.bigdataetmoi.startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ca.uqac.bigdataetmoi.InfoActivity;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.authentification.LoginActivity;
import ca.uqac.bigdataetmoi.menu.AboutActivity;
import ca.uqac.bigdataetmoi.menu.ProfileActivity;
import ca.uqac.bigdataetmoi.permission_manager.PermissionActivity;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.Prefs;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_accueil :
                // Don't start MainMenuActivity if we already are in
                // startActivity(new Intent(MainMenuActivity.this, MainMenuActivity.class));
                break;
            case R.id.nav_profile :
                if (ActivityFetcherActivity.getUserId() != null) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
                break;
            case R.id.nav_parametre :
                startActivity(new Intent(MainActivity.this, PermissionActivity.class));
                break;
            case R.id.nav_propos :
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.nav_deconnection :
                ActivityFetcherActivity.setUserID(null);
                Prefs.setBoolean(this, Constants.SHARED_PREFS, Constants.IS_LOGGED, false);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_from_left, R.anim.stationary);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
