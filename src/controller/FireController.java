package controller;

public class FireController {
    private model.FireModel model;
    private view.FireView view;

    public FireController(model.FireModel model, view.FireView view) {
        this.model = model;
        this.view = view;
        init();
    }

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
