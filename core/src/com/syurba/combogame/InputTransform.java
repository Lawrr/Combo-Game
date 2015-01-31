package com.syurba.combogame;

public class InputTransform {
    // Transforms x and y coords (which start from the top right) to proper libgdx coords (which start from the bottom left)
    public static float getCursorToModelX (int x) {
        return (((float)x) * ComboGame.screenWidth) / ((float)ComboGame.screenWidth);
    }

    public static float getCursorToModelY (int y) {
        return ((float)(ComboGame.screenHeight - y)) * ComboGame.screenHeight / ((float)ComboGame.screenHeight) ;
    }
}
