import javafx.application.Application;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    private model.FireModel model;
    private view.FireView view;
    private controller.FireController controller;

    @Override
    public void start(Stage primaryStage) {
        model = new model.FireModel();
        GridPane gridPane = new GridPane();
        Button nextStepButton = new Button("Étape suivante");
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(gridPane, nextStepButton);
        Scene scene = new Scene(root, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        view = new view.FireView(gridPane, nextStepButton, model);
        controller = new controller.FireController(model, view);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
