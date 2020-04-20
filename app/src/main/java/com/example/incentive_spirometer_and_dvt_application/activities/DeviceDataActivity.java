package com.example.incentive_spirometer_and_dvt_application.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.incentive_spirometer_and_dvt_application.R;
import com.example.incentive_spirometer_and_dvt_application.fragments.DvtFragment;
import com.example.incentive_spirometer_and_dvt_application.fragments.SpirometerFragment;
import com.example.incentive_spirometer_and_dvt_application.helpers.DatabaseHelper;
import com.example.incentive_spirometer_and_dvt_application.models.Patient;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

/**
 *
 * This is the activity that holds the graph and table for the devices
 * (Hosts the SpirometerFragment and DvtFragment)
 *
 *  v1.0: 04/20/20
 */

public class DeviceDataActivity extends AppCompatActivity {
    static final int REQUEST_CODE = 1;
    String firstName;
    String lastName;
    int patientId;
    int doctorId;
    Toolbar toolbar;
    TabLayout tabLayout;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_data);

        Intent intent = getIntent();
        if (intent != null) {
            patientId = intent.getIntExtra("patientId", -1);
            doctorId = intent.getIntExtra("doctorId", -1);

            Patient patient = databaseHelper.getPatient(patientId);

            firstName = patient.getFirstName();
            lastName = patient.getLastName();
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(firstName + " " + lastName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.device_data_menu, menu);

        // set menu item icon color
        Drawable drawable = menu.findItem(R.id.detailMenuItem).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorAccent));
        menu.findItem(R.id.detailMenuItem).setIcon(drawable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.detailMenuItem:
                Intent intent = new Intent(DeviceDataActivity.this, PatientInfoActivity.class);
                intent.putExtra("patientId", patientId);
                intent.putExtra("doctorId", doctorId);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // update name in toolbar if it was changed in the patient info activity
            Patient patient = databaseHelper.getPatient(patientId);

            firstName = patient.getFirstName();
            lastName = patient.getLastName();

            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(firstName + " " + lastName);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("patientId", patientId);
            bundle.putInt("doctorId", doctorId);

            switch (position) {
                case 0:
                    SpirometerFragment spirometerFragment = new SpirometerFragment();
                    spirometerFragment.setArguments(bundle);
                    return spirometerFragment;
                case 1:
                    DvtFragment dvtFragment = new DvtFragment();
                    dvtFragment.setArguments(bundle);
                    return dvtFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Spirometer";
                case 1:
                    return "Dvt";
            }
            return null;
        }
    }
}