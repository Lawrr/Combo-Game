package com.syurba.combogame;

import com.badlogic.gdx.math.Rectangle;

public class Block extends Rectangle {

    public static float stationaryX = 130;

    private int index;
    private BlockColor color;

    public Block(float x, float y, float width, float height, int index) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.index = index;
        this.color = BlockColor.CLEAR;
    }

    public Block(float x, float y, float width, float height, int index, BlockColor color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.index = index;
        this.color = color;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public BlockColor getColor() {
        return color;
    }
    public void setColor(BlockColor color) {
        this.color = color;
    }
}
