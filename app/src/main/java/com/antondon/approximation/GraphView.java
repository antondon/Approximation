package com.antondon.approximation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {

    Paint paint = new Paint();
    private int width, height;
    private float padding_dp = 30f;
    private final float scale;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scale = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCoordinateSystem(canvas);
    }

    private void drawCoordinateSystem(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);

        height = getHeight();
        width = getWidth();

        int padding = dpsToPixels(padding_dp);
        int axisShift = dpsToPixels(10f);
        int arrowShift = dpsToPixels(5f);
        int divisionShift = dpsToPixels(7f);

        //Draw coordinate axis
        canvas.drawLine(padding, padding, padding, height - padding + axisShift, paint);
        canvas.drawLine(padding - axisShift, height - padding, width - padding, height - padding, paint);

        //Draw arrows
        canvas.drawLine(padding, padding, padding + arrowShift, padding + arrowShift, paint);
        canvas.drawLine(padding, padding, padding - arrowShift, padding + arrowShift, paint);
        canvas.drawLine(width - padding, height - padding, width - padding - arrowShift, height - padding - arrowShift, paint);
        canvas.drawLine(width - padding, height - padding, width - padding - arrowShift, height - padding + arrowShift, paint);

        //Draw divisions
        int divisionStep = (width - padding * 2 - arrowShift * 2) / 6;
        int textShift = dpsToPixels(7f);
        int textSize = dpsToPixels(14f);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        //X-axis
        for (int i = 1; i < (width - padding) / divisionStep; i++){
            int startX = padding + divisionStep * i;
            int startY = height - padding - divisionShift;
            int stopX = padding + divisionStep * i;
            int stopY = height - padding + divisionShift;

            canvas.drawLine(startX, startY, stopX, stopY, paint);
            canvas.drawText(Integer.toString(i), startX, stopY + textShift + textSize / 2, paint);
}

        //Y-axis
        for (int i = 1; i < (height - padding) / divisionStep; i++){
            int startX = padding - divisionShift;
            int startY =  height - padding - divisionStep * i;
            int stopX = padding + divisionShift;
            int stopY = height - padding - divisionStep * i;

            canvas.drawLine(startX, startY,stopX, stopY, paint);
            canvas.drawText(Integer.toString(i), startX - textShift, stopY + textSize / 2, paint);
        }
    }

    private int dpsToPixels(float dps){
        return (int) (dps * scale + 0.5f);
    }
}
