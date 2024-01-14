package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author scizz
 *
 */
/**
 * @author scizz
 *
 */
public class PrimaryController {
	int maxImage = -1;
	private DirectoryChooser directoryChooser;
	private FileChooser fileChooser;
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
	public void initialize() throws IOException { // Méthode appelé pour initialiser cette vue
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
		if (Model.getInstance().dicomLoader != null) {
			selectDirectory(true);

			traitementImageButton.setVisible(true);
			traitementImageButton.setDisable(false);
			changeImage.setVisible(true);
			changeImage.setDisable(false);
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
			if (Model.getInstance().photoNumber != -1) {
				changeImageSliderValue(Model.getInstance().photoNumber);
				imageContainer1.setVisible(true);
				imageContainer1.setDisable(false);
				postImage = convertToFxImage(Model.getInstance().pretraitement.echographyImg);
				imageContainer1.setImage(postImage);
				imageSelectionButton.setVisible(true);
				imageSelectionButton.setDisable(false);
			}

		}

	}

	@FXML
	void switchImage(MouseEvent event) throws IOException {
		App.setRoot("secondary");
		Model.getInstance().photoNumber = (int) sliderImage.getValue();
		Model.getInstance().isDicomImage = true;
	}

	@FXML
	void buttonCall(MouseEvent event) {
		selectDirectory(false);
	}

	@FXML
	void submitValue(ActionEvent event) throws IOException {
		int value = Integer.parseInt(sliderTextField.getText());
		changeImageSliderValue(value);
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
	void clickOnlistview(MouseEvent event) throws IOException {
		String imagename = listview.getSelectionModel().getSelectedItem();
		Model.getInstance().nomImage = imagename;
		String imagePath = selectedDirectory.getAbsolutePath() + "\\" + imagename;
		File f = new File(imagePath);
		putImage(f);
	}

	@FXML // Action lorsque l'on clique sur l'image de prétraitement
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
		stage.setTitle("Image en prétraitement");
		stage.show();
	}

	@FXML // Action lorsque l'on clique sur l'image de postTraitement
	void imageClicked2(MouseEvent event) {
		Stage stage = new Stage();
		StackPane root = new StackPane();
		ImageView temp = new ImageView(postImage);
		root.getChildren().add(temp);
		Scene scene = new Scene(root);
		stage.getIcons().add(new Image("file:src\\main\\resources\\lung.png"));
		stage.setWidth(postImage.getWidth() + 50);
		stage.setHeight(postImage.getHeight() + 50);
		stage.setScene(scene);
		stage.setTitle("Image post-traitement");
		stage.show();
	}

	// Fais le prétraitement sur l'image et l'affiche
	@FXML
	void cutImage(MouseEvent event) throws Exception {
		Model.getInstance().isDicomImage = true;
		Model.getInstance().pretraitement.buffImg = Model.getInstance().dicomLoader.dicomImage;
		Model.getInstance().pretraitement.ThreadBuffImagetoPixelMatrix(Model.getInstance().isDicomImage,
				Model.getInstance().pretraitement.buffImg);
		Model.getInstance().pretraitement.BufferedImageToSonogram();
		imageContainer1.setVisible(true);
		imageContainer1.setDisable(false);
		postImage = convertToFxImage(Model.getInstance().pretraitement.echographyImg);
		imageContainer1.setImage(postImage);
		imageSelectionButton.setVisible(true);
		imageSelectionButton.setDisable(false);
	}

