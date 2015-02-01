package com.syurba.combogame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen {
    private final ComboGame game;

    private OrthographicCamera camera = new OrthographicCamera();
    private long lastBlockCreateTime;

    private Texture clearBlockImage = new Texture("clear-block.png");
    private Texture redBlockImage = new Texture("red-block.png");
    private Texture greenBlockImage = new Texture("green-block.png");
    private Texture blueBlockImage = new Texture("blue-block.png");
    private Texture yellowBlockImage = new Texture("yellow-block.png");

    private Array<Block> fallingBlocks = new Array<Block>();
    private Array<Block> stationaryBlocks = new Array<Block>();

    private float fallingBlockSpeed = 80;
    private float stationaryBlockSpeed = 3000;
    private float createBlockDelay = 0.85f;
    private float blockPosX = 130;
    private int numStationary = 0;
    private int numFalling = 0;

    public GameScreen (final ComboGame game) {
        this.game = game;
        camera.setToOrtho(false, ComboGame.screenWidth, ComboGame.screenHeight);
        createFallingBlock();
    }

    @Override
    public void render (float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tell the camera to update its matrices
        camera.update();

        // Tell the SpriteBatch to render in the co-ord sys specified by camera
        game.batch.setProjectionMatrix(camera.combined);

        // Begin drawing
        game.batch.begin();
        for (Block fallingBlock : fallingBlocks) {
            game.batch.draw(getBlockImage(fallingBlock.getColor()), fallingBlock.getX(), fallingBlock.getY());
        }
        for (Block stationaryBlock : stationaryBlocks) {
            game.batch.draw(getBlockImage(stationaryBlock.getColor()), stationaryBlock.getX(), stationaryBlock.getY());
        }
        game.batch.end();

        // Spawn blocks
        createBlockTick();

        // Move blocks
        moveFallingBlocks();
        moveStationaryBlocks();
    }

    private Texture getBlockImage (BlockColor color) {
        Texture blockImage;
        switch (color) {
            case CLEAR:
                blockImage = clearBlockImage;
                break;
            case RED:
                blockImage = redBlockImage;
                break;
            case GREEN:
                blockImage = greenBlockImage;
                break;
            case BLUE:
                blockImage = blueBlockImage;
                break;
            case YELLOW:
                blockImage = yellowBlockImage;
                break;
            default:
                blockImage = clearBlockImage;
                break;
        }
        return blockImage;
    }

    private void createBlockTick () {
        // Check if it is time to create a new block
        if (TimeUtils.nanoTime() - lastBlockCreateTime > createBlockDelay * 1000000000) {
            createFallingBlock();
        }
    }

    private void moveFallingBlocks () {
        // Moves falling blocks' y positions
        Iterator<Block> iter = fallingBlocks.iterator();
        while (iter.hasNext()) {
            Block fallingBlock = iter.next();
            if (fallingBlock.getY() - (fallingBlockSpeed * Gdx.graphics.getDeltaTime()) < numStationary * fallingBlock.getHeight()) {
                createStationaryBlock(fallingBlock);
                iter.remove();
            } else {
                fallingBlock.setY(fallingBlock.getY() - fallingBlockSpeed * Gdx.graphics.getDeltaTime());
            }
        }
    }

    private void moveStationaryBlocks () {
        // Moves stationary blocks' y positions
        for (Block stationaryBlock : stationaryBlocks) {
            if (stationaryBlock.getY() - (stationaryBlockSpeed * Gdx.graphics.getDeltaTime()) > stationaryBlock.getIndex() * stationaryBlock.getHeight()) {
                stationaryBlock.setY(stationaryBlock.getY() - stationaryBlockSpeed * Gdx.graphics.getDeltaTime());
            } else {
                stationaryBlock.setY(stationaryBlock.getIndex() * stationaryBlock.getHeight());
            }
        }
    }

    public void handleTouchDown (int x, int y, int pointer, int button) {
        // Transform points
        float pointerX = InputTransform.getCursorToModelX(x);
        float pointerY = InputTransform.getCursorToModelY(y);

        boolean touchedFallingBlock = false;

        // Fill falling block
        for (Block fallingBlock : fallingBlocks) {
            if (fallingBlock.contains(pointerX, pointerY) && fallingBlock.getColor() == BlockColor.CLEAR) {
                touchedFallingBlock = true;
                // TODO Temp placement
                Random rand = new Random();
                switch (rand.nextInt(4)) {
                    case 0:
                        fallingBlock.setColor(BlockColor.RED);
                        break;
                    case 1:
                        fallingBlock.setColor(BlockColor.GREEN);
                        break;
                    case 2:
                        fallingBlock.setColor(BlockColor.BLUE);
                        break;
                    case 3:
                        fallingBlock.setColor(BlockColor.YELLOW);
                        break;
                    default:
                        fallingBlock.setColor(BlockColor.RED);
                        break;
                }
                break;
            }
        }
        setNewStationaryBlocks();

        if (!touchedFallingBlock) {
            for (Block stationaryBlock : stationaryBlocks) {
                if (stationaryBlock.contains(pointerX, pointerY) && stationaryBlock.getColor() != BlockColor.CLEAR) {
                    clearBlockCombo(stationaryBlock.getIndex());
                }
            }
        }
    }

    private void clearBlockCombo (int index) {
        Block startBlock = stationaryBlocks.get(index);
        BlockColor comboColor = startBlock.getColor();
        int upperIndex;
        int lowerIndex;
        // Get upper index
        for (upperIndex = index; upperIndex < stationaryBlocks.size - 1; upperIndex++) {
            Block upperBlock = stationaryBlocks.get(upperIndex + 1);
            if (upperBlock.getColor() != comboColor) {
                break;
            }
        }
        // Get lower index
        for (lowerIndex = index; lowerIndex > 0; lowerIndex--) {
            Block lowerBlock = stationaryBlocks.get(lowerIndex - 1);
            if (lowerBlock.getColor() != comboColor) {
                break;
            }
        }
        int blocksInCombo = (upperIndex - lowerIndex) + 1;
        // Check if valid combo
        if (blocksInCombo > 1) {
            // Clear blocks
            for (int i = lowerIndex; i <= upperIndex; i++) {
                removeStationaryBlock(lowerIndex);
            }
            // Set new indices and drop blocks down to their new positions
            for (int i = lowerIndex; i < stationaryBlocks.size; i++) {
                Block stationaryBlock = stationaryBlocks.get(i);
                stationaryBlock.setIndex(stationaryBlock.getIndex() - blocksInCombo);
            }
        }
    }

    private void setNewStationaryBlocks () {
        // Drops blocks into stationary position
        int numBotFilled;
        // Count how many blocks from the bottom are filled
        for (numBotFilled = 0; numBotFilled < fallingBlocks.size; numBotFilled++) {
            Block lowerBlock = fallingBlocks.get(numBotFilled);
            if (lowerBlock.getColor() == BlockColor.CLEAR) {
                break;
            }
        }
        // Check if any blocks need dropping
        if (numBotFilled > 0) {
            // Drop blocks
            for (int i = 0; i < numBotFilled; i++) {
                Block fallingBlock = fallingBlocks.get(0);
                removeFallingBlock(0);
                createStationaryBlock(fallingBlock);
            }
            // Set new indices
            for (int i = 0; i < fallingBlocks.size; i++) {
                Block fallingBlock = fallingBlocks.get(i);
                fallingBlock.setIndex(fallingBlock.getIndex() - numBotFilled);
            }
        }
    }

    private void createStationaryBlock (Block block) {
        // Creates a stationary block at the bottom of the screen
        Block newBlock = new Block(blockPosX, block.getY(), blueBlockImage.getWidth(), blueBlockImage.getHeight(), numStationary, block.getColor());
        stationaryBlocks.add(newBlock);
        numStationary++;
    }

    private void removeStationaryBlock (int index) {
        // Removes a stationary block
        stationaryBlocks.removeIndex(index);
        numStationary--;
    }

    private void createFallingBlock () {
        // Spawns a block at the top of the screen
        if (fallingBlocks.size + stationaryBlocks.size < 12) {
            Block fallingBlock = new Block(blockPosX, ComboGame.screenHeight, clearBlockImage.getWidth(), clearBlockImage.getHeight(), numFalling);
            fallingBlocks.add(fallingBlock);
            lastBlockCreateTime = TimeUtils.nanoTime();
            numFalling++;
        }
    }

    private void removeFallingBlock (int index) {
        // Removes a falling block
        fallingBlocks.removeIndex(index);
        numFalling--;
    }

    @Override
    public void dispose () {
        // Dispose of all the native resources
        clearBlockImage.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GameInputProcessor(this));
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}