package com.iotdesignshop.brewcentral;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements TemperatureFragment.OnFragmentInteractionListener, TemperatureAdjustFragment.OnTemperatureAdjustListener, FlowFragment.OnFlowAdjustListener,
        OnStatusFragmentStateListener, UARTManager.UARTCommandListener
{

    private static final String UART_DEVICE = "UART6";     // Pico7 Dev Board UART. May need to change for different platforms
    private static final int UART_BAUD = 9600;

    private static final int MIXER_TEMP_ADJUST = 1;
    private static final int MIXER_FLOW_ADJUST = 2;
    private static final int MASH_FLOW_ADJUST = 3;

    private TemperatureAdjustFragment mMixerTemp;
    private TemperatureFragment mHLTTemp;
    private TemperatureFragment mCWTTemp;
    private TemperatureFragment mMashTemp;

    private FlowFragment mMixerFlow;
    private FlowFragment mMashFlow;
    private Fragment mCurrentStateFragment;

    private BeerRecipe mCurrentRecipe;
    private UARTManager mUARTManager;

    private HashMap<String, ArrayList<CommandUpdateListener>> mUpdateListeners = new HashMap<String, ArrayList<CommandUpdateListener>>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.os.Process.myPid();

        mMixerTemp = (TemperatureAdjustFragment)getFragmentManager().findFragmentById(R.id.temp_mixer);
        if (mMixerTemp != null)
        {
            mMixerTemp.setTitle(getString(R.string.mixer_setpoint));
            mMixerTemp.setSetPoint(160.0f);
            mMixerTemp.setRangeTolerance(Utility.getMashTempTolerancePref(this));
            mMixerTemp.setCurrentTemperature(0.0f);

            addUpdateListener("t-mix", mMixerTemp);

        }

        mMashTemp = (TemperatureFragment)getFragmentManager().findFragmentById(R.id.temp_mash);
        if (mMashTemp != null)
        {
            mMashTemp.setTitle(getString(R.string.mash_temp));
            mMashTemp.setTemperatureRange(150.0f, 156.0f);
            mMashTemp.setCurrentTemperature(0.0f);

            addUpdateListener("t-msh", mMashTemp);
        }

        mMashFlow = (FlowFragment)getFragmentManager().findFragmentById(R.id.flow_mash);
        if (mMashFlow != null)
        {
            mMashFlow.setTitle(getString(R.string.mash_flow_setpoint));
            mMashFlow.setRangeTolerance(.1f);
            mMashFlow.setSetPoint(0.0f);
            mMashFlow.setCurrentFlow(0.0f);

            addUpdateListener("f-msh", mMashFlow);
        }

        mMixerFlow = (FlowFragment)getFragmentManager().findFragmentById(R.id.flow_mixer);
        if (mMixerFlow != null)
        {
            mMixerFlow.setTitle(getString(R.string.mixer_flow_setpoint));
            mMixerFlow.setRangeTolerance(.1f);
            mMixerFlow.setSetPoint(0.0f);
            mMixerFlow.setCurrentFlow(0.0f);

            addUpdateListener("f-mix", mMixerFlow);
        }



        // Setup inital welcome fragment
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.main_status) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            WelcomeFragment firstFragment = new WelcomeFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.main_status, firstFragment).commit();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        // Start communications with hardware
        mUARTManager = new UARTManager();
        mUARTManager.setListener(this);
        mUARTManager.openDevice(UART_DEVICE, UART_BAUD);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // Subscribe to a hardware update message
    private void addUpdateListener(String message, CommandUpdateListener listener)
    {
        ArrayList<CommandUpdateListener> list = mUpdateListeners.get(message);
        if (list == null)
        {
            // This is the first entry for this message
            list = new ArrayList<CommandUpdateListener>();

            mUpdateListeners.put(message, list);
        }
        list.add(listener);
    }

    // Unsubscribe from a hardware update message
    private void removeUpdateListener(String message, CommandUpdateListener listener) {
        ArrayList<CommandUpdateListener> list = mUpdateListeners.get(message);
        if (list != null) {
            list.remove(listener);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    public void onExpandTemperatureAdjuster(String adjusterName)
    {
        Intent intent = new Intent(this, TemperatureAdjustActivity.class);
        intent.putExtra("name", adjusterName);
        intent.putExtra("temp", mMixerTemp.getCurrentSetPoint());
        startActivityForResult(intent, MIXER_TEMP_ADJUST);
    }

    public void onExpandFlowFragment(String flowName)
    {
        Intent intent = new Intent(this, FlowAdjustActivity.class);
        intent.putExtra("name", flowName);

        if (flowName.equals(getString(R.string.mixer_flow_setpoint))) {
            intent.putExtra("flow", mMixerFlow.getSetPoint());
            startActivityForResult(intent, MIXER_FLOW_ADJUST);
        }
        else if (flowName.equals(getString(R.string.mash_flow_setpoint))) {
            intent.putExtra("flow", mMashFlow.getSetPoint());
            startActivityForResult(intent, MASH_FLOW_ADJUST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Aborted?
        if (resultCode != RESULT_OK)
            return;

        CommandManager commandManager = new CommandManager(mUARTManager);

        // Check which request we're responding to
        if (requestCode == MIXER_TEMP_ADJUST)
        {
            float temp = data.getFloatExtra("temp", 0.0f);
            commandManager.setMixerTemp(temp);
        }
        else if (requestCode == MIXER_FLOW_ADJUST)
        {
            float flow = data.getFloatExtra("flow", 0.0f);
            commandManager.setMixerFlow(flow);
        }
        else if (requestCode == MASH_FLOW_ADJUST)
        {
            float flow = data.getFloatExtra("flow", 0.0f);
            commandManager.setMashFlow(flow);
        }
    }

    public void onFragmentStateChange(OnStatusFragmentStateListener.SystemStates nextState)
    {
        switch (nextState)
        {
            case STATE_WELCOME:
                break;

            case STATE_HEATING:
                loadRecipe();

                // Load HLT warm-up fragment
                HLTWarmUpFragment hltFragment = new HLTWarmUpFragment();

                Bundle hltArgs = new Bundle();
                hltArgs.putFloat(HLTWarmUpFragment.ARG_GOAL_TEMP, Utility.getHLTTempPref(this));
                hltFragment.setArguments(hltArgs);

                // Replace
                android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_status, hltFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                mCurrentStateFragment = hltFragment;
                break;

            case STATE_MASH_IN:


                // Load Mash In Fragment
                MashInFragment miFragment = new MashInFragment();

                Bundle miArgs = new Bundle();
                miArgs.putFloat(MashInFragment.ARG_STRIKE_TEMP, mCurrentRecipe.getStrikeTemp());        // Strike water temp from recipe
                miArgs.putFloat(MashInFragment.ARG_STRIKE_VOLUME, mCurrentRecipe.totalGrainWeight() * Utility.volumeQuartsToGal(Utility.getMashVolumePref(this)));     // Total mash volume = weight of grain X grist-water ratio
                miArgs.putFloat(MashInFragment.ARG_MASH_FLOW_RATE, Utility.getMashFlowRatePref(this));      // Flow rate
                miArgs.putFloat(MashInFragment.ARG_GRAIN_WEIGHT, mCurrentRecipe.totalGrainWeight());
                miArgs.putFloat(MashInFragment.ARG_TARGET_MASH_TEMP, mCurrentRecipe.getMashTemp());
                miFragment.setArguments(miArgs);

                // Replace
                android.app.FragmentTransaction miTransaction = getFragmentManager().beginTransaction();
                miTransaction.replace(R.id.main_status, miFragment);
                miTransaction.addToBackStack(null);
                miTransaction.commit();

                // Adjust mash temp range visualization to match recipe
                mMashTemp.setTemperatureRange(mCurrentRecipe.getMashTemp()-Utility.getMashTempTolerancePref(this), mCurrentRecipe.getMashTemp()+Utility.getMashTempTolerancePref(this));

                // Add required listeners
                addUpdateListener("t-msh", miFragment);
                addUpdateListener("f-mix", miFragment);
                addUpdateListener("v-mix", miFragment);

                // Hook fragment up to command manager on UART channel
                miFragment.setCommandManager(new CommandManager(mUARTManager));

                // Turn off flow update for now
                removeUpdateListener("f-msh", mMashFlow);

                mCurrentStateFragment = miFragment;

                break;

            case STATE_MASH_REST:
                // Clean up previous view
                removeUpdateListener("t-msh", (MashInFragment)mCurrentStateFragment);
                removeUpdateListener("f-mix", (MashInFragment)mCurrentStateFragment);
                removeUpdateListener("v-mix", (MashInFragment)mCurrentStateFragment);

                MashCountdownFragment mcFragment = new MashCountdownFragment();

                Bundle mcArgs = new Bundle();
                mcArgs.putFloat(MashCountdownFragment.ARG_MASH_TIME, mCurrentRecipe.getMashTime());
                mcFragment.setArguments(mcArgs);

                // Replace
                android.app.FragmentTransaction mcTransaction = getFragmentManager().beginTransaction();
                mcTransaction.replace(R.id.main_status, mcFragment);
                mcTransaction.addToBackStack(null);
                mcTransaction.commit();

                // Add required listeners
                addUpdateListener("v-mix", mcFragment);

                // Hook fragment up to command manager on UART channel
                mcFragment.setCommandManager(new CommandManager(mUARTManager));


                mCurrentStateFragment = mcFragment;
                break;

            case STATE_SPARGING:

                // Clean up previous view
                removeUpdateListener("v-mix", (MashCountdownFragment)mCurrentStateFragment);

                SpargingFragment spFragment = new SpargingFragment();

                Bundle spArgs = new Bundle();
                spArgs.putFloat(SpargingFragment.ARG_FLOW_RATE, mCurrentRecipe.getFinalVolume()/Utility.getSpargeTimePref(this));   // Gallons/min
                spArgs.putFloat(SpargingFragment.ARG_SPARGE_TEMP, mCurrentRecipe.getSpargeTemp());
                spArgs.putFloat(SpargingFragment.ARG_TARGET_VOLUME, Utility.getPreBoilPref(this));
                spArgs.putFloat(SpargingFragment.ARG_LAUTER_END_PCT, Utility.getLauterEndPercent(this));
                spFragment.setArguments(spArgs);

                // Replace
                android.app.FragmentTransaction spTransaction = getFragmentManager().beginTransaction();
                spTransaction.replace(R.id.main_status, spFragment);
                spTransaction.addToBackStack(null);
                spTransaction.commit();

                // Add required listeners
                addUpdateListener("v-msh", spFragment);
                addUpdateListener("f-msh", spFragment);

                // Restore update listener on mash flow
                addUpdateListener("f-msh", mMashFlow);



                // Hook fragment up to command manager on UART channel
                spFragment.setCommandManager(new CommandManager(mUARTManager));

                mCurrentStateFragment = spFragment;

                break;

            case STATE_BOIL:

                // Clean up previous view
                removeUpdateListener("f-msh", (SpargingFragment)mCurrentStateFragment);
                removeUpdateListener("v-msh", (SpargingFragment)mCurrentStateFragment);

                BoilTimerFragment boilFragment = new BoilTimerFragment();

                Bundle boilArgs = new Bundle();
                boilFragment.setArguments(boilArgs);

                // Replace
                android.app.FragmentTransaction boilTransaction = getFragmentManager().beginTransaction();
                boilTransaction.replace(R.id.main_status, boilFragment);
                boilTransaction.addToBackStack(null);
                boilTransaction.commit();

                mCurrentStateFragment = boilFragment;

                break;


        }

    }


    private void loadRecipe()
    {
        // Create a temporary recipe
        mCurrentRecipe = new BeerRecipe();

        mCurrentRecipe.setRecipeName("IoT English Brown Ale");
        mCurrentRecipe.setUnits(BeerRecipe.Units.UNIT_IMPERIAL);

        mCurrentRecipe.addGrainIngredient(new BeerRecipe.GrainIngredient("UK Pale Malt", 3.15f*2.2f));
        mCurrentRecipe.addGrainIngredient(new BeerRecipe.GrainIngredient("Caramel 30L", 1.4f*2.2f));
        mCurrentRecipe.addGrainIngredient(new BeerRecipe.GrainIngredient("Chocolate Malt", .25f*2.2f));

        mCurrentRecipe.addHopIngredient(new BeerRecipe.HopIngredient("Fuggles", 4.5f, 1.2f, 90f));
        mCurrentRecipe.addHopIngredient(new BeerRecipe.HopIngredient("East Kent Golding", 5f, 1.8f, 5f));


        mCurrentRecipe.setStrikeTemp(165f);
        mCurrentRecipe.setMashTemp(156f);
        //mCurrentRecipe.setMashTime(60f);
        mCurrentRecipe.setMashTime(15);
        mCurrentRecipe.setMashoutTime(0);
        mCurrentRecipe.setSpargeTemp(177f);

        mCurrentRecipe.setFinalVolume(6.0f);
        mCurrentRecipe.setBoilTime(90f);

        mCurrentRecipe.setYeast("British Ale II Yeast - WYeast 1335");

        // Set title bar to match
        setTitle("BrewCentral - Brewing: " + mCurrentRecipe.getRecipeName());

    }



    public void uartCommandReceived(String[] parameters)
    {
        ArrayList<CommandUpdateListener> subscribers = mUpdateListeners.get(parameters[0]);
        if (subscribers != null)
        {
            for (CommandUpdateListener subscriber : subscribers)
            {
                subscriber.commandReceived(parameters);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
