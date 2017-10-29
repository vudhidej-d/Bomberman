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
import com.thms.bomberman.client.controllers.PlayerControl;
import com.thms.bomberman.messages.*;
import javafx.scene.input.KeyCode;

public class BombermanClient extends GameApplication {
    public static final int TILE_SIZE = 50;
    public static final int GAME_WIDTH = 750;
    public static final int GAME_HEIGHT = 650;
    public static String hostIP;
    private Client client;

    private int numOfPlayer = 0;
    private BombermanType clientOwner = null;
    private GameEntity player1, player2, player3, player4;
    private PlayerControl player1Control, player2Control, player3Control, player4Control;
    private boolean isPhysicAdded = false;

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
        settings.setApplicationMode(ApplicationMode.RELEASE);
    }

    @Override
    protected void initGame() {
        initNetwork();
        TextLevelParser levelParser = new TextLevelParser(getGameWorld().getEntityFactory());

        Level level = levelParser.parse("levels/0.txt");

        getGameWorld().setLevel(level);
        getGameWorld().spawn("BG");

        client.send(new ClientMessage(ClientMessagePhrase.PLAYER_SPAWN, clientOwner, "PlayerSpawnPacket"));
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move_Right") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessagePhrase.MOVE_RIGHT, clientOwner,
                        "MoveRightPacket"));
            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("Move_Left") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessagePhrase.MOVE_LEFT, clientOwner,
                        "MoveLeftPacket"));
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Move_Up") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessagePhrase.MOVE_UP, clientOwner,
                        "MoveUpPacket"));
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Move_Down") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessagePhrase.MOVE_DOWN, clientOwner,
                        "MoveDownPacket"));
            }
        }, KeyCode.DOWN);

        input.addAction(new UserAction("Place_Bomb") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessagePhrase.PLACE_BOMB, clientOwner,
                        "PlaceBombPacket"));
            }
        }, KeyCode.SPACE);
    }

    public void onBrickDestroyed(Entity brick) {
        int random = FXGLMath.random(1, 100);
        if (random <= 35) {
            int x = Entities.getPosition(brick).getGridX(TILE_SIZE);
            int y = Entities.getPosition(brick).getGridY(TILE_SIZE);
            client.send(new ClientMessage(ClientMessagePhrase.POWERUP_SPAWN, clientOwner, "PowerUpSpawnPacket/"+x+"/"+y));
        }
    }

    protected void initNetwork() {
        client = new Client(hostIP, 21488);
        client.connect();
    }

    @Override
    protected void onUpdate(double tpf) {

        if (!client.updateQueue.isEmpty()) {
            System.out.println("\nClient has an update...");
            ServerMessage updateMessage = client.updateQueue.poll();
            System.out.println(updateMessage);
            switch (updateMessage.getHeader()) {
                case CONNECTED:
                    try {
                        numOfPlayer = Integer.parseInt(updateMessage.getData().split("/")[2]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        client.disconnect();
                    }
                    if (clientOwner == null) {
                        switch (numOfPlayer) {
                            case 1:
                                clientOwner = BombermanType.PLAYER1;
                                break;
                            case 2:
                                clientOwner = BombermanType.PLAYER2;
                                break;
                            case 3:
                                clientOwner = BombermanType.PLAYER3;
                                break;
                            case 4:
                                clientOwner = BombermanType.PLAYER4;
                        }
                    }
                    if (!isPhysicAdded) {
                        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BombermanType.PLAYER1 , BombermanType.POWERUP) {
                            @Override
                            protected void onCollision(Entity p, Entity powerup) {
                                if(clientOwner.equals(BombermanType.PLAYER1)) {
                                    client.send(new ClientMessage(ClientMessagePhrase.POWERUP, clientOwner, "PowerUpPacket"));
                                }
                                powerup.removeFromWorld();
                            }
                        });
                        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BombermanType.PLAYER2, BombermanType.POWERUP) {
                            @Override
                            protected void onCollision(Entity p2, Entity powerup) {
                                if(clientOwner.equals(BombermanType.PLAYER2)) {
                                    client.send(new ClientMessage(ClientMessagePhrase.POWERUP, clientOwner, "PowerUpPacket"));
                                }
                                powerup.removeFromWorld();
                            }
                        });
                        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BombermanType.PLAYER3, BombermanType.POWERUP) {
                            @Override
                            protected void onCollision(Entity p3, Entity powerup) {
                                if(clientOwner.equals(BombermanType.PLAYER3)) {
                                    client.send(new ClientMessage(ClientMessagePhrase.POWERUP, clientOwner, "PowerUpPacket"));
                                }
                                powerup.removeFromWorld();
                            }
                        });
                        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BombermanType.PLAYER4, BombermanType.POWERUP) {
                            @Override
                            protected void onCollision(Entity p4, Entity powerup) {
                                if(clientOwner.equals(BombermanType.PLAYER4)) {
                                    client.send(new ClientMessage(ClientMessagePhrase.POWERUP, clientOwner, "PowerUpPacket"));
                                }
                                powerup.removeFromWorld();
                            }
                        });
                        System.out.println("Physic is set for "+clientOwner);
                        isPhysicAdded = true;
                    }
                    System.out.println(updateMessage.getData().split("/")[1]);
                    break;

                case DISCONNECTED:
                    client.disconnect();
                    System.out.println(updateMessage.getData().split("/")[1]);
                    System.exit(0);
                    break;

                case PLAYER_SPAWNED:
                    switch (clientOwner) {
                        case PLAYER1:
                            switch (numOfPlayer) {
                                case 1:
                                    player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
                                    player1Control = player1.getControl(PlayerControl.class);
                                    break;
                                case 2:
                                    player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
                                    player2Control = player2.getControl(PlayerControl.class);
                                    break;
                                case 3:
                                    player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
                                    player3Control = player3.getControl(PlayerControl.class);
                                    break;
                                case 4:
                                    player4 = (GameEntity) getGameWorld().spawn("Player4", 650, 550);
                                    player4Control = player4.getControl(PlayerControl.class);
                                    break;
                            }
                            break;
                        case PLAYER2:
                            switch (numOfPlayer) {
                                case 2:
                                    player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
                                    player1Control = player1.getControl(PlayerControl.class);
                                    player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
                                    player2Control = player2.getControl(PlayerControl.class);
                                    break;
                                case 3:
                                    player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
                                    player3Control = player3.getControl(PlayerControl.class);
                                    break;
                                case 4:
                                    player4 = (GameEntity) getGameWorld().spawn("Player4", 650, 550);
                                    player4Control = player4.getControl(PlayerControl.class);
                                    break;
                            }
                            break;
                        case PLAYER3:
                            switch (numOfPlayer) {
                                case 3:
                                    player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
                                    player1Control = player1.getControl(PlayerControl.class);
                                    player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
                                    player2Control = player2.getControl(PlayerControl.class);
                                    player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
                                    player3Control = player3.getControl(PlayerControl.class);
                                    break;
                                case 4:
                                    player4 = (GameEntity) getGameWorld().spawn("Player4", 650, 550);
                                    player4Control = player4.getControl(PlayerControl.class);
                                    break;
                            }
                            break;
                        case PLAYER4:
                            player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
                            player1Control = player1.getControl(PlayerControl.class);
                            player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
                            player2Control = player2.getControl(PlayerControl.class);
                            player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
                            player3Control = player3.getControl(PlayerControl.class);
                            player4 = (GameEntity) getGameWorld().spawn("Player4", 650, 550);
                            player4Control = player4.getControl(PlayerControl.class);
                            break;
                    }
                    break;

                case MOVED_RIGHT:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is moved right...");
                            player1Control.moveRight();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is moved right...");
                            player2Control.moveRight();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is moved right...");
                            player3Control.moveRight();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is moved right...");
                            player4Control.moveRight();
                            break;
                    }
                    break;

                case MOVED_LEFT:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is moved left...");
                            player1Control.moveLeft();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is moved left...");
                            player2Control.moveLeft();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is moved left...");
                            player3Control.moveLeft();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is moved left...");
                            player4Control.moveLeft();
                            break;
                    }
                    break;

                case MOVED_UP:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is moved up...");
                            player1Control.moveUp();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is moved up...");
                            player2Control.moveUp();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is moved up...");
                            player3Control.moveUp();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is moved up...");
                            player4Control.moveUp();
                            break;
                    }
                    break;

                case MOVED_DOWN:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is moved down...");
                            player1Control.moveDown();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is moved down...");
                            player2Control.moveDown();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is moved down...");
                            player3Control.moveDown();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is moved down...");
                            player4Control.moveDown();
                            break;
                    }
                    break;

                case PLACED_BOMB:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is placed bomb...");
                            player1Control.placeBomb();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is placed bomb...");
                            player2Control.placeBomb();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is placed bomb...");
                            player3Control.placeBomb();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is placed bomb...");
                            player4Control.placeBomb();
                            break;
                    }
                    break;

                case POWER_UP_SPAWNED:
                    String[] data = updateMessage.getData().split("/");
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    getGameWorld().spawn("PowerUp", (x*BombermanClient.TILE_SIZE)+5, (y*BombermanClient.TILE_SIZE)+5);
                    break;

                case POWERED_UP:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            player1Control.increaseMaxBombs();
                            break;
                        case PLAYER2:
                            player2Control.increaseMaxBombs();
                            break;
                        case PLAYER3:
                            player3Control.increaseMaxBombs();
                            break;
                        case PLAYER4:
                            player4Control.increaseMaxBombs();
                            break;
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
        hostIP = args[3];
    }
}
