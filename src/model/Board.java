package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    private final CellStatus[][] grid;
    private final List<Ship> ships;
    private final int size = 16;

    public Board() {
        grid = new CellStatus[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(grid[i], CellStatus.EMPTY);
        }
        ships = new ArrayList<>();
    }
    public boolean canPlaceShip(Coordinate start, boolean horizontal, int length) {
        int row = start.getRow();
        int col = start.getCol();

        if (horizontal) {
            if (col + length > 16) {
                return false;
            }
        } else {
            if (row + length > 16) {
                return false;
            }
        }

        for (int i = 0; i < length; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            if (grid[r][c] != CellStatus.EMPTY) {
                return false;
            }
        }

        for (int i = 0; i < length; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int nr = r + dr;
                    int nc = c + dc;
                    if (nr >= 0 && nr < 16 && nc >= 0 && nc < 16) {
                        if (grid[nr][nc] == CellStatus.SHIP) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void placeShip(Coordinate start, boolean horizontal, int length) {
        if (!canPlaceShip(start, horizontal, length)) {
            throw new  IllegalArgumentException("Нельзя разместить корабль, он находится слишком близко к другому");
        }

        List<Coordinate> cells = new ArrayList<>();
        int row = start.getRow();
        int col = start.getCol();

        for (int i = 0; i < length; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            Coordinate coord = new Coordinate(r, c);
            cells.add(coord);
            grid[r][c] = CellStatus.SHIP;
        }
        ships.add(new Ship(cells));
    }

    public ShotResult shoot(Coordinate coord) {
        int row = coord.getRow();
        int col = coord.getCol();

        if (grid[row][col] == CellStatus.MISS || grid[row][col] == CellStatus.HIT || grid[row][col] == CellStatus.SUNK) {
            return ShotResult.ALREADY_SHOT;
        }

        if (grid[row][col] == CellStatus.EMPTY) {
            grid[row][col] = CellStatus.MISS;
            return ShotResult.MISS;
        }

        grid[row][col] = CellStatus.HIT;

        for (Ship ship : ships) {
            if (ship.hit(coord)){
                if (ship.isSunk()){
                    for (Coordinate c : ship.getCells()){
                        grid[c.getRow()][c.getCol()] = CellStatus.SUNK;
                    }
                    markAroundSunkShip(ship);
                    return ShotResult.SUNK;
                }
                return ShotResult.HIT;
            }
        }
        return ShotResult.HIT;
    }

    public void reveal(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == CellStatus.SHIP) {
                    System.out.print("■ ");
                }
                else if (grid[i][j] == CellStatus.HIT ||  grid[i][j] == CellStatus.SUNK) {
                    System.out.print("x ");
                }
                else if (grid[i][j] == CellStatus.MISS) {
                    System.out.print("• ");
                }
                else{
                    System.out.print("~ ");
                }
            }
            System.out.println();
        }
    }

    private void markAroundSunkShip(Ship ship) {
        for (Coordinate cell : ship.getCells()) {
            int row = cell.getRow();
            int col = cell.getCol();

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = row + dr;
                    int nc = col + dc;

                    if (nr >= 0 && nr < size && nc >= 0 && nc < size) {
                        if (grid[nr][nc] == CellStatus.EMPTY) {
                            grid[nr][nc] = CellStatus.MISS;
                        }
                    }
                }
            }
        }
    }

    public void forcePlaceShip(Coordinate coord) {
        List<Coordinate> cells = new ArrayList<>();
        cells.add(coord);
        grid[coord.getRow()][coord.getCol()] = CellStatus.SHIP;
        ships.add(new Ship(cells));
    }

    public boolean allShipsSunk(){
        return ships.stream().allMatch(Ship::isSunk);
    }

    public CellStatus getCell(int row, int col) {
        return grid[row][col];
    }

    public List<Ship> getShips() {
        return new ArrayList<>(ships);
    }

    public int getSize() {
        return size;
    }
}