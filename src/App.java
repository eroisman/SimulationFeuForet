import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {

        // Chargement des propriétés à partir du fichier config.properties
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("C:\\Projets\\SimulationFeuForet\\src\\config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Récupération des dimensions de la grille et des autres paramètres
        int rows = Integer.parseInt(properties.getProperty("grid.rows"));
        int columns = Integer.parseInt(properties.getProperty("grid.columns"));
        List<CellPosition> initialFireCells = parseInitialFireCells(properties.getProperty("initial.fire.cells"));
        double fireSpreadProbability = Double.parseDouble(properties.getProperty("fire.spread.probability"));

        // Initialisation de la grille
        int[][] grid = new int[rows][columns];
        initializeGrid(grid, initialFireCells);

        Scanner scanner = new Scanner(System.in);

        // Boucle principale de simulation du feu
        boolean fireStillBurning = true;
        while (fireStillBurning) {
            // Affichage de la grille à chaque étape
            displayGrid(grid);
            System.out.println("Appuyez sur Entrée pour avancer le feu...");
            scanner.nextLine();
            advanceFire(grid, fireSpreadProbability);

            // Vérification si le feu est toujours en train de brûler
            fireStillBurning = hasFire(grid);

            // Si le feu est éteint, afficher la grille une dernière fois avant de quitter la boucle
            if (!fireStillBurning) {
                displayGrid(grid);
            }
        }

        // Affichage du message final
        System.out.println("Le feu s'est éteint.");

        scanner.close();
    }

    // Méthode pour analyser les positions initiales du feu à partir de la chaîne de configuration
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

    // Méthode pour initialiser la grille avec les positions initiales du feu
    public static void initializeGrid(int[][] grid, List<CellPosition> initialFireCells) {
        for (CellPosition position : initialFireCells) {
            grid[position.row][position.col] = 1; // 1 représente une case en feu
        }
    }

    // Méthode pour faire avancer le feu dans la grille
    public static void advanceFire(int[][] grid, double probability) {
        int[][] tempGrid = new int[grid.length][grid[0].length];
        Random random = new Random();

        // Parcours de la grille
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 1) {
                    tempGrid[i][j] = 2; // Le feu s'éteint dans cette case (2 représente une case en cendres)
                    // Probabilité que le feu se propage aux cases adjacentes
                    if (i > 0 && grid[i - 1][j] == 0 && random.nextDouble() < probability) {
                        tempGrid[i - 1][j] = 1; // 1 représente une case en feu
                    }
                    if (i < grid.length - 1 && grid[i + 1][j] == 0 && random.nextDouble() < probability) {
                        tempGrid[i + 1][j] = 1; // 1 représente une case en feu
                    }
                    if (j > 0 && grid[i][j - 1] == 0 && random.nextDouble() < probability) {
                        tempGrid[i][j - 1] = 1; // 1 représente une case en feu
                    }
                    if (j < grid[i].length - 1 && grid[i][j + 1] == 0 && random.nextDouble() < probability) {
                        tempGrid[i][j + 1] = 1; // 1 représente une case en feu
                    }
                } else {
                    tempGrid[i][j] = grid[i][j]; // Copie de la valeur de la case sans modification
                }
            }
        }

        // Copie tempGrid dans grid
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(tempGrid[i], 0, grid[i], 0, grid[i].length);
        }
    }

    // Méthode pour vérifier si des cases en feu sont encore présentes dans la grille
    public static boolean hasFire(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 1) {
                    return true; // Si une case en feu est trouvée, retourne vrai
                }
            }
        }
        return false; // Si aucune case en feu n'est trouvée, retourne faux
    }

    // Méthode pour afficher la grille
    public static void displayGrid(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell + " "); // Affichage de la valeur de chaque case
            }
            System.out.println(); // Saut de ligne à la fin de chaque ligne de la grille
        }
    }

    // Classe interne représentant la position d'une cellule dans la grille
    static class CellPosition {
        int row; // Ligne de la cellule
        int col; // Colonne de la cellule

        // Constructeur de la classe
        CellPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
