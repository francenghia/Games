package com.flyfly.runcoin.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class RunCoin extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    int manState = 0;
    int pause = 0;
    float gravity = 0.2f;
    float velocity = 0;
    int manY = 0;
    Rectangle manRectangle;
    Random random;

    ArrayList<Integer> coinXs = new ArrayList<Integer>();
    ArrayList<Integer> coinYs = new ArrayList<Integer>();
    ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
    Texture coin;
    int coinCount;

    ArrayList<Integer> boomXs = new ArrayList<Integer>();
    ArrayList<Integer> boomYs = new ArrayList<Integer>();
    ArrayList<Rectangle> boomRectangles = new ArrayList<Rectangle>();
    Texture boom;
    int boomCount;

    int score;
    BitmapFont font;

    int gameState;
    Texture dizzy;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        manY = Gdx.graphics.getHeight() / 2;
        coin = new Texture("coin.png");
        boom = new Texture("bomb.png");

        dizzy = new Texture("dizzy-1.png");
        random = new Random();
        manRectangle = new Rectangle();

        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(10);
    }

    public void makeCoin() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    public void makeBoom() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        boomYs.add((int) height);
        boomXs.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            //BOOMS
            if (boomCount < 250) {
                boomCount++;
            } else {
                boomCount = 0;
                makeBoom();
            }
            boomRectangles.clear();
            for (int i = 0; i < boomXs.size(); i++) {
                batch.draw(boom, boomXs.get(i), boomYs.get(i));
                boomXs.set(i, boomXs.get(i) - 8);
                boomRectangles.add(new Rectangle(boomXs.get(i), boomYs.get(i), boom.getWidth(), boom.getHeight()));
            }


            //COINS
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }
            coinRectangles.clear();
            for (int i = 0; i < coinXs.size(); i++) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 4);
                coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
            }
            if (Gdx.input.justTouched()) {
                velocity = -10;
            }
            if (pause < 8) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }
            velocity += gravity;

            manY -= velocity;

            if (manY <= 0) {
                manY = 0;
            }

        } else if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
                manY = Gdx.graphics.getHeight() / 2;
                score = 0;
                velocity = 0;
                coinXs.clear();
                coinYs.clear();
                coinRectangles.clear();
                coinCount = 0;

                boomXs.clear();
                boomYs.clear();
                boomRectangles.clear();
                boomCount = 0;

            }
        }

        if (gameState == 2) {
            batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
        } else {
            batch.draw(man[manState],Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY );
        }


        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY,
                man[manState].getWidth(), man[manState].getHeight());

        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
                score++;
                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }
        for (int i = 0; i < boomRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, boomRectangles.get(i))) {
                Gdx.app.log("Kiem tra", "Collision!");
                gameState = 2;
            }
        }
        font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
