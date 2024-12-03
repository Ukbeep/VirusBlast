package application;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameController {
	
    @FXML
    private Pane gamePane;

    @FXML
    private ImageView orb1, orb2, orb3, orb4, orb5, orb6,
    				  orb7, orb8, orb9, orb10;

    @FXML
    private ImageView orb11, orb22, orb33, orb44, orb55, orb66,
	  orb77, orb88, orb99, orb100;
    
    // Buttons for orb selection
    @FXML
    private Button aButton, sButton, dButton, fButton, gButton, 
                   hButton, jButton, kButton, lButton, semicolonButton;

    private final Random random = new Random();

    // List of orbs collected by the player
    private final ArrayList<String> currentOrbs = new ArrayList<>();

    // Virus configuration
    private final String[] virusTypes = {
            "basic", "double_orb", "triple_orb", "partial_immunity", "boss", "fast", "tank"
    };
    
    @FXML
    private Rectangle healthBar;
  	
    private final Map<String, Integer> virusDamageValues = Map.of(
            "basic", 4,
            "double_orb", 6,	
            "triple_orb", 10,
            "partial_immunity", 15,
            "tank", 25,
            "boss", 40
        );
    
    @FXML
    private Button fireButton, resetButton;
    
    private final Map<ImageView, Label> virusLabelMap = new HashMap<>();
    
    private final Map<String, List<String>> virusWeaknesses = Map.of(
    	    "basic", List.of("heat"),
    	    "double_orb", List.of("acid", "heat"),
    	    "triple_orb", List.of("heat", "electric", "metal"),
    	    "partial_immunity", List.of("cold", "bio", "cold", "bio"),
    	    "tank", List.of("acid", "chemical", "heat", "metal", "electric", "base"),  
    	    "fast", List.of("cold", "electric"),
    	    "boss", List.of("heat", "electric", "cold", "bio", "chemical", "base", "acid", "heat", "heat", "electric")
    );
    
    //For label
    private final Map<String, List<String>> virusWeaknessesLabel = Map.of(
    		   "basic", List.of("G"),
    		   "double_orb", List.of("A", "G"),
    		   "triple_orb", List.of("G", "K", ";"),
    		   "partial_immunity", List.of("L", "D", "L", "D"),
    		   "tank", List.of("A", "F", "G", ";", "K", "S"),
    		   "fast", List.of("L", "K"),
    		   "boss", List.of("G", "K", "L", "D", "F", "S", "A", "G", "G", "K")
    		);
    
    private Timeline virusSpawnTimeline; // Store the timeline for virus spawning
    private List<Timeline> activeTimelines = new ArrayList<>(); // Store active timelines if needed
    final Map<String, String> virusImages = new HashMap<>();
    private final Map<String, Double> spawnRates = new HashMap<>(); // seconds per spawn
    private final Map<String, Double> fallSpeeds = new HashMap<>(); // speed in pixels per frame
    private final Map<String, double[]> virusSizes = new HashMap<>(); // width and height
    private final Map<String, Double> initialSpawnRates = new HashMap<>();
    private final Map<String, Double> maxSpawnRates = new HashMap<>();
    private final Map<String, Double> initialFallSpeeds = new HashMap<>();
    private final Map<String, Double> maxFallSpeeds = new HashMap<>();
    private Timeline timeTracker;
    
    @FXML
    private Label scoreLabel;
    private int currentScore = 0;
    private int highestComboStreak = 0; // Track highest combo
    private int currentCombo = 0; // Track current combo
    private int totalActions = 0; // Total attempts to fire orbs
    private int successfulActions = 0; // Successful virus destructions
    private long startTime; // Store start time
    private long endTime; // Store end time
    private long totalPlayTime = 0;
    private AudioClip virusDestroySound;
    private AudioClip orbClickSound;
    private MediaPlayer backgroundMusicPlayer;
    
    // Define point values for different virus types
    private final Map<String, Integer> virusPointValues = Map.of(
        "basic", 10,
        "fast", 20,
        "double_orb", 30,
        "partial_immunity", 40,
        "triple_orb", 50,
        "tank", 60,
        "boss", 100
    );
   
    private int playerHealth = 100; // Player's health
    private boolean isGameOver = false; // Track if the game is over

    public void initialize() {
    	 gamePane.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); 
    	setupBackgroundMusic();
    	try {
            virusDestroySound = new AudioClip(getClass().getResource("resources/audio/hitAudio.mp3").toExternalForm());
            virusDestroySound.setVolume(0.8);
            orbClickSound = new AudioClip(getClass().getResource("resources/audio/orbClickAudio.wav").toExternalForm());
            orbClickSound.setVolume(0.8);
        } catch (Exception e) {
            System.err.println("Error loading virus destroy sound: " + e.getMessage());
        }
    	// Start the game timer immediately
    	//displayImages();
    	GameStatistics.getInstance().reset();
        GameStatistics.getInstance().startTimer();
        // Create a Timeline to periodically update total play time
        Timeline timeUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!isGameOver) {
                // This will ensure the time is continuously updated
                long currentPlayTime = GameStatistics.getInstance().getTimePlayed();
                System.out.println("Current Play Time: " + currentPlayTime + " seconds");
            }
        }));
        timeUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        timeUpdateTimeline.play();
    	
    	setupTimeTracking();
        timeTracker.setCycleCount(Timeline.INDEFINITE);
        timeTracker.play();
        gamePane.setFocusTraversable(true);
        gamePane.requestFocus();
        setupVirusConfigurations();
        startVirusSpawning();
        disableButtonFocus();
        updateHealthBar();
        Platform.runLater(() -> gamePane.requestFocus());
        // Attach event handlers to the buttons
        aButton.setOnAction(event -> handleOrbClick(event));
        sButton.setOnAction(event -> handleOrbClick(event));
        dButton.setOnAction(event -> handleOrbClick(event));
        fButton.setOnAction(event -> handleOrbClick(event));
        gButton.setOnAction(event -> handleOrbClick(event));
        hButton.setOnAction(event -> handleOrbClick(event));
        jButton.setOnAction(event -> handleOrbClick(event));
        kButton.setOnAction(event -> handleOrbClick(event));
        lButton.setOnAction(event -> handleOrbClick(event));
        semicolonButton.setOnAction(event -> handleOrbClick(event));
        fireButton.setOnAction(event -> fireOrbs());
        resetButton.setOnAction(event -> resetOrb());

        gamePane.focusedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Game pane focused: " + newVal);
        });
        // Add key event handler for keyboard input
        gamePane.setOnKeyPressed(event -> {
            // Consume the event to prevent default button focus
            event.consume();
            ImageView orbToAnimate = null;
            
            switch (event.getCode()) {
                case A:
                    addOrb("acid");
                    orbToAnimate = orb11;
                    break;
                case S:
                    addOrb("base");
                    orbToAnimate = orb22;
                    break;
                case D:
                    addOrb("bio");
                    orbToAnimate = orb33;
                    break;
                case F:
                    addOrb("chemical");
                    orbToAnimate = orb44;
                    break;
                case G:
                	addOrb("heat"); 
                	orbToAnimate = orb55;
                    break;
                case H:
                    addOrb("crystal");
                    orbToAnimate = orb66;
                    break;
                case J:
                    addOrb("earth");
                    orbToAnimate = orb77;
                    break;
                case K:
                    addOrb("electric");
                    orbToAnimate = orb88;
                    break;
                case L:
                	addOrb("cold");
                	orbToAnimate = orb99;
                    break;
                case SEMICOLON:
                    addOrb("metal");
                    orbToAnimate = orb100;
                    break;
                case SPACE:
                    fireOrbs();
                    break;
                case CONTROL:
                    resetOrb();
                    break;
                default:
                    break;
            }
            // Animate the orb if one was found
            if (orbToAnimate != null) {
                animateOrb(orbToAnimate);
                if (orbClickSound != null) {
                    orbClickSound.play(); // Play the orb click sound
                }
            }
        });

        // Ensure game pane always has focus when clicked
        gamePane.setOnMouseClicked(event -> gamePane.requestFocus());
    }
    
    private void disableButtonFocus() {
        aButton.setFocusTraversable(false);
        sButton.setFocusTraversable(false);
        dButton.setFocusTraversable(false);
        fButton.setFocusTraversable(false);
        gButton.setFocusTraversable(false);
        hButton.setFocusTraversable(false);
        jButton.setFocusTraversable(false);
        kButton.setFocusTraversable(false);
        lButton.setFocusTraversable(false);
        semicolonButton.setFocusTraversable(false);
        fireButton.setFocusTraversable(false);
        resetButton.setFocusTraversable(false);
    }