	@FXML
	void selectFileButtonAction(MouseEvent event) throws IOException {
		fileChooser = new FileChooser();
		fileChooser.setTitle("Sélectionner l'image à traiter");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image file", "*.png", "*.jpg",
				"*.dcm");
		fileChooser.getExtensionFilters().add(extFilter);
		File f = new File(Model.getInstance().favDir);
		if (f != null)
			fileChooser.setInitialDirectory(f);
		File fc = fileChooser.showOpenDialog((Stage) changeImage.getScene().getWindow());
		putImage(fc);
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
		} else {
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
			selectedDirectory = directoryChooser.showDialog((Stage) changeImage.getScene().getWindow());
		}

		if (selectedDirectory == null) {
			// No Directory selected
		} else {
			Model.getInstance().favDir = selectedDirectory.getAbsolutePath();
			System.out.println(selectedDirectory.getAbsolutePath());
			selectFileFromDirectory();
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
						// System.out.println(file.getAbsolutePath());
						// p.add(file.getName());
					}

					else if (getExtensionByStringHandling(file.getAbsolutePath()).get().equals("dcm")
							|| getExtensionByStringHandling(file.getAbsolutePath()).get().equals("dicom")
							|| getExtensionByStringHandling(file.getAbsolutePath()).get().equals("png")
							|| getExtensionByStringHandling(file.getAbsolutePath()).get().equals("jpg")) {
						System.out.println(
								"Extension de fichier : " + getExtensionByStringHandling(file.getAbsolutePath()).get());
						System.out.println(file.getAbsolutePath());
						p.add(file.getName());
					}
				}
			}
			listview.getItems().clear();
			listview.getItems().addAll(p);
			listview.setVisible(true);
			listview.setDisable(false);
			labelList.setVisible(true);
			labelList.setText("Liste des fichiers disponibles pour le dossier suivant : \n"
					+ selectedDirectory.getAbsolutePath());
			labelList.setDisable(false);

		}
	}

	// Change the image printed on screen based on the value of the slider
	void changeImageSliderValue(int newImageValue) throws IOException {
		System.out.println(maxImage);
		if (newImageValue >= 0 && newImageValue <= maxImage) {
			sliderImage.setValue(newImageValue);
			Model.getInstance().dicomLoader.dicomImage = Model.getInstance().dicomLoader
					.chargeImageDicomBufferise(newImageValue);
			currentImage = convertToFxImage(Model.getInstance().dicomLoader.dicomImage);
			imageContainer.setImage(null);
			imageContainer.setImage(currentImage);
		} else {
			sliderTextField.setText(null);
		}
		sliderTextField.prefColumnCountProperty().bind(sliderTextField.textProperty().length());

	}

	// méthode qui permet de sélectionner une image dicom et d'avoir accès au slider
	void putDicomImage(String fileAbsolutePath) {
		try {
			Model.getInstance().dicomLoader = new DicomLoader(fileAbsolutePath, 0);
			currentImage = convertToFxImage(Model.getInstance().dicomLoader.dicomImage);
			imageContainer.setImage(null);
			imageContainer.setImage(currentImage);
			traitementImageButton.setVisible(true);
			traitementImageButton.setDisable(false);
			changeImage.setVisible(true);
			changeImage.setDisable(false);
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
						Model.getInstance().dicomLoader.dicomImage = null;
						System.gc();
						Model.getInstance().dicomLoader.dicomImage = Model.getInstance().dicomLoader
								.chargeImageDicomBufferise(value);
						sliderTextField.setText(Integer.toString(value));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Deletre proprement l'image
					currentImage = null;
					imageContainer.setImage(null);
					System.gc();
					currentImage = convertToFxImage(Model.getInstance().dicomLoader.dicomImage);
					imageContainer.setImage(currentImage);
					imageContainer1.setVisible(true);
					imageContainer1.setDisable(false);
					labelSlider.setVisible(true);
				}
			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Méthode qui recoit un fichier en entrée et selon son type s'occupe de la
	// suite
	void putImage(File fc) throws IOException {
		if (fc != null) {

			if (getExtensionByStringHandling(fc.getAbsolutePath()).get().equals("dcm")
					|| getExtensionByStringHandling(fc.getAbsolutePath()).get().equals("dicom")) {
				putDicomImage(fc.getAbsolutePath());
			} else if (getExtensionByStringHandling(fc.getAbsolutePath()).get().equals("png")
					|| getExtensionByStringHandling(fc.getAbsolutePath()).get().equals("jpg")) {// On a une image en
																								// .png ou .jpg
				Model.getInstance().pretraitement.echographyImg = ImageIO.read(fc);
				Model.getInstance().pretraitement.ThreadBuffImagetoPixelMatrix(false,
						Model.getInstance().pretraitement.echographyImg);
				App.setRoot("secondary");
			}
		}
	}
}
