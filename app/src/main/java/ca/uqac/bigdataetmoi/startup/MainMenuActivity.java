package ca.uqac.bigdataetmoi.startup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.application_usage.UsageFragment;
import ca.uqac.bigdataetmoi.authentification.LoginActivity;
import ca.uqac.bigdataetmoi.menu.AboutActivity;
import ca.uqac.bigdataetmoi.menu.ProfileActivity;
import ca.uqac.bigdataetmoi.permission_manager.PermissionActivity;
import ca.uqac.bigdataetmoi.service.BigDataService;
import ca.uqac.bigdataetmoi.utils.Constants;
import ca.uqac.bigdataetmoi.utils.Prefs;

public class MainMenuActivity extends BaseActivity {

    private MainMenuPresenter mainMenuPresenter;
    private DrawerLayout mDrawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Add the nav drawer button to the Action Bar
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeActionContentDescription("menu");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        // Initialize nav drawer features
        setupNavigationDrawerFeatures();

        // On assigne l'activite courante dans le Fetcher
        ActivityFetcherActivity.setCurrentActivity(this);

        // On met l'identifieur du téléphone dans la classe ActivityFetcherActivity
        ActivityFetcherActivity.setUserID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        // L'activity créer le gfragment et le lie avece le presenter
        /*MainMenuFragment frag = (MainMenuFragment) getSupportFragmentManager().findFragmentByTag(R.layout.fragment_main_menu);
        if (frag == null) {
            frag = new MainMenuFragment();
            // On l'ajoute a l'activite a l'aide des FragmentTransactions
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id., frag);
            transaction.commit();
        }

        mainMenuPresenter = new MainMenuPresenter(frag);
        */

        BigDataService.startRecurrence(getApplicationContext());

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MainMenuFragment(), "CARTE");
        adapter.addFrag(new UsageFragment(), "APPS");
        viewPager.setAdapter(adapter);
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
        final Context self = this;
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
                                if (ActivityFetcherActivity.getUserId() != null) {
                                    startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class));
                                }
                                break;
                            case R.id.nav_parametre :
                                startActivity(new Intent(MainMenuActivity.this, PermissionActivity.class));
                                break;
                            case R.id.nav_propos :
                                startActivity(new Intent(MainMenuActivity.this, AboutActivity.class));
                                break;
                            case R.id.nav_deconnection :
                                ActivityFetcherActivity.setUserID(null);
                                Prefs.setBoolean(self, Constants.SHARED_PREFS, Constants.IS_LOGGED, false);
                                startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.slide_from_left, R.anim.stationary);
                                finish();
                                break;
                        }
                        return true;
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
