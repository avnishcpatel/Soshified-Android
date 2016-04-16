package com.soshified.soshified.util;

/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.transition.Transition;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;

/**
 * Utility methods for working with animations.
 */
public class AnimUtils {

    private AnimUtils() { }

    /**
     * Animates the provided view from invisible to visible
     *
     * @param v View to be animated
     * @param duration Duration of animation
     * @param visibility Whether to be Invisible or Visible
     */
    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public static void doSimpleYAnimation(View view, float offset, Interpolator interpolator) {
        view.setTranslationY(offset);
        view.setAlpha(0.8f);
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(interpolator)
                .start();

    }

    /**
     * Blurs the header image for when the AppBar collapses
     * @param context Context to build RenderScript from
     * @param bitmap Original bitmap.
     * @param supportRS Whether or not to use the Support Lib RenderScript library.
     * @return Blurred Bitmap
     */
    @SuppressLint("NewApi")
    public static BitmapDrawable blur(Context context, Bitmap bitmap, boolean supportRS) {
        int width = Math.round(bitmap.getWidth() * 0.1f);
        int height = Math.round(bitmap.getHeight() * 0.1f);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Bitmap finalBitmap = Bitmap.createBitmap(inputBitmap);

        if (supportRS) {
            RenderScript renderScript = RenderScript.create(context);
            ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript,
                    Element.U8_4(renderScript));
            Allocation tmpIn = Allocation.createFromBitmap(renderScript, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(renderScript, finalBitmap);
            intrinsicBlur.setRadius(15.5f);
            intrinsicBlur.setInput(tmpIn);
            intrinsicBlur.forEach(tmpOut);
            tmpOut.copyTo(finalBitmap);
        } else {
            android.renderscript.RenderScript renderScript =
                    android.renderscript.RenderScript.create(context);

            android.renderscript.ScriptIntrinsicBlur intrinsicBlur =
                    android.renderscript.ScriptIntrinsicBlur.create(renderScript,
                            android.renderscript.Element.U8_4(renderScript));

            android.renderscript.Allocation tmpIn = android.renderscript.Allocation
                    .createFromBitmap(renderScript, inputBitmap);
            android.renderscript.Allocation tmpOut = android.renderscript.Allocation
                    .createFromBitmap(renderScript, finalBitmap);
            intrinsicBlur.setRadius(15.5f);
            intrinsicBlur.setInput(tmpIn);
            intrinsicBlur.forEach(tmpOut);
            tmpOut.copyTo(finalBitmap);
        }

        return new BitmapDrawable(context.getResources(), finalBitmap);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static class TransitionListenerAdapter implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }

}
