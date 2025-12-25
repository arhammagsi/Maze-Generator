package model;

import java.util.Objects;

public class Cell implements Comparable<Cell> {
    public int row, col;
    public Cell parent;
    public int g; // Cost from start to this cell
    public int h; // Heuristic cost from this cell to end (for A*)

    public Cell(int r, int c, Cell p) {
        this.row = r;
        this.col = c;
        this.parent = p;
        this.g = 0;
        this.h = 0;
    }

    public Cell(int r, int c, Cell p, int g, int h) {
        this.row = r;
        this.col = c;
        this.parent = p;
        this.g = g;
        this.h = h;
    }

    public int getF() {
        return g + h;
    }

    @Override
    public int compareTo(Cell other) {
        return Integer.compare(this.getF(), other.getF());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
