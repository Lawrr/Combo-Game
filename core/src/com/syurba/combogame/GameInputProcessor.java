package com.syurba.combogame;

import com.badlogic.gdx.InputProcessor;

import java.util.Iterator;

public class GameInputProcessor implements InputProcessor {
    private final GameScreen gameScreen;

    public GameInputProcessor (GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        float pointerX = InputTransform.getCursorToModelX(x);
        float pointerY = InputTransform.getCursorToModelY(y);

        Iterator<FallingBlock> iter = gameScreen.getFallingBlocks().iterator();
        while (iter.hasNext()) {
            FallingBlock fallingBlock = iter.next();
            if (fallingBlock.contains(pointerX, pointerY)) {
                float placedBlockY = gameScreen.getNumPlaced() * fallingBlock.getHeight();
                PlacedBlock newBlock = new PlacedBlock(80, placedBlockY, gameScreen.getPlacedBlockImage().getWidth(), gameScreen.getPlacedBlockImage().getHeight());
                gameScreen.getPlacedBlocks().add(newBlock);
                gameScreen.setNumPlaced(gameScreen.getNumPlaced() + 1);
                iter.remove();
            }
        }
        return true;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved (int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        return false;
    }
}