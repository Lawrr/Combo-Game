package com.syurba.combogame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final ComboGame game;

    private OrthographicCamera camera = new OrthographicCamera();
    private long lastBlockCreateTime;

    private Texture clearBlockImage = new Texture("clear-block.png");
    private Texture redBlockImage = new Texture("red-block.png");
    private Texture greenBlockImage = new Texture("green-block.png");
    private Texture blueBlockImage = new Texture("blue-block.png");
    private Texture yellowBlockImage = new Texture("yellow-block.png");
    private Texture redPreviewImage = new Texture("red-preview.png");
    private Texture greenPreviewImage = new Texture("green-preview.png");
    private Texture bluePreviewImage = new Texture("blue-preview.png");
    private Texture yellowPreviewImage = new Texture("yellow-preview.png");

    private Array<Block> fallingBlocks = new Array<Block>();
    private Array<Block> stationaryBlocks = new Array<Block>();
    private Array<BlockColor> incomingColors = new Array<BlockColor>();

    private float fallingBlockSpeed = 70;
    private float stationaryBlockSpeed = 3000;
    private float createBlockDelay = 1.2f * 1000000000;
    private int totalBlocks = 12;
    private int numStationary = 0;
    private int numFalling = 0;
    private int gameOverDelay = 6;
    private float gameOverDelayCount = 0;

    public GameScreen (final ComboGame game) {
        this.game = game;
        Gdx.input.setCatchBackKey(true);

        camera.setToOrtho(false, ComboGame.screenWidth, ComboGame.screenHeight);
        createFallingBlock();
        for (int i = 0; i < 5; i++) {
            addIncomingColor();
        }
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
            Texture blockImage = getBlockImage(fallingBlock.getColor());
            game.batch.draw(blockImage, fallingBlock.getX(), fallingBlock.getY());
        }
        for (Block stationaryBlock : stationaryBlocks) {
            Texture blockImage = getBlockImage(stationaryBlock.getColor());
            game.batch.draw(blockImage, stationaryBlock.getX(), stationaryBlock.getY());
        }
        for (int i = 0; i < incomingColors.size; i++) {
            Texture previewImage = getPreviewImage(incomingColors.get(i));
            if (i == 0) {
                game.batch.draw(previewImage, 30, ComboGame.screenHeight - 45 - (50 * (incomingColors.size - i)), 65, 65);
            } else {
                game.batch.draw(previewImage, 40, ComboGame.screenHeight - 20 - (50 * (incomingColors.size - i)));
            }
        }
        if (gameOverDelayCount > 0) {
            game.font.setScale(4);
            game.font.draw(game.batch, String.valueOf((int)(gameOverDelay - gameOverDelayCount)), ComboGame.screenWidth / 2, ComboGame.screenHeight / 2);
            game.font.setScale(1);
        }
        game.batch.end();

        // Spawn blocks
        createBlockTick();

        // Move blocks
        moveFallingBlocks();
        moveStationaryBlocks();
    }

    public void handleTouchDown (int x, int y, int pointer, int button) {
        // Transform points
        float pointerX = InputTransform.getCursorToModelX(x);
        float pointerY = InputTransform.getCursorToModelY(y);

        boolean touchedFallingBlock = false;

        // Fill falling block
        for (Block fallingBlock : fallingBlocks) {
            if (fallingBlock.contains(pointerX, pointerY) && fallingBlock.getColor() == BlockColor.CLEAR) {
                fallingBlock.setColor(incomingColors.get(0));
                incomingColors.removeIndex(0);
                addIncomingColor();
                touchedFallingBlock = true;
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

    private void createBlockTick () {
        // TODO Refactor this function
        // Check if it is time to create a new block
        if (TimeUtils.timeSinceNanos(lastBlockCreateTime) > createBlockDelay) {
            if (fallingBlocks.size + stationaryBlocks.size < totalBlocks) {
                createFallingBlock();
                gameOverDelayCount = 0;
            } else if (stationaryBlocks.size == totalBlocks) {
                gameOverDelayCount += Gdx.graphics.getDeltaTime();
                if (gameOverDelayCount > 5) {
                    gotoMainMenu();
                }
            }
        }
    }

    private void moveFallingBlocks () {
        // Moves falling blocks' y positions
        Iterator<Block> iter = fallingBlocks.iterator();
        while (iter.hasNext()) {
            Block fallingBlock = iter.next();
            float dropAmount = fallingBlockSpeed * Gdx.graphics.getDeltaTime();
            float stationaryY = numStationary * fallingBlock.getHeight();
            if (fallingBlock.getY() - dropAmount < stationaryY) {
                createStationaryBlock(fallingBlock);
                iter.remove();
            } else {
                fallingBlock.setY(fallingBlock.getY() - dropAmount);
            }
        }
    }

    private void moveStationaryBlocks () {
        // Moves stationary blocks' y positions
        for (Block stationaryBlock : stationaryBlocks) {
            float dropAmount = stationaryBlockSpeed * Gdx.graphics.getDeltaTime();
            float stationaryY = stationaryBlock.getIndex() * stationaryBlock.getHeight();
            if (stationaryBlock.getY() - dropAmount > stationaryY) {
                stationaryBlock.setY(stationaryBlock.getY() - dropAmount);
            } else if (stationaryBlock.getY() != stationaryY) {
                stationaryBlock.setY(stationaryY);
            }
        }
    }

    private void addIncomingColor () {
        int randNum = MathUtils.random(0, 3);
        switch (randNum) {
            case 0:
                incomingColors.add(BlockColor.RED);
                break;
            case 1:
                incomingColors.add(BlockColor.GREEN);
                break;
            case 2:
                incomingColors.add(BlockColor.BLUE);
                break;
            case 3:
                incomingColors.add(BlockColor.YELLOW);
                break;
            default:
                throw new RuntimeException("Unexpected random number generated: " + String.valueOf(randNum));
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
        boolean clearBlockBelow = false;
        for (lowerIndex = index; lowerIndex > 0; lowerIndex--) {
            Block lowerBlock = stationaryBlocks.get(lowerIndex - 1);
            if (lowerBlock.getColor() != comboColor) {
                if (lowerBlock.getColor() == BlockColor.CLEAR && stationaryBlocks.get(lowerIndex).getColor() == comboColor) {
                    clearBlockBelow = true;
                }
                break;
            }
        }
        int blocksInCombo = (upperIndex - lowerIndex) + 1;
        // Check if valid combo
        if (blocksInCombo > 1) {
            // If there is a clear block below, count that to be removed as well
            if (clearBlockBelow) {
                lowerIndex--;
                blocksInCombo++;
            }
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
        Block newBlock = new Block(Block.stationaryX, block.getY(), block.getWidth(), block.getHeight(), numStationary, block.getColor());
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
        Block fallingBlock = new Block(Block.stationaryX, ComboGame.screenHeight, clearBlockImage.getWidth(), clearBlockImage.getHeight(), numFalling);
        fallingBlocks.add(fallingBlock);
        lastBlockCreateTime = TimeUtils.nanoTime();
        numFalling++;
    }

    private void removeFallingBlock (int index) {
        // Removes a falling block
        fallingBlocks.removeIndex(index);
        numFalling--;
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
                throw new RuntimeException("Unknown block color: " + color.toString());
        }
        return blockImage;
    }

    private Texture getPreviewImage (BlockColor color) {
        Texture blockImage;
        switch (color) {
            case RED:
                blockImage = redPreviewImage;
                break;
            case GREEN:
                blockImage = greenPreviewImage;
                break;
            case BLUE:
                blockImage = bluePreviewImage;
                break;
            case YELLOW:
                blockImage = yellowPreviewImage;
                break;
            default:
                throw new RuntimeException("Unknown block color: " + color.toString());
        }
        return blockImage;
    }

    public void gotoMainMenu () {
        game.setScreen(new MainMenuScreen(game));
    }

    @Override
    public void dispose () {
        // Dispose of all the native resources
        clearBlockImage.dispose();
        redBlockImage.dispose();
        greenBlockImage.dispose();
        blueBlockImage.dispose();
        yellowBlockImage.dispose();
        redBlockImage.dispose();
        greenBlockImage.dispose();
        blueBlockImage.dispose();
        yellowBlockImage.dispose();
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