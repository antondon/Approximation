package com.antondon.approximation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GraphView extends View {

    Paint paint = new Paint();
    private int width, height;
    private float padding_dp = 30f;
    private float scale;
    private Canvas canvas = null;

    int divisionStep;
    //Coordinates of the borders of the system of axes area
    private int xStartSOA, xEndSOA, yStartSOA, yEndSOA;

    private float pointX = 0, pointY = 0;
    private int padding;
    private int arrowShift;
    private int divisionShift;
    private int divisionCount;
    private int textShift;
    private int textSize;

    private boolean variablesInitialized = false;
    Point[] points = new Point[6];
    int currentPoint;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVariables();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.canvas == null)
            this.canvas = canvas;
        if (!variablesInitialized){
            initVariables();
            variablesInitialized = true;
        }

        canvas.drawColor(Color.WHITE);
        drawAxisSystem();
        for (int i = 0; i < 6; i++)
        {
            if (points[i].getX() != 0 && points[i].getY() != 0)
                drawPoint(points[i]);
        }
    }

    private void drawPoint(Point point){
        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStrokeWidth(3);
        canvas.drawPoint(point.getX(), point.getY(), pointPaint);
    }


    //Initializing fields
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initVariables();
    }


    //Indicate current point
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = null;
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Point in range of axis system
                if (x > xStartSOA && x < xEndSOA && y > yStartSOA && y < yEndSOA) {
                    for (int i = 0; i < 5; i++) {
                        float rangeStart = (i + 0.5f) * divisionStep;
                        float rangeEnd = (i + 1.5f) * divisionStep;
                        if ((x >= xStartSOA + rangeStart) && (x < xStartSOA + rangeEnd)) {
                            points[i].setXY((i + 1) * divisionStep + xStartSOA, y);
                            currentPoint = i;
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //if x in range of current point redraw it
                if (x > points[currentPoint].getX() - divisionStep * 1f / 2 && x <= points[currentPoint].getX() + divisionStep * 1f / 2){
                    points[currentPoint].setXY((currentPoint + 1) * divisionStep + xStartSOA, y);
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    //Init variables and constants
    private void initVariables(){
        scale = getResources().getDisplayMetrics().density;
        height = getHeight();
        width = getWidth();

        //rename padding
        padding = dpsToPixels(padding_dp);
        arrowShift = dpsToPixels(5f);
        divisionShift = dpsToPixels(7f);

        xStartSOA = padding;
        xEndSOA = width - padding;
        yStartSOA = padding;
        yEndSOA = height - padding;

        divisionCount = 6;
        divisionStep = (width - padding * 2 - arrowShift * 2) / divisionCount;
        textShift = dpsToPixels(7f);
        textSize = dpsToPixels(14f);

        for (int i = 0; i < 6; i++){
            points[i] = new Point(0, 0);
        }
    }

    private void drawAxisSystem(){
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);

        //Draw coordinate axis
        canvas.drawLine(xStartSOA, yStartSOA, xStartSOA, yEndSOA, paint);
        canvas.drawLine(xStartSOA, yEndSOA, xEndSOA, yEndSOA, paint);

        //Draw arrows
        canvas.drawLine(xStartSOA, yStartSOA, xStartSOA + arrowShift, yStartSOA + arrowShift, paint);
        canvas.drawLine(xStartSOA, yStartSOA, xStartSOA - arrowShift, yStartSOA + arrowShift, paint);
        canvas.drawLine(xEndSOA, yEndSOA, xEndSOA - arrowShift, yEndSOA - arrowShift, paint);
        canvas.drawLine(xEndSOA, yEndSOA, xEndSOA - arrowShift, yEndSOA + arrowShift, paint);

        //Draw divisions
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        //X-axis
        canvas.drawText(Integer.toString(0), xStartSOA, yEndSOA + divisionShift + textShift + textSize / 2, paint);
        for (int i = 0; i <= (width - padding * 2) / divisionStep; i++){
            int startX = xStartSOA + divisionStep * i;
            int startY = yEndSOA - divisionShift;
            int stopX = xStartSOA + divisionStep * i;
            int stopY = yEndSOA + divisionShift;

            canvas.drawLine(startX, startY, stopX, stopY, paint);
            canvas.drawText(Integer.toString(i), startX, stopY + textShift + textSize / 2, paint);
        }

        //Y-axis
        for (int i = 0; i <= (height - padding * 2) / divisionStep; i++){
            int startX = xStartSOA - divisionShift;
            int startY =  yEndSOA - divisionStep * i;
            int stopX = xStartSOA + divisionShift;
            int stopY = yEndSOA - divisionStep * i;
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            if (i != 0)
                canvas.drawText(Integer.toString(i), startX - textShift, stopY + textSize / 2, paint);
        }
    }

    private void drawGraph(){

    }

    private int dpsToPixels(float dps){
        return (int) (dps * scale + 0.5f);
    }
}
