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

    private OrthographicCamera camera;
    private long lastBlockSpawnTime;

    private Texture fallingBlockImage;
    private Array<FallingBlock> fallingBlocks;
    private Texture placedBlockImage;
    private Array<PlacedBlock> placedBlocks;

    private float spawnDelay;
    private float fallSpeed;
    private int numPlaced;
    private int numFalling;

    public GameScreen (final ComboGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, ComboGame.screenWidth, ComboGame.screenHeight);
        fallingBlockImage = new Texture("falling-block.png");
        fallingBlocks = new Array<FallingBlock>();
        placedBlockImage = new Texture("blue-block.jpg");
        placedBlocks = new Array<PlacedBlock>();

        spawnDelay = 1.5f;
        fallSpeed = 50;
        numPlaced = 0;
        numFalling = 0;

        createFallingBlock();
    }

    @Override
    public void render (float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(1, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tell the camera to update its matrices
        camera.update();

        // Tell the SpriteBatch to render in the co-ord sys specified by camera
        game.batch.setProjectionMatrix(camera.combined);

        // Begin drawing
        game.batch.begin();
        for (PlacedBlock placedBlock : placedBlocks) {
            if (!placedBlock.isEmpty()) {
                game.batch.draw(placedBlockImage, placedBlock.x, placedBlock.y);
            } else {
                game.batch.draw(fallingBlockImage, placedBlock.x, placedBlock.y);
            }
        }
        for (FallingBlock fallingBlock : fallingBlocks) {
            if (!fallingBlock.isPlaced()) {
                game.batch.draw(fallingBlockImage, fallingBlock.x, fallingBlock.y);
            } else {
                game.batch.draw(placedBlockImage, fallingBlock.x, fallingBlock.y);
            }
        }
        game.batch.end();

        // Spawn blocks
        if (TimeUtils.nanoTime() - lastBlockSpawnTime > spawnDelay * 1000000000) {
            createFallingBlock();
        }

        // Move blocks
        Iterator<FallingBlock> iter = fallingBlocks.iterator();
        while (iter.hasNext()) {
            FallingBlock fallingBlock = iter.next();
            fallingBlock.setY(fallingBlock.getY() - fallSpeed * Gdx.graphics.getDeltaTime());
            if (fallingBlock.getY() < numPlaced * fallingBlock.getHeight()) {
                createPlacedBlock(!fallingBlock.isPlaced());
                iter.remove();
            }

        }
    }

    public void handleTouchDown (int x, int y, int pointer, int button) {
        float pointerX = InputTransform.getCursorToModelX(x);
        float pointerY = InputTransform.getCursorToModelY(y);

        Iterator<FallingBlock> iter = fallingBlocks.iterator();
        int indexDecrement = 0;
        while (iter.hasNext()) {
            FallingBlock fallingBlock = iter.next();
            fallingBlock.setIndex(fallingBlock.getIndex() - indexDecrement);
            if ((fallingBlock.contains(pointerX, pointerY) && !fallingBlock.isPlaced()) || (fallingBlock.getIndex() == 0 && fallingBlock.isPlaced())) {
                if (fallingBlock.getIndex() == 0) {
                    indexDecrement++;
                    createPlacedBlock(false);
                    iter.remove();
                } else {
                    fallingBlock.setPlaced(true);
                }
            }
        }
    }

    public void createPlacedBlock (boolean empty) {
        float placedBlockY = numPlaced * placedBlockImage.getHeight();
        PlacedBlock newBlock = new PlacedBlock(80, placedBlockY, placedBlockImage.getWidth(), placedBlockImage.getHeight(), empty);
        placedBlocks.add(newBlock);
        numPlaced++;
    }

    private void createFallingBlock () {
        // Spawns a block at the top of the screen
        if (fallingBlocks.size + placedBlocks.size < 12) {
            FallingBlock fallingBlock = new FallingBlock(80, ComboGame.screenHeight, fallingBlockImage.getWidth(), fallingBlockImage.getHeight(), numFalling);
            fallingBlocks.add(fallingBlock);
            lastBlockSpawnTime = TimeUtils.nanoTime();
            numFalling++;
        }
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