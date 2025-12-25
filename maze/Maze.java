import model.MazeData;
import model.Cell;
import java.awt.*;
import javax.swing.*;
import solvers.*;

import java.util.Objects;
import java.util.function.Supplier;

public class Maze extends JPanel {

    private MazeData mazeData;
    private volatile boolean isSolving = false;
    private Thread solverThread = null;

    int border = 0;
    int speedSleep = 10;
    boolean mazeExists = false;
    
    int width = -1;
    int height = -1;

    int totalWidth;
    int totalHeight;
    int left;
    int top;

    public Maze() {
        this.mazeData = new MazeData();
        setBackground(mazeData.getColor(MazeData.BACKGROUND_CODE));
        setPreferredSize(new Dimension(mazeData.getBlockSize() * mazeData.getColumns(), mazeData.getBlockSize() * mazeData.getRows()));
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        JFrame window = new JFrame("Maze Solver");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(new Color(40, 40, 40));

        Maze mazePanel = new Maze();

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(60, 60, 60));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton newMazeButton = new JButton("New Maze");
        JComboBox<String> algorithmComboBox = new JComboBox<>(new String[]{"DFS", "BFS", "Dijkstra"});
        JButton solveButton = new JButton("Solve");
        JButton resetButton = new JButton("Reset");

        styleButton(newMazeButton);
        styleButton(solveButton);
        styleButton(resetButton);

        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmLabel.setForeground(Color.WHITE);
        styleComboBox(algorithmComboBox);

        controlPanel.add(newMazeButton);
        controlPanel.add(algorithmLabel);
        controlPanel.add(algorithmComboBox);
        controlPanel.add(solveButton);
        controlPanel.add(resetButton);

        newMazeButton.addActionListener(__ -> {
            setControlsEnabled(false, newMazeButton, solveButton, resetButton, algorithmComboBox);
            mazePanel.stopSolver();
            new Thread(() -> {
                try {
                    mazePanel.generateNewMaze();
                } finally {
                    SwingUtilities.invokeLater(() -> setControlsEnabled(true, newMazeButton, solveButton, resetButton, algorithmComboBox));
                }
            }).start();
        });

        solveButton.addActionListener(__ -> {
            mazePanel.stopSolver();
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();

            mazePanel.startSolveMaze(selectedAlgorithm);
        });

        resetButton.addActionListener(__ -> {
            mazePanel.stopSolver();
            mazePanel.resetSolutionData();
        });

        window.setLayout(new BorderLayout());
        window.add(mazePanel, BorderLayout.CENTER);
        window.add(controlPanel, BorderLayout.SOUTH);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        new Thread(() -> {
            mazePanel.generateNewMaze();
            SwingUtilities.invokeLater(() -> setControlsEnabled(true, newMazeButton, solveButton, resetButton, algorithmComboBox));
        }).start();
    }

    private static void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private static void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(new Color(80, 80, 80));
        comboBox.setForeground(Color.WHITE);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private static void setControlsEnabled(boolean enabled, JComponent... components) {
        for (JComponent component : components) {
            component.setEnabled(enabled);
        }
    }

    void checkSize() {
        if (getWidth() != width || getHeight() != height) {
            width = getWidth();
            height = getHeight();
            int r = mazeData.getRows();
            int c = mazeData.getColumns();
            int bs = mazeData.getBlockSize();
            totalWidth = bs * c;
            totalHeight = bs * r;
            left = (width - totalWidth) / 2;
            top = (height - totalHeight) / 2;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        checkSize();
        redrawMaze(g);
    }

    void redrawMaze(Graphics g) {
        if (mazeExists) {
            for (int j = 0; j < mazeData.getColumns(); j++) {
                for (int i = 0; i < mazeData.getRows(); i++) {
                    g.setColor(mazeData.getColor(mazeData.getCode(i, j)));
                    g.fillRect(left + j * mazeData.getBlockSize(), top + i * mazeData.getBlockSize(), mazeData.getBlockSize(), mazeData.getBlockSize());
                }
            }
        }
    }

    public void generateNewMaze() {
        mazeExists = true;
        MazeGenerator generator = new MazeGenerator(mazeData, () -> {
            SwingUtilities.invokeLater(this::repaint);
            try {
                Thread.sleep(speedSleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        generator.generate();
        mazeExists = true;
        SwingUtilities.invokeLater(this::repaint);
    }

    public void startSolveMaze(String algorithm) {
        if (isSolving) {
            return;
        }
        solverThread = new Thread(() -> {
            isSolving = true;
            resetSolutionData();

            Runnable uiUpdater = () -> {
                SwingUtilities.invokeLater(this::repaint);
                try {
                    Thread.sleep(speedSleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };

            Supplier<Boolean> isSolvingChecker = () -> isSolving;

            Solver solver;
            switch (algorithm) {
                case "DFS":
                    solver = new DFSSolver(mazeData, uiUpdater, isSolvingChecker);
                    break;
                case "BFS":
                    solver = new BFSSolver(mazeData, uiUpdater, isSolvingChecker);
                    break;
//                case "A*":
//                    solver = new AStarSolver(mazeData, uiUpdater, isSolvingChecker);
//                    break;
                case "Dijkstra":
                    solver = new DijkstraSolver(mazeData, uiUpdater, isSolvingChecker);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
            }

            solver.solve();

            isSolving = false;
            SwingUtilities.invokeLater(this::repaint);
        });
        solverThread.start();
    }

    public void stopSolver() {
        isSolving = false;
        if (solverThread != null) {
            try {
                solverThread.interrupt();
                solverThread.join(1000);
            } catch (InterruptedException e) {
                // ignore
            } finally {
                solverThread = null;
            }
        }
    }

    public void resetSolutionData() {
        if (mazeData.getMazeGrid() == null) return;
        for (int i = 0; i < mazeData.getRows(); i++) {
            for (int j = 0; j < mazeData.getColumns(); j++) {
                int code = mazeData.getCode(i, j);
                if (code == MazeData.PATH_CODE || code == MazeData.VISITED_CODE) {
                    mazeData.setCode(i, j, MazeData.EMPTY_CODE);
                }
            }
        }
        // Restore start and end points
        mazeData.setCode(mazeData.getStartRow(), mazeData.getStartCol(), MazeData.START_CODE);
        mazeData.setCode(mazeData.getFinishRow(), mazeData.getFinishCol(), MazeData.FINISH_CODE);
        repaint();
    }
private static class Cell implements Comparable<Cell> {
    int row;
    int col;
    Cell parent;
    int g;
    int h;

    Cell(int row, int col, Cell parent) {
        this.row = row;
        this.col = col;
        this.parent = parent;
        this.g = 0;
        this.h = 0;
    }

    Cell(int row, int col, Cell parent, int g, int h) {
        this.row = row;
        this.col = col;
        this.parent = parent;
        this.g = g;
        this.h = h;
    }

    public int getF() {
        return g + h;
    }

    public int compareTo(Cell other) {
        return Integer.compare(this.getF(), other.getF());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}}

