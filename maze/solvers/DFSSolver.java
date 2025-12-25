package solvers;
import model.MazeData;
import model.Cell;

import java.util.function.Supplier;

public class DFSSolver extends AbstractSolver {

    public DFSSolver(MazeData mazeData, Runnable uiUpdater, Supplier<Boolean> isSolvingChecker) {
        super(mazeData, uiUpdater, isSolvingChecker);
    }

    @Override
    public boolean solve() {
        java.util.Stack<model.Cell> stack = new java.util.Stack<>();
        boolean[][] visited = new boolean[mazeData.getRows()][mazeData.getColumns()];

        int startR = mazeData.getStartRow();
        int startC = mazeData.getStartCol();
        int goalR = mazeData.getFinishRow();
        int goalC = mazeData.getFinishCol();

        stack.push(new model.Cell(startR, startC, null));
        visited[startR][startC] = true;

        model.Cell current = null;
        boolean found = false;
        int a = 0;
        while (!stack.isEmpty() && isSolvingChecker.get()) {
            a = 0;
            current = stack.pop();
            a++;

            if (current.row == goalR && current.col == goalC) {
                found = true;
                break;
            }


            mazeData.setCode(current.row, current.col, MazeData.VISITED_CODE);
            uiUpdater.run();

            int[] dr = {0, 0, 1, -1}; // Order can be changed for different path textures
            int[] dc = {1, -1, 0, 0};

            for (int i = 0; i < 4; i++) {
                int nextR = current.row + dr[i];
                int nextC = current.col + dc[i];

                if (nextR >= 0 && nextR < mazeData.getRows() && nextC >= 0 && nextC < mazeData.getColumns() &&
                    mazeData.getCode(nextR, nextC) != MazeData.WALL_CODE && !visited[nextR][nextC]) {
                    visited[nextR][nextC] = true;
                    stack.push(new model.Cell(nextR, nextC, current));
                }
            }

        }
        double time = a/1_000_000_000.0;

        System.out.println(time);

        if (found && isSolvingChecker.get()) {
            model.Cell pathCell = current;
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
