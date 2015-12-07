package com.antondon.approximation;

public class Approximation {

    private static final float ROUND_SCALE = 1000f;
    private static float[][] matrix = new float[4][5];

    public static float[][] getLeastSquaredMatrix(Point[] points) {
        float[] X = new float[points.length];
        float[] Y = new float[points.length];
        for (int i = 0; i < points.length; i++) {
            X[i] = points[i].getX();
            Y[i] = points[i].getY();
        }
        for (int i = 0; i < 4; i++)
            for (int k = 0; k < 4; k++) {
                matrix[i][k] = sum(X, i + k);
            }
        for (int i = 0; i < 4; i++)
            matrix[i][4] = sumDouble(X, Y, i, 1);
        return matrix;
    }

    //Simple sum method
    private static float sum(float[] values, int exponent) {
        float result = 0;
        for (float value : values)
            result += Math.pow(value, exponent);
        return result;
    }

    //Sum method method for two vectors
    private static float sumDouble(float[] values1, float[] values2, int values1_exponent, int values2_exponent) {
        float sum = 0;
        for (int i = 0; i < values1.length; i++)
            sum += Math.pow(values1[i], values1_exponent) * Math.pow(values2[i], values2_exponent);
        return sum;
    }

    public static float[] getLeastSquaredParams(Point[] points) {
        float[] params = new float[4];
        float M;
        matrix = getLeastSquaredMatrix(points);
        int n = matrix.length;
        //Finding a diagonal matrix by Gauss method
        // i - column
        for (int i = 0; i < n; i++) {
            // j - row
            for (int j = 0; j < n; j++) {
                M = matrix[j][i] / matrix[i][i];
                if (j != i)
                    // k - column
                    for (int k = 0; k <= n; k++)
                        matrix[j][k] = Math.round((matrix[j][k] - matrix[i][k] * M) * ROUND_SCALE) / ROUND_SCALE;
            }
        }
        //Getting approximation params
        for (int i = 0; i < n; i++)
            params[i] = Math.round(matrix[i][n] / matrix[i][i] * ROUND_SCALE) / ROUND_SCALE;
        return params;
    }

    public static float leastSquaredPolynomial(float[] params, float x) {
        return (float) (params[0] + params[1] * x + params[2] * Math.pow(x, 2) + params[3] * Math.pow(x, 3));
    }

    public static float lagrangePolynomial(float a, Point[] points) {
        float result = 0;
        float product;
        for (int i = 0; i < points.length; i++) {
            //Y[i]
            product = 1;
            for (int j = 0; j < points.length; j++) {
                if (j != i)
                    product *= (a - points[j].getX()) / (points[i].getX() - points[j].getX());
            }
            result += points[i].getY() * product;
        }
        return result;
    }
}
