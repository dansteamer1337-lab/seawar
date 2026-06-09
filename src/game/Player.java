package game;

import model.Board;

public class Player {
    private final String name;
    private final Board myBoard;
    private Board enemyBoard;
    private int shotsFired;
    private int hits;

    public Player(String name) {
        this.name = name;
        this.myBoard = new Board();
        this.enemyBoard = new Board();
        this.shotsFired = 0;
        this.hits = 0;
    }

    public String getName() {
        return name;
    }

    public Board getMyBoard() {
        return myBoard;
    }

    public Board getEnemyBoard(){
        return enemyBoard;
    }

    public void setEnemyBoard(Board board) {
        this.enemyBoard = board;
    }

    public void incrementShots(){
        shotsFired++;
    }

    public void incrementHits(){
        hits++;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public int getHits() {
        return hits;
    }

    public double getAccuracy() {
        if (shotsFired == 0) { return 0; }
        return (double) hits / (double) shotsFired * 100;
    }

    public int getRemainingShots() {
        return shotsFired;
    }

    public int getRemainingHits() {
        return (int) myBoard.getShips().stream().filter(ship -> !ship.isAlive()).count();
    }
}