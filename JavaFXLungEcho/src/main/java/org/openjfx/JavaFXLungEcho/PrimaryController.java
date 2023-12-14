package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class PrimaryController {
	int maxImage = -1;
	boolean isSelectingDir = false;
	private DirectoryChooser directoryChooser;
	private File selectedDirectory;
	@FXML
	private Button changeImage;

	@FXML
	private ImageView imageContainer;

	@FXML
	private ImageView imageContainer1;

	@FXML
	private Button imageSelectionButton;

	@FXML
	private Label labelList;

	@FXML
	private Label labelSlider;

	@FXML
	private ListView<String> listview;

	@FXML
	private Button selectDirButton;

	@FXML
	private Button selectFileButton;

	@FXML
	private Slider sliderImage;

	@FXML
	private TextField sliderTextField;

	@FXML
	private Button traitementImageButton;

	private Image currentImage;
	private Image postImage;

	@FXML
	public void initialize() { // Méthode appelé pour initialiser cette vue
		labelList.setDisable(true);
		labelList.setVisible(false);
		listview.setDisable(true);
		listview.setVisible(false);
		sliderImage.setVisible(false);
		imageContainer.setVisible(false);
		imageContainer.setDisable(true);
		imageContainer1.setVisible(false);
		imageContainer1.setDisable(true);
		traitementImageButton.setVisible(false);
		imageSelectionButton.setVisible(false);
		changeImage.setVisible(false);
		sliderTextField.setVisible(false);
		sliderTextField.setDisable(true);
		labelSlider.setVisible(false);
		if (Model.getInstance().favDir != null)
			selectDirectory(true);
	}

	@FXML
	void switchImage(MouseEvent event) throws IOException {
		App.setRoot("secondary");
	}

	@FXML
	void buttonCall(MouseEvent event) {
		selectDirectory(false);
	}

	@FXML
	void submitValue(ActionEvent event) throws IOException {
		int value = Integer.parseInt(sliderTextField.getText());
		System.out.println(maxImage);
		if (value >= 0 && value <= maxImage) {
			sliderImage.setValue(value);
			Model.getInstance().dicomLoader.dicomImage = Model.getInstance().dicomLoader
					.chargeImageDicomBufferise(value);
			currentImage = convertToFxImage(Model.getInstance().dicomLoader.dicomImage);
			imageContainer.setImage(null);
			imageContainer.setImage(currentImage);
		} else {
			sliderTextField.setText(null);
		}
		sliderTextField.prefColumnCountProperty().bind(sliderTextField.textProperty().length());
	}

	@FXML // Utiliser quand on veut changer d'image
	void changerImage(MouseEvent event) {
		imageContainer.setVisible(false);
		imageContainer.setDisable(true);
		imageContainer1.setVisible(false);
		imageContainer1.setDisable(true);
		imageSelectionButton.setDisable(true);
		imageSelectionButton.setVisible(false);
		traitementImageButton.setDisable(true);
		traitementImageButton.setVisible(false);
		sliderImage.setVisible(false);
		sliderImage.setDisable(true);
		labelSlider.setVisible(false);
		sliderTextField.setVisible(false);
		sliderTextField.setDisable(true);
		changeImage.setVisible(false);
		sliderImage.setDisable(true);
		changeImage.setDisable(true);

		listview.setVisible(true);
		listview.setDisable(false);

		selectDirButton.setVisible(true);
		selectDirButton.setDisable(false);
		selectFileButton.setVisible(true);
		selectFileButton.setDisable(false);
		labelList.setVisible(true);
		
	}

	// Affiche l'image correspondant au fichier sélectionner par l'utilisateur dans
	// la listview
	@FXML
	void clickOnlistview(MouseEvent event) {
		String imagename = listview.getSelectionModel().getSelectedItem();
		System.out.println("clicked on " + imagename);
		System.out.println("dirPath : " + selectedDirectory.getAbsolutePath());
		String imagePath = selectedDirectory.getAbsolutePath() + "\\" + imagename;
		System.out.println(imagePath);
		try {
			Model.getInstance().dicomLoader = new DicomLoader(selectedDirectory.getAbsolutePath(),
					listview.getSelectionModel().getSelectedItem(), 0);
			currentImage = convertToFxImage(Model.getInstance().dicomLoader.dicomImage);
			imageContainer.setImage(null);
			imageContainer.setImage(currentImage);

			traitementImageButton.setVisible(true);
			traitementImageButton.setDisable(false);
			changeImage.setVisible(true);
			imageContainer.setVisible(true);
			imageContainer.setDisable(false);
			sliderImage.setVisible(true);
			sliderImage.setDisable(false);
			listview.setVisible(false);

			maxImage = Model.getInstance().dicomLoader.getNbImages() - 1;
			sliderImage.setMax(maxImage);
			sliderImage.setValue(0);
			sliderTextField.setText("0");
			sliderTextField.setVisible(true);
			sliderTextField.setDisable(false);
			selectDirButton.setVisible(false);
			selectDirButton.setDisable(true);
			selectFileButton.setVisible(false);
			selectFileButton.setDisable(true);
			labelList.setVisible(false);
			labelSlider.setText("/" + maxImage);
			this.sliderImage.valueProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
					try {
						sliderImage.setValue(newNumber.intValue());
						int value = (int) sliderImage.getValue();
						Model.getInstance().dicomLoader.dicomImage = Model.getInstance().dicomLoader
								.chargeImageDicomBufferise(value);
						sliderTextField.setText(Integer.toString(value));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currentImage = convertToFxImage(Model.getInstance().dicomLoader.dicomImage);
					imageContainer.setImage(null);
					imageContainer.setImage(currentImage);
					labelSlider.setVisible(true);
				}
			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML // Action lorsque l'on clique sur l'image de prétraitement
	void imageClicked(MouseEvent event) {
		Stage stage = new Stage();
		StackPane root = new StackPane();
		ImageView temp = new ImageView(currentImage);
		root.getChildren().add(temp);
		Scene scene = new Scene(root,1280, 720);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Image en prétraitement");
		stage.show();
	}

	@FXML // Action lorsque l'on clique sur l'image de postTraitement
	void imageClicked2(MouseEvent event) {
		Stage stage = new Stage();
		StackPane root = new StackPane();
		ImageView temp = new ImageView(postImage);
		root.getChildren().add(temp);

		stage.setScene(new Scene(root, 1280, 720));
		stage.setTitle("Image posttraitement");
		stage.show();
	}

	// Fais le prétraitement sur l'image et l'affiche
	@FXML
	void cutImage(MouseEvent event) {
		Model.getInstance().pretraitement.buffImg = Model.getInstance().dicomLoader.dicomImage;
		Model.getInstance().pretraitement.BufferedImageToPixelMatrix(Model.getInstance().pretraitement.buffImg);
		Model.getInstance().pretraitement.BufferedImageToSonogram();
		imageContainer1.setVisible(true);
		imageContainer1.setDisable(false);
		postImage = convertToFxImage(Model.getInstance().pretraitement.echographyImg);
		imageContainer1.setImage(postImage);
		imageSelectionButton.setVisible(true);
	}

	@FXML
	void selectFileButtonAction(MouseEvent event) {

	}

	private Optional<String> getExtensionByStringHandling(String filename) {
		return Optional.ofNullable(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));
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

	void selectDirectory(boolean isFirstTime) {
		if (isFirstTime) {
			selectedDirectory = new File(Model.getInstance().favDir);
			if (selectedDirectory != null || selectedDirectory.exists() == true)
				selectFileFromDirectory();
		} else if (isSelectingDir == false) {
			isSelectingDir = true;
			directoryChooser = new DirectoryChooser();
			String tempFavDir;
			if (Model.getInstance().favDir == null)
				tempFavDir = "src/main/resources/images";
			else
				tempFavDir = Model.getInstance().favDir;
			File f = new File(tempFavDir);
			if (f.exists() == false) {
				tempFavDir = "src/main/resources/images";
				f = new File(tempFavDir);
			}
			directoryChooser.setInitialDirectory(f);
			selectedDirectory = directoryChooser.showDialog(null);
		}
		if (isSelectingDir == false) {

			if (selectedDirectory == null) {
				// No Directory selected
			} else {
				Model.getInstance().favDir = selectedDirectory.getAbsolutePath();
				System.out.println(selectedDirectory.getAbsolutePath());
				selectFileFromDirectory();
				isSelectingDir = false;
			}
		}
	}

	// Selectionne les fichiers depuis le directory indiqué dans selectedDirectory
	// et remplit la listview
	void selectFileFromDirectory() {
		if (selectedDirectory.isDirectory() == true) {

			ArrayList<String> p = new ArrayList<String>();
			for (File file : selectedDirectory.listFiles()) {
				if (!file.isDirectory()) {
					if (!getExtensionByStringHandling(file.getAbsolutePath()).isPresent()) {
						System.out.println(file.getAbsolutePath());
						p.add(file.getName());
					}

					else if (getExtensionByStringHandling(file.getAbsolutePath()).get().equals("dcm")
							|| getExtensionByStringHandling(file.getAbsolutePath()).get().equals("dicom")) {
						System.out.println(
								"Extension de fichier : " + getExtensionByStringHandling(file.getAbsolutePath()).get());
						System.out.println(file.getAbsolutePath());
						p.add(file.getName());
					}
				}
			}
			listview.getItems().addAll(p);
			listview.setVisible(true);
			listview.setDisable(false);
			labelList.setVisible(true);
			labelList.setText("Liste des fichiers disponibles pour le dossier suivant : \n"
					+ selectedDirectory.getAbsolutePath());
			labelList.setDisable(false);
		}
	}
}
