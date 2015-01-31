package com.syurba.combogame;

import com.badlogic.gdx.math.Rectangle;

public class FallingBlock extends Rectangle {

    private int index;
    private boolean placed;

    public FallingBlock (float x, float y, float width, float height, int index) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.index = index;
        this.placed = false;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public boolean isPlaced() {
        return placed;
    }
    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

}
