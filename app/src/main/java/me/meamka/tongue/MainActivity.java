package me.meamka.tongue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    private Fragment translateFragment;
    private Fragment bookmarkFragment;
    private Fragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find fragments
        fragmentManager = getSupportFragmentManager();

        translateFragment = new TranslateFragment();
        bookmarkFragment = new BookmarkFragment();
        historyFragment = new HistoryFragment();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        invalidateOptionsMenu();

        // Set default fragment
        fragmentManager.beginTransaction().add(R.id.container, translateFragment).commit();

        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Boolean result = false;
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                fragmentTransaction.replace(R.id.container, translateFragment).commit();
                                result = true;
                                break;
                            case R.id.navigation_dashboard:
                                fragmentTransaction.replace(R.id.container, bookmarkFragment).commit();
                                result = true;
                                break;
                            case R.id.navigation_notifications:
                                fragmentTransaction.replace(R.id.container, historyFragment).commit();
                                result = true;
                                break;
                        }

                        invalidateOptionsMenu();
                        return result;
                    }
                });
    }

}
