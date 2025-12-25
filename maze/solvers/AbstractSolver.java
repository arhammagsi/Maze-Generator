package solvers;
import model.MazeData;
import model.Cell;

import java.util.function.Supplier;

public abstract class AbstractSolver implements Solver {
    protected final MazeData mazeData;
    protected final Runnable uiUpdater;
    protected final Supplier<Boolean> isSolvingChecker;

    public AbstractSolver(MazeData mazeData, Runnable uiUpdater, Supplier<Boolean> isSolvingChecker) {
        this.mazeData = mazeData;
        this.uiUpdater = uiUpdater;
        this.isSolvingChecker = isSolvingChecker;
    }

    @Override
    public abstract boolean solve();

}
