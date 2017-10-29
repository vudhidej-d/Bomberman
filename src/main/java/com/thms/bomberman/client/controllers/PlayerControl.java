//5810401040 Vudhidej Dejmul

package com.thms.bomberman.client.controllers;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.TypeComponent;
import com.thms.bomberman.client.BombermanClient;
import com.thms.bomberman.messages.BombermanType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class PlayerControl extends Control {
    private PositionComponent position;
    private int maxBombs = 1;
    private int bombsPlaced = 0;

    @Override
    public void onUpdate(Entity entity, double v) {}

    public void increaseMaxBombs() {
        if (maxBombs < 5) maxBombs++;
    }

    public void placeBomb() {
        if (bombsPlaced == maxBombs) {
            return;
        }

        bombsPlaced++;

        int x = position.getGridX(BombermanClient.TILE_SIZE);
        int y = position.getGridY(BombermanClient.TILE_SIZE);

        Entity bomb = FXGL.getApp().getGameWorld()
                .spawn("Bomb", new SpawnData(x * BombermanClient.TILE_SIZE, y * BombermanClient.TILE_SIZE)
                        .put("radius", BombermanClient.TILE_SIZE / 2));

        FXGL.getMasterTimer().runOnceAfter(() -> {
            bomb.getControl(BombControl.class).explode();
            bombsPlaced--;
        }, Duration.seconds(2));
    }

    public void moveRight() {
        if (canMove(new Point2D(BombermanClient.TILE_SIZE, 0)))
            position.translateX(BombermanClient.TILE_SIZE);
    }

    public void moveLeft() {
        if (canMove(new Point2D(-BombermanClient.TILE_SIZE, 0)))
            position.translateX(-BombermanClient.TILE_SIZE);
    }

    public void moveUp() {
        if (canMove(new Point2D(0, -BombermanClient.TILE_SIZE)))
            position.translateY(-BombermanClient.TILE_SIZE);
    }

    public void moveDown() {
        if (canMove(new Point2D(0, BombermanClient.TILE_SIZE)))
            position.translateY(BombermanClient.TILE_SIZE);
    }

    private boolean canMove(Point2D direction) {
        Point2D newPosition = position.getValue().add(direction);
        return FXGL.getApp()
                .getGameScene()
                .getViewport()
                .getVisibleArea()
                .contains(newPosition)

                &&

                FXGL.getApp()
                        .getGameWorld()
                        .getEntitiesAt(newPosition)
                        .stream()
                        .filter(e -> e.hasComponent(TypeComponent.class))
                        .map(e -> e.getComponent(TypeComponent.class))
                        .filter(type -> type.isType(BombermanType.BRICK)
                                || type.isType(BombermanType.WALL)
                                || type.isType(BombermanType.BOMB)
                                || type.isType(BombermanType.PLAYER1)
                                || type.isType(BombermanType.PLAYER2)
                                || type.isType(BombermanType.PLAYER3)
                                || type.isType(BombermanType.PLAYER4))
                        .count() == 0;
    }
}
