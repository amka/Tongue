package me.meamka.tongue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    private Fragment translateFragment;
    private Fragment bookmarkFragment;
    private Fragment historyFragment;

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    fragmentTransaction.replace(R.id.container, translateFragment).commit();
//                    return true;
//                case R.id.navigation_dashboard:
//                    fragmentTransaction.replace(R.id.container, bookmarkFragment).commit();
//                    return true;
//                case R.id.navigation_notifications:
//                    fragmentTransaction.replace(R.id.container, historyFragment).commit();
//                    return true;
//            }
//            return false;
//        }
//
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Find fragments
        fragmentManager = getSupportFragmentManager();

        translateFragment = new TranslateFragment();
        bookmarkFragment = new BookmarkFragment();
        historyFragment = new HistoryFragment();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        // Set default fragment
        fragmentManager.beginTransaction().add(R.id.container, translateFragment).commit();

        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener(){
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                fragmentTransaction.replace(R.id.container, translateFragment).commit();
                                return true;
                            case R.id.navigation_dashboard:
                                fragmentTransaction.replace(R.id.container, bookmarkFragment).commit();
                                return true;
                            case R.id.navigation_notifications:
                                fragmentTransaction.replace(R.id.container, historyFragment).commit();
                                return true;
                        }
                        return false;
                    }
                });
    }

}
