import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("C:\\Projets\\SimulationFeuForet\\src\\config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int rows = Integer.parseInt(properties.getProperty("grid.rows"));
        int columns = Integer.parseInt(properties.getProperty("grid.columns"));

        List<CellPosition> initialFireCells = parseInitialFireCells(properties.getProperty("initial.fire.cells"));

        double fireSpreadProbability = Double.parseDouble(properties.getProperty("fire.spread.probability"));

        int[][] grid = new int[rows][columns];
        initializeGrid(grid, initialFireCells);

        Scanner scanner = new Scanner(System.in);

        boolean fireStillBurning = true;
        while (fireStillBurning) {
            displayGrid(grid);
            System.out.println("Press Enter to advance the fire...");
            scanner.nextLine();
            advanceFire(grid, fireSpreadProbability);

            // Vérifier si des cases en feu sont encore présentes après l'avancement du feu
            fireStillBurning = hasFire(grid);

            // Si le feu est encore en train de brûler, continuer la propagation
            // Sinon, afficher la grille une dernière fois avant de quitter la boucle
            if (!fireStillBurning) {
                displayGrid(grid); // Afficher la grille une dernière fois
            }
        }

        System.out.println("Le feu s'est éteint.");

        scanner.close();
    }

    public static List<CellPosition> parseInitialFireCells(String input) {
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
    
    public static void initializeGrid(int[][] grid, List<CellPosition> initialFireCells) {
        for (CellPosition position : initialFireCells) {
            grid[position.row][position.col] = 1;
        }
    }

    public static void advanceFire(int[][] grid, double probability) {
        int[][] tempGrid = new int[grid.length][grid[0].length];
        Random random = new Random();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 1) {
                    tempGrid[i][j] = 2; // Le feu s'éteint dans cette case
                    // Probabilité que le feu se propage aux cases adjacentes
                    if (i > 0 && grid[i - 1][j] == 0 && random.nextDouble() < probability) {
                        tempGrid[i - 1][j] = 1;
                    }
                    if (i < grid.length - 1 && grid[i + 1][j] == 0 && random.nextDouble() < probability) {
                        tempGrid[i + 1][j] = 1;
                    }
                    if (j > 0 && grid[i][j - 1] == 0 && random.nextDouble() < probability) {
                        tempGrid[i][j - 1] = 1;
                    }
                    if (j < grid[i].length - 1 && grid[i][j + 1] == 0 && random.nextDouble() < probability) {
                        tempGrid[i][j + 1] = 1;
                    }
                } else {
                    tempGrid[i][j] = grid[i][j];
                }
            }
        }

        // Copie tempGrid dans grid
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(tempGrid[i], 0, grid[i], 0, grid[i].length);
        }
    }

    public static boolean hasFire(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void displayGrid(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    static class CellPosition {
        int row;
        int col;

        CellPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}