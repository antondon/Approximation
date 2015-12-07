package com.antondon.approximation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class GraphView extends View {

    private int width, height;
    private float padding_dp = 30f;
    private float scale;

    int divisionStep;
    //Coordinates of the borders of the system of axes area
    private int xStartSOA, xEndSOA, yStartSOA, yEndSOA;

    private int padding, arrowShift, divisionShift, textShift, textSize;
    private int divisionCount;
    private int currentPointIndex;

    private boolean variablesInitialized = false;
    Point[] points = new Point[5];
    private int pointSize;
    private int lineSize;
    private int drawingStep;

    private ArrayList<Point> leastSquaresPoints = new ArrayList<>();
    private ArrayList<Point> lagrangePoints = new ArrayList<>();

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //Init variables and constants
    private void initVariables() {
        scale = getResources().getDisplayMetrics().density;
        height = getHeight();
        width = getWidth();

        padding = dpsToPixels(padding_dp);
        arrowShift = dpsToPixels(5f);
        divisionShift = dpsToPixels(7f);

        xStartSOA = padding;
        xEndSOA = width - padding;
        yStartSOA = padding;
        yEndSOA = height - padding;

        divisionCount = 6;
        divisionStep = (width - padding * 2 - arrowShift * 2) / divisionCount;
        drawingStep = dpsToPixels(1f);

        textShift = dpsToPixels(7f);
        textSize = dpsToPixels(14f);
        pointSize = dpsToPixels(3f);
        lineSize = dpsToPixels(1f);

        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(0, 0);
        }
    }

    private void drawAxisSystem(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(lineSize);

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
        for (int i = 0; i <= (width - padding * 2) / divisionStep; i++) {
            int startX = xStartSOA + divisionStep * i;
            int startY = yEndSOA - divisionShift;
            int stopX = xStartSOA + divisionStep * i;
            int stopY = yEndSOA + divisionShift;

            canvas.drawLine(startX, startY, stopX, stopY, paint);
            canvas.drawText(Integer.toString(i), startX, stopY + textShift + textSize / 2, paint);
        }

        //Y-axis
        for (int i = 0; i <= (height - padding * 2) / divisionStep; i++) {
            int startX = xStartSOA - divisionShift;
            int startY = yEndSOA - divisionStep * i;
            int stopX = xStartSOA + divisionShift;
            int stopY = yEndSOA - divisionStep * i;
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            if (i != 0)
                canvas.drawText(Integer.toString(i), startX - textShift, stopY + textSize / 2, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!variablesInitialized) {
            initVariables();
            variablesInitialized = true;
        }

        canvas.drawColor(Color.WHITE);
        drawAxisSystem(canvas);
        drawLeastSquaresGraph(canvas);
        drawLagrangeGraph(canvas);
        drawPoints(canvas);
    }

    private void drawPoints(Canvas canvas){
        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStrokeWidth(pointSize);
        for (int i = 0; i < points.length; i++)
            if (points[i].getX() != 0 && points[i].getY() != 0)
                canvas.drawPoint(points[i].getX(), points[i].getY(), pointPaint);
    }

    private void drawLeastSquaresGraph(Canvas canvas){
        if (leastSquaresPoints.isEmpty())
            return;
        leastSquaresApproximation();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(lineSize);

        for (int i = 1; i < leastSquaresPoints.size(); i++)
            canvas.drawLine(leastSquaresPoints.get(i - 1).getX(), leastSquaresPoints.get(i - 1).getY(),
                    leastSquaresPoints.get(i).getX(), leastSquaresPoints.get(i).getY(), paint);
    }

    public void clear(){
        leastSquaresPoints.clear();
        lagrangePoints.clear();
        for (Point point : points) point.setXY(0, 0);
        invalidate();
    }

    private void drawLagrangeGraph(Canvas canvas){
        if (lagrangePoints.isEmpty())
            return;
        lagrangeApproximation();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(lineSize);
        for (int i = 1; i < lagrangePoints.size(); i++)
            canvas.drawLine(lagrangePoints.get(i - 1).getX(), lagrangePoints.get(i - 1).getY(),
                    lagrangePoints.get(i).getX(), lagrangePoints.get(i).getY(), paint);

    }

    public void setCurrentPointIndex(float x) {
        for (int i = 0; i  < points.length; i++){
            float rangeStart = (i + 0.5f) * divisionStep;
            float rangeEnd = (i + 1.5f) * divisionStep;
            if ((x >= xStartSOA + rangeStart) && (x < xStartSOA + rangeEnd)) {
                this.currentPointIndex = i;
                return;
            }
        }
        currentPointIndex = -1;
    }

    public void initPoint(float x, float y){
        //Coordinates in range of axis system
        if (x > xStartSOA && x < xEndSOA && y > yStartSOA && y < yEndSOA) {
            for (int i = 0; i < points.length; i++) {
                float rangeStart = (i + 0.5f) * divisionStep;
                float rangeEnd = (i + 1.5f) * divisionStep;
                if ((x >= xStartSOA + rangeStart) && (x < xStartSOA + rangeEnd))
                    points[i].setXY((i + 1) * divisionStep + xStartSOA, y);
            }
            invalidate();
        }
    }

    public void movePoint(float y){
        if (currentPointIndex != -1 && y > yStartSOA && y < yEndSOA)
        {
            points[currentPointIndex].setXY((currentPointIndex + 1) * divisionStep + xStartSOA, y);
            invalidate();
        }
    }

    private Point[] getRelativeCoordinates (Point[] points){
        Point[] relativePoints = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            float x = (points[i].getX() - xStartSOA) / divisionStep;
            float y = (yEndSOA - points[i].getY()) / divisionStep;
            relativePoints[i] = new Point(x, y);
        }
        return relativePoints;
    }

    public void leastSquaresApproximation() {
        for (int i = 0; i < points.length; i++)
            if (points[i].getX() == 0 && points[i].getY() == 0)
            {
                Toast.makeText(getContext(), "You should define all 5 points", Toast.LENGTH_SHORT).show();
                return;
            }
        leastSquaresPoints.clear();
        Point[] relativeCoordinates =  getRelativeCoordinates(points);
        float[] leastSquaresParams = Approximation.getLeastSquaredParams(relativeCoordinates);
        float x = xStartSOA;
        float y = Approximation.leastSquaredPolynomial(leastSquaresParams, getRelativeX(x));
        leastSquaresPoints.add(new Point(x, getAbsoluteY(y)));
        while (x < xStartSOA + divisionStep * 6){
            x += drawingStep;
            y = Approximation.leastSquaredPolynomial(leastSquaresParams, getRelativeX(x));
            leastSquaresPoints.add(new Point(x, getAbsoluteY(y)));
        }
        invalidate();
    }

    public void lagrangeApproximation(){
        for (int i = 0; i < points.length; i++)
            if (points[i].getX() == 0 && points[i].getY() == 0)
            {
                Toast.makeText(getContext(), "You should define all 5 points", Toast.LENGTH_SHORT).show();
                return;
            }
        lagrangePoints.clear();
        Point[] relativeCoordinates =  getRelativeCoordinates(points);
        float x = xStartSOA;
        float y = Approximation.lagrangePolynomial(getRelativeX(x), relativeCoordinates);
        lagrangePoints.add(new Point(x, getAbsoluteY(y)));
        while (x < xStartSOA + divisionStep * 6){
            x += drawingStep;
            y = Approximation.lagrangePolynomial(getRelativeX(x), relativeCoordinates);
            lagrangePoints.add(new Point(x, getAbsoluteY(y)));
        }
        invalidate();
    }

    private float getRelativeX(float x){
        return (x - xStartSOA) / divisionStep;
    }

    private float getAbsoluteY(float y){
        return yEndSOA - y * divisionStep;
    }

    private int dpsToPixels(float dps) {
        return (int) (dps * scale + 0.5f);
    }
}
