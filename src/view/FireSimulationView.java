package view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;

public class FireSimulationView {
    private final GridPane gridPane; // Grille d'affichage
    private final Button nextStepButton; // Bouton "Étape suivante"
    private final model.FireSimulationModel model; // Modèle de simulation de feu

    // Constructeur de la vue
    public FireSimulationView(GridPane gridPane, Button nextStepButton, model.FireSimulationModel model) {
        this.gridPane = gridPane;
        this.nextStepButton = nextStepButton;
        this.model = model;
    }

    // Met à jour l'affichage de la grille en fonction des données du modèle
    public void updateGrid(int[][] grid) {
        gridPane.getChildren().clear();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Button cell = new Button();
                String color = switch (grid[i][j]) {
                    case 0 -> "green"; // Couleur des cellules sans feu
                    case 1 -> "red"; // Couleur des cellules en feu
                    default -> "gray"; // Couleur des cellules éteintes
                };
                cell.setStyle("-fx-background-color: " + color + ";");
                gridPane.add(cell, j, i);
            }
        }
    }

    // Renvoie le bouton "Étape suivante"
    public Button getNextStepButton() {
        return nextStepButton;
    }

    // Affiche une alerte à la fin de la simulation
    public void showEndAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Simulation de propagation de feu de forêt");
        alert.setHeaderText("Le feu s'est éteint.");
        alert.setContentText("Que voulez-vous faire ?");

        ButtonType restartButton = new ButtonType("Recommencer");
        ButtonType cancelButton = new ButtonType("Annuler");

        alert.getButtonTypes().setAll(restartButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == restartButton) {
                // Recommencer la simulation
                model.initializeGrid(); // Réinitialiser la grille dans le modèle
                updateGrid(model.getGrid()); // Mettre à jour l'affichage avec la nouvelle grille
            } else {
                // Fermer l'application
                Platform.exit();
            }
        });
    }
}
