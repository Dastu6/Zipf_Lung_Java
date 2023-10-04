package org.openjfx.JavaFXLungEcho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.aspose.imaging.Image;
import com.aspose.imaging.fileformats.dicom.DicomImage;
import com.aspose.imaging.imageoptions.JpegOptions;

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
    
    public static void test() {
    	System.out.println("test");
    	DicomImage dicomImage;
    	//dicomImage.setActivePage(dicomImage.getDicomPages()[0]);
    	//JpegOptions jpegOptions = new JpegOptions();
    	//dicomImage.save();
    }

    public static void main(String[] args) {
    	test();
        launch();
    }

}