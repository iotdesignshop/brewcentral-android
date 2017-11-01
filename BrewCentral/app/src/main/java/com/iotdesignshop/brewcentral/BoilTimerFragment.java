package com.iotdesignshop.brewcentral;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStatusFragmentStateListener} interface
 * to handle interaction events.
 */
public class BoilTimerFragment extends Fragment {

    private OnStatusFragmentStateListener mListener;
    private ConstraintLayout mLayout;

    public BoilTimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_boil_timer, container, false);

        Button startButton = (Button)mLayout.findViewById(R.id.button_advance);

        // Register a click listener for advancing
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Temporary - back to beginning of workflow.... in the future, we will have a boil timer and continue
                // the state machine
                mListener.onFragmentStateChange(OnStatusFragmentStateListener.SystemStates.STATE_WELCOME);
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


}
