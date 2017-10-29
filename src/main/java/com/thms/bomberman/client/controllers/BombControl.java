//5810401040 Vudhidej Dejmul

package com.thms.bomberman.client.controllers;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.TypeComponent;
import com.thms.bomberman.client.BombermanClient;
import com.thms.bomberman.messages.BombermanType;
import javafx.geometry.Point2D;

public class BombControl extends Control {
    private PositionComponent position;
    private int radius;

    public BombControl(int radius) {
        this.radius = radius;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) { }

    public void explode() {
        BoundingBoxComponent bBox = Entities.getBBox(getEntity());

        int x = position.getGridX(BombermanClient.TILE_SIZE);
        int y = position.getGridY(BombermanClient.TILE_SIZE);

        FXGL.getApp()
                .getGameWorld()
                .spawn("Explosion", x*BombermanClient.TILE_SIZE, y*BombermanClient.TILE_SIZE);

        if (canExplode(new Point2D(50, 0))) {
            FXGL.getApp()
                    .getGameWorld()
                    .spawn("Explosion", (x*BombermanClient.TILE_SIZE)+50, y*BombermanClient.TILE_SIZE);
        }

        if (canExplode(new Point2D(-50, 0))) {
            FXGL.getApp()
                    .getGameWorld()
                    .spawn("Explosion", (x*BombermanClient.TILE_SIZE)-50, y*BombermanClient.TILE_SIZE);
        }

        if (canExplode(new Point2D(0, 50))) {
            FXGL.getApp()
                    .getGameWorld()
                    .spawn("Explosion", x*BombermanClient.TILE_SIZE, (y*BombermanClient.TILE_SIZE)+50);
        }

        if (canExplode(new Point2D(0, -50))) {
            FXGL.getApp()
                    .getGameWorld()
                    .spawn("Explosion", x*BombermanClient.TILE_SIZE, (y*BombermanClient.TILE_SIZE)-50);
        }

        FXGL.getMasterTimer().runOnceAfter(() -> {
            FXGL.getApp()
                    .getGameWorld().getEntitiesInRange(bBox.range(radius, radius))
                    .stream()
                    .filter(e -> Entities.getType(e).isType(BombermanType.EXPLOSION))
                    .forEach(e -> {
                        e.removeFromWorld();
                    });
        }, javafx.util.Duration.seconds(2));

        FXGL.getApp()
                .getGameWorld()
                .getEntitiesInRange(bBox.range(radius,-1))
                .stream()
                .filter(e -> Entities.getType(e).isType(BombermanType.BRICK)
                        || Entities.getType(e).isType(BombermanType.PLAYER1)
                        || Entities.getType(e).isType(BombermanType.PLAYER2)
                        || Entities.getType(e).isType(BombermanType.PLAYER3)
                        || Entities.getType(e).isType(BombermanType.PLAYER4))
                .forEach(e -> {
                    FXGL.<BombermanClient>getAppCast().onBrickDestroyed(e);
                    e.removeFromWorld();
                });

        FXGL.getApp()
                .getGameWorld()
                .getEntitiesInRange(bBox.range(-1,radius))
                .stream()
                .filter(e -> Entities.getType(e).isType(BombermanType.BRICK)
                        || Entities.getType(e).isType(BombermanType.PLAYER1)
                        || Entities.getType(e).isType(BombermanType.PLAYER2)
                        || Entities.getType(e).isType(BombermanType.PLAYER3)
                        || Entities.getType(e).isType(BombermanType.PLAYER4))
                .forEach(e -> {
                    FXGL.<BombermanClient>getAppCast().onBrickDestroyed(e);
                    e.removeFromWorld();
                });

        getEntity().removeFromWorld();
    }

    private boolean canExplode(Point2D direction) {
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
                        .filter(type -> type.isType(BombermanType.WALL)
                                || type.isType(BombermanType.BOMB))
                        .count() == 0;
    }
}
