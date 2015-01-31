package com.syurba.combogame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final ComboGame game;

    private Vector3 touchPos;
    private OrthographicCamera camera;
    private long lastBlockSpawnTime;

    private Texture fallingBlockImage;
    private Texture placedBlockImage;

    private int spawnSpeed = 1;
    private int fallSpeed = 50;

    public Array<FallingBlock> fallingBlocks;
    public Array<PlacedBlock> placedBlocks;

    public GameScreen (final ComboGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.screenWidth, game.screenHeight);
        fallingBlockImage = new Texture("falling-block.png");
        fallingBlocks = new Array<FallingBlock>();
        placedBlockImage = new Texture("falling-block.png");
        placedBlocks = new Array<PlacedBlock>();

        spawnFallingBlock();
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
            game.batch.draw(placedBlockImage, placedBlock.x, placedBlock.y);
        }
        for (FallingBlock fallingBlock : fallingBlocks) {
            game.batch.draw(fallingBlockImage, fallingBlock.x, fallingBlock.y);
        }
        game.batch.end();

        // Spawn blocks
        if (TimeUtils.nanoTime() - lastBlockSpawnTime > spawnSpeed * 1000000000) {
            spawnFallingBlock();
        }

        // Move blocks
        Iterator<FallingBlock> iter = fallingBlocks.iterator();
        while (iter.hasNext()) {
            FallingBlock fallingBlock = iter.next();
            fallingBlock.setY(fallingBlock.getY() - fallSpeed * Gdx.graphics.getDeltaTime());
            if (fallingBlock.getY() + fallingBlock.getHeight() < 0) {
                iter.remove();
            }

        }
    }

    private void spawnFallingBlock () {
        // Spawns a block at the top of the screen
        FallingBlock fallingBlock = new FallingBlock(80, game.screenHeight, fallingBlockImage.getWidth(), fallingBlockImage.getHeight());
        fallingBlocks.add(fallingBlock);
        lastBlockSpawnTime = TimeUtils.nanoTime();
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