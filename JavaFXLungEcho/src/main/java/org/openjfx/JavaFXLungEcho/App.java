package org.openjfx.JavaFXLungEcho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.File;

import javax.imageio.ImageIO;



/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene;
    private static Model model;
    
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
 
    	model.dicomLoader = new DicomLoader("2019010K", 0);
    	model.traitement = new TraitementBufferedImage();
    	model.traitement.buffImg = model.dicomLoader.dicomImage;
    	model.traitement.BufferedImageToPixelMatrix(model.traitement.buffImg);
    	model.traitement.BufferedImageToSonogram();
    	File newF = new File("src/main/resources/images/saved_or_converted/test_echo.png");
    	ImageIO.write(model.traitement.echographyImg, "PNG", newF );
    }

    public static void main(String[] args) throws IOException {
    	model = Model.getInstance();

        TraitementDicom();
        model.traitement = new TraitementBufferedImage();
        launch();
    }
}
