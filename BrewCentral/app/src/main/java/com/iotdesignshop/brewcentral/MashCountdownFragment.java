package com.iotdesignshop.brewcentral;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStatusFragmentStateListener} interface
 * to handle interaction events.
 */
public class MashCountdownFragment extends Fragment implements CommandUpdateListener {

    public static final String ARG_MASH_TIME = "mashTime";

    private ConstraintLayout mLayout;
    private float mMashTime;

    private float mDispenseVolume;

    private OnStatusFragmentStateListener mListener;

    private CommandManager mCommandManager;
    public void setCommandManager(CommandManager manager)
    {
        mCommandManager = manager;
    }

    private CountDownTimer mCountDownTimer;


    public MashCountdownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMashTime = getArguments().getFloat(ARG_MASH_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_mash_countdown, container, false);

        TextView mashCountdown = (TextView)mLayout.findViewById(R.id.mash_time);
        mashCountdown.setText(String.format("%.0f:00", mMashTime));

        // Create a new timer that counts down every second
        mCountDownTimer = new CountDownTimer((long)mMashTime * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long sec = (millisUntilFinished / 1000) % 60;

                TextView mashCountdown = (TextView)mLayout.findViewById(R.id.mash_time);
                mashCountdown.setText(String.format("%02d:%02d", minutes, sec));
            }

            @Override
            public void onFinish() {

                TextView mashCountdown = (TextView)mLayout.findViewById(R.id.mash_time);
                mashCountdown.setText("00:00");

                // Stop any flows in process
                mCommandManager.setMixerFlow(0.0f);

                if (mListener != null)
                {
                    mListener.onFragmentStateChange(OnStatusFragmentStateListener.SystemStates.STATE_SPARGING);
                }
            }
        };
        mCountDownTimer.start();

        Button dispenseColdButton = (Button)mLayout.findViewById(R.id.button_dispense_cold);
        Button dispenseHotButton = (Button)mLayout.findViewById(R.id.button_dispense_hot);

        // Register handler for dispense button
        dispenseColdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button dispenseColdButton = (Button)mLayout.findViewById(R.id.button_dispense_cold);
                Button dispenseHotButton = (Button)mLayout.findViewById(R.id.button_dispense_hot);

                mDispenseVolume += 0.25f;
                dispenseColdButton.setText(String.format("Dispensing %.2f Gal", mDispenseVolume));
                dispenseHotButton.setEnabled(false);

                mCommandManager.setMixerTemp(50.0f);
                mCommandManager.setMixerFlow(0.5f);

            }
        });

        // Register handler for dispense button
        dispenseHotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button dispenseColdButton = (Button)mLayout.findViewById(R.id.button_dispense_cold);
                Button dispenseHotButton = (Button)mLayout.findViewById(R.id.button_dispense_hot);

                mDispenseVolume += 0.25f;
                dispenseHotButton.setText(String.format("Dispensing %.2f Gal", mDispenseVolume));
                dispenseColdButton.setEnabled(false);

                mCommandManager.setMixerTemp(210.0f);
                mCommandManager.setMixerFlow(0.5f);

            }
        });


        return mLayout;
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
            if (parameters[0].equals("v-mix")) {
                // Update volume
                volumeUpdate(Utility.volumeLtoGal(Float.parseFloat(parameters[1])));
            }
        }
    }

    private void volumeUpdate(float dispensedVolume)
    {
        // Are we done a manual dispense operation?
        if (mDispenseVolume > 0.0f && dispensedVolume >= mDispenseVolume)
        {
            Button dispenseColdButton = (Button)mLayout.findViewById(R.id.button_dispense_cold);
            Button dispenseHotButton = (Button)mLayout.findViewById(R.id.button_dispense_hot);

            dispenseColdButton.setText(R.string.dispense_cold);
            dispenseHotButton.setText(R.string.dispense_hot);

            dispenseColdButton.setEnabled(true);
            dispenseHotButton.setEnabled(true);

            // Stop flow
            mCommandManager.setMixerFlow(0.0f);
            mCommandManager.resetMixerVolume();
            mDispenseVolume = 0.0f;
        }

    }

}
