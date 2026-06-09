package bot;

import model.Board;
import model.Coordinate;
import model.ShotResult;

import java.util.*;

public class SmartBot {
    private final String name = "Bot";
    private final Set<Coordinate> missedShots = new HashSet<>();
    private final Set<Coordinate> hitShots = new HashSet<>();
    private final Queue<Coordinate> targetQueue = new LinkedList<>();
    private final Board myBoard = new Board();
    private final Random random = new Random();

    private Coordinate lastShot = null;
    private ShotResult lastResult = null;

    public Coordinate makeMove(Board enemyBoard) {
        Coordinate shot;

        if (!targetQueue.isEmpty()) {
            shot = targetQueue.poll();
            System.out.println("Бот добивает: " + shot);
            return shot;
        }

        shot = findNewTarget(enemyBoard);
        System.out.println("Бот ищет корабль " + shot);
        return shot;

    }

    public void processResult(Coordinate shot, ShotResult result) {
        lastShot = shot;
        lastResult = result;

        if (result == ShotResult.MISS){
            missedShots.add(shot);
        }
        else if (result == ShotResult.HIT){
            hitShots.add(shot);
            addNeighborsToQueue(shot);
        }
        else if (result == ShotResult.SUNK){
            hitShots.add(shot);

            targetQueue.clear();

            markAroundSunkShip(shot);
        }
    }

    private Coordinate findNewTarget(Board enemyBoard) {
        List<Coordinate> candidates = new ArrayList<>();

        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                Coordinate coord = new Coordinate(row, col);

                if ((row + col) % 2 == 0) {
                    if (!missedShots.contains(coord) && !hitShots.contains(coord)) {
                        candidates.add(coord);
                    }
                }
            }
        }
        if (candidates.isEmpty()) {
            for (int row = 0; row < 16; row++) {
                for (int col = 0; col < 16; col++) {
                    Coordinate coord = new Coordinate(row, col);
                    if(!missedShots.contains(coord) && !hitShots.contains(coord)) {
                        candidates.add(coord);
                    }
                }
            }
        }

        if (candidates.isEmpty()){
            return new Coordinate(0, 0);
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    private void addNeighborsToQueue(Coordinate shot) {
        int row = shot.getRow();
        int col = shot.getCol();

        List<Coordinate> neighbors = Arrays.asList(
                new Coordinate(row - 1, col),
                new Coordinate(row + 1, col),
                new Coordinate( row, col - 1),
                new Coordinate( row, col + 1)
        );

        for (Coordinate neighbor : neighbors) {
            if (neighbor.getRow() >= 0 && neighbor.getRow() < 16 &&
                    neighbor.getCol() >= 0 && neighbor.getCol() < 16) {

                if (!missedShots.contains(neighbor) && !hitShots.contains(neighbor)) {
                    targetQueue.add(neighbor);
                }
            }
        }
    }

    private void markAroundSunkShip(Coordinate lastHit) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int r = lastHit.getRow() + dr;
                int c = lastHit.getCol() + dc;

                if (r >= 0 && r < 16 && c >= 0 && c < 16) {
                    Coordinate coord = new Coordinate(r, c);

                    if (!hitShots.contains(coord)) {
                        missedShots.add(coord);
                    }
                }
            }
        }
        targetQueue.clear();
    }

    public String getName() {
        return name;
    }

    public void reset() {
        missedShots.clear();
        hitShots.clear();
        targetQueue.clear();
        lastShot = null;
        lastResult = null;
    }

    public Board getBoard() {
        return myBoard;
    }

}