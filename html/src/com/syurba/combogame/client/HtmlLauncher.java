package com.syurba.combogame.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.syurba.combogame.ComboGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(ComboGame.screenWidth, ComboGame.screenHeight);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new ComboGame();
        }
}