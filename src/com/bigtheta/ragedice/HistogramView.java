package com.bigtheta.ragedice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HistogramView extends View {
    private final Paint m_paint = new Paint();
    
    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        
    }
}