@FXML
private void handleOrbClick(ActionEvent event) {
    // Get the button that was clicked
    Button clickedButton = (Button) event.getSource();
    String buttonText = clickedButton.getText();
    ImageView orbToAnimate = null;
    // Map button text to orb types and add to the stack
    switch (buttonText) {
        case "A":
            addOrb("acid"); // Black Orb
            orbToAnimate = orb11;
            break;
        case "S":
            addOrb("base"); // White Orb
            orbToAnimate = orb22;
            break;
        case "D":
            addOrb("bio"); // Green Orb
            orbToAnimate = orb33;
            break;
        case "F":
            addOrb("chemical"); // Purple Orb
            orbToAnimate = orb44;
            break;
        case "G":
        	addOrb("heat"); // Red Orb
        	orbToAnimate = orb55;
            break;
        case "H":
            addOrb("crystal"); // Pink Orb
            orbToAnimate = orb66;
            break;
        case "J":
            addOrb("earth"); // brown Orb
            orbToAnimate = orb77;
            break;
        case "K":
            addOrb("electric"); // Yellow Orb
            orbToAnimate = orb88;
            break;
        case "L":
        	addOrb("cold");// Blue Orb
        	orbToAnimate = orb99;
            break;
        case ";":
            addOrb("metal"); // orange Orb
            orbToAnimate = orb100;
            break;   	
        case "SPACE":
        	fireOrbs();
        	break;
        case "CONTROL":
        	resetOrb();
        	break;
        default:
            System.out.println("Invalid button pressed.");    
    }
    if (orbToAnimate != null) {
        animateOrb(orbToAnimate);
        if (orbClickSound != null) {
            orbClickSound.play(); // Play the orb click sound
        }
    }
}

    private void setupVirusConfigurations() {
        // Assign images to virus types
        String basePath = "resources/img/Viruses/";
        virusImages.put("basic", getClass().getResource(basePath + "basicVirus.png").toExternalForm());
        virusImages.put("double_orb", getClass().getResource(basePath + "double_orb.png").toExternalForm());
        virusImages.put("triple_orb", getClass().getResource(basePath + "triple_orb.png").toExternalForm());
        virusImages.put("partial_immunity", getClass().getResource(basePath + "partial_immunity.png").toExternalForm());
        virusImages.put("boss", getClass().getResource(basePath + "bossVirus.png").toExternalForm());
        virusImages.put("fast", getClass().getResource(basePath + "fastVirus.png").toExternalForm());
        virusImages.put("tank", getClass().getResource(basePath + "tankVirus.png").toExternalForm());

        spawnRates.clear();
        fallSpeeds.clear();
        initialSpawnRates.clear();
        maxSpawnRates.clear();
        initialFallSpeeds.clear();
        maxFallSpeeds.clear();

        // Assign base spawn rates with initial and max values
        setupVirusSpawnRate("basic", 4.0, 2.0, 0.5);
        setupVirusSpawnRate("double_orb", 9.0, 5.0, 3.5);
        setupVirusSpawnRate("triple_orb", 15.0, 7.0, 4.0);
        setupVirusSpawnRate("partial_immunity", 14.0, 9.0, 4.6);
        setupVirusSpawnRate("boss", 40.0, 25.0, 15.0);
        setupVirusSpawnRate("fast", 7.0, 4.2, 1.0);
        setupVirusSpawnRate("tank", 13.0, 9.1, 5.0);

        // Assign base fall speeds with initial and max values
        //String virusType, double initial, double midPoint, double maximum
        setupVirusFallSpeed("basic", 2.0, 4.0, 5.0);
        setupVirusFallSpeed("double_orb", 1.5, 3.0, 4.0);
        setupVirusFallSpeed("triple_orb", 1.2, 2.4, 3.5);
        setupVirusFallSpeed("partial_immunity", 1.0, 2.0, 3.0);
        setupVirusFallSpeed("boss", 0.2, 0.9, 1.8);
        setupVirusFallSpeed("fast", 3.0, 5.0, 6.0);
        setupVirusFallSpeed("tank", 0.5, 1.0, 2.0);

        // Set sizes (width, height)
        virusSizes.put("basic", new double[]{60, 60});
        virusSizes.put("double_orb", new double[]{80, 80});
        virusSizes.put("triple_orb", new double[]{100, 100});
        virusSizes.put("partial_immunity", new double[]{90, 90});
        virusSizes.put("boss", new double[]{350, 350});
        virusSizes.put("fast", new double[]{50, 50});
        virusSizes.put("tank", new double[]{120, 120});
    }
    
    private void setupVirusSpawnRate(String virusType, double initial, double midPoint, double minimum) {
        initialSpawnRates.put(virusType, initial);
        maxSpawnRates.put(virusType, minimum);
        spawnRates.put(virusType, initial);
    }
    
    private void setupVirusFallSpeed(String virusType, double initial, double midPoint, double maximum) {
        initialFallSpeeds.put(virusType, initial);
        maxFallSpeeds.put(virusType, maximum);
        fallSpeeds.put(virusType, initial);
    }
    
    private void adjustDifficulty() {
        // Linear difficulty progression
        double difficultyFactor = 1.0;

        if (currentScore >= 1000) {
            // Highest difficulty level
            difficultyFactor = 2.0;
        } else if (currentScore >= 500) {
            // Medium difficulty level
            difficultyFactor = 1.5;
        } else if (currentScore >= 100) {
            // Initial difficulty increase
            difficultyFactor = 1.25;
        }
        
        for (String virusType : virusTypes) {
            // Adjust spawn rates
            double initialRate = initialSpawnRates.get(virusType);
            double minRate = maxSpawnRates.get(virusType);
            double adjustedRate = Math.max(
                minRate, 
                initialRate / difficultyFactor
            );
            spawnRates.put(virusType, adjustedRate);

            // Adjust fall speeds
            double initialSpeed = initialFallSpeeds.get(virusType);
            double maxSpeed = maxFallSpeeds.get(virusType);
            double adjustedSpeed = Math.min(
                maxSpeed, 
                initialSpeed * difficultyFactor
            );
            fallSpeeds.put(virusType, adjustedSpeed);
        }

        // Optional: Introduce more boss-level challenges at 1000 points
        if (currentScore >= 1000) {
            // Increase boss virus spawn rate and decrease spawn interval
            spawnRates.put("boss", spawnRates.get("boss") / 2.0);
        }
    }


    private void startVirusSpawning() {
        for (String virusType : virusTypes) {
            double spawnRate = spawnRates.get(virusType);
            Timeline spawnTimeline = new Timeline(new KeyFrame(Duration.seconds(spawnRate), event -> {
                // Add score-based condition for spawning specific viruses
                switch (virusType) {
                    case "boss":
                        if (currentScore >= 1000) {
                            createFallingVirus(virusType);
                        }
                        break;
                    case "tank":
                        if (currentScore >= 600) {
                            createFallingVirus(virusType);
                        }
                        break;
                    case "partial_immunity":
                        if (currentScore >= 200) {
                            createFallingVirus(virusType);
                        }
                        break;
                    default:
                        // Other viruses spawn normally without score restrictions
                        createFallingVirus(virusType);
                        break;
                }
            }));
            spawnTimeline.setCycleCount(Timeline.INDEFINITE);
            spawnTimeline.play();
            activeTimelines.add(spawnTimeline);
        }
    }

    private void createFallingVirus(String virusType) {
        // Load the virus image
        String imagePath = virusImages.get(virusType);
        Image virusImage = new Image(imagePath);

        // Create the virus entity
        ImageView virus = new ImageView(virusImage);
        double[] size = virusSizes.get(virusType);
        if (size != null) {
            virus.setFitWidth(size[0]);
            virus.setFitHeight(size[1]);
        } else {
            // Default size if not found in the map
            virus.setFitWidth(80);
            virus.setFitHeight(80);
        }
        virus.setPreserveRatio(true);

        // Randomize its initial position
        virus.setX(random.nextDouble() * (gamePane.getWidth() - virus.getFitWidth()));
        virus.setY(-virus.getFitHeight()); // Start just outside the screen

        // Create weakness label
        Label weaknessLabel = new Label();
        List<String> weaknesses = virusWeaknessesLabel.get(virusType);
        if (weaknesses != null) {
            weaknessLabel.setText(String.join("  ", weaknesses));
            weaknessLabel.setId("weaknessLabel");
        }

        // Add the virus and label to the gamePane
        gamePane.getChildren().addAll(virus, weaknessLabel);

        // Store the mapping of virus and its label
        virusLabelMap.put(virus, weaknessLabel);

        // Center the label after adding it to the Pane
        updateLabelPosition(weaknessLabel, virus);

        // Get the falling speed for the virus
        double fallSpeed = fallSpeeds.get(virusType);

        // Use a flag to track if the virus is still active
        final boolean[] isActive = {true};

        // Animate its falling behavior
        Timeline fallAnimation = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            if (isActive[0] && gamePane.getChildren().contains(virus)) { // Check if virus exists
                virus.setY(virus.getY() + fallSpeed); // Falling speed
                weaknessLabel.setLayoutY(weaknessLabel.getLayoutY() + fallSpeed);

                // Update the position of the label to keep it centered
                updateLabelPosition(weaknessLabel, virus);
                
                // Check if the virus has reached the bottom
                if (virus.getY() > gamePane.getHeight()) {
                    if (isActive[0]) {
                        triggerHitEffect(virus, virusType); // Reduce health
                    }
                    isActive[0] = false; // Set to inactive
                    // Remove the virus and its label when it falls out of bounds
                    gamePane.getChildren().removeAll(virus, weaknessLabel);
                    virusLabelMap.remove(virus); // Remove from the map
                }
            }
        }));
        fallAnimation.setCycleCount(Timeline.INDEFINITE);
        fallAnimation.play();
    }

    // Helper method to update the label position
    private void updateLabelPosition(Label weaknessLabel, ImageView virus) {
        weaknessLabel.setLayoutX(virus.getX() + (virus.getFitWidth() - weaknessLabel.getLayoutBounds().getWidth()) / 2);
        weaknessLabel.setLayoutY(virus.getY() + virus.getFitHeight() + 5); // Adjust Y position as needed
    }

    // Method to update the orb display based on the currentOrbs stack
    private void updateOrbDisplay() {
        // Hide all orbs initially
        orb1.setVisible(false);
        orb2.setVisible(false);
        orb3.setVisible(false);
        orb4.setVisible(false);
        orb5.setVisible(false);
        orb6.setVisible(false);
        orb7.setVisible(false);
        orb8.setVisible(false);
        orb9.setVisible(false);
        orb10.setVisible(false);

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
                case 5: orbView = orb6; break;
                case 6: orbView = orb7; break;
                case 7: orbView = orb8; break;
                case 8: orbView = orb9; break;
                case 9: orbView = orb10; break;
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
        if (currentOrbs.size() < 10) {
            currentOrbs.add(orbElement);
            updateOrbDisplay(); // Update the display
        }
    }

    // Get the correct image path for each orb element
    private String getOrbImagePath(String orbElement) {
        String basePath = "resources/img/Orbs/"; // Change this to the correct path of your orb images
        switch (orbElement) {
            case "heat": return getClass().getResource(basePath + "heatOrb.png").toExternalForm();
            case "cold": return getClass().getResource(basePath + "coldOrb.png").toExternalForm();
            case "bio": return getClass().getResource(basePath + "bioOrb.png").toExternalForm();
            case "electric": return getClass().getResource(basePath + "electricOrb.png").toExternalForm();
            case "chemical": return getClass().getResource(basePath + "chemicalOrb.png").toExternalForm();
            case "base": return getClass().getResource(basePath + "baseOrb.png").toExternalForm();
            case "acid": return getClass().getResource(basePath + "acidOrb.png").toExternalForm();
            case "metal": return getClass().getResource(basePath + "metalOrb.png").toExternalForm();
            case "crystal": return getClass().getResource(basePath + "crystalOrb.png").toExternalForm();
            case "earth": return getClass().getResource(basePath + "earthOrb.png").toExternalForm();
            default: return getClass().getResource(basePath + "baseOrb.png").toExternalForm();
        }
    }
    
    @FXML
    private void fireOrbs() {
        System.out.println("FireOrbs method called. Current orbs: " + currentOrbs);
        System.out.println("Time Played: " + GameStatistics.getInstance().getTimePlayed() + " seconds");
        totalActions++;
        if (currentOrbs.isEmpty()) {
            System.out.println("No orbs to fire!");
            breakComboStreak();
            return;
        }

        // Find the lowest virus that can be destroyed
        ImageView lowestVirus = null;
        double lowestY = -1;
        Label associatedLabel = null;
        String destroyedVirusType = null;

        // Get all nodes in the game pane
        for (Node node : new ArrayList<>(gamePane.getChildren())) {
            if (node instanceof ImageView && isVirusImage((ImageView)node)) {
                ImageView virus = (ImageView)node;
                String virusType = determineVirusType(virus);
                
                // Only attempt to destroy if we have the exact combination
                if (canDestroyVirus(virusType, currentOrbs)) {
                    double virusY = virus.getY();
                    if (virusY > lowestY) {
                        lowestY = virusY;
                        lowestVirus = virus;
                        destroyedVirusType = virusType;
                        
                        // Find the associated label using the mapping
                        associatedLabel = virusLabelMap.get(virus);
                    }
                }
            }
        }

        // If we found a virus to destroy
        if (lowestVirus != null) {
            if (virusDestroySound != null) {
                virusDestroySound.play();
            }
        	successfulActions++;
        	incrementComboStreak();
            // Add points based on virus type
            int pointsEarned = virusPointValues.getOrDefault(destroyedVirusType, 10);
            currentScore += pointsEarned;
            updateScoreLabel();
            System.out.println("Total viruses defeated after increment: " + GameStatistics.getInstance().getTotalVirusesDefeated());
            GameStatistics.getInstance().incrementTotalVirusesDefeated();
            addHitAnimation(lowestVirus);
            gamePane.getChildren().remove(lowestVirus);
            
            // Remove the associated label if it exists
            if (associatedLabel != null) {
                gamePane.getChildren().remove(associatedLabel);
                // Also remove from the mapping
                virusLabelMap.remove(lowestVirus);
            }
        } else {
            // Failed to destroy a virus - break the combo streak
            breakComboStreak();
            
            Label missLabel = new Label("Miss");
            missLabel.setStyle("-fx-text-fill: red; -fx-font-size: 24px;");
            missLabel.setLayoutX(gamePane.getWidth() / 2 - 50); // Center horizontally
            missLabel.setLayoutY(gamePane.getHeight() / 2 - 50); // Center vertically
            
            gamePane.getChildren().add(missLabel);
            
            // Remove the label after 1 second
            Timeline removeLabel = new Timeline(new KeyFrame(
                Duration.seconds(0.5),
                event -> gamePane.getChildren().remove(missLabel)
            ));
            removeLabel.play();
        }

        // Clear orbs after firing
        currentOrbs.clear();
        updateOrbDisplay();
        updateAccuracy();
        updateComboStatistics();
    }
    
    private void updateScoreLabel() {
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(currentScore));
            adjustDifficulty();
        }
    }
    @FXML
    private void resetOrb() {
        currentOrbs.clear();
        updateOrbDisplay();
    }
    
    private boolean isVirusImage(ImageView imageView) {
        String imagePath = imageView.getImage().getUrl();
        return imagePath != null && imagePath.contains("Viruses/");
    }
    
    private String determineVirusType(ImageView virusImage) {
        String imagePath = virusImage.getImage().getUrl().toLowerCase(); // Make case-insensitive
        
        // Map of filename patterns to virus types
        Map<String, String> virusPatterns = Map.of(
            "basicvirus", "basic",
            "double_orb", "double_orb",
            "triple_orb", "triple_orb",
            "partial_immunity", "partial_immunity",
            "bossvirus", "boss",
            "fastvirus", "fast",
            "tankvirus", "tank"
        );
        
        // Check each pattern
        for (Map.Entry<String, String> entry : virusPatterns.entrySet()) {
            if (imagePath.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        
        // Log unrecognized virus type for debugging
        System.out.println("Unrecognized virus image path: " + imagePath);
        return "basic"; // Default fallback
    }
    
    private boolean canDestroyVirus(String virusType, List<String> currentOrbs) {
        List<String> requiredOrbs = new ArrayList<>(virusWeaknesses.get(virusType));
        
        if (requiredOrbs == null || currentOrbs.size() != requiredOrbs.size()) {
            return false;
        }

        // Convert orb names to their corresponding weakness keys
        List<String> currentOrbWeaknesses = new ArrayList<>();
        for (String orb : currentOrbs) {
            currentOrbWeaknesses.add(orb);
        }

        // Sort both lists to compare contents regardless of order
        Collections.sort(requiredOrbs);
        Collections.sort(currentOrbWeaknesses);

        // Compare the sorted lists
        return requiredOrbs.equals(currentOrbWeaknesses);
    }


    // Helper method to add hit animation (unchanged)
    private void addHitAnimation(ImageView virusImage) {
        // Load the hit image
        Image hitImage = new Image(getClass().getResource("/application/resources/img/Animations/hit.png").toExternalForm());
        ImageView hitAnimation = new ImageView(hitImage);

        // Position the hit animation the same as the virus image
        hitAnimation.setX(virusImage.getX());
        hitAnimation.setY(virusImage.getY());
        hitAnimation.setFitWidth(virusImage.getFitWidth());
        hitAnimation.setFitHeight(virusImage.getFitHeight());

        // Add the hit animation to the game pane
        gamePane.getChildren().add(hitAnimation);

        // Remove the hit animation after a short duration
        Timeline removeAnimation = new Timeline(new KeyFrame(
            Duration.seconds(0.5),
            event -> gamePane.getChildren().remove(hitAnimation)
        ));
        removeAnimation.play();
    }
    
    private void updateHealthBar() {
        double maxHealth = 100; // Assuming the maximum health is 100
        double maxWidth = 1282; // Assuming the maximum width of the health bar
        healthBar.setWidth((playerHealth / maxHealth) * maxWidth); // Update width based on current health
    }
    
    private void triggerHitEffect(ImageView virus, String virusType) {
        // Reduce health based on the virus type
        int damage = virusDamageValues.getOrDefault(virusType, 0); // Default to 0 if not found
        playerHealth = Math.max(0, playerHealth - damage); // Ensure health doesn't go below 0
        updateHealthBar();
        if (playerHealth <= 0) {
        	isGameOver = true;
            displayPostGameSummary();
            stopGame();// Call the summary display if health is zero
        }
        // Debug output
        System.out.println("Player Health: " + playerHealth);
        System.out.println("Health Bar Width: " + healthBar.getWidth());

        // Play hit animation
        Image hitImage = new Image(getClass().getResource("/application/resources/img/Animations/BloodOverlay.png").toExternalForm());
        ImageView hitAnimation = new ImageView(hitImage);
        if (hitImage == null || hitImage.isError()) {
            System.out.println("Error loading image: " + hitImage.getException());
        } else {
            System.out.println("Image loaded successfully.");
        }
        
        for (Node node : new ArrayList<>(gamePane.getChildren())) {
            if (node instanceof ImageView && node == virus) {
                // Find and remove associated weakness label
                for (Node labelNode : gamePane.getChildren()) {
                    if (labelNode instanceof Label && labelNode.getLayoutX() == virus.getX()) {
                        gamePane.getChildren().remove(labelNode);
                        break;
                    }
                }
                // Remove the virus
                gamePane.getChildren().remove(node);
                break;
            }
        }
        hitAnimation.setX(0);
        hitAnimation.setY(0);
        hitAnimation.setFitWidth(gamePane.getWidth());
        hitAnimation.setFitHeight(gamePane.getHeight());
        gamePane.getChildren().add(hitAnimation);
        hitAnimation.toFront();
        hitAnimation.setOpacity(0.5);
        
        Timeline removeAnimation = new Timeline(new KeyFrame(
            Duration.seconds(0.5),
            event -> gamePane.getChildren().remove(hitAnimation)
        ));
        removeAnimation.play();
    }
    
    private void animateOrb(ImageView orb) {
        // Check if the orb is already animating
        if (orb.getUserData() != null && (Boolean) orb.getUserData()) {
            return; // Prevent overlapping animations
        }

        // Store the original position
        double originalY = orb.getY();
        
        // Set user data to indicate that the orb is animating
        orb.setUserData(true);

        // Create a TranslateTransition to move the orb up
        TranslateTransition translateUp = new TranslateTransition(Duration.millis(200), orb);
        translateUp.setByY(-15); // Move up by 50 pixels

        // Create a RotateTransition to rotate the orb
        RotateTransition rotate = new RotateTransition(Duration.millis(200), orb);
        rotate.setFromAngle(0);
        rotate.setToAngle(360); // Rotate 360 degrees

        // Create a TranslateTransition to move the orb back down
        TranslateTransition translateDown = new TranslateTransition(Duration.millis(200), orb);
        translateDown.setByY(15); // Move down by 50 pixels

        // Create a SequentialTransition to play the animations one after the other
        SequentialTransition animation = new SequentialTransition(translateUp, rotate, translateDown);

        // Set onFinished to reset the Y position and user data
        animation.setOnFinished(event -> {
            orb.setY(originalY); // Reset to original position
            orb.setUserData(false); // Reset user data to indicate that the animation is done
        });

        // Play the animation
        animation.play();
    }
    
    private void displayPostGameSummary() {
        try {
            // Prepare current game results
            int totalVirusesDefeated = GameStatistics.getInstance().getTotalVirusesDefeated();
            int finalScore = currentScore; // Current score from this game
            int highestComboStreak = GameStatistics.getInstance().getHighestComboStreak();
            double accuracyPercentage = GameStatistics.getInstance().getAccuracyPercentage();
            long timePlayed = GameStatistics.getInstance().getTimePlayed();
            // Create FXMLLoader for the SummaryView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SummaryView.fxml"));
            Parent summaryView = loader.load();
            SummaryController summaryController = loader.getController();

            // Pass the current game results to the SummaryController
            summaryController.displaySummary(totalVirusesDefeated, finalScore, highestComboStreak, accuracyPercentage, timePlayed);
            summaryController.setGameController(this);
            
            // Show Summary View
            Stage summaryStage = new Stage();
          
            summaryStage.setScene(new Scene(summaryView));
            summaryStage.setTitle("Game Over");
            summaryStage.show();
            summaryController.setGameStage((Stage) gamePane.getScene().getWindow()); // Pass the existing game stage
            summaryController.setStage(summaryStage);
        } catch (IOException e) {
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }
    
    public void resetGame() {
        // Reset game statistics
        currentScore = 0;
        playerHealth = 100;
        currentOrbs.clear();
        currentCombo = 0; // Reset current combo
        highestComboStreak = 0; // Explicitly reset highest combo streak
        isGameOver = false;
        totalActions = 0; // Reset total actions
        successfulActions = 0; // Reset successful actions

        // Update UI elements
        updateOrbDisplay();
        updateHealthBar();

        // Stop and clear existing time tracker
        if (timeTracker != null) {
            timeTracker.stop();
            timeTracker = null;
        }
        
        // Restart background music if it was stopped
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.play();
        }

        // Reset GameStatistics completely
        GameStatistics gameStats = GameStatistics.getInstance();
        gameStats.reset(); // This should reset all internal timers
        gameStats.startTimer(); // Start a fresh timer

        // Restart time tracking
        setupTimeTracking();
        
        // Reset total play time and score label
        totalPlayTime = 0;
        scoreLabel.setText(String.valueOf(currentScore));
        
        // Restart virus spawning
        startVirusSpawning();

        // Ensure game pane has focus
        Platform.runLater(() -> {
            gamePane.requestFocus();
        });
    }
    
    
    private void stopGame() {
        // Set the game over flag
        isGameOver = true;
        if (timeTracker != null) {
            timeTracker.stop();
        }
        
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }

        // Stop the timer in GameStatistics
        GameStatistics.getInstance().stopTimer();
        // Stop all ongoing virus spawning
        for (Timeline timeline : activeTimelines) {
            timeline.stop();
        }
        // Remove all active viruses from the game pane
        for (Node node : new ArrayList<>(gamePane.getChildren())) {
            if (node instanceof ImageView && isVirusImage((ImageView)node)) {
                // Remove the virus
                gamePane.getChildren().remove(node);
                // Also remove its associated label
                Label associatedLabel = virusLabelMap.get(node);
                if (associatedLabel != null) {
                    gamePane.getChildren().remove(associatedLabel);
                    virusLabelMap.remove(node); // Remove from the mapping
                }
            }
        }

        // Disable all buttons to prevent further interactions
        disableButtonFocus(); // This disables all buttons that allow user interaction

        System.out.println("Game has been stopped. All viruses removed.");
    }
    
    private void updateCombo() {
        currentCombo++;
        if (currentCombo > highestComboStreak) {
            highestComboStreak = currentCombo;
            GameStatistics.getInstance().setHighestComboStreak(highestComboStreak);
        }
    }
    
    private void incrementComboStreak() {
        currentCombo++;
        
        // Update highest combo streak
        if (currentCombo > highestComboStreak) {
            highestComboStreak = currentCombo;
            
            // Update in GameStatistics
            GameStatistics.getInstance().setHighestComboStreak(highestComboStreak);
        }
        
        System.out.println("Combo Streak Increased: " + currentCombo);
    }
    
    private void breakComboStreak() {
        if (currentCombo > 0) {
            System.out.println("Combo Streak Broken! Previous Streak: " + currentCombo);
        }
        
        // Reset combo streak to zero
        currentCombo = 0;
    }
    
    private void updateComboStatistics() {
        // Update combo-related statistics in GameStatistics
        GameStatistics.getInstance().setHighestComboStreak(highestComboStreak);
    }
    
    private void updateAccuracy() {
        double accuracy = (double) successfulActions / totalActions * 100;
        GameStatistics.getInstance().setAccuracyPercentage(accuracy);
    }
    
    private void setupTimeTracking() {
        // Stop any existing time tracker
        if (timeTracker != null) {
            timeTracker.stop();
        }
        
        GameStatistics.getInstance().resetTimer();
        
        // Create a new time tracker
        timeTracker = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!isGameOver) {
                long currentPlayTime = GameStatistics.getInstance().getTimePlayed();
                System.out.println("Current Play Time: " + currentPlayTime + " seconds");
            }
        }));
        timeTracker.setCycleCount(Timeline.INDEFINITE);
        timeTracker.play();

        // Ensure the timer in GameStatistics is started
        GameStatistics.getInstance().startTimer();
    }
    
    private void setupBackgroundMusic() {
        try {
            Media backgroundMusic = new Media(getClass().getResource("resources/audio/backgroundMusic.mp3").toExternalForm());
            backgroundMusicPlayer = new MediaPlayer(backgroundMusic);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely
            backgroundMusicPlayer.setVolume(0.5); // Adjust volume as needed
            backgroundMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }
    
    public void displayImages(){
    	    // Load images for all ImageView controls
    	    orb11.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/acidOrb.png").toExternalForm()));
    	    orb22.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/baseOrb.png").toExternalForm()));
    	    orb33.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/bioOrb.png").toExternalForm()));
    	    orb44.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/chemicalOrb.png").toExternalForm()));
    	    orb55.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/heatOrb.png").toExternalForm()));
    	    orb66.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/crystalOrb.png").toExternalForm()));
    	    orb77.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/earthOrb.png").toExternalForm()));
    	    orb88.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/electricOrb.png").toExternalForm()));
    	    orb99.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/coldOrb.png").toExternalForm()));
    	    orb100.setImage(new Image(getClass().getResource("/application/resources/img/Orbs/metalOrb.png").toExternalForm()));
    }
}
