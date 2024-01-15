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
import javafx.scene.image.Image;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * JavaFX App.
 */
public class App extends Application {
    
    /** The scene. */
    private static Scene scene;
    
    /** The model. */
    private static Model model;
    
    /**
     * Start.
     *
     * @param stage the stage
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 1280, 720);
        stage.setTitle("Traitement d'image avec la loi de Zipf");
        stage.getIcons().add(new Image("file:src\\main\\resources\\lung.png"));
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

    /**
     * Sets the root.
     *
     * @param fxml the new root
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Load FXML.
     *
     * @param fxml the fxml
     * @return the parent
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(String[] args) throws IOException {
    	model = Model.getInstance();

        //TraitementDicom();
        model.pretraitement = new TraitementBufferedImage();
        
        openFile();
        launch();
    }
    
    /**
     * Open file.
     *
     * @throws FileNotFoundException the file not found exception
     */
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
    
    /**
     * Close file.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void closeFile() throws IOException {
    FileWriter  f = new FileWriter("src/main/resources/favDir.txt");
    	if( f!=null ) { 
    		 f.write(Model.getInstance().favDir);
    	      f.close();
    	        System.out.println("Nouveau fav directory : "+ Model.getInstance().favDir);
    	      }
    }
    
}