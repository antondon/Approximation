package com.antondon.approximation;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnTouchListener, View.OnClickListener {

    private String TAG = "canvas size";
    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphView = (GraphView)findViewById(R.id.graphView);
        graphView.setOnTouchListener(this);
        Button btnLeastSquared = (Button) findViewById(R.id.btnLeastSquares);
        btnLeastSquared.setOnClickListener(this);
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
                graphView.drawLeastSquaresApproximation();
                break;
        }

    }
}
