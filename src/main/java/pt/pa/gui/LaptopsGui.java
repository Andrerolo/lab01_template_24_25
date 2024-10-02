package pt.pa.gui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pt.pa.model.Laptop;
import pt.pa.model.Review;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author amfs
 */
public class LaptopsGui extends BorderPane {

    private static final String DATA_PATH = "src/main/resources/laptop_reviews.json";

    private static final String BANNER_PATH = "src/main/resources/header_banner.png";

    List<Laptop> laptops;

    ListView<Laptop> listViewLaptops;

    private VBox mainContent;


    public LaptopsGui() throws Exception {
        try {
            this.laptops = loadData();
            initializeComponents();
        } catch (FileNotFoundException e) {
            throw new Exception("Unable to initialize Laptops GUI");
        }
    }

    public void initializeComponents() throws FileNotFoundException {
        // Create a banner at the top of the window
        ImageView banner = loadThumbnailImage();
        banner.setFitHeight(150);
        banner.setFitWidth(800);

        // Create a label for the title
        Label title = new Label("Laptop Reviews");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setAlignment(Pos.CENTER);

        // Initialize the ListView and populate it with laptops
        listViewLaptops = new ListView<>();
        listViewLaptops.getItems().addAll(laptops);

        // Configure how each laptop will be displayed in the ListView
        listViewLaptops.setCellFactory(param -> new ListCell<Laptop>() {
            @Override
            protected void updateItem(Laptop laptop, boolean empty) {
                super.updateItem(laptop, empty);
                if (empty || laptop == null) {
                    setText(null);
                } else {
                    setText(laptop.getDisplayName() + " (" + laptop.getReleaseDate() + ")");
                }
            }
        });

        // Event listener to update laptop details when a laptop is selected
        listViewLaptops.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateLaptopDetails(newSelection);
            }
        });

        // Initially show details for the first laptop (if available)
        if (!laptops.isEmpty()) {
            listViewLaptops.getSelectionModel().selectFirst();
        }

        // Create a VBox for laptop list
        VBox laptopListSection = new VBox(10);
        laptopListSection.setAlignment(Pos.TOP_LEFT);
        laptopListSection.setPadding(new Insets(10));
        laptopListSection.getChildren().addAll(listViewLaptops);

        // Create an empty VBox for laptop details, to be updated when a laptop is selected
        mainContent = new VBox(10);
        mainContent.setAlignment(Pos.TOP_LEFT);
        mainContent.setPadding(new Insets(10));

        // Layout setup: left side with the list, right side for the details
        HBox mainSection = new HBox(20); // Horizontal box with spacing between components
        mainSection.getChildren().addAll(laptopListSection, mainContent);

        // Set the top of the BorderPane to the banner and the center to the mainSection (list + details)
        setTop(banner);
        setCenter(mainSection);

        // Initially display details for the first laptop, if present
        if (!laptops.isEmpty()) {
            updateLaptopDetails(laptops.get(0)); // Display the first laptop details initially
        }
    }

    private void updateLaptopDetails(Laptop laptop) {
        // Clear the current content of the details section

        if( mainContent == null){

        } else {
            mainContent.getChildren().clear();
        }


        // Create the details section for the selected laptop
        Label laptopTitle = new Label("Laptop Information");
        laptopTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label releaseDate = new Label("Display Name: " + laptop.getDisplayName() + "Release Date: " + laptop.getReleaseDate() + "CPU: " + laptop.getCpu() + "RAM: " + laptop.getRam());
        Label ssd = new Label("SSD: " + laptop.getSsd());

        // Create a section to display reviews
        Label reviewsTitle = new Label("Reviews:");
        reviewsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox reviewsBox = new VBox(5); // Space between reviews
        for (Review review : laptop.getReviews()) {
            Label reviewLabel = new Label("User: " + review.getUserName() + " Rating: " + review.getRating() + "\nComment: " + review.getComment() +"\n");
            reviewsBox.getChildren().add(reviewLabel);
        }

        // If no reviews, add a message indicating so
        if (laptop.getReviews().isEmpty()) {
            reviewsBox.getChildren().add(new Label("No reviews available."));
        }

        // Add all details to the main content area
        mainContent.getChildren().addAll(laptopTitle, releaseDate, ssd, reviewsTitle, reviewsBox);
    }

    /**
     * Load the data  contain on json file specified on DATA_PATH.
     * @return list of Lapstop contained on the file
     * @throws FileNotFoundException in case of the file not exists
     */
    private List<Laptop> loadData() throws FileNotFoundException {

        Gson gson = new Gson();

        Type arrayListType = new TypeToken<ArrayList<Laptop>>() {
        }.getType();

        return gson.fromJson(new FileReader(DATA_PATH), arrayListType);
    }

    /**
     * create an Image View from the image file specified on BANNER_PATH
     * @return the Image View created from the file specified on BANNER_PATH
     * @throws FileNotFoundException in case the file not exists
     */
    ImageView loadThumbnailImage() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(BANNER_PATH);
        Image image = new Image(inputStream);
        return new ImageView(image);
    }

}