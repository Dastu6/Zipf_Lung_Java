package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    private LineChart<Number, Number> zipfChart;


    @FXML
    private VBox vBox;
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

	//méthode appelé quand on appuie sur le bouton pour lancer la loi de zipf
	@FXML
	void launchZipf(MouseEvent event) {
		Model model = Model.getInstance();
		//LineChart<Number, Number> zzipfChart = new LineChart<Number, Number>(new LogarithmicAxis(), new LogarithmicAxis());
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		model.traitementZipf = new TraitementZipf(model.pretraitement.greyMatrixOnlySonogram);
		model.traitementZipf.motifMapFromGreyMatrix();
		HashMap<Integer,Integer> mapso= model.traitementZipf.sortMapByOccurence();
		int i = 0;
		for(Entry<Integer, Integer> entry : mapso.entrySet())
		{
			Integer value = entry.getValue();
			series.getData().add(new XYChart.Data<Number,Number>(value,i));
			System.out.println("Valeur : "+value+" , i : "+i );
			i++;
		}
		//zipfChart
		zipfChart.getData().add(series);
		model.traitementZipf.printMapValuesAndKeys(mapso);
		/*
		XYChart.Series<Number, Number> series1 = new XYChart.Series<Number, Number>();
	    series1.getData().add(new XYChart.Data<Number, Number>(1, 20));
	    series1.getData().add(new XYChart.Data<Number, Number>(2, 100));
	    series1.getData().add(new XYChart.Data<Number, Number>(3, 80));
	    series1.getData().add(new XYChart.Data<Number, Number>(4, 180));
	    series1.getData().add(new XYChart.Data<Number, Number>(5, 20));
	    series1.getData().add(new XYChart.Data<Number, Number>(6, 10));
	    zipfChart.getData().add(series1);*/
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
