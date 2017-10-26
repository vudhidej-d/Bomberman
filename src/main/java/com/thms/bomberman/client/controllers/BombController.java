package com.thms.bomberman.client.controllers;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.thms.bomberman.client.BombermanClient;
import com.thms.bomberman.client.BombermanType;

public class BombController extends Control {
    private int radius;

    public BombController(int radius) {
        this.radius = radius;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) { }

    public void explode() {
        BoundingBoxComponent bbox = Entities.getBBox(getEntity());

        FXGL.getApp()
                .getGameWorld()
                .getEntitiesInRange(bbox.range(radius,-1))
                .stream()
                .filter(e -> Entities.getType(e).isType(BombermanType.BRICK))
                .forEach(e -> {
                    FXGL.<BombermanClient>getAppCast().onWallDestroyed(e);
                    e.removeFromWorld();
                });

        FXGL.getApp()
                .getGameWorld()
                .getEntitiesInRange(bbox.range(-1,radius))
                .stream()
                .filter(e -> Entities.getType(e).isType(BombermanType.BRICK))
                .forEach(e -> {
                    FXGL.<BombermanClient>getAppCast().onWallDestroyed(e);
                    e.removeFromWorld();
                });

        getEntity().removeFromWorld();
    }
}
