# Simulation de propagation de feu de forêt

Application Java (JavaFX) qui simule la propagation d’un feu sur une grille.

## Objectif

Le projet suit une architecture **MVC** :

- **Model** : calcule l’évolution du feu d’une étape à l’autre.
- **View** : affiche la grille et les couleurs des cellules.
- **Controller** : relie le bouton d’action à la logique du modèle.

États des cellules :

- `0` : zone intacte (vert)
- `1` : en feu (rouge)
- `2` : brûlée (gris)

## Structure du projet

- `src/App.java` : point d’entrée JavaFX
- `src/model/FireSimulationModel.java` : logique de simulation + chargement de config
- `src/view/FireSimulationView.java` : rendu de la grille + boîte de fin
- `src/controller/FireSimulationController.java` : gestion des interactions
- `src/resources/config.properties` : paramètres de simulation

## Prérequis

- **JDK 17+** recommandé
- **Maven 3.9+** recommandé
- VS Code + extensions Java (Extension Pack for Java)

## Configuration JavaFX (important)

Le projet fournit un `pom.xml` avec dépendances JavaFX. C’est la méthode recommandée.

### Option recommandée : Maven

Compilation :

```bash
mvn clean compile
```

Exécution :

```bash
mvn javafx:run
```

### Option manuelle : SDK JavaFX local

Si vous avez des erreurs du type `The import javafx cannot be resolved`, vous devez ajouter JavaFX au lancement.

Exemple d’arguments VM :

```bash
--module-path "C:\\javafx-sdk\\lib" --add-modules javafx.controls,javafx.graphics
```

Adaptez le chemin à votre installation locale.

## Lancement dans VS Code

1. Ouvrir le dossier du projet.
2. Vérifier que la version Java sélectionnée est correcte.
3. Si vous lancez sans Maven, configurer les arguments VM JavaFX (Run/Debug).
4. Lancer `App.java`.

## Lancement en ligne de commande (Windows)

Depuis la racine du projet :

```bash
mvn clean compile
mvn javafx:run
```

## Configuration de la simulation

Le fichier `src/resources/config.properties` contient :

- `grid.rows` : nombre de lignes
- `grid.columns` : nombre de colonnes
- `initial.fire.cells` : cellules initialement en feu, format `(ligne;colonne),(ligne;colonne)`
- `fire.spread.probability` : probabilité de propagation entre `0.0` et `1.0`

Exemple :

```properties
grid.rows=9
grid.columns=9
initial.fire.cells=(8;8),(4;0),(5;4),(6;6)
fire.spread.probability=0.5
```

## Fonctionnement

- Au démarrage, la grille est initialisée depuis la configuration.
- Chaque clic sur **Étape suivante** avance la simulation.
- Quand il n’y a plus de cellule en feu, une boîte de dialogue propose **Recommencer** (réinitialiser la grille) ou **Annuler** (fermer l’application).

## Remarques techniques

- Le chargement de `config.properties` est **portable** (classpath + chemins de secours), sans chemin absolu dépendant d’une machine.
- Si un paramètre est invalide, des valeurs par défaut sûres sont appliquées.
