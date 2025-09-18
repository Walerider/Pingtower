package com.walerider.pingdom.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.walerider.pingdom.R;

public class AnimatedStatusView extends View {
    private Paint onlinePaint;
    private Paint offlinePaint;
    private Paint borderPaint;
    private boolean isOnline = false;
    private int borderWidth = 4;
    private ValueAnimator animator;

    public AnimatedStatusView(Context context) {
        super(context);
        init();
    }
    public AnimatedStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        onlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        onlinePaint.setColor(Color.GREEN);
        onlinePaint.setStyle(Paint.Style.FILL);

        offlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        offlinePaint.setColor(Color.RED);
        offlinePaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
    }

    public void setOnlineWithAnimation(boolean online) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        if (isOnline == online) {
            return;
        }

        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            // Можно добавить анимацию изменения размера или цвета
            invalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOnline = online;
                invalidate();
            }
        });

        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2 - borderWidth;

        Paint statusPaint = isOnline ? onlinePaint : offlinePaint;
        canvas.drawCircle(centerX, centerY, radius, statusPaint);
        canvas.drawCircle(centerX, centerY, radius, borderPaint);
    }
}
