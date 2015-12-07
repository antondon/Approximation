package com.antondon.approximation;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity implements View.OnTouchListener, View.OnClickListener {

    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphView = (GraphView)findViewById(R.id.graphView);
        graphView.setOnTouchListener(this);
        findViewById(R.id.btnLeastSquares).setOnClickListener(this);
        findViewById(R.id.btnLagrangePolynomial).setOnClickListener(this);
        findViewById(R.id.btnClear).setOnClickListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.graphView:
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        graphView.setCurrentPointIndex(event.getX());
                        graphView.initPoint(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        graphView.movePoint(event.getY());
                        break;
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLeastSquares:
                graphView.leastSquaresApproximation();
                break;
            case R.id.btnLagrangePolynomial:
                graphView.lagrangeApproximation();
                break;
            case R.id.btnClear:
                graphView.clear();
        }
    }
}
