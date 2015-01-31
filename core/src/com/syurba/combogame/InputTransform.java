package com.syurba.combogame;

import com.badlogic.gdx.Gdx;

public class InputTransform {
    // Transforms x and y coords (which start from the top left) to proper libgdx coords (which start from the bottom left)
    // Also deals with screen size scaling
    public static float getCursorToModelX (int x) {
        return (((float)x) * ComboGame.screenWidth) / ((float) Gdx.graphics.getWidth());
    }

    public static float getCursorToModelY (int y) {
        return ((float)(Gdx.graphics.getHeight() - y)) * ComboGame.screenHeight / ((float)Gdx.graphics.getHeight()) ;
    }
}
