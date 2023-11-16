package org.openjfx.JavaFXLungEcho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javax.imageio.ImageIO;

import java.io.File;
import javax.imageio.ImageIO;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    public static void TraitementDicom() throws IOException {
    	DicomLoader dicomLoaded = new DicomLoader("2019010K", 10);
    	TraitementBufferedImage traitement = new TraitementBufferedImage();
    	traitement.buffImg = dicomLoaded.dicomImage;
    	traitement.BufferedImageToPixelMatrix(traitement.buffImg);
    	traitement.BufferedImageToSonogram();
    	File newF = new File("src/main/resources/images/saved_or_converted/test_echo.png");
    	ImageIO.write(traitement.echographyImg, "PNG", newF );
    }

    public static void main(String[] args) throws IOException {
    	TraitementDicom();
        launch();
    }

}