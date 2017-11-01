package com.iotdesignshop.brewcentral;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static java.lang.Math.max;
import static java.lang.Math.min;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommandUpdateListener} interface
 * to handle interaction events.
 */
public class MashInFragment extends Fragment implements CommandUpdateListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_STRIKE_TEMP = "strikeTemp";
    public static final String ARG_STRIKE_VOLUME = "strikeVol";
    public static final String ARG_MASH_FLOW_RATE = "flowRate";
    public static final String ARG_GRAIN_WEIGHT = "grainWeight";
    public static final String ARG_TARGET_MASH_TEMP = "mashTemp";

    private float mStrikeTemp;
    private float mStrikeVol;
    private float mFlowRate;
    private float mGrainWeight;
    private float mTargetMashTemp;

    private OnStatusFragmentStateListener mListener;
    private ConstraintLayout mLayout;


    private float mTotalDispensedWater;

    private CommandManager mCommandManager;
    public void setCommandManager(CommandManager manager)
    {
        mCommandManager = manager;
    }

    public MashInFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStrikeTemp = getArguments().getFloat(ARG_STRIKE_TEMP);
            mStrikeVol = getArguments().getFloat(ARG_STRIKE_VOLUME);
            mFlowRate = getArguments().getFloat(ARG_MASH_FLOW_RATE);
            mGrainWeight = getArguments().getFloat(ARG_GRAIN_WEIGHT);
            mTargetMashTemp = getArguments().getFloat(ARG_TARGET_MASH_TEMP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_mash_in, container, false);

        Button startButton = (Button)mLayout.findViewById(R.id.button_start_mash);

        // Register a click listener for advancing
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout instructions = (ConstraintLayout)mLayout.findViewById(R.id.instruction_layout);
                instructions.setVisibility(View.GONE);

                ConstraintLayout dispensing = (ConstraintLayout)mLayout.findViewById(R.id.dispensing_layout);
                dispensing.setVisibility(View.VISIBLE);

                mTotalDispensedWater = 0.0f;
                startDispensing();

            }
        });



        // Set up mixer temp
        mCommandManager.setMixerTemp(mStrikeTemp);

        return mLayout;
    }

    void volumeUpdate(float dispensedVol)
    {
        // Update display
        TextView volLabel = (TextView)mLayout.findViewById(R.id.text_volume_dispensed);
        ProgressBar volBar = (ProgressBar)mLayout.findViewById(R.id.sparge_progress);

        // Sanity check
        if (volLabel == null || volBar == null) return;

        volBar.setMax((int)(mStrikeVol*10f));

        if (dispensedVol > mTotalDispensedWater)
            mTotalDispensedWater = dispensedVol;

        if (mTotalDispensedWater >= mStrikeVol)
        {
            stopDispensing();

            // We're done
            volLabel.setText(String.format("%.1f", mStrikeVol));
            volBar.setProgress((int)(mStrikeVol*10f));

            if (mListener != null)
            {
                mListener.onFragmentStateChange(OnStatusFragmentStateListener.SystemStates.STATE_MASH_REST);
            }
        }
        else
        {
            volLabel.setText(String.format("%.1f", dispensedVol));
            volBar.setProgress((int)(dispensedVol*10f));
        }
    }

    void tempUpdate(float mashTemp)
    {
        // Temp adjustment - watch the last 20-25% of pour and adjust temp to hit setpoint
        if (mTotalDispensedWater > 0.7f*mStrikeVol && mTotalDispensedWater < 0.75f*mStrikeVol)
        {
            float currentPct = mTotalDispensedWater / mStrikeVol;
            float remainPct = 1.0f - currentPct;

            float infusionTemp = (mTargetMashTemp - currentPct * mashTemp) / (remainPct);

            // Clamp
            infusionTemp = min(infusionTemp, 200.0f);
            infusionTemp = max(infusionTemp, 70.0f);

            Log.d("ADJUST", "Adjusted temperature: "+Float.toString(infusionTemp));
            mCommandManager.setMixerTemp(infusionTemp);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStatusFragmentStateListener) {
            mListener = (OnStatusFragmentStateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void commandReceived(String[] parameters)
    {
        // Quick sanity check
        if (parameters.length >= 2) {
            if (parameters[0].equals("v-mix")) {
                // Update volume
                volumeUpdate(Utility.volumeLtoGal(Float.parseFloat(parameters[1])));
            }

            if (parameters[0].equals("t-msh")) {
                // Update mash temp logic
                tempUpdate(Utility.tempCToDegreesF(Float.parseFloat(parameters[1])));
            }
        }
    }

    private void startDispensing()
    {
        // Start flow
        mCommandManager.setMixerFlow(mFlowRate);
        mCommandManager.setMashFlow(1.0f);

    }

    private void stopDispensing()
    {
        // Stop flow
        mCommandManager.setMixerFlow(0.0f);
        mCommandManager.resetMixerVolume();
        mCommandManager.setMashFlow(0.0f);

    }
}
