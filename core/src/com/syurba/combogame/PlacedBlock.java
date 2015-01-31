package com.syurba.combogame;

import com.badlogic.gdx.math.Rectangle;

public class PlacedBlock extends Rectangle {

    private boolean empty;

    public PlacedBlock(float x, float y, float width, float height, boolean empty) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.empty = empty;
    }

    public boolean isEmpty() {
        return empty;
    }
}
