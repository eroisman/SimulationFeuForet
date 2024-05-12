import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Optional;

public class App extends Application {

    private int[][] grid; // La grille du feu
    private List<CellPosition> initialFireCells; // Liste des positions initiales du feu
    private double fireSpreadProbability; // Probabilité de propagation du feu
    private int rows; // Nombre de lignes dans la grille
    private int columns; // Nombre de colonnes dans la grille

    @Override
    public void start(Stage primaryStage) {
        // Chargement des propriétés à partir du fichier config.properties
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("C:\\Projets\\SimulationFeuForet\\src\\resources\\config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Récupération des dimensions de la grille et des autres paramètres
        rows = Integer.parseInt(properties.getProperty("grid.rows"));
        columns = Integer.parseInt(properties.getProperty("grid.columns"));
        initialFireCells = parseInitialFireCells(properties.getProperty("initial.fire.cells"));
        fireSpreadProbability = Double.parseDouble(properties.getProperty("fire.spread.probability"));

        // Initialisation de la grille
        grid = new int[rows][columns];
        initializeGrid(grid, initialFireCells);

        // Création de la fenêtre principale
        primaryStage.setTitle("Simulation de feu de forêt");
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 300, 300);
        root.getChildren().addAll(createGridPane(), createNextStepButton(scene));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Création d'un panneau de grille pour afficher la grille
    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        // Ajouter des éléments de la grille à la grille JavaFX
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // Création d'un élément de grille JavaFX pour chaque cellule
                Button cell = new Button();
                // Définir la couleur en fonction de la valeur de la cellule
                if (grid[i][j] == 0) {
                    cell.setStyle("-fx-background-color: green;");
                } else if (grid[i][j] == 1) {
                    cell.setStyle("-fx-background-color: red;");
                } else {
                    cell.setStyle("-fx-background-color: gray;");
                }
                gridPane.add(cell, j, i); // Ajouter la cellule à la grille JavaFX
            }
        }
        return gridPane;
    }

    // Création d'un bouton pour effectuer l'étape suivante de la simulation
    private Button createNextStepButton(Scene scene) {
        Button nextStepButton = new Button("Étape suivante");
        nextStepButton.setOnAction(event -> {
            advanceFire(grid, fireSpreadProbability);
            updateGrid(scene);
            if (!hasFire(grid)) {
                showEndAlert(scene);
            }
        });
        return nextStepButton;
    }

    // Mettre à jour l'apparence de chaque cellule dans la grille JavaFX
    private void updateGrid(Scene scene) {
        VBox root = (VBox) scene.getRoot();
        GridPane gridPane = (GridPane) root.getChildren().get(0);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Button cell = (Button) gridPane.getChildren().get(i * grid[i].length + j);
                if (grid[i][j] == 0) {
                    cell.setStyle("-fx-background-color: green;");
                } else if (grid[i][j] == 1) {
                    cell.setStyle("-fx-background-color: red;");
                } else {
                    cell.setStyle("-fx-background-color: gray;");
                }
            }
        }
    }

    // Afficher une boîte de dialogue à la fin de la simulation
    private void showEndAlert(Scene scene) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Simulation de feu de forêt");
        alert.setHeaderText("Le feu s'est éteint.");
        alert.setContentText("Que voulez-vous faire ?");
    
        ButtonType restartButton = new ButtonType("Recommencer");
        ButtonType cancelButton = new ButtonType("Annuler");
    
        alert.getButtonTypes().setAll(restartButton, cancelButton);
    
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == restartButton) {
            grid = resetGrid(); // Met à jour la grille avec la nouvelle grille retournée par resetGrid()
            updateGrid(scene);
        } else {
            Stage stage = (Stage) scene.getWindow();
            stage.close();
        }
    }    
    
    // Réinitialisation de la grille
    private int[][] resetGrid() {
        int[][] newGrid = new int[rows][columns];
        initializeGrid(newGrid, initialFireCells);
        return newGrid;
    }      

    // Analyser les positions initiales du feu à partir de la chaîne de configuration
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

    // Initialiser la grille avec les positions initiales du feu
    public void initializeGrid(int[][] grid, List<CellPosition> initialFireCells) {
        for (CellPosition position : initialFireCells) {
            grid[position.row][position.col] = 1; // 1 représente une case en feu
        }
    }

    // Méthode pour faire avancer le feu dans la grille
    public void advanceFire(int[][] grid, double probability) {
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
    public boolean hasFire(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 1) {
                    return true; // Si une case en feu est trouvée, retourne vrai
                }
            }
        }
        return false; // Si aucune case en feu n'est trouvée, retourne faux
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

    public static void main(String[] args) {
        launch(args);
    }
}
