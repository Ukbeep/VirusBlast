package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    @FXML
    private ImageView orb6; // First orb
    @FXML
    private ImageView orb7; // Second orb
    @FXML
    private ImageView orb8; // Third orb
    @FXML
    private ImageView orb9; // Fourth orb
    @FXML
    private ImageView orb10; // Fifth orb

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
    private Button fireButton, resetButton;
    
    private final Map<String, List<String>> virusWeaknesses = Map.of(
    	    "basic", List.of("heat"),
    	    "double_orb", List.of("acid", "heat"),
    	    "triple_orb", List.of("heat", "electric", "metal"),
    	    "partial_immunity", List.of("cold", "bio"),
    	    "tank", List.of("acid", "chemical", "heat", "metal", "electric"),  
    	    "fast", List.of("cold", "electric"),
    	    "boss", List.of("heat", "electric", "cold", "bio", "chemical")
    );
    
    //For label
    private final Map<String, List<String>> virusWeaknessesLabel = Map.of(
    		   "basic", List.of("G"),
    		   "double_orb", List.of("A", "G"),
    		   "triple_orb", List.of("G", "K", ";"),
    		   "partial_immunity", List.of("L", "D"),
    		   "tank", List.of("A", "F", "G", ";", "K"),
    		   "fast", List.of("L", "K"),
    		   "boss", List.of("G", "K", "L", "D", "F")
    		);
    final Map<String, String> virusImages = new HashMap<>();
    private final Map<String, Double> spawnRates = new HashMap<>(); // seconds per spawn
    private final Map<String, Double> fallSpeeds = new HashMap<>(); // speed in pixels per frame
    private final Map<String, double[]> virusSizes = new HashMap<>(); // width and height

    
   
    public void initialize() {
        gamePane.setFocusTraversable(true);
        gamePane.requestFocus();
        setupVirusConfigurations();
        startVirusSpawning();
        disableButtonFocus();
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

            switch (event.getCode()) {
                case A:
                    addOrb("acid");
                    break;
                case S:
                    addOrb("base");
                    break;
                case D:
                    addOrb("bio");
                    break;
                case F:
                    addOrb("chemical");
                    break;
                case G:
                	addOrb("heat"); 
                    break;
                case H:
                    addOrb("crystal");
                    break;
                case J:
                    addOrb("earth");
                    break;
                case K:
                    addOrb("electric");
                    break;
                case L:
                	addOrb("cold");
                    break;
                case SEMICOLON:
                    addOrb("metal");
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

    // Map button text to orb types and add to the stack
    switch (buttonText) {
        case "A":
            addOrb("acid"); // Black Orb
            break;
        case "S":
            addOrb("base"); // White Orb
            break;
        case "D":
            addOrb("bio"); // Green Orb
            break;
        case "F":
            addOrb("chemical"); // Purple Orb
            break;
        case "G":
        	addOrb("heat"); // Red Orb
            break;
        case "H":
            addOrb("crystal"); // Pink Orb
            break;
        case "J":
            addOrb("earth"); // brown Orb
            break;
        case "K":
            addOrb("electric"); // Yellow Orb
            break;
        case "L":
        	addOrb("cold");// Blue Orb
            break;
        case ";":
            addOrb("metal"); // orange Orb
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
        spawnRates.put("double_orb", 6.0);
        spawnRates.put("triple_orb", 12.0);
        spawnRates.put("partial_immunity", 10.0);
        spawnRates.put("boss", 20.0);
        spawnRates.put("fast", 5.0);
        spawnRates.put("tank", 13.0);

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

        // Create weakness label
        Label weaknessLabel = new Label();
        List<String> weaknesses = virusWeaknessesLabel.get(virusType);
        if (weaknesses != null) {
            weaknessLabel.setText(String.join("  ", weaknesses));
            weaknessLabel.setStyle("-fx-font-size: 12px;"); // Default style, can be adjusted
        }

        // Position the label below the virus
        weaknessLabel.setLayoutX(virus.getX());
        weaknessLabel.setLayoutY(virus.getY() + virus.getFitHeight());

        // Add to gamePane
        gamePane.getChildren().addAll(virus, weaknessLabel);

        // Get the falling speed for the virus
        double fallSpeed = fallSpeeds.get(virusType);

        // Animate its falling behavior
        Timeline fallAnimation = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            virus.setY(virus.getY() + fallSpeed); // Falling speed
            weaknessLabel.setLayoutY(weaknessLabel.getLayoutY() + fallSpeed);
            
            if (virus.getY() > gamePane.getHeight()) {
                // Remove when it falls out of bounds
                gamePane.getChildren().removeAll(virus, weaknessLabel);
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

        if (currentOrbs.isEmpty()) {
            System.out.println("No orbs to fire!");
            return;
        }

        // Find the lowest virus that can be destroyed
        ImageView lowestVirus = null;
        double lowestY = -1;
        Label associatedLabel = null;

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
                        
                        // Find the associated label
                        for (Node labelNode : gamePane.getChildren()) {
                            if (labelNode instanceof Label) {
                                Label label = (Label)labelNode;
                                if (Math.abs(label.getLayoutX() - virus.getX()) < 10 && 
                                    Math.abs(label.getLayoutY() - (virus.getY() + virus.getFitHeight())) < 10) {
                                    associatedLabel = label;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // If we found a virus to destroy
        if (lowestVirus != null) {
            addHitAnimation(lowestVirus);
            gamePane.getChildren().remove(lowestVirus);
            if (associatedLabel != null) {
                gamePane.getChildren().remove(associatedLabel);
            }
        }

        // Clear orbs after firing
        currentOrbs.clear();
        updateOrbDisplay();
    }    
    @FXML
    private void resetOrb() {
        currentOrbs.clear();
        updateOrbDisplay();
        
        gamePane.getChildren().removeIf(node -> 
            node instanceof ImageView && isVirusImage((ImageView)node)
        );
        
    }
    
    private boolean isVirusImage(ImageView imageView) {
        String imagePath = imageView.getImage().getUrl();
        return imagePath != null && imagePath.contains("Viruses/");
    }
    
    private String determineVirusType(ImageView virusImage) {
        String imagePath = virusImage.getImage().getUrl();
        for (String virusType : virusTypes) {
            if (imagePath.contains(virusType + "Virus")) {
                return virusType;
            }
        }
        return "basic";
    }
    
    private boolean canDestroyVirus(String virusType, List<String> currentOrbs) {
    	
        List<String> requiredOrbs = virusWeaknesses.get(virusType);
        System.out.println("Virus type: " + virusType + ", required orbs: " + requiredOrbs); // Debug statement
        if (requiredOrbs == null) return false;
        
        // Check if sizes match
        if (currentOrbs.size() != requiredOrbs.size()) {
            return false;
        }
        
        // Check if sequence matches exactly
        for (int i = 0; i < requiredOrbs.size(); i++) {
            if (!currentOrbs.get(i).equals(requiredOrbs.get(i))) {
                return false;
            }
        }
        
        return true;
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
}