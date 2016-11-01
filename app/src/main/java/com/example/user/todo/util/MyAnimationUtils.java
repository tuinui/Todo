package com.example.user.todo.util;

import android.animation.Animator;
import android.support.annotation.IntDef;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Saran on 1/11/2559.
 */
public class MyAnimationUtils {
    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    public static final int VISIBLE = 0x00000000;

    public static final int INVISIBLE = 0x00000004;

    public static final int GONE = 0x00000008;


    public static void animateRevealCompat(final View myView, @Visibility final int visibility, final Animator.AnimatorListener listener) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && myView.isAttachedToWindow()) {
            if (visibility == VISIBLE) {
                // get the center for the clipping circle
                int cx = myView.getWidth() / 2;
                int cy = myView.getHeight() / 2;

// get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

// create the animator for this view (the start radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
                anim.setInterpolator(new FastOutSlowInInterpolator());
// make the view visible and start the animation


                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        myView.setVisibility(View.VISIBLE);
                        if (listener != null) {
                            listener.onAnimationStart(animator);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (listener != null) {
                            listener.onAnimationEnd(animator);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        if (listener != null) {
                            listener.onAnimationCancel(animator);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        if (listener != null) {
                            listener.onAnimationRepeat(animator);
                        }
                    }
                });
                anim.start();
            } else {

// get the center for the clipping circle
                int cx = myView.getWidth() / 2;
                int cy = myView.getHeight() / 2;

// get the initial radius for the clipping circle
                float initialRadius = (float) Math.hypot(cx, cy);

// create the animation (the final radius is zero)
                final Animator anim =
                        ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
                anim.setDuration(myView.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));

// make the view invisible when the animation is done
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        if (listener != null) {
                            listener.onAnimationStart(animator);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        listener.onAnimationEnd(anim);
                        if (visibility == INVISIBLE) {
                            myView.setVisibility(View.INVISIBLE);
                        } else if (visibility == GONE) {
                            myView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        if (listener != null) {
                            listener.onAnimationCancel(animator);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        if (listener != null) {
                            listener.onAnimationRepeat(animator);
                        }
                    }
                });
//                anim.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        listener.onAnimationEnd(anim);
//                        if (visibility == INVISIBLE) {
//                            myView.setVisibility(View.INVISIBLE);
//                        } else if (visibility == GONE) {
//                            myView.setVisibility(View.GONE);
//                        }
//                    }
//                });

// start the animation
                anim.start();
            }
        } else {
            myView.setVisibility(visibility);
        }
    }

    public static void animateRevealCompat(final View myView, @Visibility final int visibility) {
        if (visibility == VISIBLE) {
            animateRevealCompat(myView, visibility, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    myView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            animateRevealCompat(myView, visibility, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (visibility == INVISIBLE) {
                        myView.setVisibility(View.INVISIBLE);
                    } else if (visibility == GONE) {
                        myView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

    }


}
