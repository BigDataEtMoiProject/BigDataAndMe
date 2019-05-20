package ca.uqac.bigdataetmoi;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.uqac.bigdataetmoi.application_usage.UsageFragment;
import ca.uqac.bigdataetmoi.authentification.LoginActivity;
import ca.uqac.bigdataetmoi.contact_sms.TelephoneSmsFragment;
import ca.uqac.bigdataetmoi.fragments.MapFragment;
import ca.uqac.bigdataetmoi.menu.AboutActivity;
import ca.uqac.bigdataetmoi.permission_manager.PermissionActivity;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.Prefs;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    public ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        setupNavListener();

        requestPermissions(new String[] { Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_SMS }, 2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
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
        adapter.addFrag(new UsageFragment(), "Applications");
        adapter.addFrag(new TelephoneSmsFragment(), "Repertoire");
        // Repertoire téléphonique
        // SSID
        viewPager.setAdapter(adapter);
    }

    private void setupNavListener() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        drawerLayout.closeDrawers();
                        switch (item.getItemId()) {
                            case R.id.nav_parametre :
                                startActivity(new Intent(MainActivity.this, PermissionActivity.class));
                                break;
                            case R.id.nav_propos :
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                                break;
                            case R.id.nav_deconnection :
                                Prefs.setBoolean(MainActivity.this, Constants.SHARED_PREFS, Constants.IS_LOGGED, false);
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.slide_from_left, R.anim.stationary);
                                finish();
                                break;
                        }
                        return true;
                    }
                }
        );
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

        if(requestCode == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("####################################################### PERMISSION_GRANTED pour READ_CONTACTS");
            } else if(! shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                displayOptions();
            } else {
                System.out.println("####################################################### PERMISSION_DENIED pour READ_CONTACTS");
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
                requestPermissions(new String[] { Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_SMS }, 2);
            }
        }).show();
    }
}
