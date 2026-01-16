package com.eveningoutpost.dexdrip.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

public final class ObservableBackground extends BaseObservable {
    @Nullable
    private Integer mDrawableResource;
    @Nullable
    private Integer mColorResource;
    @Nullable
    private Integer mColorValue;
    @Nullable
    private Drawable mDrawable;
    @Nullable
    private Bitmap mBitmap;

    private void reset() {
        this.mDrawableResource = null;
        this.mColorResource = null;
        this.mColorValue = null;
        this.mDrawable = null;
    }

    public final void setDrawable(Drawable drawable) {
        this.reset();
        this.mDrawable = drawable;
        this.notifyChange();
    }

    public final void setBitmap(Bitmap bitmap) {
        this.reset();
        this.mBitmap = bitmap;
        this.notifyChange();
    }

    public final void clear() {
        this.reset();
        this.notifyChange();
    }

    @BindingAdapter(value = "background")
    public static void setBackground(View view, ObservableBackground observable) {
        Integer resource;
        if (observable.getmDrawableResource() != null) {
            resource = observable.getmDrawableResource();
            if (resource != null) {
                view.setBackgroundResource(resource);
            }
        } else if (observable.getmColorResource() != null) {
            resource = observable.getmColorResource();
            if (resource != null) {
                final int mcolor = ContextCompat.getColor(view.getContext(), resource);
                view.setBackgroundColor(mcolor);
            }
        } else if (observable.getmColorValue() != null) {
            final Integer colorVal = observable.getmColorValue();
            if (colorVal != null) {
                view.setBackgroundColor(colorVal);
            }
        } else if (observable.getmDrawable() != null) {
            final Drawable drawable = observable.getmDrawable();
            if (drawable != null) {
                view.setBackground(drawable);
            }
        } else if (observable.getmBitmap() != null) {
            Bitmap bitmap = observable.getmBitmap();
            if (bitmap != null) {
                view.setBackground((new BitmapDrawable(view.getContext().getResources(), bitmap)));
            }
        } else {
            view.setBackgroundResource(0);
        }
    }

    public final void setDrawableResource(@DrawableRes int drawableResource) {
        this.reset();
        this.mDrawableResource = drawableResource;
        this.notifyChange();
    }

    public final void setColorResource(@ColorRes int colorResource) {
        this.reset();
        this.mColorResource = colorResource;
        this.notifyChange();
    }

    public final void setColorValue(int colorValue) {
        this.reset();
        this.mColorValue = colorValue;
        this.notifyChange();
    }

    @Nullable
    public Integer getmDrawableResource() {
        return mDrawableResource;
    }

    public void setmDrawableResource(@Nullable Integer mDrawableResource) {
        this.mDrawableResource = mDrawableResource;
    }

    @Nullable
    public Integer getmColorResource() {
        return mColorResource;
    }

    public void setmColorResource(@Nullable Integer mColorResource) {
        this.mColorResource = mColorResource;
    }

    @Nullable
    public Integer getmColorValue() {
        return mColorValue;
    }

    public void setmColorValue(@Nullable Integer mColorValue) {
        this.mColorValue = mColorValue;
    }

    @Nullable
    public Drawable getmDrawable() {
        return mDrawable;
    }

    public void setmDrawable(@Nullable Drawable mDrawable) {
        this.mDrawable = mDrawable;
    }

    @Nullable
    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(@Nullable Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }
}
