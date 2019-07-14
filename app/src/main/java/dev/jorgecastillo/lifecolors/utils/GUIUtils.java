package dev.jorgecastillo.lifecolors.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

public class GUIUtils {

  public static void animateRevealHide(final View view, final int color, int bgColor,
      final int finalRadius, final OnRevealAnimationListener listener) {
    int cx = (view.getLeft() + view.getRight()) / 2;
    int cy = (view.getTop() + view.getBottom()) / 2;
    int initialRadius = view.getWidth();

    Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, finalRadius);
    anim.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        view.setBackgroundColor(color);
      }

      @Override public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        view.setBackgroundColor(bgColor);
        listener.onRevealHide();
      }
    });
    anim.setDuration(150);
    anim.start();
  }

  public static void animateRevealShow(final View view, final int startRadius, final int color, int x, int y,
      final OnRevealAnimationListener listener) {

    float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

    Animator anim = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, finalRadius);
    anim.setDuration(200);
    anim.setStartDelay(50);
    anim.setInterpolator(new AccelerateDecelerateInterpolator());
    anim.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        view.setBackgroundColor(color);
      }

      @Override public void onAnimationEnd(Animator animation) {
        view.setVisibility(View.VISIBLE);
        listener.onRevealShow();
      }
    });
    anim.start();
  }
}
