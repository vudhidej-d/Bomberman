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
import com.thms.bomberman.server.ServerMessage;
import javafx.scene.input.KeyCode;

public class BombermanClient extends GameApplication {
    public static final int TILE_SIZE = 50;
    public static final int GAME_WIDTH = 750;
    public static final int GAME_HEIGHT = 650;
    private Client client;

    private int numOfPlayer; //หาทางรับ Input จาก Menu
    private BombermanType clientOwner = BombermanType.PLAYER1; //หาทางรับ Input จาก Menu
    private GameEntity player1, player2, player3, player4;
    public PlayerControl player1Control, player2Control, player3Control, player4Control;

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

    @Override
    protected void initUI() {

    }

    @Override
    protected void initGame() {
        initNetwork();
        TextLevelParser levelParser = new TextLevelParser(getGameWorld().getEntityFactory());

        Level level = levelParser.parse("levels/0.txt");

        getGameWorld().setLevel(level);
        getGameWorld().spawn("BG");

//        client.send(new ClientMessage(ClientMessageType.PLAYER_SPAWN, clientOwner, "PlayerSpawnPacket/"+numOfPlayer));
        player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
        player1Control = player1.getControl(PlayerControl.class);

        player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
        player2Control = player2.getControl(PlayerControl.class);

        player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
        player3Control = player3.getControl(PlayerControl.class);

        player4 = (GameEntity) getGameWorld().spawn("Player4", 650, 550);
        player4Control = player4.getControl(PlayerControl.class);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move_Right") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessageType.MOVE_RIGHT, clientOwner,
                        "MoveRightPacket"));
            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("Move_Left") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessageType.MOVE_LEFT, clientOwner,
                        "MoveLeftPacket"));
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Move_Up") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessageType.MOVE_UP, clientOwner,
                        "MoveUpPacket"));
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Move_Down") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessageType.MOVE_DOWN, clientOwner,
                        "MoveDownPacket"));
            }
        }, KeyCode.DOWN);

        input.addAction(new UserAction("Place_Bomb") {
            @Override
            protected void onActionBegin() {
                client.send(new ClientMessage(ClientMessageType.PLACE_BOMB, clientOwner,
                        "PlaceBombPacket"));
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(clientOwner, BombermanType.POWERUP) {
            @Override
            protected void onCollision(Entity pl, Entity powerup) {
                client.send(new ClientMessage(ClientMessageType.POWERUP, clientOwner, "PowerUpPacket"));
                powerup.removeFromWorld();
            }
        });
    }

    public void onBrickDestroyed(Entity brick) {
        int random = FXGLMath.random(1, 100);
        if (random <= 30) {
            int x = Entities.getPosition(brick).getGridX(TILE_SIZE);
            int y = Entities.getPosition(brick).getGridY(TILE_SIZE);
            client.send(new ClientMessage(ClientMessageType.POWERUP_SPAWN, clientOwner, "PowerUpSpawnPacket/"+x+"/"+y));
        }
    }

    protected void initNetwork() {
        client = new Client("localhost", 13000);
        client.connect();
    }

    @Override
    protected void onUpdate(double tpf) {
        if (!client.updateQueue.isEmpty()) {
            System.out.println("Client has an update...");
            ServerMessage updateMessage = client.updateQueue.poll();

            switch (updateMessage.getHeader()) {
                case CONNECTING:
                    System.out.println("Server connected...");
                    break;

//                case PLAYER_SPAWN:
//                    String[] clients = updateMessage.getData().split("/");
//                    if(clients.length == 2) numOfPlayer = Integer.parseInt(clients[1]);
//                    switch (numOfPlayer) {
//                        case 1:
//                            player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
//                            player1Control = player1.getControl(PlayerControl.class);
//                            break;
//                        case 2:
//                            player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
//                            player1Control = player1.getControl(PlayerControl.class);
//                            player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
//                            player2Control = player2.getControl(PlayerControl.class);
//                            break;
//                        case 3:
//                            player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
//                            player1Control = player1.getControl(PlayerControl.class);
//                            player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
//                            player2Control = player2.getControl(PlayerControl.class);
//                            player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
//                            player3Control = player3.getControl(PlayerControl.class);
//                            break;
//                        case 4:
//                            player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
//                            player1Control = player1.getControl(PlayerControl.class);
//                            player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
//                            player2Control = player2.getControl(PlayerControl.class);
//                            player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
//                            player3Control = player3.getControl(PlayerControl.class);
//                            player4 = (GameEntity) getGameWorld().spawn("Player4", 650, 550);
//                            player4Control = player4.getControl(PlayerControl.class);
//                            break;
//                    }
//                    switch (updateMessage.getPacketOwner()) {
//                        case PLAYER1:
//                            player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
//                            player1Control = player1.getControl(PlayerControl.class);
//                            break;
//                        case PLAYER2:
//                            player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
//                            player2Control = player2.getControl(PlayerControl.class);
//                            break;
//                        case PLAYER3:
//                            player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
//                            player3Control = player3.getControl(PlayerControl.class);
//                            break;
//                        case PLAYER4:
//                            player4 = (GameEntity) getGameWorld().spawn("Player4", 650, 550);
//                            player4Control = player4.getControl(PlayerControl.class);
//                            break;
//                    }
//                    break;

                case MOVE_RIGHT:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is moving right...");
                            player1Control.moveRight();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is moving right...");
                            player2Control.moveRight();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is moving right...");
                            player3Control.moveRight();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is moving right...");
                            player4Control.moveRight();
                            break;
                    }
                    break;

                case MOVE_LEFT:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is moving left...");
                            player1Control.moveLeft();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is moving left...");
                            player2Control.moveLeft();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is moving left...");
                            player3Control.moveLeft();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is moving left...");
                            player4Control.moveLeft();
                            break;
                    }
                    break;

                case MOVE_UP:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is moving up...");
                            player1Control.moveUp();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is moving up...");
                            player2Control.moveUp();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is moving up...");
                            player3Control.moveUp();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is moving up...");
                            player4Control.moveUp();
                            break;
                    }
                    break;

                case MOVE_DOWN:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is moving down...");
                            player1Control.moveDown();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is moving down...");
                            player2Control.moveDown();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is moving down...");
                            player3Control.moveDown();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is moving down...");
                            player4Control.moveDown();
                            break;
                    }
                    break;

                case PLACE_BOMB:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            System.out.println("Player1 is placing bomb...");
                            player1Control.placeBomb();
                            break;
                        case PLAYER2:
                            System.out.println("Player2 is placing bomb...");
                            player2Control.placeBomb();
                            break;
                        case PLAYER3:
                            System.out.println("Player3 is placing bomb...");
                            player3Control.placeBomb();
                            break;
                        case PLAYER4:
                            System.out.println("Player4 is placing bomb...");
                            player4Control.placeBomb();
                            break;
                    }
                    break;

                case POWERUP_SPAWN:
                    String[] data = updateMessage.getData().split("/");
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    getGameWorld().spawn("PowerUp", (x*BombermanClient.TILE_SIZE)+5, (y*BombermanClient.TILE_SIZE)+5);
                    break;

                case POWERUP:
                    switch (updateMessage.getPacketOwner()) {
                        case PLAYER1:
                            setPowerUpPhysic(BombermanType.PLAYER1);
                            player1Control.increaseMaxBombs();
                            break;
                        case PLAYER2:
                            setPowerUpPhysic(BombermanType.PLAYER2);
                            player2Control.increaseMaxBombs();
                            break;
                        case PLAYER3:
                            setPowerUpPhysic(BombermanType.PLAYER3);
                            player3Control.increaseMaxBombs();
                            break;
                        case PLAYER4:
                            setPowerUpPhysic(BombermanType.PLAYER4);
                            player4Control.increaseMaxBombs();
                            break;
                    }
                    break;
            }
        }
    }

    public void setPowerUpPhysic(BombermanType playerPowerup) {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(playerPowerup, BombermanType.POWERUP) {
            @Override
            protected void onCollision(Entity pl, Entity powerup) {
                powerup.removeFromWorld();
            }
        });
    }

//    public void spawnPlayer(int numOfPlayer) {
//        switch (numOfPlayer) {
//            case 1:
//                player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
//                player1Control = player1.getControl(PlayerControl.class);
//                break;
//            case 2:
//                player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
//                player1Control = player1.getControl(PlayerControl.class);
//                player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
//                player2Control = player2.getControl(PlayerControl.class);
//                break;
//            case 3:
//                player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
//                player1Control = player1.getControl(PlayerControl.class);
//                player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
//                player2Control = player2.getControl(PlayerControl.class);
//                player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
//                player3Control = player3.getControl(PlayerControl.class);
//                break;
//            case 4:
//                player1 = (GameEntity) getGameWorld().spawn("Player1", 50, 50);
//                player1Control = player1.getControl(PlayerControl.class);
//                player2 = (GameEntity) getGameWorld().spawn("Player2", 650, 50);
//                player2Control = player2.getControl(PlayerControl.class);
//                player3 = (GameEntity) getGameWorld().spawn("Player3", 50, 550);
//                player3Control = player3.getControl(PlayerControl.class);
//                player4 = (GameEntity) getGameWorld().spawn("Player4", 650, 550);
//                player4Control = player4.getControl(PlayerControl.class);
//                break;
//        }
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
