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
	void launchZipf(MouseEvent event) throws IOException {
		Model model = Model.getInstance();
		// zipfChart.au
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

		// TEST
		TraitementBufferedImage er = new TraitementBufferedImage();
		//BufferedImage I = ImageIO.read(new File("src/main/resources/images/test/testlena.jpg"));
		//BufferedImage I = ImageIO.read(new File("src/main/resources/images/test/testbateau.png"));
		BufferedImage I = ImageIO.read(new File("src/main/resources/images/test/test_vert.png"));
		er.BufferedImageToPixelMatrix(I);
		 model.traitementZipf = new TraitementZipf(er.greyPixelsLevels,5,true,false);
		//model.traitementZipf = new TraitementZipf(model.pretraitement.greyMatrixOnlySonogram, 0, true, false);
		model.traitementZipf.motifMapFromGreyMatrix();
		model.traitementZipf.sortMapByOccurence();
		HashMap<String, Integer> mapso = model.traitementZipf.mapSortedCodedMotifOccurence;
		int i = 1;
		int maxvalue = 0, maxrange = mapso.size();
		for (Entry<String, Integer> entry : mapso.entrySet()) {
			Integer value = entry.getValue();
			if (value > maxvalue)
				maxvalue = value;

			series.getData().add(new XYChart.Data<Number, Number>(i, value));
			System.out.println("Valeur : " + value + " , rang : " + i);

			i++;
		}
		LineChart<Number, Number> zipfChart = new LineChart<Number, Number>(new LogarithmicAxis(1, maxrange-1	),
				new LogarithmicAxis(1, maxvalue));
		
		zipfChart.getData().add(series);
		
		
		Stage secondStage = new Stage();
		 StackPane root = new StackPane();
		 root.getChildren().add(zipfChart);
        secondStage.setScene(new Scene(root,1280,720));
        secondStage.setTitle("Loi de zipf appliqué avec un motif "+choicebox.getValue());
        secondStage.show();
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
