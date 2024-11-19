package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameController {

    @FXML
    private Pane gamePane;

    @FXML
    private ImageView orb1; // First orb
    @FXML
    private ImageView orb2; // Second orb
    @FXML
    private ImageView orb3; // Third orb
    @FXML
    private ImageView orb4; // Fourth orb
    @FXML
    private ImageView orb5; // Fifth orb

    private final Random random = new Random();

    // List of orbs collected by the player
    private final ArrayList<String> currentOrbs = new ArrayList<>();

    // Virus configuration
    private final String[] virusTypes = {
            "basic", "double_orb", "triple_orb", "partial_immunity", "boss", "fast", "tank"
    };

    private final Map<String, String> virusImages = new HashMap<>();
    private final Map<String, Double> spawnRates = new HashMap<>(); // seconds per spawn
    private final Map<String, Double> fallSpeeds = new HashMap<>(); // speed in pixels per frame
    private final Map<String, double[]> virusSizes = new HashMap<>(); // width and height

    public void initialize() {
    	gamePane.setFocusTraversable(true);
    	gamePane.requestFocus();
        setupVirusConfigurations();
        startVirusSpawning();
        System.out.println("GamePane dimensions: " + gamePane.getWidth() + " x " + gamePane.getHeight());

        // Set up key event listener for adding orbs
        gamePane.setOnKeyPressed(this::handleKeyPress);
        
    }

    private void setupVirusConfigurations() {
        // Assign images to virus types
        String basePath = "resources/img/Viruses/";
        virusImages.put("basic", getClass().getResource(basePath + "basicVirus.png").toExternalForm());
        virusImages.put("double_orb", getClass().getResource(basePath + "doubleOrbVirus.png").toExternalForm());
        virusImages.put("triple_orb", getClass().getResource(basePath + "tripleOrbVirus.png").toExternalForm());
        virusImages.put("partial_immunity", getClass().getResource(basePath + "partialImmuneVirus.png").toExternalForm());
        virusImages.put("boss", getClass().getResource(basePath + "bossVirus.png").toExternalForm());
        virusImages.put("fast", getClass().getResource(basePath + "fastVirus.png").toExternalForm());
        virusImages.put("tank", getClass().getResource(basePath + "tankVirus.png").toExternalForm());

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

        // Set sizes (width, height)
        virusSizes.put("basic", new double[]{60, 60});
        virusSizes.put("double_orb", new double[]{80, 80});
        virusSizes.put("triple_orb", new double[]{100, 100});
        virusSizes.put("partial_immunity", new double[]{90, 90});
        virusSizes.put("boss", new double[]{350, 350});
        virusSizes.put("fast", new double[]{50, 50});
        virusSizes.put("tank", new double[]{120, 120});
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

        // Get size for the virus type
        double[] size = virusSizes.get(virusType);
        if (size != null) {
            virus.setFitWidth(size[0]);
            virus.setFitHeight(size[1]);
        } else {
            // Default size if not specified
            virus.setFitWidth(80);
            virus.setFitHeight(80);
        }

        virus.setPreserveRatio(true);

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

    // Method to update the orb display based on the currentOrbs stack
    private void updateOrbDisplay() {
        // Hide all orbs initially
        orb1.setVisible(false);
        orb2.setVisible(false);
        orb3.setVisible(false);
        orb4.setVisible(false);
        orb5.setVisible(false);

        // Loop through the currentOrbs stack and show corresponding orbs
        for (int i = 0; i < currentOrbs.size(); i++) {
            String orbElement = currentOrbs.get(i);
            Image orbImage = new Image(getOrbImagePath(orbElement)); // Get the image for the orb

            // Determine which orb to update based on the stack index
            ImageView orbView = null;
            switch (i) {
                case 0: orbView = orb1; break;
                case 1: orbView = orb2; break;
                case 2: orbView = orb3; break;
                case 3: orbView = orb4; break;
                case 4: orbView = orb5; break;
            }

            // Set the orb image and make it visible
            if (orbView != null) {
                orbView.setImage(orbImage);
                orbView.setVisible(true);
            }
        }
    }

    // Method to add an orb to the stack
    private void addOrb(String orbElement) {
        if (currentOrbs.size() < 5) {
            currentOrbs.add(orbElement);
            updateOrbDisplay(); // Update the display
        }
    }

    // Handle key press events for orb selection
    @FXML
    private void handleKeyPress(KeyEvent event) {
    	System.out.println("Key pressed: " + event.getCode());
        if (currentOrbs.size() < 5) {  // Ensure there is space for more orbs
            switch (event.getCode()) {
                case A: // Heat Orb (Red)
                    addOrb("heat");
                    break;
                case S: // Cold Orb (Blue)
                    addOrb("cold");
                    break;
                case D: // Bio Orb (Green)
                    addOrb("bio");
                    break;
                case F: // Electric Orb (Yellow)
                    addOrb("electric");
                    break;
                case G: // Chemical Orb (Purple)
                    addOrb("chemical");
                    break;
                case H: // Base Orb (White)
                    addOrb("base");
                    break;
                case J: // Acid Orb (Black)
                    addOrb("acid");
                    break;
                case K: // Metal Orb (Orange)
                    addOrb("metal");
                    break;
                case L: // Crystal Orb (Pink)
                    addOrb("crystal");
                    break;
                case SEMICOLON: // Earth Orb (Brown)
                    addOrb("earth");
                    break;
                default:
                    break;  // Ignore other keys
            }
        }
    }

    // Get the correct image path for each orb element
    private String getOrbImagePath(String orbElement) {
        String basePath = "resources/img/Orbs/"; // Change this to the correct path of your orb images
        switch (orbElement) {
            case "heat": return basePath + "heatOrb.png";
            case "cold": return basePath + "coldOrb.png";
            case "bio": return basePath + "bioOrb.png";
            case "electric": return basePath + "electricOrb.png";
            case "chemical": return basePath + "chemicalOrb.png";
            case "base": return basePath + "baseOrb.png";
            case "acid": return basePath + "acidOrb.png";
            case "metal": return basePath + "metalOrb.png";
            case "crystal": return basePath + "crystalOrb.png";
            case "earth": return basePath + "earthOrb.png";
            default: return basePath + "baseOrb.png";
        }
    }
}
