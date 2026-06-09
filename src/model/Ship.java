package model;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private final List<Coordinate> cells;
    private final List<Boolean> hits;
    private final int size;

    public Ship(List<Coordinate> cells) {
        this.cells = new ArrayList<>(cells);
        this.size = cells.size();
        this.hits = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            hits.add(false);
        }
    }

    public boolean hit(Coordinate coord) { // попал или не попал
        for (int i = 0; i < cells.size(); i++) {
            if(cells.get(i).equals(coord)) {
                hits.set(i, true);
                return true;
            }
        }
        return false;
    }

    public boolean isHitAt(Coordinate coord) {
        for (int i = 0; i < cells.size(); i++) {
            if(cells.get(i).equals(coord)) {
                return hits.get(i);
            }
        }
        return false;
    }

    public boolean isSunk(){ // убил или не убил
        return hits.stream().allMatch(hit->hit);
    }

    public boolean isAlive(){
        return !isSunk();
    }

    public List<Coordinate> getCells() {
        return new ArrayList<>(cells);
    }

    public int getSize() {
        return size;
    }
}
