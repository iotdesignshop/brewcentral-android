package com.iotdesignshop.brewcentral;

import com.iotdesignshop.brewcentral.MainActivity;

/**
 * Created by trentshumay on 2017-10-20.
 */

public interface OnStatusFragmentStateListener {

    public enum SystemStates
    {
        STATE_WELCOME,          // Activity is currently in welcome state
        STATE_HEATING,          // Hot water heating state
        STATE_MASH_IN,          // Mash in state
        STATE_MASH_REST,        // Mash rest
        STATE_SPARGING,         // Sparge
        STATE_BOIL,             // Boil
        STATE_FINISHED          // End of brew
    }

    void onFragmentStateChange(SystemStates nextState);
}
