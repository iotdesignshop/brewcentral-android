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
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStatusFragmentStateListener} interface
 * to handle interaction events.
 */
public class HLTWarmUpFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_GOAL_TEMP = "goalTemp";

    private float mGoalTemp;


    public void setGoalTemp(float goalTemp)
    {
        mGoalTemp = goalTemp;
    }

    private OnStatusFragmentStateListener mListener;
    private ConstraintLayout mLayout;

    public HLTWarmUpFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGoalTemp = getArguments().getFloat(ARG_GOAL_TEMP, mGoalTemp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_hltwarm_up, container, false);

        Button nextButton = (Button)mLayout.findViewById(R.id.continue_button);

        // Register a click listener for advancing
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                {
                    mListener.onFragmentStateChange(OnStatusFragmentStateListener.SystemStates.STATE_MASH_IN);
                }
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
