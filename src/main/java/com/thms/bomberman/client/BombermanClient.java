package com.thms.bomberman.client;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import com.thms.bomberman.client.controllers.PlayerController;
import javafx.scene.input.KeyCode;

public class BombermanClient extends GameApplication {

    public static final int TILE_SIZE = 50;
    public static final int GAME_WIDTH = 750;
    public static final int GAME_HEIGHT = 650;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Bomberman");
        settings.setVersion("0.1");
        settings.setWidth(GAME_WIDTH);
        settings.setHeight(GAME_HEIGHT);
        settings.setIntroEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setMenuEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private GameEntity player;
    private PlayerController playerController;

    @Override
    protected void initGame() {
        initNetwork();
        TextLevelParser levelParser = new TextLevelParser(getGameWorld().getEntityFactory());

        Level level = levelParser.parse("levels/0.txt");

        getGameWorld().setLevel(level);
        getGameWorld().spawn("BG");

        player = (GameEntity) getGameWorld().spawn("Player");
        playerController = player.getControl(PlayerController.class);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                playerController.moveRight();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                playerController.moveLeft();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onActionBegin() {
                playerController.moveUp();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onActionBegin() {
                playerController.moveDown();
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Place Bomb") {
            @Override
            protected void onActionBegin() {
                playerController.placeBomb();
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BombermanType.PLAYER, BombermanType.POWERUP) {
            @Override
            protected void onCollision(Entity pl, Entity powerup) {
                powerup.removeFromWorld();
                playerController.increaseMaxBombs();
            }
        });
    }

    public void onWallDestroyed(Entity wall) {
        if (FXGLMath.randomBoolean()) {
            int x = Entities.getPosition(wall).getGridX(TILE_SIZE);
            int y = Entities.getPosition(wall).getGridY(TILE_SIZE);

            getGameWorld().spawn("PowerUp", (x*BombermanClient.TILE_SIZE)+5, (y*BombermanClient.TILE_SIZE)+5);
        }
    }

    protected void initNetwork() {
        Client client = new Client("localhost", 13000);
        client.connect();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
