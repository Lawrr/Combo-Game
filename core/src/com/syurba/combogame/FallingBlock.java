package com.syurba.combogame;

import com.badlogic.gdx.math.Rectangle;

public class FallingBlock extends Rectangle {
    private boolean selected;

    public FallingBlock (int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
