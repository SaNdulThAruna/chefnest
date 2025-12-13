package com.sandul.chefnest.ui.customUI;

import androidx.appcompat.widget.AppCompatImageView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class RoundedImageView extends AppCompatImageView {

    private float radius = 20.0f; // Adjust the radius as needed
    private Path path;

    public RoundedImageView(Context context) {
        super(context);
        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        path.reset();
        path.addRoundRect(0, 0, w, h, radius, radius, Path.Direction.CW);
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

}
