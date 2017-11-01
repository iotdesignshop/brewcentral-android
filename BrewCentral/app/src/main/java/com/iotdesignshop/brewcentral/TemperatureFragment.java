package com.iotdesignshop.brewcentral;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TemperatureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TemperatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TemperatureFragment extends Fragment implements CommandUpdateListener {

    // TemperatureFragment parameter
    private static final String ARG_TEMP_TITLE = "TemperatureFragment Title";
    public String mTitle;

    private OnFragmentInteractionListener mListener;
    private ConstraintLayout mLayout;

    private float mCurrentTemp;
    private float mColdTemp, mHotTemp;

    public TemperatureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Name of the temperature value
     * @return A new instance of fragment TemperatureFragment.
     */
    public static TemperatureFragment newInstance(String title) {
        TemperatureFragment fragment = new TemperatureFragment();
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

    /**
     * Use this method to set the "Title" of the temperature block
     * @param title Name of the temperature value
     */
    public void setTitle(String title)
    {
        mTitle = title;

        TextView titleView = (TextView)mLayout.findViewById(R.id.temp_title);
        titleView.setText(title);

        Bundle args = new Bundle();
        args.putString(ARG_TEMP_TITLE, title);
        this.setArguments(args);
    }

    public void setUnits(String units)
    {
        TextView unitView = (TextView)mLayout.findViewById(R.id.temp_units);
        unitView.setText(units);
    }

    public void setCurrentTemperature(float temperature)
    {
        TextView tempView = (TextView)mLayout.findViewById(R.id.temp_value);
        tempView.setText(String.format("%.1f", temperature));

        // Set background color according to temp
        if (temperature <= mColdTemp)
            mLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.coldColor, getActivity().getTheme()));
        else if (temperature >= mHotTemp)
            mLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.hotColor, getActivity().getTheme()));
        else
            mLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.desiredColor, getActivity().getTheme()));

        mCurrentTemp = temperature;
    }

    public void setTemperatureRange(float coldThreshold, float hotThreshold)
    {
        mColdTemp = coldThreshold;
        mHotTemp = hotThreshold;
    }

    public float getCurrentTemperature()
    {
        return mCurrentTemp;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_temperature, container, false);

        return mLayout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * This method is called when a temperature update is received from the hardware
     * @param parameters Will be [ controlname, temp, setpoint ]
     */
    public void commandReceived(String[] parameters)
    {
        // Basic validation
        if (parameters.length >= 2)
        {
            float value = Float.parseFloat(parameters[1]);
            setCurrentTemperature(Utility.tempCToDegreesF(value));
        }

    }
}
