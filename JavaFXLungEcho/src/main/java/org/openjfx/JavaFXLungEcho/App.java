package org.openjfx.JavaFXLungEcho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.aspose.imaging.Image;

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

    public static void main(String[] args) {
    	DicomImage dicomImage = (DicomImage) Image.load("C:\\Users\\scizz\\Desktop\\Cours\\4A\\projet_genie_log\\101M0\\test.dcm");

    	// Définir la page active à convertir en JPEG
    	dicomImage.setActivePage(dicomImage.getDicomPages()[0]);

    	JpegOptions jpegOptions = new JpegOptions();

    	// Enregistrer au format JPEG
    	dicomImage.save("C:\\Users\\scizz\\Desktop\\Cours\\4A\\projet_genie_log\\101M0\\return.jpg", jpegOptions);
        launch();

    }

}