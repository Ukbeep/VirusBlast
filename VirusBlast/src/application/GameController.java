package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameController {

    @FXML
    private Pane gamePane;

    private final Random random = new Random();

    // Virus configuration
    private final String[] virusTypes = {
            "basic", "double_orb", "triple_orb", "partial_immunity", "boss", "fast", "tank"
    };

    private final Map<String, String> virusImages = new HashMap<>();
    private final Map<String, Double> spawnRates = new HashMap<>(); // seconds per spawn
    private final Map<String, Double> fallSpeeds = new HashMap<>(); // speed in pixels per frame

    public void initialize() {
        setupVirusConfigurations();
        startVirusSpawning();
    }

    private void setupVirusConfigurations() {
        // Assign images to virus types
        virusImages.put("basic", "resources/img/Viruses/basicVirus.png");
        virusImages.put("double_orb", "resources/img/Viruses/doubleOrbVirus.png");
        virusImages.put("triple_orb", "resources/img/Viruses/tripleOrbVirus.png");
        virusImages.put("partial_immunity", "resources/img/Viruses/partialImmuneVirus.png");
        virusImages.put("boss", "resources/img/Viruses/bossVirus.png");
        virusImages.put("fast", "resources/img/Viruses/fastVirus.png");
        virusImages.put("tank", "resources/img/Viruses/tankVirus.png");

        // Adjust spawn rates (in seconds)
        spawnRates.put("basic", 2.0);
        spawnRates.put("double_orb", 4.0);
        spawnRates.put("triple_orb", 6.0);
        spawnRates.put("partial_immunity", 5.0);
        spawnRates.put("boss", 20.0);
        spawnRates.put("fast", 1.0);
        spawnRates.put("tank", 10.0);

        // Adjust falling speeds (in pixels per frame)
        fallSpeeds.put("basic", 2.0);
        fallSpeeds.put("double_orb", 1.5);
        fallSpeeds.put("triple_orb", 1.2);
        fallSpeeds.put("partial_immunity", 1.0);
        fallSpeeds.put("boss", 0.8);
        fallSpeeds.put("fast", 3.0);
        fallSpeeds.put("tank", 0.5);
    }

    private void startVirusSpawning() {
        for (String virusType : virusTypes) {
            double spawnRate = spawnRates.get(virusType);

            Timeline spawnTimeline = new Timeline(new KeyFrame(Duration.seconds(spawnRate), event -> {
                createFallingVirus(virusType);
            }));
            spawnTimeline.setCycleCount(Timeline.INDEFINITE);
            spawnTimeline.play();
        }
    }

    private void createFallingVirus(String virusType) {
        // Load the virus image
        String imagePath = virusImages.get(virusType);
        Image virusImage = new Image(imagePath);

        // Create the virus entity
        ImageView virus = new ImageView(virusImage);
        virus.setFitWidth(50); // Adjust size as needed
        virus.setFitHeight(50);

        // Randomize its initial position
        virus.setX(random.nextDouble() * (gamePane.getWidth() - virus.getFitWidth()));
        virus.setY(-virus.getFitHeight()); // Start just outside the screen

        // Add to gamePane
        gamePane.getChildren().add(virus);

        // Get the falling speed for the virus
        double fallSpeed = fallSpeeds.get(virusType);

        // Animate its falling behavior
        Timeline fallAnimation = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            virus.setY(virus.getY() + fallSpeed); // Falling speed
            if (virus.getY() > gamePane.getHeight()) {
                // Remove when it falls out of bounds
                gamePane.getChildren().remove(virus);
            }
        }));
        fallAnimation.setCycleCount(Timeline.INDEFINITE);
        fallAnimation.play();
    }
}
