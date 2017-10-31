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
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FlowFragment.OnFlowAdjustListener} interface
 * to handle interaction events.
 * Use the {@link FlowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FlowFragment extends Fragment implements CommandUpdateListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FLOW_TITLE = "Flow Title";

    private String mTitle;

    private OnFlowAdjustListener mListener;
    private ConstraintLayout mLayout;

    private float mCurrentFlow;
    private float mSetPoint;
    private float mRangeTolerance;



    public FlowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Name of the flow controller
     * @return A new instance of fragment FlowFragment.
     */
    public static FlowFragment newInstance(String title) {
        FlowFragment fragment = new FlowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FLOW_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_FLOW_TITLE);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_flow, container, false);

        Button expandButton = (Button)mLayout.findViewById(R.id.expand_button);

        // Register a click listener for expanding the view
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                {
                    mListener.onExpandFlowFragment(mTitle);
                }
            }
        });


        return mLayout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onExpandFlowFragment(mTitle);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFlowAdjustListener) {
            mListener = (OnFlowAdjustListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFlowAdjustListener {

        void onExpandFlowFragment(String name);
    }

    public void setTitle(String title)
    {
        mTitle = title;

        TextView titleView = (TextView)mLayout.findViewById(R.id.flow_title);
        titleView.setText(title);

    }

    public void setUnits(String units)
    {
        TextView unitView = (TextView)mLayout.findViewById(R.id.flow_units);
        unitView.setText(units);

        unitView = (TextView)mLayout.findViewById(R.id.current_units);
        unitView.setText(units);
    }

    public void setSetPoint(float setPoint)
    {
        mSetPoint = setPoint;

        TextView setPointView = (TextView)mLayout.findViewById(R.id.flow_value);
        setPointView.setText(String.format("%.1f", setPoint));

    }

    public float getSetPoint()
    {
        return mSetPoint;
    }

    public void setRangeTolerance(float tolerance)
    {
        mRangeTolerance = tolerance;
    }

    public void setCurrentFlow(float flow)
    {
        TextView tempView = (TextView)mLayout.findViewById(R.id.current_value);
        tempView.setText(String.format("%.1f", flow));

        // Set background color according to temp
        if (flow <= (mSetPoint - mRangeTolerance))
            mLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.slowColor, getActivity().getTheme()));
        else if (flow >= (mSetPoint + mRangeTolerance))
            mLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.fastColor, getActivity().getTheme()));
        else
            mLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.desiredColor, getActivity().getTheme()));

        mCurrentFlow = flow;
    }

    public float getCurrentFlow()
    {
        return mCurrentFlow;
    }

    /**
     * This method is called when a temperature update is received from the hardware
     * @param parameters Will be [ controlname, temp ]
     */
    public void commandReceived(String[] parameters)
    {
        // Basic validation
        if (parameters.length == 3)
        {
            float value = Float.parseFloat(parameters[1]);
            setCurrentFlow(Utility.volumeLtoGal(value));

            float setPoint = Float.parseFloat(parameters[2]);
            setSetPoint(Utility.volumeLtoGal(setPoint));
        }

    }
}
