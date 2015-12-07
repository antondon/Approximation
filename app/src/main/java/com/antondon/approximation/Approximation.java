package com.antondon.approximation;

public class Approximation {

    private static final float ROUND_SCALE = 1000f;
    private static float[][] matrix = new float[4][5];

    /**
     * Get matrix for Least Squared method.
     *
     * @param points coordinates of points
     * @return matrix
     */
    public static float[][] getLeastSquaredMatrix(Point[] points) {
        float[] x = new float[points.length];
        float[] y = new float[points.length];
        for (int i = 0; i < points.length; i++) {
            x[i] = points[i].getX();
            y[i] = points[i].getY();
        }
        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < 4; k++) {
                matrix[i][k] = sum(x, i + k);
            }
        }
        for (int i = 0; i < 4; i++) {
            matrix[i][4] = sumDouble(x, y, i, 1);
        }
        return matrix;
    }

    /**
     * Count sum of each value in powered exponent
     *
     * @param values   values
     * @param exponent exponent
     * @return sum of exponentiation values
     */
    private static float sum(float[] values, int exponent) {
        float result = 0;
        for (float value : values) {
            result += Math.pow(value, exponent);
        }
        return result;
    }

    /**
     * Count sum of multiplication values1 in powered values1_exponent and values2 in powered values2_exponent
     *
     * @param values1          first values array
     * @param values2          second values array
     * @param values1_exponent first exponent
     * @param values2_exponent second exponent
     * @return sum of multiplication in powered values
     */
    private static float sumDouble(float[] values1, float[] values2, int values1_exponent, int values2_exponent) {
        float sum = 0;
        for (int i = 0; i < values1.length; i++) {
            sum += Math.pow(values1[i], values1_exponent) * Math.pow(values2[i], values2_exponent);
        }
        return sum;
    }

    /**
     * Get coefficients of approximating polynomial
     *
     * @param points tabular set points
     * @return array of coefficients
     */
    public static float[] getLeastSquaredParams(Point[] points) {
        float[] params = new float[4];
        float coeff;
        matrix = getLeastSquaredMatrix(points);
        //Finding a diagonal matrix by Gauss method
        // i - column
        for (int i = 0; i < matrix.length; i++) {
            // j - row
            for (int j = 0; j < matrix.length; j++) {
                coeff = matrix[j][i] / matrix[i][i];
                if (j != i) {
                    // k - column
                    for (int k = 0; k <= matrix.length; k++) {
                        matrix[j][k] = Math.round((matrix[j][k] - matrix[i][k] * coeff) * ROUND_SCALE) / ROUND_SCALE;
                    }
                }
            }
        }
        //Getting approximation params
        for (int i = 0; i < matrix.length; i++) {
            params[i] = Math.round(matrix[i][matrix.length] / matrix[i][i] * ROUND_SCALE) / ROUND_SCALE;
        }
        return params;
    }

    /**
     * Get value of approximation polynomial with coefficients "params" at point x
     *
     * @param params coefficients of approximation polynomial
     * @param x      variable
     * @return value of approximation polynomial at point x
     */
    public static float leastSquaredPolynomial(float[] params, float x) {
        return (float) (params[0] + params[1] * x + params[2] * Math.pow(x, 2) + params[3] * Math.pow(x, 3));
    }

    /**
     * Get value of Lagrange polynomial built by tabular set points at point a
     *
     * @param a      variable
     * @param points tabular set points
     * @return value of Lagrange polynomial at point a
     */
    public static float lagrangePolynomial(float a, Point[] points) {
        float result = 0;
        float product;
        for (int i = 0; i < points.length; i++) {
            product = 1;
            for (int j = 0; j < points.length; j++) {
                if (j != i) {
                    product *= (a - points[j].getX()) / (points[i].getX() - points[j].getX());
                }
            }
            result += points[i].getY() * product;
        }
        return result;
    }
}
