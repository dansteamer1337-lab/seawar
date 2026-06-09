package bot;

import model.Board;
import model.Coordinate;
import model.Direction;
import model.ShotResult;
import model.CellStatus;

import java.util.*;

public class SmartBot {
    private final String name = "Bot";
    private final Set<Coordinate> missedShots = new HashSet<>();
    private final Set<Coordinate> hitShots = new HashSet<>();
    private final Queue<Coordinate> targetQueue = new LinkedList<>();
    private final Board myBoard = new Board();
    private final Random random = new Random();
    private Direction lastDirection = null;
    private Coordinate firstHit = null;
    private List<Coordinate> currentShipHits = new ArrayList<>();
    private boolean tryingOpposite = false;

    private Coordinate lastShot = null;
    private ShotResult lastResult = null;

    public Coordinate makeMove(Board enemyBoard) {
        Coordinate shot;

        if (!targetQueue.isEmpty()) {
            shot = targetQueue.poll();
            if (missedShots.contains(shot) || hitShots.contains(shot)) {
                return makeMove(enemyBoard);
            }
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

        if (result == ShotResult.MISS) {
            missedShots.add(shot);
            handleMiss();
        }
        else if (result == ShotResult.HIT) {
            hitShots.add(shot);
            currentShipHits.add(shot);
            tryingOpposite = false;

            if (firstHit == null) {
                firstHit = shot;
                addNeighborsToQueue(shot);
            } else {
                continueInSameDirection(shot);
            }
        }
        else if (result == ShotResult.SUNK) {
            hitShots.add(shot);
            currentShipHits.add(shot);
            targetQueue.clear();
            markAroundSunkShip();
            resetShipHunt();
        }
    }

    private void handleMiss() {
        if (firstHit != null && !tryingOpposite) {
            tryingOpposite = true;
            tryOppositeDirection();
        } else if (firstHit != null && tryingOpposite) {
            targetQueue.clear();
            resetShipHunt();
        }
    }

    private Coordinate findNewTarget(Board enemyBoard) {
        List<Coordinate> candidates = new ArrayList<>();

        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                Coordinate coord = new Coordinate(row, col);

                CellStatus status = enemyBoard.getCell(row, col);
                if (status == CellStatus.MISS || status == CellStatus.HIT || status == CellStatus.SUNK) {
                    missedShots.add(coord);
                }

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
                    if (!missedShots.contains(coord) && !hitShots.contains(coord)) {
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
                new Coordinate(row, col - 1),
                new Coordinate(row, col + 1)
        );

        Collections.shuffle(neighbors);

        for (Coordinate neighbor : neighbors) {
            if (neighbor.getRow() >= 0 && neighbor.getRow() < 16 &&
                    neighbor.getCol() >= 0 && neighbor.getCol() < 16) {

                if (!missedShots.contains(neighbor) && !hitShots.contains(neighbor)) {
                    targetQueue.add(neighbor);
                    if (neighbor.getRow() == row - 1) lastDirection = Direction.UP;
                    else if (neighbor.getRow() == row + 1) lastDirection = Direction.DOWN;
                    else if (neighbor.getCol() == col - 1) lastDirection = Direction.LEFT;
                    else if (neighbor.getCol() == col + 1) lastDirection = Direction.RIGHT;
                    break;
                }
            }
        }
    }

    private void continueInSameDirection(Coordinate shot) {
        Coordinate next = getNextInDirection(shot, lastDirection);
        if (next != null && !missedShots.contains(next) && !hitShots.contains(next)) {
            targetQueue.clear();
            targetQueue.add(next);
        } else {
            tryOppositeDirection();
        }
    }

    private void tryOppositeDirection() {
        Direction opposite = null;
        if (lastDirection == Direction.UP) opposite = Direction.DOWN;
        else if (lastDirection == Direction.DOWN) opposite = Direction.UP;
        else if (lastDirection == Direction.LEFT) opposite = Direction.RIGHT;
        else if (lastDirection == Direction.RIGHT) opposite = Direction.LEFT;

        Coordinate next = getNextInDirection(firstHit, opposite);
        if (next != null && !missedShots.contains(next) && !hitShots.contains(next)) {
            targetQueue.clear();
            targetQueue.add(next);
            lastDirection = opposite;
        } else {
            targetQueue.clear();
            resetShipHunt();
        }
    }

    private Coordinate getNextInDirection(Coordinate start, Direction dir) {
        if (dir == null) return null;
        int row = start.getRow();
        int col = start.getCol();
        switch (dir) {
            case UP: row--; break;
            case DOWN: row++; break;
            case LEFT: col--; break;
            case RIGHT: col++; break;
            default: return null;
        }
        if (row >= 0 && row < 16 && col >= 0 && col < 16) {
            return new Coordinate(row, col);
        }
        return null;
    }

    private void markAroundSunkShip() {
        for (Coordinate hit : currentShipHits) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int r = hit.getRow() + dr;
                    int c = hit.getCol() + dc;
                    if (r >= 0 && r < 16 && c >= 0 && c < 16) {
                        Coordinate coord = new Coordinate(r, c);
                        if (!hitShots.contains(coord)) {
                            missedShots.add(coord);
                        }
                    }
                }
            }
        }
    }

    private void resetShipHunt() {
        firstHit = null;
        lastDirection = null;
        currentShipHits.clear();
        targetQueue.clear();
        tryingOpposite = false;
    }

    public String getName() {
        return name;
    }

    public void reset() {
        missedShots.clear();
        hitShots.clear();
        targetQueue.clear();
        currentShipHits.clear();
        firstHit = null;
        lastDirection = null;
        tryingOpposite = false;
        lastShot = null;
        lastResult = null;
    }

    public Board getBoard() {
        return myBoard;
    }
}