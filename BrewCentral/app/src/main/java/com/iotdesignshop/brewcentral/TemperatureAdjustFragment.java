package com.iotdesignshop.brewcentral;

import android.content.Context;
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
 * {@link TemperatureAdjustFragment.OnTemperatureAdjustListener} interface
 * to handle interaction events.
 * Use the {@link TemperatureAdjustFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TemperatureAdjustFragment extends Fragment implements CommandUpdateListener {

    // TemperatureFragment parameter
    private static final String ARG_TEMP_TITLE = "TemperatureFragment Title";
    public String mTitle;

    private OnTemperatureAdjustListener mListener;
    private ConstraintLayout mLayout;

    private float mCurrentTemperature;
    private float mSetPoint;
    private float mRangeTolerance;


    public TemperatureAdjustFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title TemperatureFragment Control Name
     * @return A new instance of fragment TemperatureAdjustFragment.
     */
    public static TemperatureAdjustFragment newInstance(String title) {
        TemperatureAdjustFragment fragment = new TemperatureAdjustFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEMP_TITLE, title);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TEMP_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_temperature_adjust, container, false);

        Button expandButton = (Button)mLayout.findViewById(R.id.expand_button);

        // Register a click listener for expanding the view
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                {
                    mListener.onExpandTemperatureAdjuster(mTitle);
                }
            }
        });

        return mLayout;
    }


    public void setTitle(String title)
    {
        mTitle = title;

        TextView titleView = (TextView)mLayout.findViewById(R.id.temp_title);
        titleView.setText(title);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTemperatureAdjustListener) {
            mListener = (OnTemperatureAdjustListener) context;
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
    public interface OnTemperatureAdjustListener {

        // User has pressed the transparent button overlaying the temperature control
        void onExpandTemperatureAdjuster(String name);
    }

    public void setUnits(String units)
    {
        TextView unitView = (TextView)mLayout.findViewById(R.id.temp_units);
        unitView.setText(units);

        unitView = (TextView)mLayout.findViewById(R.id.setpoint_unit);
        unitView.setText(units);
    }

    public void setSetPoint(float setPoint)
    {
        mSetPoint = setPoint;

        TextView setPointView = (TextView)mLayout.findViewById(R.id.setpoint_value);
        setPointView.setText(String.format("%.1f", setPoint));

    }

    public float getCurrentSetPoint()
    {
        return mSetPoint;
    }

    public void setRangeTolerance(float tolerance)
    {
        mRangeTolerance = tolerance;
    }

    public void setCurrentTemperature(float temperature)
    {
        TextView tempView = (TextView)mLayout.findViewById(R.id.temp_value);
        tempView.setText(String.format("%.1f", temperature));

        // Set background color according to temp
        if (temperature <= (mSetPoint - mRangeTolerance))
            mLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.coldColor, getActivity().getTheme()));
        else if (temperature >= (mSetPoint + mRangeTolerance))
            mLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.hotColor, getActivity().getTheme()));
        else
            mLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.desiredColor, getActivity().getTheme()));

        mCurrentTemperature = temperature;
    }

    public float getCurrentTemperature()
    {
        return mCurrentTemperature;
    }

    /**
     * This method is called when a temperature update is received from the hardware
     * @param parameters Will be [ controlname, temp, setpoint ]
     */
    public void commandReceived(String[] parameters)
    {
        // Basic validation
        if (parameters.length == 3)
        {
            float value = Float.parseFloat(parameters[1]);
            setCurrentTemperature(Utility.tempCToDegreesF(value));

            float setPoint = Float.parseFloat(parameters[2]);
            setSetPoint(Utility.tempCToDegreesF(setPoint));
        }

    }

}
