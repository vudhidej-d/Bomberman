package com.thms.bomberman.client;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.thms.bomberman.client.controllers.BombControl;
import com.thms.bomberman.client.controllers.PlayerControl;
import com.thms.bomberman.messages.BombermanType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

@SetEntityFactory
public class BombermanFactory implements TextEntityFactory {

    @Spawns("BG")
    public GameEntity initBg(SpawnData data) {
        return Entities.builder()
                .at(0, 0)
                .viewFromNode(new EntityView(new Rectangle(BombermanClient.GAME_WIDTH,
                        BombermanClient.GAME_WIDTH, Color.LIGHTGREEN), RenderLayer.BACKGROUND))
                .build();
    }

    @Spawns("Player1")
    public GameEntity createPlayer1(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.PLAYER1)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(blockWidth(), blockHeight(), Color.DARKBLUE))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    @Spawns("Player2")
    public GameEntity createPlayer2(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.PLAYER2)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(blockWidth(), blockHeight(), Color.DARKMAGENTA))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    @Spawns("Player3")
    public GameEntity createPlayer3(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.PLAYER3)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(blockWidth(), blockHeight(), Color.SADDLEBROWN))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    @Spawns("Player4")
    public GameEntity createPlayer4(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.PLAYER4)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(blockWidth(), blockHeight(), Color.DARKRED))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    @Spawns("Bomb")
    public GameEntity createBomb(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.BOMB)
                .from(data)
                .viewFromNodeWithBBox(new Circle(blockWidth()/2, Color.BLACK))
                .with(new BombControl(data.get("radius")))
                .build();
    }

    @Spawns("PowerUp")
    public GameEntity createPowerUp(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.POWERUP)
                .from(data)
                .viewFromNodeWithBBox(new Circle((blockWidth()/2)-5, Color.YELLOW))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("Explosion")
    public GameEntity createExplosion(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.EXPLOSION)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(blockWidth(), blockHeight(), Color.LIGHTYELLOW))
                .with(new CollidableComponent())
                .build();
    }

    @SpawnSymbol('w')
    public GameEntity createWall(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.WALL)
                .from(data)
                .viewFromNode(new Rectangle(blockWidth(), blockHeight(), Color.LIGHTGRAY))
                .build();
    }

    @SpawnSymbol('b')
    public GameEntity createBrick(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.BRICK)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(blockWidth(), blockHeight(), Color.ORANGE))
                .build();
    }

    @Override
    public char emptyChar() {
        return 0;
    }

    @Override
    public int blockWidth() {
        return BombermanClient.TILE_SIZE;
    }

    @Override
    public int blockHeight() {
        return BombermanClient.TILE_SIZE;
    }
}
