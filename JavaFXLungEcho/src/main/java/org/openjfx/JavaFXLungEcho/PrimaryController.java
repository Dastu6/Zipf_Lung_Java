package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

public class PrimaryController {
private File selectedDirectory;
    @FXML
    private ListView<String> listview;

    @FXML
    private Button primaryButton;
    
    @FXML
    private Button primaryButton1;

    @FXML
    private Slider sliderImage;
    
    @FXML
    private ImageView imageContainer;

    @FXML
    private ImageView imageContainer1;
    
    private Image currentImage;


    @FXML
    void buttonCall(MouseEvent event) {
    	DirectoryChooser directoryChooser = new DirectoryChooser();
    	selectedDirectory = directoryChooser.showDialog(null);

    	if(selectedDirectory == null){
    	     //No Directory selected
    	}else{
    	     System.out.println(selectedDirectory.getAbsolutePath());
    	     ArrayList<String> p = new ArrayList<String>();
    	     for (File file : selectedDirectory.listFiles()) {
    	            if (!file.isDirectory()) {
    	            	if(!getExtensionByStringHandling(file.getAbsolutePath()).isPresent())
    	            	{
    	            		System.out.println(file.getAbsolutePath());
    	            		p.add(file.getName());
    	            	}
    	            		
    	            	else if(getExtensionByStringHandling(file.getAbsolutePath()).get().equals("dcm")||getExtensionByStringHandling(file.getAbsolutePath()).get().equals("dicom"))
    	            		{
    	            		System.out.println("Extension de fichier : "+getExtensionByStringHandling(file.getAbsolutePath()).get());
    	            		System.out.println(file.getAbsolutePath());
    	            		p.add(file.getName());
    	            		}
    	            }
    	        }
    	     listview.setVisible(true);
    	     listview.getItems().addAll(p);
    	     listview.setDisable(false);
    	     listview.setVisible(true);
    	}
    }
    
    @FXML
    void clickOnlistview(MouseEvent event) {
    	String imagename = listview.getSelectionModel().getSelectedItem();
    	 System.out.println("clicked on " + imagename);
    	 System.out.println("dirPath : "+selectedDirectory.getAbsolutePath());
    	 String imagePath =selectedDirectory.getAbsolutePath()+"\\"+ imagename;
    	 System.out.println(imagePath);
    	 try {
			Model.getInstance().dicomLoader = new DicomLoader(selectedDirectory.getAbsolutePath(),listview.getSelectionModel().getSelectedItem(),0);
			currentImage= convertToFxImage(Model.getInstance().dicomLoader.dicomImage);
			imageContainer.setImage(currentImage);
			sliderImage.setVisible(true);
			sliderImage.setMax(Model.getInstance().dicomLoader.getNbImages()-1);
			sliderImage.valueProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
					try {
						Model.getInstance().dicomLoader.dicomImage = Model.getInstance().dicomLoader.chargeImageDicomBufferise(newNumber.intValue());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currentImage= convertToFxImage(Model.getInstance().dicomLoader.dicomImage);
					imageContainer.setImage(currentImage);
				}			
			});
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void cutImage(MouseEvent event) {
    	Model.getInstance().pretraitement.buffImg = Model.getInstance().dicomLoader.dicomImage;
    	Model.getInstance().pretraitement.BufferedImageToPixelMatrix(Model.getInstance().pretraitement.buffImg);
    	Model.getInstance().pretraitement.BufferedImageToSonogram();
    	imageContainer1.setImage(convertToFxImage(Model.getInstance().pretraitement.echographyImg));
    }
    

    public Optional<String> getExtensionByStringHandling(String filename) {
    	return Optional.ofNullable(filename)
    			.filter(f -> f.contains("."))
    			.map(f -> f.substring(filename.lastIndexOf(".") + 1));
    	}
    
    //Fonction pour convertir une bufferedImage en Image JavaFX
    private Image convertToFxImage(BufferedImage image) {
    	WritableImage wr = null;
    	if (image != null) {
    		wr = new WritableImage(image.getWidth(), image.getHeight());
    		PixelWriter pw = wr.getPixelWriter();
    		for (int x = 0; x < image.getWidth(); x++) {
    			for (int y = 0; y < image.getHeight(); y++) {
    				pw.setArgb(x, y, image.getRGB(x, y));
    			}
    		}
    	}
    	
    	return new ImageView(wr).getImage();
    }
    }


