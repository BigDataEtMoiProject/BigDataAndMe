package ca.uqac.bigdataetmoi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.uqac.bigdataetmoi.application_usage.UsageFragment;
import ca.uqac.bigdataetmoi.authentification.LoginActivity;
import ca.uqac.bigdataetmoi.menu.AboutActivity;
import ca.uqac.bigdataetmoi.menu.ProfileActivity;
import ca.uqac.bigdataetmoi.permission_manager.PermissionActivity;
import ca.uqac.bigdataetmoi.startup.ActivityFetcherActivity;
import ca.uqac.bigdataetmoi.startup.MainMenuActivity;
import ca.uqac.bigdataetmoi.startup.MainMenuFragment;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.Prefs;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MainMenuFragment(), "Carte");
        adapter.addFrag(new UsageFragment(), "Applications");
        viewPager.setAdapter(adapter);
    }

    private void setupNavListener() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        drawerLayout.closeDrawers();
                        switch (item.getItemId()) {
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

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
