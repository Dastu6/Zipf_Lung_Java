package org.openjfx.JavaFXLungEcho;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che3.imageio.plugins.dcm.*;
// TODO: Auto-generated Javadoc

/**
 * Classe chargée de charger des images dicom et ressortir une bufferedImage
 */
public class DicomLoader {
	
	/** The dicom file. */
	private File dicomFile;
	
	/** The dicom image. */
	public BufferedImage dicomImage;
	
	/**
	 * Instantiates a new dicom loader.
	 *
	 * @param absolutePath the absolute path of the File
	 * @param frameIndex the frame index of the dicom Image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	//Fonction appelée pour charger, sauvegarder une frame d'un fichier Dicom
	public DicomLoader(String absolutePath, int frameIndex) throws IOException
	{
		dicomFile = new File(absolutePath);
		dicomImage = chargeImageDicomBufferise(frameIndex);

	}
	
	/**
	 * Instantiates a new dicom loader.
	 *
	 * @param dirPath the dir path
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public DicomLoader(String dirPath,String fileName) throws IOException
	{
		
		dicomFile = new File(dirPath +"\\"+ fileName );
	}
	
	/**
	 * Instantiates a new dicom loader.
	 *
	 * @param dirPath the dir path
	 * @param fileName the file name
	 * @param frameIndex the frame index
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public DicomLoader(String dirPath,String fileName, int frameIndex) throws IOException
	{
		dicomFile = new File(dirPath +"\\"+ fileName );
		dicomImage = chargeImageDicomBufferise(frameIndex);
		sauverImage(dicomImage,fileName);
	}
	
	
	/**
	 * Instantiates a new dicom loader.
	 */
	public DicomLoader()
	{
		
	}
	
	/**
	 * Gets the nb images.
	 *
	 * @return the nb images
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	int getNbImages() throws IOException
	{
		Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");//spécifie l'image
		 ImageReader readers = iter.next();//on se déplace dans l'image dicom
		DicomImageReadParam param1 = (DicomImageReadParam) readers.getDefaultReadParam();//return DicomImageReadParam
		 // Adjust the values of Rows and Columns in it and add a Pixel Data attribute with the bytearray from the DataBuffer of the scaled Raster
		 ImageInputStream iis = ImageIO.createImageInputStream(dicomFile);
		 readers.setInput(iis, false);
		return readers.getNumImages(true);
	}
	
	/**
	 * Charge image dicom bufferise.
	 *
	 * @param value the value
	 * @return the buffered image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public BufferedImage chargeImageDicomBufferise(int value) throws IOException { //Value = frame du fichier dicom
		 Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");//spécifie l'image
		 ImageReader readers = iter.next();//on se déplace dans l'image dicom
		DicomImageReadParam param1 = (DicomImageReadParam) readers.getDefaultReadParam();//return DicomImageReadParam
		 // Adjust the values of Rows and Columns in it and add a Pixel Data attribute with the bytearray from the DataBuffer of the scaled Raster
		 ImageInputStream iis = ImageIO.createImageInputStream(dicomFile);
		 readers.setInput(iis, false);//sets the input source to use the given ImageInputSteam or otherObject
		 BufferedImage image = readers.read(value,param1);//BufferedImage image =reader.read(frameNumber, param); frameNumber = int qui est l'imageIndex
		 
		 System.out.println("buffered image data : " + image);//affichage au terminal des caractères de l'image
		 //Essayez de l'enlever pour que ça lag moinsx
		 readers.dispose();//Releases all of the native sreen resources used by this Window, itssubcomponents, and all of its owned children
		 return image;
	}
	
	/**
	 * Sauvegarde l'image au format png dans le chemin passé en paramètre
	 *
	 * @param image the image
	 * @param nomImage Le chemin vers la nouvelle image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sauverImage(BufferedImage image, String nomImage) throws IOException
	{
	File nomfichier = new File("src/main/resources/images/saved_or_converted/" + nomImage + ".png");// ou jpg
	ImageIO.write(removeAlphaChannel(image), "PNG", nomfichier);//ou JPG
	}
	
	/**
	 * Removes the alpha channel.
	 *
	 * @param img the img
	 * @return the buffered image
	 */
	private  BufferedImage removeAlphaChannel(BufferedImage img) {
	    if (!img.getColorModel().hasAlpha()) {
	        return img;
	    }

	    BufferedImage target = createImage(img.getWidth(), img.getHeight(), false);
	    Graphics2D g = target.createGraphics();
	    g.fillRect(0, 0, img.getWidth(), img.getHeight());
	    g.drawImage(img, 0, 0, null);
	    g.dispose();

	    return target;
	}
	
	/**
	 * Creates the image.
	 *
	 * @param width the width
	 * @param height the height
	 * @param hasAlpha the has alpha
	 * @return the buffered image
	 */
	private  BufferedImage createImage(int width, int height, boolean hasAlpha) {
	    return new BufferedImage(width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
	}
	
}
