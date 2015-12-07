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

    private static final int DIVISION_COUNT = 6;
    private static final int POINT_COUNT = 5;

    private int width, height;
    private int divisionStep, drawingStep;
    //Coordinates of the borders of the system of axes area
    private int xStartSOA, xEndSOA, yStartSOA, yEndSOA;
    private int padding, arrowShift, divisionShift, textShift, textSize;
    private int currentPointIndex;

    private Point[] points = new Point[POINT_COUNT];
    private ArrayList<Point> leastSquaresPoints = new ArrayList<>();
    private ArrayList<Point> lagrangePoints = new ArrayList<>();

    private Paint axisSystemPaint, pointPaint, leastSquaresPaint, lagrangePaint;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVariables();
    }

    //Init variables and constants
    private void initVariables() {
        padding = getResources().getDimensionPixelSize(R.dimen.axis_padding);
        arrowShift = getResources().getDimensionPixelSize(R.dimen.arrow_shift);
        divisionShift = getResources().getDimensionPixelSize(R.dimen.division_shift);
        drawingStep = getResources().getDimensionPixelSize(R.dimen.drawing_step);
        textShift = getResources().getDimensionPixelSize(R.dimen.text_shift);
        textSize = getResources().getDimensionPixelSize(R.dimen.text_size);
        int lineSize = getResources().getDimensionPixelSize(R.dimen.line_size);

        axisSystemPaint = new Paint();
        axisSystemPaint.setColor(Color.BLACK);
        axisSystemPaint.setStrokeWidth(lineSize);
        axisSystemPaint.setTextSize(textSize);
        axisSystemPaint.setTextAlign(Paint.Align.CENTER);

        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.point_size));

        leastSquaresPaint = new Paint();
        leastSquaresPaint.setStyle(Paint.Style.STROKE);
        leastSquaresPaint.setAntiAlias(true);
        leastSquaresPaint.setColor(Color.BLUE);
        leastSquaresPaint.setStrokeWidth(lineSize);

        lagrangePaint = new Paint();
        lagrangePaint.setStyle(Paint.Style.STROKE);
        lagrangePaint.setAntiAlias(true);
        lagrangePaint.setColor(Color.GREEN);
        lagrangePaint.setStrokeWidth(lineSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPointIndex = getCurrentPointIndex(event.getX());
                setCurrentPoint(event.getY());
                if (!lagrangePoints.isEmpty()) {
                    lagrangeApproximation();
                }
                if (!leastSquaresPoints.isEmpty()) {
                    leastSquaresApproximation();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                setCurrentPoint(event.getY());
                if (!lagrangePoints.isEmpty()) {
                    lagrangeApproximation();
                }
                if (!leastSquaresPoints.isEmpty()) {
                    leastSquaresApproximation();
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        xStartSOA = padding;
        xEndSOA = width - padding;
        yStartSOA = padding;
        yEndSOA = height - padding;
        divisionStep = (width - padding * 2 - arrowShift * 2) / DIVISION_COUNT;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        drawAxisSystem(canvas);
        if (!leastSquaresPoints.isEmpty()) {
            drawLeastSquaresGraph(canvas);
        }
        if (!lagrangePoints.isEmpty()) {
            drawLagrangeGraph(canvas);
        }
        drawPoints(canvas);
    }

    private void drawAxisSystem(Canvas canvas) {
        //Draw coordinate axis
        canvas.drawLine(xStartSOA, yStartSOA, xStartSOA, yEndSOA, axisSystemPaint);
        canvas.drawLine(xStartSOA, yEndSOA, xEndSOA, yEndSOA, axisSystemPaint);

        //Draw arrows
        canvas.drawLine(xStartSOA, yStartSOA, xStartSOA + arrowShift, yStartSOA + arrowShift, axisSystemPaint);
        canvas.drawLine(xStartSOA, yStartSOA, xStartSOA - arrowShift, yStartSOA + arrowShift, axisSystemPaint);
        canvas.drawLine(xEndSOA, yEndSOA, xEndSOA - arrowShift, yEndSOA - arrowShift, axisSystemPaint);
        canvas.drawLine(xEndSOA, yEndSOA, xEndSOA - arrowShift, yEndSOA + arrowShift, axisSystemPaint);

        //Draw divisions
        //X-axis
        canvas.drawText(Integer.toString(0), xStartSOA, yEndSOA + divisionShift + textShift + textSize / 2, axisSystemPaint);
        int startX, startY, stopX, stopY;
        for (int i = 0; i <= (width - padding * 2) / divisionStep; i++) {
            startX = xStartSOA + divisionStep * i;
            startY = yEndSOA - divisionShift;
            stopX = xStartSOA + divisionStep * i;
            stopY = yEndSOA + divisionShift;

            canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
            canvas.drawText(Integer.toString(i), startX, stopY + textShift + textSize / 2, axisSystemPaint);
        }

        //Y-axis
        for (int i = 0; i <= (height - padding * 2) / divisionStep; i++) {
            startX = xStartSOA - divisionShift;
            startY = yEndSOA - divisionStep * i;
            stopX = xStartSOA + divisionShift;
            stopY = yEndSOA - divisionStep * i;

            canvas.drawLine(startX, startY, stopX, stopY, axisSystemPaint);
            if (i != 0) {
                canvas.drawText(Integer.toString(i), startX - textShift, stopY + textSize / 2, axisSystemPaint);
            }
        }
    }

    private void drawPoints(Canvas canvas) {
        for (Point point : points) {
            if (point != null) {
                canvas.drawPoint(point.getX(), point.getY(), pointPaint);
            }
        }
    }

    private void drawLeastSquaresGraph(Canvas canvas) {
        for (int i = 1; i < leastSquaresPoints.size(); i++) {
            canvas.drawLine(leastSquaresPoints.get(i - 1).getX(), leastSquaresPoints.get(i - 1).getY(),
                    leastSquaresPoints.get(i).getX(), leastSquaresPoints.get(i).getY(), leastSquaresPaint);
        }
    }

    public void clear() {
        leastSquaresPoints.clear();
        lagrangePoints.clear();
        points = new Point[POINT_COUNT];
        invalidate();
    }

    private void drawLagrangeGraph(Canvas canvas) {
        for (int i = 1; i < lagrangePoints.size(); i++) {
            canvas.drawLine(lagrangePoints.get(i - 1).getX(), lagrangePoints.get(i - 1).getY(),
                    lagrangePoints.get(i).getX(), lagrangePoints.get(i).getY(), lagrangePaint);
        }
    }

    private int getCurrentPointIndex(float x) {
        for (int i = 0; i < POINT_COUNT; i++) {
            float rangeStart = (i + 0.5f) * divisionStep;
            float rangeEnd = (i + 1.5f) * divisionStep;
            if ((x >= xStartSOA + rangeStart) && (x < xStartSOA + rangeEnd)) {
                return i;
            }
        }
        return -1;
    }

    private void setCurrentPoint(float y) {
        if (currentPointIndex != -1 && y > yStartSOA && y < yEndSOA) {
            points[currentPointIndex] = new Point((currentPointIndex + 1) * divisionStep + xStartSOA, y);
        }
    }

    private Point[] getRelativeCoordinates(Point[] points) {
        Point[] relativePoints = new Point[points.length];
        float x, y;
        for (int i = 0; i < points.length; i++) {
            x = (points[i].getX() - xStartSOA) / divisionStep;
            y = (yEndSOA - points[i].getY()) / divisionStep;
            relativePoints[i] = new Point(x, y);
        }
        return relativePoints;
    }

    public void leastSquaresApproximation() {
        leastSquaresPoints.clear();
        Point[] relativeCoordinates = getRelativeCoordinates(points);
        float[] leastSquaresParams = Approximation.getLeastSquaredParams(relativeCoordinates);
        float x = xStartSOA;
        float y;
        do {
            y = Approximation.leastSquaredPolynomial(leastSquaresParams, getRelativeX(x));
            leastSquaresPoints.add(new Point(x, getAbsoluteY(y)));
            x += drawingStep;
        } while (x < xStartSOA + divisionStep * DIVISION_COUNT);
    }

    public void lagrangeApproximation() {
        lagrangePoints.clear();
        Point[] relativeCoordinates = getRelativeCoordinates(points);
        float x = xStartSOA;
        float y;
        do {
            y = Approximation.lagrangePolynomial(getRelativeX(x), relativeCoordinates);
            lagrangePoints.add(new Point(x, getAbsoluteY(y)));
            x += drawingStep;
        } while (x < xStartSOA + divisionStep * DIVISION_COUNT);
    }

    private float getRelativeX(float x) {
        return (x - xStartSOA) / divisionStep;
    }

    private float getAbsoluteY(float y) {
        return yEndSOA - y * divisionStep;
    }

    public boolean checkPointCount() {
        for (Point point : points) {
            if (point == null) {
                return false;
            }
        }
        return true;
    }
}
