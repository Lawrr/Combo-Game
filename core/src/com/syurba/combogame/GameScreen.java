package com.syurba.combogame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final ComboGame game;

    private OrthographicCamera camera = new OrthographicCamera();
    private long lastBlockCreateTime;

    private Texture fallingBlockImage = new Texture("falling-block.png");
    private Array<Block> fallingBlocks = new Array<Block>();
    private Texture stationaryBlockImage = new Texture("blue-block.png");
    private Array<Block> stationaryBlocks = new Array<Block>();

    private float fallSpeed = 50;
    private float createTime = 1.5f;
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
            if (fallingBlock.getColor() == BlockColor.CLEAR) {
                game.batch.draw(fallingBlockImage, fallingBlock.getX(), fallingBlock.getY());
            } else {
                game.batch.draw(stationaryBlockImage, fallingBlock.getX(), fallingBlock.getY());
            }
        }
        for (Block stationaryBlock : stationaryBlocks) {
            if (stationaryBlock.getColor() == BlockColor.CLEAR) {
                game.batch.draw(fallingBlockImage, stationaryBlock.getX(), stationaryBlock.getY());
            } else {
                game.batch.draw(stationaryBlockImage, stationaryBlock.getX(), stationaryBlock.getY());
            }
        }
        game.batch.end();

        // Spawn blocks
        createBlockTick();

        // Move blocks
        moveBlockTick();
    }

    private void createBlockTick () {
        // Check if it is time to create a new block
        if (TimeUtils.nanoTime() - lastBlockCreateTime > createTime * 1000000000) {
            createFallingBlock();
        }
    }

    private void moveBlockTick () {
        // Moves falling blocks' y positions
        Iterator<Block> iter = fallingBlocks.iterator();
        while (iter.hasNext()) {
            Block fallingBlock = iter.next();
            fallingBlock.setY(fallingBlock.getY() - fallSpeed * Gdx.graphics.getDeltaTime());
            if (fallingBlock.getY() < numStationary * fallingBlock.getHeight()) {
                createStationaryBlock(fallingBlock.getColor());
                iter.remove();
            }
        }
    }

    public void handleTouchDown (int x, int y, int pointer, int button) {
        // Transform points
        float pointerX = InputTransform.getCursorToModelX(x);
        float pointerY = InputTransform.getCursorToModelY(y);

        // Fill falling block
        for (Block fallingBlock : fallingBlocks) {
            if (fallingBlock.contains(pointerX, pointerY) && fallingBlock.getColor() == BlockColor.CLEAR) {
                fallingBlock.setColor(BlockColor.BLUE);
                break;
            }
        }
        dropBlocks();
    }

    private void dropBlocks () {
        // Drops blocks into stationary position
        int numBotFilled;
        // Count how many blocks from the bottom are filled
        for (numBotFilled = 0; numBotFilled < fallingBlocks.size; numBotFilled++) {
            Block lowerBlock = fallingBlocks.get(numBotFilled);
            if (lowerBlock.getColor() == BlockColor.CLEAR) {
                break;
            }
        }
        // Drop and set new falling block indices
        if (numBotFilled > 0) {
            int numRemove = numBotFilled;
            for (int i = 0; i < fallingBlocks.size; i++) {
                Block fallingBlock = fallingBlocks.get(i);
                if (numRemove > 0) {
                    removeFallingBlock(i);
                    createStationaryBlock(fallingBlock.getColor());
                    numRemove--;
                    i--;
                } else {
                    fallingBlock.setIndex(fallingBlock.getIndex() - numBotFilled);
                }
            }
        }
    }

    private void createStationaryBlock (BlockColor color) {
        // Creates a stationary block at the bottom of the screen
        float stationaryBlockY = numStationary * stationaryBlockImage.getHeight();
        Block newBlock = new Block(blockPosX, stationaryBlockY, stationaryBlockImage.getWidth(), stationaryBlockImage.getHeight(), numStationary, color);
        stationaryBlocks.add(newBlock);
        numStationary++;
    }

    private void createFallingBlock () {
        // Spawns a block at the top of the screen
        if (fallingBlocks.size + stationaryBlocks.size < 12) {
            Block fallingBlock = new Block(blockPosX, ComboGame.screenHeight, fallingBlockImage.getWidth(), fallingBlockImage.getHeight(), numFalling);
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
        fallingBlockImage.dispose();
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