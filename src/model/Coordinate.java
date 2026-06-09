package model;

import java.util.Objects;

public class Coordinate {

    private final int row;
    private final int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Coordinate(String coord) {

        coord = coord.toUpperCase().trim();
        char colLetter = coord.charAt(0);
        int rowNum = Integer.parseInt(coord.substring(1));

        this.col = colLetter - 'A';
        this.row = rowNum - 1;

        if (this.row < 0 || this.row > 15 || this.col < 0 || this.col > 15) {
            throw new IllegalArgumentException("Невреные коодринаты: " + coord + "'Должно быть А1-P16'");
        }
    }

    public int getRow() {return row;}
    public int getCol() {return col;}

    @Override
    public String toString() {
        return String.valueOf((char)('A' + col)) + (row + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Coordinate that = (Coordinate) obj;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}