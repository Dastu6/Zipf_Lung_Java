package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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

	private Image currentImage;

	@FXML
	public void initialize() {
		currentImage = convertToFxImage(Model.getInstance().pretraitement.echographyImg);
		imageViewer.setImage(currentImage);
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

	// Méthode qui va permettre d'avoir les valeurs du motif en X et Y en fonction
	// du choix fait par l'utilisateur
	@FXML
	int[] parseChoiceMotif() {
		String choiceMotif = choicebox.getValue();
		String[] motifsString = choiceMotif.split("x"); // C'est le x dans 3x3
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

		model.traitementZipf = new TraitementZipf(model.pretraitement.greyMatrixOnlySonogram, spinnerSeuil.getValue(),
				true, false, motifs[0], motifs[1], model.pretraitement.booleanZipfMatrix);
		// model.traitementZipf.printGreyMatrix();
model.traitementZipf.printGreyMatrix();
		model.traitementZipf.newTech();

		model.traitementZipf.sortMapByOccurence();
		HashMap<String, Integer> mapso = model.traitementZipf.mapSortedCodedMotifOccurence;
		int i = 1;
		int maxvalue = 0, maxrange = mapso.size();
		if (maxrange == 1) {
			maxrange++;
		}
		for (Entry<String, Integer> entry : mapso.entrySet()) {
			Integer value = entry.getValue();
			if (value > maxvalue)
				maxvalue = value;

			series.getData().add(new XYChart.Data<Number, Number>(i, value));
			i++;
		}
		i = 0;
		System.out.println(series.getData().size() + "size of datas");
		// model.traitementZipf.printbooleanZipf();
		System.out.println("Maxvalue " + maxvalue + " Max range : " + (maxrange - 1));
		int index = 0;
		int xmaxvalue = (int) Math.pow(10, index);
		int ymaxvalue = (int) Math.pow(10, index);
		while (ymaxvalue < maxvalue) {
			index++;
			ymaxvalue = (int) Math.pow(10, index);
		}
		index = 0;
		while (xmaxvalue < maxrange) {
			index++;
			xmaxvalue = (int) Math.pow(10, index);
		}
		LineChart<Number, Number> zipfChart = new LineChart<Number, Number>(new LogarithmicAxis(1, xmaxvalue),
				new LogarithmicAxis(1, ymaxvalue));
		String basedText = "Aucun motif sélectionné";
		for (int i1 = 0; i1 <= motifs[1]; i1++) {
			basedText += "\n";
		}
		zipfChart.setTitle(basedText);
		for (Entry<String, Integer> entry : mapso.entrySet()) {
			Integer value = entry.getValue();
			String key = entry.getKey();
			XYChart.Data<Number, Number> data = series.getData().get(i);
			data.setNode(new HoveredThresholdNode(key, value, motifs[0], motifs[1], zipfChart, basedText));
			i++;
		}
		System.out.println(series.getData().size() + "size of datas");
		zipfChart.getData().add(series);

		zipfChart.setCursor(Cursor.CROSSHAIR);
		Stage secondStage = new Stage();
		StackPane root = new StackPane();
		root.getChildren().add(zipfChart);
		secondStage.getIcons().add(new Image("file:src\\main\\resources\\lung.png"));
		secondStage.setScene(new Scene(root, 1280, 720));
		secondStage.setTitle("Loi de zipf appliqué à " + model.nomImage + " avec un motif " + choicebox.getValue()
				+ " et un seuil de " + spinnerSeuil.getValue());
		secondStage.show();
		// model.traitementZipf.printMapValuesAndKeys(mapso);
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

	@FXML // Action lorsque l'on clique sur l'image
	void imageClicked(MouseEvent event) {
		Stage stage = new Stage();
		StackPane root = new StackPane();
		ImageView image = new ImageView(currentImage);
		root.getChildren().add(image);
		Scene scene = new Scene(root);
		stage.getIcons().add(new Image("file:src\\main\\resources\\lung.png"));
		stage.setWidth(currentImage.getWidth());
		stage.setHeight(currentImage.getHeight());
		stage.setScene(scene);
		stage.setResizable(true);
		stage.setTitle("Image à analyser");
		stage.show();
	}

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

	/** a node which displays a value on hover, but is otherwise empty */
	class HoveredThresholdNode extends StackPane {
		int motifSizeX;
		int motifSizeY;
		String showValue;
		String basedTitle;
		LineChart<Number, Number> refChart;

		HoveredThresholdNode(String motif, int value, int motifsizeX, int motifsizeY, LineChart<Number, Number> chart,
				String title) {
			setPrefSize(10, 10);
			this.motifSizeX = motifsizeX;
			this.motifSizeY = motifsizeY;
			refChart = chart;
			basedTitle = title;
			createDataThresholdLabel(motif, value);

			setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					refChart.setTitle(showValue);
				}
			});
			setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					refChart.setTitle(basedTitle);
				}
			});
		}

		private void createDataThresholdLabel(String motif, int value) {
			String result = "Motif : ";
			int count = 0;
			for (int i = 0; i < motifSizeY; i++) {
				result = result + "[ ";
				for (int j = 0; j < motifSizeX; j++) {
					result += motif.charAt(count);
					count++;
					if (j != motifSizeX - 1)
						result += " ";
				}
				if (i != motifSizeY - 1)
					result += " ]\n           ";
				else {
					result += " ]\n";
				}
			}
			showValue = result + "Nombre d'occurences : " + value;

		}
	}

}
