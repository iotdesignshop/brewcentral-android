package com.iotdesignshop.brewcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FlowAdjustActivity extends AppCompatActivity {

    private float mFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_adjust);

        Bundle extras = getIntent().getExtras();

        mFlow = extras.getFloat("flow");
        updateFlowText();

        TextView flowView = (TextView) findViewById(R.id.flow_title);
        flowView.setText(extras.getString("name"));
    }



    private void updateFlowText()
    {
        TextView flowView = (TextView) findViewById(R.id.current_flow);
        flowView.setText(String.format("%.1f", mFlow));
    }

    public void onIncreaseFlow(View v)
    {
        mFlow += 0.1f;
        updateFlowText();
    }

    public void onDecreaseFlow(View v)
    {
        mFlow -= 0.1f;
        if (mFlow < 0)
            mFlow = 0.0f;

        updateFlowText();
    }

    public void onBackButton(View v)
    {
        Intent data = new Intent();
        data.putExtra("flow", mFlow);
        setResult(RESULT_OK,data);
        finish();
    }
}
