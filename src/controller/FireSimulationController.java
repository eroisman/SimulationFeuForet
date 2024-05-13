package controller;

public class FireSimulationController {
    private model.FireSimulationModel model;
    private view.FireSimulationView view;

    public FireSimulationController(model.FireSimulationModel model, view.FireSimulationView view) {
        this.model = model;
        this.view = view;
        init();
    }

    // Initialisation du contrôleur
    private void init() {
        // Mise à jour de la grille au démarrage
        view.updateGrid(model.getGrid());

        view.getNextStepButton().setOnAction(event -> {
            model.advanceFire();
            view.updateGrid(model.getGrid());
            if (!model.hasFire()) {
                view.showEndAlert(); // Affiche l'alerte lorsque le feu s'éteint
            }
        });
    }
}
