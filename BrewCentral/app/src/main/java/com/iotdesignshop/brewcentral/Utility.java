package com.iotdesignshop.brewcentral;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by trentshumay on 2017-10-24.
 */

public class Utility {

    private static final String prefsFile = "com.iotdesignshop.brewcentral.MAIN_PREFS";

    private static final String hltTempKey = "com.iotdesignshop.brewcentral.HLT_TEMP";
    private static final float HLT_TEMP_DEFAULT = 210.0f;

    private static final String mashToleranceKey = "com.iotdesignshop.brewcentral.MASH_TOLERANCE";
    private static final float HLT_MASH_TOLERANCE_DEFAULT = 2.0f;

    private static final String mashVolumeKey = "com.iotdesignshop.brewcentral.MASH_VOLUME";
    private static final float HLT_MASH_VOLUME_DEFAULT = 1.5f;

    private static final String spargeTimeKey = "com.iotdesignshop.brewcentral.SPARGE_TIME";
    private static final float SPARGE_TIME_DEFAULT = 30f;

    private static final String preboilVolumeKey = "com.iotdesignshop.brewcentral.PREBOIL_VOLUME";
    private static final float PREBOIL_VOL_DEFAULT = 6.5f;

    private static final String mashFlowKey = "com.iotdesignshop.brewcentral.MASH_IN_RATE";
    private static final float MASH_FLOW_DEFAULT = 2.5f;


    /**
     * Retrieves the user preference for HLT Temperature
     * @param context
     * @return HLT Temp from Shared Preferences
     */
    static float getHLTTempPref(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        float temp = prefs.getFloat(hltTempKey, HLT_TEMP_DEFAULT);
        return temp;
    }

    /**
     * Writes the user preference for HLT Temperature
     * @param context
     * @param hltTemp HLT Temperature Value
     */
    static void setHLTTempPref(Context context, float hltTemp)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(hltTempKey, hltTemp);
        editor.commit();
    }

    /**
     * Retrieves the user preference for Mash Temperature Tolerance
     * @param context
     * @return Mash temperature tolerance
     */
    static float getMashTempTolerancePref(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        float temp = prefs.getFloat(mashToleranceKey, HLT_MASH_TOLERANCE_DEFAULT);
        return temp;
    }


    /**
     * Writes the user preference for Mash Temperature Tolerance
     * @param context
     * @param mashTemp Mash Temperature Value
     */
    static void setMashTempTolerancePref(Context context, float mashTemp)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(mashToleranceKey, mashTemp);
        editor.commit();
    }


    /**
     * Retrieves the user preference for Mash Volume (Water-to-Grist Ratio)
     * @param context
     * @return Water to grist ratio
     */
    static float getMashVolumePref(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        float vol = prefs.getFloat(mashVolumeKey, HLT_MASH_VOLUME_DEFAULT);
        return vol;
    }

    /**
     * Writes the user preference for Mash Volume (Water-to-Grist Ratio)
     * @param context
     * @param wtgRatio Water-to-Grist Ratio
     */
    static void setMashVolumePref(Context context, float wtgRatio)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(mashVolumeKey, wtgRatio);
        editor.commit();
    }

    /**
     * Retrieves the user preference for Sparge Time
     * @param context
     * @return Ideal sparging time
     */
    static float getSpargeTimePref(Context context)
    {
        /*SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        float t = prefs.getFloat(spargeTimeKey, SPARGE_TIME_DEFAULT);
        return t;
        */
        return 2.0f;
    }

    /**
     * Writes the user preference for Sparge Time
     * @param context
     * @param spargeTime Ideal sparging time
     */
    static void setSpargeTimePref(Context context, float spargeTime)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(spargeTimeKey, spargeTime);
        editor.commit();
    }


    /**
     * Retrieves the user preference for Pre-Boil Volume
     * @param context
     * @return Desired Pre-Boil Volume
     */
    static float getPreBoilPref(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        float t = prefs.getFloat(preboilVolumeKey, PREBOIL_VOL_DEFAULT);
        return t;
    }

    /**
     * Writes the user preference for Pre-Boil Volume
     * @param context
     * @param volume Pre-Boil Volume
     */
    static void setPreBoilPref(Context context, float volume)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(preboilVolumeKey, volume);
        editor.commit();
    }

    /**
     * Retrieves the user preference for mash-in flow rate
     * @param context
     * @return Mash-in flow rate
     */
    static float getMashFlowRatePref(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        float t = prefs.getFloat(mashFlowKey, MASH_FLOW_DEFAULT);
        return t;
    }

    /**
     * Sets the user preference for mash-in flow rate
     * @param context
     * @param rate Mash-in flow rate
     */
    static void setMashFlowRatePref(Context context, float rate)
    {
        SharedPreferences prefs = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(mashFlowKey, rate);
        editor.commit();
    }



    /** Retrieves the user preference for end of lauter %
     * @param context
     * @return Sparge inflow will be cut at this % of total boil volume
     */
    static float getLauterEndPercent(Context context)
    {
        return .9f;     // ToDo - Make this configurable
    }

    /**
     * Metric to imperial temp conversion
     * @param temp Temperature in Celsius
     * @return Temperature in Fahrenheit
     */
    static float tempCToDegreesF(float temp)
    {
        return temp * 9.0f / 5.0f + 32.0f;
    }

    /**
     * Imperial to metric temp conversion
     * @param temp Temperature in Fahrenheit
     * @return Temperature in Celsius
     */
    static float tempFToDegreesC(float temp)
    {
        return (temp-32.0f)*5.0f/9.0f;
    }

    /**
     * Imperial to metric volume conversion
     * @param vol Volume in Gallons
     * @return Volume in Liters
     */
    static float volumeGaltoL(float vol)
    {
        return vol * 3.8f;
    }

    /**
     * Metric to imperial volume conversion
     * @param vol Volume in Liters
     * @return Volume in Gallons
     */
    static float volumeLtoGal(float vol)
    {
        return vol/3.8f;
    }

    /**
     * Quarts to gallons volume conversion
     * @param vol Volume in Quarts
     * @return Volume in Gallons
     */
    static float volumeQuartsToGal(float vol)
    {
        return vol/4;
    }

    /**
     * Quarts to gallons volume conversion
     * @param vol Volume in Gallons
     * @return Volume in Quarts
     */
    static float volumeGalToQuarts(float vol)
    {
        return vol*4;
    }
}
