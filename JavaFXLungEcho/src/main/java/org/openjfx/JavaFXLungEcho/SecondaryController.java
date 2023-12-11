package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.chart.NumberAxis;
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
	public void initialize() {
		imageViewer.setImage(convertToFxImage(Model.getInstance().pretraitement.echographyImg));
		choicebox.setValue("3x3");
		choicebox.setItems(motifList);
		choiceboxRecouvrement.setVisible(false);
		labelRecouvrement.setVisible(false);
		checkbox.setDisable(true);
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

	// méthode appelé quand on appuie sur le bouton pour lancer la loi de zipf
	@FXML
	void launchZipf(MouseEvent event) {
		Model model = Model.getInstance();
		//LineChart<Number, Number> zipfChart = new LineChart<Number, Number>(new LogarithmicAxis(),new LogarithmicAxis());
		LineChart<Number, Number> zipfChart = new LineChart<Number, Number>(new NumberAxis(),new NumberAxis());
		//zipfChart.au
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		model.traitementZipf = new TraitementZipf(model.pretraitement.greyMatrixOnlySonogram, true, false);
		model.traitementZipf.motifMapFromGreyMatrix();
		model.traitementZipf.sortMapByOccurence();
		HashMap<String, Integer> mapso = model.traitementZipf.mapSortedCodedMotifOccurence;
		int i = 0;
		for (Entry<String, Integer> entry : mapso.entrySet()) {
			Integer value = entry.getValue();
			if (i != 0) {
				series.getData().add(new XYChart.Data<Number, Number>(value, i));
				System.out.println("Valeur : " + value + " , i : " + i);
			}

			i++;
		}
		zipfChart.getData().add(series);
		mainPane.getChildren().add(zipfChart);
		model.traitementZipf.printMapValuesAndKeys(mapso);

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
