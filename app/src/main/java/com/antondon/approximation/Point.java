package com.antondon.approximation;

public class Point {
    private float x , y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setXY(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX() {

        return x;
    }

    public float getY() {
        return y;
    }
}
