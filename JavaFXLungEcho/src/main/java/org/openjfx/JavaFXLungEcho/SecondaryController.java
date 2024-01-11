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
		XYChart.Data<Number, Number> datas = new XYChart.Data<Number, Number>();
		int[] motifs = parseChoiceMotif();
		// les méthodes ne vont pas être les mêmes si on a une image dicom
		// model.traitementZipf = new
		// TraitementZipf(model.pretraitement.greyMatrixOnlySonogram,
		// spinnerSeuil.getValue(), true, false, motifs[0], motifs[1]);

		model.traitementZipf = new TraitementZipf(model.pretraitement.greyMatrixOnlySonogram, spinnerSeuil.getValue(),
				true, false, motifs[0], motifs[1], model.pretraitement.booleanZipfMatrix);
		//model.traitementZipf.printGreyMatrix();

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
		i=0;// Pas encore fini, mais c'est pour afficher les motifs on hover sur le graph
		for (Entry<String, Integer> entry : mapso.entrySet()) {
			Integer value = entry.getValue();
			String key = entry.getKey();
			 XYChart.Data<Number, Number> data = series.getData().get(i);
			data.setNode(
			          new HoveredThresholdNode(
			        		  key, value, motifs[0],motifs[1]
			          )
			      );
			i++;
		}
		
		
		
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
		zipfChart.getData().add(series);
		
		zipfChart.setCursor(Cursor.CROSSHAIR);
		Stage secondStage = new Stage();
		StackPane root = new StackPane();
		root.getChildren().add(zipfChart);
		secondStage.setScene(new Scene(root, 1280, 720));
		secondStage.setTitle("Loi de zipf appliqué avec un motif " + choicebox.getValue() + " et un seuil de "
				+ spinnerSeuil.getValue());
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

	/**
	 * @return plotted y values for monotonically increasing integer x values,
	 *         starting from x=1
	 */
	public ObservableList<XYChart.Data<Integer, Integer>> plot(int... y) {
		final ObservableList<XYChart.Data<Integer, Integer>> dataset = FXCollections.observableArrayList();
		int i = 0;
		while (i < y.length) {
			final XYChart.Data<Integer, Integer> data = new XYChart.Data<>(i + 1, y[i]);
			//data.setNode(new HoveredThresholdNode((i == 0) ? 0 : y[i - 1], y[i]));

			dataset.add(data);
			i++;
		}

		return dataset;
	}

	/** a node which displays a value on hover, but is otherwise empty */
	class HoveredThresholdNode extends StackPane {
		int motifSizeX;
		int motifSizeY;
		HoveredThresholdNode(String motif, int value, int motifsizeX,int motifsizeY) {
			setPrefSize(10,10);
			this.motifSizeX = motifsizeX;
			this.motifSizeY = motifsizeY;
			final Label label = createDataThresholdLabel(motif, value);

			setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					getChildren().setAll(label);
					setCursor(Cursor.NONE);
					toFront();
				}
			});
			setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					getChildren().clear();
					setCursor(Cursor.CROSSHAIR);
				}
			});
		}

		private Label createDataThresholdLabel(String motif, int value) {
			String result ="";
			int count = 0;
			for(int i=0;i<motifSizeX;i++)
			{
				result = result+"[";
				for(int j= 0;j<motifSizeY;j++)
				{ 
					result+=motif.charAt(count);
					count++;
					if(j!=motifSizeY-1)
						result+=" ";
				}
				result+="]\n";
			}
			String finalResult = result + "\n"+value;
			Label label = new Label(finalResult);
			label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
			label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

			label.setTextFill(Color.FIREBRICK);
			label.setMinSize(5, 5);
			return label;
		}
	}

}
