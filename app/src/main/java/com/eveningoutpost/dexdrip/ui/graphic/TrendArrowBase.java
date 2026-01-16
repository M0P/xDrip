package com.eveningoutpost.dexdrip.ui.graphic;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eveningoutpost.dexdrip.xdrip;

import static com.eveningoutpost.dexdrip.ui.helpers.UiHelper.convertDpToPixel;


/*
* created by jamorham
*
* Base class for graphical trend arrows
*
* Don't adjust this class, just extend it and override any methods you want to change.
* Add your new class in to the TrendArrowFactory
*
* See JamTrendArrowImpl for an example implementation.
*
*/

abstract class TrendArrowBase implements ITrendArrow {

    static float lastRotation = -1000;

    private ImageView myArrow;

    private double largeChange = 2d; // some devices use 3.0
    private double maxChange = 3.5d; // some devices use 3.0
    private float sourceScaleAdjust = 2.0f;
    private double boostScaleMaxAddition = 0.5;

    public ImageView getMyArrow() {
        return myArrow;
    }

    public double getLargeChange() {
        return largeChange;
    }

    public void setLargeChange(double largeChange) {
        this.largeChange = largeChange;
    }

    public double getMaxChange() {
        return maxChange;
    }

    public void setMaxChange(double maxChange) {
        this.maxChange = maxChange;
    }

    public float getSourceScaleAdjust() {
        return sourceScaleAdjust;
    }

    public void setSourceScaleAdjust(float sourceScaleAdjust) {
        this.sourceScaleAdjust = sourceScaleAdjust;
    }

    public double getBoostScaleMaxAddition() {
        return boostScaleMaxAddition;
    }

    public void setBoostScaleMaxAddition(double boostScaleMaxAddition) {
        this.boostScaleMaxAddition = boostScaleMaxAddition;
    }

    public TrendArrowBase() {
        this(null);
    }

    TrendArrowBase(ImageView v) {
        init(v);
        setMargins(10,18,25,0);
        update(null);
    }


    // get the view
    @Override
    public View get() {
        return myArrow;
    }


    // get the configurator view
    @Override
    public View getConfigurator() {
        return null; // stub
    }


    // update it
    @Override
    public boolean update(final Double mgdl) {

        if (mgdl != null) {

            final float newRotation = calculateRotation(mgdl);

            // not initialized
            if (lastRotation == -1000) {
                lastRotation = newRotation;
            }

            myArrow.setRotation(newRotation);

            lastRotation = newRotation;
            final float newScale = calculateScale(mgdl);
            myArrow.setScaleX(newScale * sourceScaleAdjust);
            myArrow.setScaleY(newScale * sourceScaleAdjust);
            myArrow.setVisibility(View.VISIBLE);

            return false;
        } else {
            myArrow.setVisibility(View.GONE);
            myArrow.setScaleX(0.0001f);
            myArrow.setScaleY(0.0001f);
        }
        return true;
    }

    // set the image resource being used
    void setImage(int drawableId) {
        myArrow.setImageResource(drawableId);
    }

    // calculate rotation based on slope
    float calculateRotation(double mgdl_by_minute) {
        double degrees = mgdl_by_minute * -45;
        if (degrees < -90) degrees = -90;
        if (degrees > 90) degrees = 90;
        return (float) degrees;
    }

    // when above largeChange then boost scale up to maxChange on scale based by boostScaleMaxAddition
    float calculateScale(double mgdl_by_minute) {
        final double abs = Math.abs(mgdl_by_minute);
        if (abs >= largeChange) {
            return (float) ((1 - (maxChange - Math.min(maxChange, abs)) / (maxChange - largeChange)) * boostScaleMaxAddition + 1);
        } else {
            return 1;
        }
    }

    void setMargins(int left, int top, int right, int bottom) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PAREN
        params.leftMargin = convertDpToPixel(left);
        params.topMargin = convertDpToPixel(top);
        params.rightMargin = convertDpToPixel(right);
        params.bottomMargin = convertDpToPixel(bottom);

        myArrow.setLayoutParams(params);
    }

    // create view if needed
    private void init(ImageView view) {
        if (view == null) {
            myArrow = new ImageView(xdrip.getAppContext());
        } else {
            myArrow = view;
        }
    }


}
