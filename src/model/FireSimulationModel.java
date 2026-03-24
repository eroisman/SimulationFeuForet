package model;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FireSimulationModel {
    private int[][] grid; // Grille représentant l'état du feu
    private double fireSpreadProbability; // Probabilité de propagation du feu
    private List<CellPosition> initialFireCells; // Liste des positions initiales du feu
    private int rows; // Nombre de lignes dans la grille
    private int columns; // Nombre de colonnes dans la grille

    // Constructeur
    public FireSimulationModel() {
        loadProperties(); // Charge les propriétés à partir du fichier config.properties
        initializeGrid(); // Initialise la grille
    }

    // Charge les propriétés à partir du fichier config.properties
    private void loadProperties() {
        Properties properties = new Properties();
        setDefaultConfiguration();

        try (InputStream configStream = openConfigStream()) {
            if (configStream == null) {
                return;
            }

            properties.load(configStream);
            rows = parseIntegerProperty(properties, "grid.rows", rows);
            columns = parseIntegerProperty(properties, "grid.columns", columns);
            initialFireCells = parseInitialFireCells(properties.getProperty("initial.fire.cells", "(0;0)"));
            fireSpreadProbability = parseDoubleProperty(properties, "fire.spread.probability", fireSpreadProbability);
            fireSpreadProbability = Math.max(0.0, Math.min(1.0, fireSpreadProbability));
        } catch (IOException e) {
            setDefaultConfiguration();
        }
    }

    // Analyse les positions initiales du feu à partir de la chaîne de configuration
    public List<CellPosition> parseInitialFireCells(String input) {
        List<CellPosition> initialFireCells = new ArrayList<>();
        if (input == null || input.isBlank()) {
            return initialFireCells;
        }

        Pattern pattern = Pattern.compile("\\((\\d+)\\s*[;,]\\s*(\\d+)\\)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            int row = Integer.parseInt(matcher.group(1));
            int col = Integer.parseInt(matcher.group(2));
            initialFireCells.add(new CellPosition(row, col));
        }

        return initialFireCells;
    }

    // Initialise la grille avec les positions initiales du feu
    public void initializeGrid() {
        grid = new int[rows][columns];
        for (CellPosition position : initialFireCells) {
            if (position.row >= 0 && position.row < rows && position.col >= 0 && position.col < columns) {
                grid[position.row][position.col] = 1; // 1 représente une cellule en feu
            }
        }
    }

    // Fait avancer le feu dans la grille
    public void advanceFire() {
        int[][] tempGrid = new int[rows][columns];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j] == 1) {
                    tempGrid[i][j] = 2; // 2 représente une cellule brûlée

                    // Propagation du feu aux cases adjacentes avec une probabilité
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

    // Vérifie si la grille contient encore des cases en feu
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

    // Renvoie la grille actuelle
    public int[][] getGrid() {
        return grid;
    }

    // Renvoie la probabilité de propagation du feu
    public double getFireSpreadProbability() {
        return fireSpreadProbability;
    }

    private InputStream openConfigStream() throws IOException {
        InputStream classpathStream = FireSimulationModel.class.getClassLoader().getResourceAsStream("resources/config.properties");
        if (classpathStream != null) {
            return classpathStream;
        }

        String[] fallbackPaths = {
            "src/resources/config.properties",
            "resources/config.properties"
        };

        for (String path : fallbackPaths) {
            try {
                return new FileInputStream(path);
            } catch (IOException ignored) {
                // Essaie le chemin suivant
            }
        }

        return null;
    }

    private int parseIntegerProperty(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double parseDoubleProperty(Properties properties, String key, double defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void setDefaultConfiguration() {
        rows = 9;
        columns = 9;
        fireSpreadProbability = 0.5;
        initialFireCells = new ArrayList<>();
        initialFireCells.add(new CellPosition(8, 8));
        initialFireCells.add(new CellPosition(4, 0));
        initialFireCells.add(new CellPosition(5, 4));
        initialFireCells.add(new CellPosition(6, 6));
    }

    // Classe interne représentant la position d'une cellule dans la grille
    public static class CellPosition {
        int row; // Ligne de la cellule
        int col; // Colonne de la cellule

        // Constructeur de la classe
        public CellPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
