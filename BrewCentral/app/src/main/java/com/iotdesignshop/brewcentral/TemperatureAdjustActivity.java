package com.iotdesignshop.brewcentral;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class TemperatureAdjustActivity extends AppCompatActivity {

    private float mTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_adjust);

        mTemp = getIntent().getExtras().getFloat("temp");
        updateTempText();

        TextView title = (TextView)findViewById(R.id.temp_title);
        title.setText(getIntent().getStringExtra("name"));

    }

    private void updateTempText()
    {
        TextView temp = (TextView)findViewById(R.id.temp_value);
        temp.setText(String.format("%.1f", mTemp));

    }

    public void onIncreaseTemp(View v)
    {
        mTemp += 0.5f;
        updateTempText();
    }

    public void onDecreaseTemp(View v)
    {
        mTemp -= 0.5f;
        if (mTemp < 0)
            mTemp = 0.0f;

        updateTempText();
    }

    public void onBackButton(View v)
    {
        Intent data = new Intent();
        data.putExtra("temp", mTemp);
        setResult(RESULT_OK,data);
        finish();
    }
}
