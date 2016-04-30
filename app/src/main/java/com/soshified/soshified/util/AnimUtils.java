package com.soshified.soshified.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;

import io.codetail.animation.SupportAnimator;

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

    public static void circularReveal(View view, boolean reverse) {
        int x = view.getRight();
        int y = view.getBottom();

        float radius = (float) Math.hypot(x, y);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator animator;
            if (!reverse) {
                animator = ViewAnimationUtils.createCircularReveal(view, x, y, 9, radius);
                view.setVisibility(View.VISIBLE);
            } else {
                animator = ViewAnimationUtils.createCircularReveal(view, x, y, radius, 0);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.INVISIBLE);
                    }
                });
            }
            animator.setDuration(400);
            animator.start();
        } else {
            SupportAnimator animator;
            if(!reverse) {
                animator = io.codetail.animation.ViewAnimationUtils
                        .createCircularReveal(view, x, y, 0, radius);
                view.setVisibility(View.VISIBLE);
            } else {
                animator = io.codetail.animation.ViewAnimationUtils
                        .createCircularReveal(view, x, y, radius, 0);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.INVISIBLE);
                    }
                });
            }
            animator.setDuration(400);
            animator.start();
        }
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

}
