package dev.jorgecastillo.lifecolors.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.ColorRes;
import dev.jorgecastillo.lifecolors.R;

public class GUIUtils {

  public static void animateRevealHide(final Context ctx, final View view, final @ColorRes int color,
      final int finalRadius, final OnRevealAnimationListener listener) {
    int cx = (view.getLeft() + view.getRight()) / 2;
    int cy = (view.getTop() + view.getBottom()) / 2;
    int initialRadius = view.getWidth();

    Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, finalRadius);
    anim.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        view.setBackgroundColor(ctx.getResources().getColor(color));
      }

      @Override public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        view.setVisibility(View.INVISIBLE);
        listener.onRevealHide();
      }
    });
    anim.setDuration(ctx.getResources().getInteger(R.integer.animation_duration));
    anim.start();
  }

  public static void animateRevealShow(
      final Context ctx,
      final View view,
      final int startRadius,
      final @ColorRes int color,
      int x,
      int y,
      final OnRevealAnimationListener listener) {

    float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

    Animator anim = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, finalRadius);
    anim.setDuration(ctx.getResources().getInteger(R.integer.animation_duration));
    anim.setStartDelay(100);
    anim.setInterpolator(new AccelerateDecelerateInterpolator());
    anim.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        view.setBackgroundColor(ctx.getResources().getColor(color));
      }

      @Override public void onAnimationEnd(Animator animation) {
        view.setVisibility(View.VISIBLE);
        listener.onRevealShow();
      }
    });
    anim.start();
  }
}
