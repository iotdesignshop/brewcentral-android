package com.iotdesignshop.brewcentral;

import java.util.ArrayList;

/**
 * Created by trentshumay on 2017-10-17.
 */

public class BeerRecipe {


    private String mRecipeName;
    public void setRecipeName(String name)
    {
        mRecipeName = name;
    }
    public String getRecipeName() { return mRecipeName; }

    enum Units
    {
        UNIT_IMPERIAL,
        UNIT_METRIC
    }
    private Units mUnits = Units.UNIT_IMPERIAL;     // Units for this recipe
    public void setUnits(Units newUnits) { mUnits = newUnits; }

    public static class GrainIngredient {
        String mCommonName;          // Common name of this grain

        float mWeight;               // Amount of grain by weight

        public GrainIngredient(String commonName, float weight)
        {
            mCommonName = commonName;
            mWeight = weight;
        }

        public float getWeight()    { return mWeight; }
    }
    private ArrayList<GrainIngredient> mGrainIngredients;

    public void addGrainIngredient(GrainIngredient newIngredient)
    {
        mGrainIngredients.add(newIngredient);
    }

    public float totalGrainWeight()
    {
        float total = 0.0f;

        for (GrainIngredient ingredient : mGrainIngredients)
        {
            total += ingredient.getWeight();
        }

        return total;
    }

    public static class HopIngredient {
        String mCommonName;          // Common name of the hop
        float mAAU;                  // AAU% of the hop in the recipe
        float mWeight;               // Amount of hop by weight
        float mMinutes;              // Time hop is added (in minutes of boil remaining)

        public HopIngredient(String commonName, float AAU, float weight, float minutes)
        {
            mCommonName = commonName;
            mAAU = AAU;
            mWeight = weight;
            mMinutes = minutes;
        }
    }
    private ArrayList<HopIngredient> mHopIngredients;

    public void addHopIngredient(HopIngredient newIngredient)
    {
        mHopIngredients.add(newIngredient);
    }

    private float mStrikeTemp;      // Strike temperature
    public void setStrikeTemp(float temp)   { mStrikeTemp = temp; }
    public float getStrikeTemp() { return mStrikeTemp; }

    private float mMashTemp;        // Mash temperature
    public void setMashTemp(float temp)     { mMashTemp = temp; }
    public float getMashTemp() { return mMashTemp; }

    private float mMashTime;        // Mash time
    public void setMashTime(float minutes)  { mMashTime = minutes; }
    public float getMashTime() { return mMashTime; }

    private float mMashoutTemp;     // Mashout temperature
    public void setMashoutTemp(float temp)  { mMashoutTemp = temp; }
    public float getMashoutTemp() { return mMashoutTemp; }

    private float mMashoutTime;     // Mashout time
    public void setMashoutTime(float minutes)   { mMashoutTime = minutes; }
    public float getMashoutTime() { return mMashoutTime; }

    private float mSpargeTemp;     // Sparge temperature
    public void setSpargeTemp(float temp)   { mSpargeTemp = temp; }
    public float getSpargeTemp() { return mSpargeTemp; }

    private float mFinalVolume;      // Volume required for boil
    public void setFinalVolume(float volume)     { mFinalVolume = volume; }
    public float getFinalVolume() { return mFinalVolume; }

    private float mBoilTime;        // Time to boil
    public void setBoilTime(float minutes)      { mBoilTime = minutes; }
    public float getBoilTime() { return mBoilTime; }

    private String mYeast;          // Name of yeast
    public void setYeast(String yeast)          { mYeast = yeast; }

    public BeerRecipe()
    {
        mGrainIngredients = new ArrayList<GrainIngredient>();
        mHopIngredients = new ArrayList<HopIngredient>();
    }
}
