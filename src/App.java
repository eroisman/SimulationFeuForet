import javafx.application.Application;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    private model.FireSimulationModel model; // Modèle de simulation de feu
    private view.FireSimulationView view; // Vue de la simulation de feu
    private controller.FireSimulationController controller; // Contrôleur de la simulation de feu

    // Méthode principale de l'application JavaFX
    @Override
    public void start(Stage primaryStage) {
        // Initialisation du modèle de simulation de feu
        model = new model.FireSimulationModel();

        // Configuration de la fenêtre principale
        primaryStage.setTitle("Simulation de propagation de feu de forêt");
        GridPane gridPane = new GridPane(); // Création de la grille pour afficher le feu
        Button nextStepButton = new Button("Étape suivante"); // Bouton pour passer à l'étape suivante
        VBox root = new VBox(); // Création d'un conteneur vertical pour organiser les éléments
        root.setSpacing(10); // Définition de l'espacement entre les éléments
        root.setPadding(new Insets(10)); // Définition des marges intérieures
        root.getChildren().addAll(gridPane, nextStepButton); // Ajout de la grille et du bouton au conteneur
        Scene scene = new Scene(root, 300, 300); // Création de la scène avec le conteneur comme racine
        primaryStage.setScene(scene); // Définition de la scène dans la fenêtre principale
        primaryStage.show(); // Affichage de la fenêtre principale

        // Initialisation de la vue et du contrôleur avec le modèle
        view = new view.FireSimulationView(gridPane, nextStepButton, model); // Création de la vue
        controller = new controller.FireSimulationController(model, view); // Création du contrôleur
    }

    // Méthode principale pour lancer l'application
    public static void main(String[] args) {
        launch(args);
    }
}
