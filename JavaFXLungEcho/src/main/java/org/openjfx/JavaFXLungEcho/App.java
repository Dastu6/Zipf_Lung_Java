package org.openjfx.JavaFXLungEcho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
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
        scene = new Scene(loadFXML("primary"), 1280, 720);
        stage.setScene(scene);
        stage.setResizable(false);
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
    	model.pretraitement = new TraitementBufferedImage();
    	model.pretraitement.buffImg = model.dicomLoader.dicomImage;
    	model.pretraitement.BufferedImageToPixelMatrix(model.pretraitement.buffImg);
    	model.pretraitement.BufferedImageToSonogram();
    	File newF = new File("src/main/resources/images/saved_or_converted/test_echo.png");
    	ImageIO.write(model.pretraitement.echographyImg, "PNG", newF );
    	
    	model.traitementZipf = new TraitementZipf(model.pretraitement.greyMatrixOnlySonogram, 0, true, true, 3, 3);
    	model.traitementZipf.motifMapFromGreyMatrix();
    	System.out.println("Map de base");
    	model.traitementZipf.printMapValuesAndKeys(model.traitementZipf.mapMotifNombreOccurence);
    	model.traitementZipf.sortMapByOccurence();
    	System.out.println("Map sorted");
    	model.traitementZipf.printMapValuesAndKeys(model.traitementZipf.mapSortedCodedMotifOccurence);
    }

    public static void main(String[] args) throws IOException {
    	model = Model.getInstance();

        //TraitementDicom();
        model.pretraitement = new TraitementBufferedImage();
        launch();
    }
}