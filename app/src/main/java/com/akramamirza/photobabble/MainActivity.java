package com.akramamirza.photobabble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.parse.ParseUser;

public class MainActivity extends FragmentActivity {

    static final int LOG_IN_REQUEST = 1;

    private CameraFragment cameraFragment;
    private StoriesFragment storiesFragment;
    private ReceivedSnapsFragment receivedSnapsFragment;

    private int pagerPosition;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOG_IN_REQUEST && resultCode == RESULT_CANCELED) {
            Intent intent = new Intent(MainApplication.getAppContext(), LoginActivity.class);
            startActivityForResult(intent, LOG_IN_REQUEST);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(MainApplication.getAppContext(), LoginActivity.class);
            startActivityForResult(intent, LOG_IN_REQUEST);
        }

        cameraFragment = new CameraFragment();
        storiesFragment = new StoriesFragment();
        receivedSnapsFragment = new ReceivedSnapsFragment();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(1); // set the position to second item so camera fragment is the first thing

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return receivedSnapsFragment;
                case 1:
                    return cameraFragment;
                case 2:
                    return storiesFragment;
            }

            return cameraFragment;
        }


        @Override
        public int getCount() { return 3; }

    }
}
