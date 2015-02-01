package com.syurba.combogame;

import com.badlogic.gdx.math.Rectangle;

public class StationaryBlock extends Rectangle {

    private boolean empty;

    public StationaryBlock(float x, float y, float width, float height, boolean empty) {
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
