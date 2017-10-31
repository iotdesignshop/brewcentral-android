package com.iotdesignshop.brewcentral;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.NumberPicker;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {


    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().startsWith(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Temporary - disable unit selection until we support conversion
        Spinner units = (Spinner)findViewById(R.id.units_spinner);
        units.setEnabled(false);

        // Need to look up our preferences and convert them into the right selections in the spinners
        float hltTemp = Utility.getHLTTempPref(this);
        String hltValue = String.format("%.0f", hltTemp);
        final Spinner hltSpinner = (Spinner)findViewById(R.id.hlt_spinner);
        hltSpinner.setSelection(getIndex(hltSpinner, hltValue));
        hltSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String value = hltSpinner.getItemAtPosition(position).toString();

                // Pick off the numeric part of the value
                String[] split = value.split("°");
                float degrees = Float.parseFloat(split[0]);

                Utility.setHLTTempPref(hltSpinner.getContext(), degrees);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        float mashTolerance = Utility.getMashTempTolerancePref(this);
        String mashToleranceValue = String.format("%.2f",mashTolerance);
        final Spinner mashToleranceSpinner = (Spinner)findViewById(R.id.mash_tolerance_spinner);
        mashToleranceSpinner.setSelection(getIndex(mashToleranceSpinner,mashToleranceValue));
        mashToleranceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String value = mashToleranceSpinner.getItemAtPosition(position).toString();

                // Pick off the numeric part of the value
                String[] split = value.split("°");
                float degrees = Float.parseFloat(split[0]);

                Utility.setMashTempTolerancePref(hltSpinner.getContext(), degrees);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        float mashVolume = Utility.getMashVolumePref(this);
        String mashVolumeValue = String.format("%.1f", mashVolume);
        final Spinner mashVolumeSpinner = (Spinner)findViewById(R.id.mash_volume_spinner);
        mashVolumeSpinner.setSelection(getIndex(mashVolumeSpinner, mashVolumeValue));
        mashVolumeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String value = mashVolumeSpinner.getItemAtPosition(position).toString();

                // Pick off the numeric part of the value
                String[] split = value.split(" ");
                float volume = Float.parseFloat(split[0]);

                Utility.setMashVolumePref(hltSpinner.getContext(), volume);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        float spargeTime = Utility.getSpargeTimePref(this);
        String spargeTimeValue = String.format("%.0f", spargeTime);
        final Spinner spargeTimeSpinner = (Spinner)findViewById(R.id.sparge_time_spinner);
        spargeTimeSpinner.setSelection(getIndex(spargeTimeSpinner, spargeTimeValue));
        spargeTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String value = spargeTimeSpinner.getItemAtPosition(position).toString();

                // Pick off the numeric part of the value
                String[] split = value.split(" ");
                float minutes = Float.parseFloat(split[0]);

                Utility.setSpargeTimePref(hltSpinner.getContext(), minutes);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        float preboilVolume = Utility.getPreBoilPref(this);
        String preboilVolumeValue = String.format("%.1f", preboilVolume);
        final Spinner preboilSpinner = (Spinner)findViewById(R.id.pre_boil_spinner);
        preboilSpinner.setSelection(getIndex(preboilSpinner, preboilVolumeValue));
        preboilSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String value = preboilSpinner.getItemAtPosition(position).toString();

                // Pick off the numeric part of the value
                String[] split = value.split(" ");
                float volume = Float.parseFloat(split[0]);

                Utility.setPreBoilPref(hltSpinner.getContext(), volume);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });




    }
}
