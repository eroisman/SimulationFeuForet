package view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;

public class FireView {
    private GridPane gridPane;
    private Button nextStepButton;
    private model.FireModel model;

    public FireView(GridPane gridPane, Button nextStepButton, model.FireModel model) {
        this.gridPane = gridPane;
        this.nextStepButton = nextStepButton;
        this.model = model;
    }

    public void updateGrid(int[][] grid) {
        gridPane.getChildren().clear();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Button cell = new Button();
                if (grid[i][j] == 0) {
                    cell.setStyle("-fx-background-color: green;");
                } else if (grid[i][j] == 1) {
                    cell.setStyle("-fx-background-color: red;");
                } else {
                    cell.setStyle("-fx-background-color: gray;");
                }
                gridPane.add(cell, j, i);
            }
        }
    }

    public Button getNextStepButton() {
        return nextStepButton;
    }

    public void showEndAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Simulation de la propagation d'un feu de forêt");
        alert.setHeaderText("Le feu s'est éteint.");
        alert.setContentText("Que voulez-vous faire ?");

        ButtonType restartButton = new ButtonType("Recommencer");
        ButtonType cancelButton = new ButtonType("Annuler");

        alert.getButtonTypes().setAll(restartButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == restartButton) {
                // Recommencer
                model.initializeGrid(); // Réinitialiser la grille
                updateGrid(model.getGrid());
            } else {
                // Fermer l'application
                Platform.exit();
            }
        });
    }
}

