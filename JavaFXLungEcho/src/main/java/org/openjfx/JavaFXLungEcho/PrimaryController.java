package org.openjfx.JavaFXLungEcho;

import java.io.File;
import java.util.Optional;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

public class PrimaryController {
private File selectedDirectory;
    @FXML
    private ListView<String> listview;

    @FXML
    private Button primaryButton;

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
    	     listview.getItems().addAll(p);
    	     listview.setDisable(false);
    	     listview.setVisible(true);
    	}
    }
    
    @FXML
    void clickOnlistview(MouseEvent event) {
    	 System.out.println("clicked on " + listview.getSelectionModel().getSelectedItem());
    }
    
    
    

    public Optional<String> getExtensionByStringHandling(String filename) {
    	return Optional.ofNullable(filename)
    			.filter(f -> f.contains("."))
    			.map(f -> f.substring(filename.lastIndexOf(".") + 1));
    	}
    }


