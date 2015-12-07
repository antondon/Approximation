package com.antondon.approximation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graphView = (GraphView) findViewById(R.id.graphView);
        findViewById(R.id.btnLeastSquares).setOnClickListener(this);
        findViewById(R.id.btnLagrangePolynomial).setOnClickListener(this);
        findViewById(R.id.btnClear).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeastSquares:
                if (graphView.checkPointCount()) {
                    graphView.leastSquaresApproximation();
                    graphView.invalidate();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.define_all_points), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnLagrangePolynomial:
                if (graphView.checkPointCount()) {
                    graphView.lagrangeApproximation();
                    graphView.invalidate();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.define_all_points), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnClear:
                graphView.clear();
        }
    }
}
