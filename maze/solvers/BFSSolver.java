package solvers;

import model.MazeData;
import model.Cell;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class BFSSolver extends AbstractSolver {

    public BFSSolver(MazeData mazeData, Runnable uiUpdater, Supplier<Boolean> isSolvingChecker) {
        super(mazeData, uiUpdater, isSolvingChecker);
    }

    @Override
    public boolean solve() {
        Queue<Cell> queue = new LinkedList<>();
        boolean[][] visited = new boolean[mazeData.getRows()][mazeData.getColumns()];

        int startR = mazeData.getStartRow();
        int startC = mazeData.getStartCol();
        int goalR = mazeData.getFinishRow();
        int goalC = mazeData.getFinishCol();

        queue.add(new Cell(startR, startC, null));
        visited[startR][startC] = true;

        Cell current = null;
        boolean found = false;

        while (!queue.isEmpty() && isSolvingChecker.get()) {
            current = queue.poll();

            if (current.row == goalR && current.col == goalC) {
                found = true;
                break;
            }

            mazeData.setCode(current.row, current.col, MazeData.VISITED_CODE);
            uiUpdater.run();

            int[] dr = {-1, 1, 0, 0};
            int[] dc = {0, 0, -1, 1};

            for (int i = 0; i < 4; i++) {
                int nextR = current.row + dr[i];
                int nextC = current.col + dc[i];

                if (nextR >= 0 && nextR < mazeData.getRows() && nextC >= 0 && nextC < mazeData.getColumns() &&
                    mazeData.getCode(nextR, nextC) != MazeData.WALL_CODE && !visited[nextR][nextC]) {
                    visited[nextR][nextC] = true;
                    queue.add(new Cell(nextR, nextC, current));
                }
            }
        }

        if (found && isSolvingChecker.get()) {
            Cell pathCell = current;
            while (pathCell != null) {
                mazeData.setCode(pathCell.row, pathCell.col, MazeData.PATH_CODE);
                pathCell = pathCell.parent;
                uiUpdater.run();
            }
            return true;
        }
        return false;
    }
}
