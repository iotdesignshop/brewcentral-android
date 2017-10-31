package com.iotdesignshop.brewcentral;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStatusFragmentStateListener} interface
 * to handle interaction events.
 */
public class SpargingFragment extends Fragment implements CommandUpdateListener {

    public static final String ARG_SPARGE_TEMP = "spargeTemp";
    public static final String ARG_TARGET_VOLUME = "targetVolume";
    public static final String ARG_FLOW_RATE = "flowRate";
    public static final String ARG_LAUTER_END_PCT = "lauterEnd";


    private OnStatusFragmentStateListener mListener;
    private ConstraintLayout mLayout;

    private float mSpargeTemp;
    private float mTargetVolume;
    private float mTotalDispensedWort;
    private float mTargetFlow;
    private float mLauterEndPercent;
    private boolean mStuckSparge;
    private boolean mStarted;

    private CommandManager mCommandManager;
    public void setCommandManager(CommandManager manager)
    {
        mCommandManager = manager;
    }


    public SpargingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSpargeTemp = getArguments().getFloat(ARG_SPARGE_TEMP);
            mTargetVolume = getArguments().getFloat(ARG_TARGET_VOLUME);
            mTargetFlow = getArguments().getFloat(ARG_FLOW_RATE);
            mLauterEndPercent = getArguments().getFloat(ARG_LAUTER_END_PCT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_sparging, container, false);

        Button startButton = (Button)mLayout.findViewById(R.id.button_start_mash);

        // Register a click listener for advancing
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout instructions = (ConstraintLayout)mLayout.findViewById(R.id.instruction_layout);
                instructions.setVisibility(View.GONE);

                ConstraintLayout dispensing = (ConstraintLayout)mLayout.findViewById(R.id.dispensing_layout);
                dispensing.setVisibility(View.VISIBLE);


                startOutflow();

                mTotalDispensedWort = 0.0f;

            }
        });

        final Button startSparge = (Button)mLayout.findViewById(R.id.button_start_sparge);

        // Register a click listener for advancing
        startSparge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startInflow();
                mStarted = true;

                startSparge.setText(R.string.sparging);
                startSparge.setEnabled(false);
            }
        });

        mStarted = false;
        mCommandManager.setMixerTemp(mSpargeTemp);
        mCommandManager.resetMixerVolume();
        mCommandManager.resetMashVolume();

        return mLayout;
    }


    private void startOutflow()
    {
        mCommandManager.setMashFlow(mTargetFlow);
        mStuckSparge = false;
    }

    private void startInflow()
    {
        mCommandManager.setMixerFlow(mTargetFlow);
    }

    private void stopInflow()
    {
        mCommandManager.setMixerFlow(0.0f);
    }

    private void stopOutflow()
    {
        mCommandManager.setMixerFlow(0.0f);
        mCommandManager.setMashFlow(0.0f);
    }

    void flowUpdate(float mashFlow)
    {
        // Don't manage flow until process begins
        if (mStarted == false)
            return;

        // Stuck sparge?
        if (mashFlow < mTargetFlow*0.75)
        {
            if (!mStuckSparge) {
                // Tap the handle
                mStuckSparge = true;
                mCommandManager.setMashFlow(0.0f);
            }
            else
            {
                // Restart
                mCommandManager.setMashFlow(mTargetFlow);
            }
        }

        // Match mixer inflow to mash flow
        mCommandManager.setMixerFlow(mashFlow);
    }

    void volumeUpdate(float dispensedVol)
    {
        // Update display
        TextView volLabel = (TextView)mLayout.findViewById(R.id.text_volume_dispensed);
        ProgressBar volBar = (ProgressBar)mLayout.findViewById(R.id.sparge_progress);
        volBar.setMax((int)(mTargetVolume*10f));

        if (dispensedVol > mTotalDispensedWort)
            mTotalDispensedWort = dispensedVol;

        if (mTotalDispensedWort >= mTargetVolume)
        {
            stopOutflow();

            // We're done
            volLabel.setText(String.format("%.1f", mTargetVolume));
            volBar.setProgress((int)(mTargetVolume*10f));

            if (mListener != null)
            {
                mListener.onFragmentStateChange(OnStatusFragmentStateListener.SystemStates.STATE_BOIL);
            }
        }
        if (mTotalDispensedWort/mTargetVolume > mLauterEndPercent)
        {
            // Stop inflow
            stopInflow();
        }
        else
        {
            volLabel.setText(String.format("%.1f", dispensedVol));
            volBar.setProgress((int)(dispensedVol*10f));
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
        if (parameters.length == 3) {
            if (parameters[0].equals("v-msh")) {
                // Update volume
                volumeUpdate(Utility.volumeLtoGal(Float.parseFloat(parameters[1])));
            }

            if (parameters[0].equals("f-msh")) {
                // Update volume
                flowUpdate(Utility.volumeLtoGal(Float.parseFloat(parameters[1])));
            }


        }
    }


}
