package model;

import java.awt.Color;

public class MazeData {
    private final int rows = 41;
    private final int columns = 51;
    private final int blockSize = 12;
    private int[][] maze;

    public static final int BACKGROUND_CODE = 0;
    public static final int WALL_CODE = 1;
    public static final int PATH_CODE = 2;
    public static final int EMPTY_CODE = 3;
    public static final int VISITED_CODE = 4;
    public static final int START_CODE = 5;
    public static final int FINISH_CODE = 6;

    private final Color[] colors = {
        new Color(29, 29, 29),    // BACKGROUND_CODE
        new Color(80, 80, 80),     // WALL_CODE
        new Color(50, 205, 50),    // PATH_CODE
        new Color(40, 40, 40),     // EMPTY_CODE
        new Color(70, 130, 180),   // VISITED_CODE
        new Color(255, 165, 0),   // START_CODE (Orange)
        new Color(0, 255, 255)    // FINISH_CODE (Cyan)
    };

    private final int startR = 1;
    private final int startC = 1;
    private final int finishR = rows - 2;
    private final int finishC = columns - 2;

    public MazeData() {
        this.maze = new int[rows][columns];
    }

    public int getStartRow() { return startR; }
    public int getStartCol() { return startC; }
    public int getFinishRow() { return finishR; }
    public int getFinishCol() { return finishC; }

    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public int getBlockSize() { return blockSize; }
    public int[][] getMazeGrid() { return maze; }
    public int getCode(int r, int c) { return maze[r][c]; }
    public Color getColor(int code) {
        if (code >= 0 && code < colors.length) {
            return colors[code];
        }
        // Return a default color for temporary or invalid codes during generation
        return Color.GRAY;
    }

    public void setCode(int r, int c, int code) {
        if (r >= 0 && r < rows && c >= 0 && c < columns) {
            maze[r][c] = code;
        }
    }
    
    public void resetGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                maze[i][j] = BACKGROUND_CODE;
            }
        }
    }

    public int getCols() {
        return columns;
    }
}
