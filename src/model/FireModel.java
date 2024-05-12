package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class FireModel {
    private int[][] grid;
    private double fireSpreadProbability;
    private List<CellPosition> initialFireCells;
    private int rows;
    private int columns;

    public FireModel() {
        loadProperties();
        initializeGrid();
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("C:\\Projets\\SimulationFeuForet\\src\\resources\\config.properties"));
            rows = Integer.parseInt(properties.getProperty("grid.rows"));
            columns = Integer.parseInt(properties.getProperty("grid.columns"));
            initialFireCells = parseInitialFireCells(properties.getProperty("initial.fire.cells"));
            fireSpreadProbability = Double.parseDouble(properties.getProperty("fire.spread.probability"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<CellPosition> parseInitialFireCells(String input) {
        List<CellPosition> initialFireCells = new ArrayList<>();
        input = input.replaceAll("\\(", "").replaceAll("\\)", "");
        String[] positions = input.split("\\),");
        for (String position : positions) {
            String[] coordinates = position.trim().split("[;,]");
            for (int i = 0; i < coordinates.length; i += 2) {
                int row = Integer.parseInt(coordinates[i]);
                int col = Integer.parseInt(coordinates[i + 1]);
                initialFireCells.add(new CellPosition(row, col));
            }
        }
        return initialFireCells;
    }

    public void initializeGrid() {
        grid = new int[rows][columns];
        for (CellPosition position : initialFireCells) {
            grid[position.row][position.col] = 1; // 1 représente une cellule en feu
        }
    }

    public void advanceFire() {
        int[][] tempGrid = new int[rows][columns];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j] == 1) {
                    tempGrid[i][j] = 2; // 2 représente une cellule brûlée

                    if (i > 0 && grid[i - 1][j] == 0 && random.nextDouble() < fireSpreadProbability) {
                        tempGrid[i - 1][j] = 1;
                    }
                    if (i < rows - 1 && grid[i + 1][j] == 0 && random.nextDouble() < fireSpreadProbability) {
                        tempGrid[i + 1][j] = 1;
                    }
                    if (j > 0 && grid[i][j - 1] == 0 && random.nextDouble() < fireSpreadProbability) {
                        tempGrid[i][j - 1] = 1;
                    }
                    if (j < columns - 1 && grid[i][j + 1] == 0 && random.nextDouble() < fireSpreadProbability) {
                        tempGrid[i][j + 1] = 1;
                    }
                } else {
                    tempGrid[i][j] = grid[i][j];
                }
            }
        }

        grid = tempGrid;
    }

    public boolean hasFire() {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 1) {
                    return true; // S'il y a une case en feu, retourne vrai
                }
            }
        }
        return false; // S'il n'y a aucune case en feu, retourne faux
    }

    public int[][] getGrid() {
        return grid;
    }

    public double getFireSpreadProbability() {
        return fireSpreadProbability;
    }

    public static class CellPosition {
        int row;
        int col;

        public CellPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
