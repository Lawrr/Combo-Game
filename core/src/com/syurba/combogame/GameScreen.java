package com.syurba.combogame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/**
 * Created by Lawrence on 31/01/2015.
 */
public class GameScreen implements Screen {
    private final ComboGame game;

    private OrthographicCamera camera;
    private Texture fallingBlockImage;
    private Array<Rectangle> fallingBlocks;
    private long lastBlockSpawnTime;

    public GameScreen (final ComboGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.screenWidth, game.screenHeight);
        fallingBlockImage = new Texture("falling-block.png");
        fallingBlocks = new Array<Rectangle>();
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
        for (Rectangle fallingBlock : fallingBlocks) {
            game.batch.draw(fallingBlockImage, fallingBlock.x, fallingBlock.y);
        }
        game.batch.end();

        // Spawn blocks
        if (TimeUtils.nanoTime() - lastBlockSpawnTime > 0.8 * 1000000000) {
            spawnFallingBlock();
        }

        // Process user input
        Vector3 touchPos = null;
        if (Gdx.input.isTouched()) {
            touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }

        // Move blocks
        Iterator<Rectangle> iter = fallingBlocks.iterator();
        while (iter.hasNext()) {
            Rectangle fallingBlock = iter.next();
            fallingBlock.y -= 50 * Gdx.graphics.getDeltaTime();
            if (fallingBlock.y + fallingBlock.getHeight() < 0) {
                iter.remove();
            }
            if (touchPos != null && fallingBlock.contains(touchPos.x, touchPos.y)) {
                iter.remove();
            }
        }
    }

    private void spawnFallingBlock () {
        // Spawns a block at the top of the screen
        Rectangle fallingBlock = new Rectangle();
        fallingBlock.x = 80;
        fallingBlock.y = game.screenHeight;
        fallingBlock.width = fallingBlockImage.getWidth();
        fallingBlock.height = fallingBlockImage.getHeight();
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