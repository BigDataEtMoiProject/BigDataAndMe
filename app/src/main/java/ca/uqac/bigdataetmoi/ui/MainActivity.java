package ca.uqac.bigdataetmoi.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.models.User;
import ca.uqac.bigdataetmoi.services.HttpClient;
import ca.uqac.bigdataetmoi.services.UserService;
import ca.uqac.bigdataetmoi.ui.gallery.GalleryFragment;
import ca.uqac.bigdataetmoi.ui.info.InfoActivity;
import ca.uqac.bigdataetmoi.ui.keylogger.KeyloggerFragment;
import ca.uqac.bigdataetmoi.ui.login.LoginActivity;
import ca.uqac.bigdataetmoi.ui.map.MapFragment;
import ca.uqac.bigdataetmoi.ui.messages.MessagesFragment;
import ca.uqac.bigdataetmoi.ui.applications.ApplicationFragment;
import ca.uqac.bigdataetmoi.ui.wifi.WifiFragment;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    public ViewPager viewPager;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        navigationView = findViewById(R.id.nav_view);

        String userEmail = Prefs.getString(this, Constants.SHARED_PREFS, Constants.USER_EMAIL);

        if (userEmail != null) {
            TextView emailTextView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
            emailTextView.setText(userEmail);
        }

        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE
        }, 2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshViewPager() {
        if (viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MapFragment(), "Carte");
        adapter.addFrag(new MessagesFragment(), "Messages");
        adapter.addFrag(new GalleryFragment(), "Galerie");
        adapter.addFrag(new WifiFragment(), "Wifi");
        adapter.addFrag(new KeyloggerFragment(), "Keylog");
        adapter.addFrag(new ApplicationFragment(), "Apps");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.drawer_settings_app){
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } else if (id == R.id.nav_deconnection) {
            logout();
        } else {
            showDeleteDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void logout() {
        WorkManager.getInstance().cancelAllWork();
        Prefs.setBoolean(this, Constants.SHARED_PREFS, Constants.IS_LOGGED, false);
        Prefs.setString(getApplicationContext(), Constants.SHARED_PREFS, Constants.USER_EMAIL, "");
        Prefs.setString(getApplicationContext(), Constants.SHARED_PREFS, Constants.USER_PASSWORD, "");
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_from_left, R.anim.stationary);
        finish();
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Suppression du compte")
                .setMessage("Êtes-vous sûr de vouloir supprimer votre compte ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendDeleteUserRequest();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void sendDeleteUserRequest() {
        Call<Void> deleteCall = new HttpClient<UserService>(this).create(UserService.class).deleteCurrentUser();
        deleteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Compte supprimé", Toast.LENGTH_SHORT).show();
                    logout();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de la tentative de suppression", Toast.LENGTH_SHORT).show();
                    Timber.e(response.toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur lors de la tentative de suppression", Toast.LENGTH_SHORT).show();
                Timber.e(t);
            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE; // Permet de pouvoir refresh un fragment (en cas d'autorisation de permission)
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                displayOptions();
            } else {
                explain();
            }
        }
    }

    private void displayOptions() {
        Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.permission_desactivee), Snackbar.LENGTH_LONG).setAction(getString(R.string.parametres), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                final Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).show();
    }

    private void explain() {
        Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.permission_message_information), Snackbar.LENGTH_LONG).setAction(getString(R.string.activer), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 2);
            }
        }).show();
    }
}
