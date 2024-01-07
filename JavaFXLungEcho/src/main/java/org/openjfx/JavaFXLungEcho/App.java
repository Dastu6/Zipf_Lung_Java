package org.openjfx.JavaFXLungEcho;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



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
        stage.setOnCloseRequest((e) -> {
        	try {
				closeFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            stage.close();
        });
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    public static void TraitementDicom() throws Exception {
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
        
        openFile();
        launch();
    }
    
    public static void openFile() throws FileNotFoundException {
    	File f = new File("src/main/resources/favDir.txt");
    	if(f.exists() && !f.isDirectory()) { 
    				 Scanner myReader = new Scanner(f);
    				 String data = null;
    	      while (myReader.hasNextLine()) {
    	        data = myReader.nextLine();
    	        System.out.println(data);
    	      }
    	      myReader.close();
    	      Model.getInstance().favDir = data;
    	}
    }
    public static void closeFile() throws IOException {
    	FileWriter  f = new FileWriter("src/main/resources/favDir.txt");
    	if( f!=null ) { 
    		 f.write(Model.getInstance().favDir);
    	      f.close();
    	        System.out.println("Nouveau fav directory : "+ Model.getInstance().favDir);
    	      }
    }
    
}