package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class SecondaryController {

	ObservableList<String> motifList = FXCollections.observableArrayList("3x3", "5x5", "1x3", "3x1");

	@FXML
	private CheckBox checkbox;

	@FXML
	private ChoiceBox<String> choicebox;

	@FXML
	private Label labelRecouvrement;

	@FXML
	private ChoiceBox<String> choiceboxRecouvrement;

	@FXML
	private ImageView imageViewer;

	@FXML
	private Button secondaryButton;

	@FXML
	private VBox mainPane;
	
	@FXML
	private Button backToImageSelectionButton;
	
	 @FXML
	    private Spinner<Integer> spinnerSeuil;

	@FXML
	public void initialize() {
		imageViewer.setImage(convertToFxImage(Model.getInstance().pretraitement.echographyImg));
		choicebox.setValue("3x3");
		choicebox.setItems(motifList);
		choiceboxRecouvrement.setVisible(false);
		labelRecouvrement.setVisible(false);
		checkbox.setDisable(true);
		SpinnerValueFactory<Integer> gradesValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0);
		spinnerSeuil.setValueFactory(gradesValueFactory);
	}

	// Méthode appelée quand on appuie sur le bouton recouvrement
	@FXML
	void changeRecouvrement(ActionEvent event) {
		if (checkbox.isSelected()) {
			choiceboxRecouvrement.setVisible(true);
			labelRecouvrement.setVisible(true);
			choicebox.setValue("3x3");
		} else {

		}
	}
	
	//Méthode qui va permettre d'avoir les valeurs du motif en X et Y en fonction du choix fait par l'utilisateur
	@FXML
	int[] parseChoiceMotif() {
		String choiceMotif = choicebox.getValue();
		String[] motifsString = choiceMotif.split("x"); //C'est le x dans 3x3
		int[] motifs = new int[2];
		motifs[0] = Integer.parseInt(motifsString[0]);
		motifs[1] = Integer.parseInt(motifsString[1]);
		return motifs;
	}

	// méthode appelé quand on appuie sur le bouton pour lancer la loi de zipf
	@FXML
	void launchZipf(MouseEvent event) throws IOException {
		long startTime = System.nanoTime();
		Model model = Model.getInstance();
		// zipfChart.au
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

		int[] motifs = parseChoiceMotif();
		//les méthodes ne vont pas être les mêmes si on a une image dicom
		//model.traitementZipf = new TraitementZipf(model.pretraitement.greyMatrixOnlySonogram, spinnerSeuil.getValue(), true, false, motifs[0], motifs[1]);
		
		
		model.traitementZipf = new TraitementZipf(model.pretraitement.greyMatrixOnlySonogram, 0, true, false, motifs[0], motifs[1], 
				model.pretraitement.array_pente_gauche, model.pretraitement.array_pente_droite, model.pretraitement.array_courbe_haute, 
    			model.pretraitement.array_courbe_basse_gauche,model.pretraitement.array_courbe_basse_droite,model.pretraitement.booleanZipfMatrix,model.pretraitement.gOmega, 
    			model.pretraitement.newHeight, model.pretraitement.midWidth, model.pretraitement.z, model.pretraitement.h0, 
    			model.pretraitement.dOmega, (int)model.pretraitement.prevHGY, (int)model.pretraitement.prevBGY, model.pretraitement.h2);
		model.traitementZipf.motifMapFromGreyMatrix();
		model.traitementZipf.sortMapByOccurence();
		HashMap<String, Integer> mapso = model.traitementZipf.mapSortedCodedMotifOccurence;
		int i = 1;
		int maxvalue = 0, maxrange = mapso.size();
		if(maxrange==1)
		{			
			maxrange++;
		}
		for (Entry<String, Integer> entry : mapso.entrySet()) {
			Integer value = entry.getValue();
			if (value > maxvalue)
				maxvalue = value;

			series.getData().add(new XYChart.Data<Number, Number>(i, value));

			i++;
		}
		model.traitementZipf.printMapValuesAndKeys(model.traitementZipf.mapMotifNombreOccurence);
		System.out.println("Maxvalue "+maxvalue+" Max range : "+(maxrange -1));
		LineChart<Number, Number> zipfChart = new LineChart<Number, Number>(new LogarithmicAxis(1, maxrange-1	),
				new LogarithmicAxis(1, maxvalue));
		zipfChart.getData().add(series);
		
		
		Stage secondStage = new Stage();
		 StackPane root = new StackPane();
		 root.getChildren().add(zipfChart);
        secondStage.setScene(new Scene(root,1280,720));
        secondStage.setTitle("Loi de zipf appliqué avec un motif "+choicebox.getValue()+" et un seuil de "+spinnerSeuil.getValue());
        secondStage.show();
		//model.traitementZipf.printMapValuesAndKeys(mapso);
		System.out.println(choicebox.getValue());
		System.out.println("x : " + motifs[0] + "; y : " + motifs[1]);
		 long endTime = System.nanoTime();
		 
	        // obtenir la différence entre les deux valeurs de temps nano
	        long timeElapsed = endTime - startTime;
	        long milliTimeElapsed = timeElapsed / 1000000;
	      
	        System.out.println("Execution time in milliseconds: " + milliTimeElapsed);
	}
	
	@FXML
    void goToPreviousScene(MouseEvent event) throws IOException {
		App.setRoot("primary");
    }
	
	
	// Fonction pour convertir une bufferedImage en Image JavaFX
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
