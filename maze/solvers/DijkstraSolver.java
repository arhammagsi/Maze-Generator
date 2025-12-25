package solvers;

import model.MazeData;
import model.Cell;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.function.Supplier;

public class DijkstraSolver extends AbstractSolver {

    public DijkstraSolver(MazeData mazeData, Runnable uiUpdater, Supplier<Boolean> isSolvingChecker) {
        super(mazeData, uiUpdater, isSolvingChecker);
    }

    @Override
    public boolean solve() {
        PriorityQueue<Cell> openSet = new PriorityQueue<>(Comparator.comparingInt(cell -> cell.g));
        Set<Cell> closedSet = new HashSet<>();

        int startR = mazeData.getStartRow();
        int startC = mazeData.getStartCol();
        int goalR = mazeData.getFinishRow();
        int goalC = mazeData.getFinishCol();

        Cell startNode = new Cell(startR, startC, null, 0, 0);
        openSet.add(startNode);

        Cell current = null;
        boolean found = false;

        while (!openSet.isEmpty() && isSolvingChecker.get()) {
            current = openSet.poll();

            if (current.row == goalR && current.col == goalC) {
                found = true;
                break;
            }

            if (!closedSet.add(current)) {
                continue;
            }

            mazeData.setCode(current.row, current.col, MazeData.VISITED_CODE);
            uiUpdater.run();

            int[] dr = {-1, 1, 0, 0};
            int[] dc = {0, 0, -1, 1};

            for (int i = 0; i < 4; i++) {
                int nextR = current.row + dr[i];
                int nextC = current.col + dc[i];

                if (nextR >= 0 && nextR < mazeData.getRows() && nextC >= 0 && nextC < mazeData.getColumns() &&
                    mazeData.getCode(nextR, nextC) != MazeData.WALL_CODE) {
                    
                    if (closedSet.contains(new Cell(nextR, nextC, null))) {
                        continue;
                    }

                    int tentativeG = current.g + 1;
                    
                    Cell neighbor = null;
                    for (Cell cell : openSet) {
                        if (cell.row == nextR && cell.col == nextC) {
                            neighbor = cell;
                            break;
                        }
                    }

                    if (neighbor == null) {
                        openSet.add(new Cell(nextR, nextC, current, tentativeG, 0));
                    } else if (tentativeG < neighbor.g) {
                        openSet.remove(neighbor);
                        neighbor.g = tentativeG;
                        neighbor.parent = current;
                        openSet.add(neighbor);
                    }
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
