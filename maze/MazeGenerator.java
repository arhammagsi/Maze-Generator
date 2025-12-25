import model.MazeData;

public class MazeGenerator {

    private final MazeData mazeData;
    private final Runnable updateCallback;

    public MazeGenerator(MazeData mazeData, Runnable updateCallback) {
        this.mazeData = mazeData;
        this.updateCallback = updateCallback;
    }

    public void generate() {
        mazeData.resetGrid();
        updateCallback.run();

        int emptyCt = 0;
        int wallCt = 0;
        int[] wallrow = new int[(mazeData.getRows() * mazeData.getColumns()) / 2];
        int[] wallcol = new int[(mazeData.getRows() * mazeData.getColumns()) / 2];

        for (int i = 0; i < mazeData.getRows(); i++)
            for (int j = 0; j < mazeData.getColumns(); j++)
                mazeData.setCode(i, j, MazeData.WALL_CODE);

        for (int i = 1; i < mazeData.getRows() - 1; i += 2)
            for (int j = 1; j < mazeData.getColumns() - 1; j += 2) {
                emptyCt++;
                mazeData.setCode(i, j, -emptyCt);
                if (i < mazeData.getRows() - 2) {
                    wallrow[wallCt] = i + 1;
                    wallcol[wallCt] = j;
                    wallCt++;
                }
                if (j < mazeData.getColumns() - 2) {
                    wallrow[wallCt] = i;
                    wallcol[wallCt] = j + 1;
                    wallCt++;
                }
            }

        for (int i = wallCt - 1; i > 0; i--) {
            int r = (int) (Math.random() * i);
            tearDown(wallrow[r], wallcol[r]);
            wallrow[r] = wallrow[i];
            wallcol[r] = wallcol[i];
        }

        for (int i = 1; i < mazeData.getRows() - 1; i++)
            for (int j = 1; j < mazeData.getColumns() - 1; j++)
                if (mazeData.getCode(i, j) < 0)
                    mazeData.setCode(i, j, MazeData.EMPTY_CODE);

        // Set start and end points
        mazeData.setCode(mazeData.getStartRow(), mazeData.getStartCol(), MazeData.START_CODE);
        mazeData.setCode(mazeData.getFinishRow(), mazeData.getFinishCol(), MazeData.FINISH_CODE);
    }

    private void tearDown(int row, int col) {
        if (row % 2 == 1 && mazeData.getCode(row, col - 1) != mazeData.getCode(row, col + 1)) {
            fill(row, col - 1, mazeData.getCode(row, col - 1), mazeData.getCode(row, col + 1));
            mazeData.setCode(row, col, mazeData.getCode(row, col + 1));
            updateCallback.run();
        } else if (row % 2 == 0 && mazeData.getCode(row - 1, col) != mazeData.getCode(row + 1, col)) {
            fill(row - 1, col, mazeData.getCode(row - 1, col), mazeData.getCode(row + 1, col));
            mazeData.setCode(row, col, mazeData.getCode(row + 1, col));
            updateCallback.run();
        }
    }

    private void fill(int row, int col, int replace, int replaceWith) {
        // ✅ Bounds check to avoid crash
        if (row < 0 || row >= mazeData.getRows() || col < 0 || col >= mazeData.getCols()) {
            return; // Out of maze bounds
        }

        if (mazeData.getCode(row, col) == replace) {
            mazeData.setCode(row, col, replaceWith);
            fill(row + 1, col, replace, replaceWith);
            fill(row - 1, col, replace, replaceWith);
            fill(row, col + 1, replace, replaceWith);
            fill(row, col - 1, replace, replaceWith);
        }
    }
}
