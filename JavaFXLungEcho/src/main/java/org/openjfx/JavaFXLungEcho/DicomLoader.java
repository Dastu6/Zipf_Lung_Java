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

public class DicomLoader {
	private File f;

	public DicomLoader(String fileName) throws IOException {
		f = new File(fileName);
		sauverImage(chargeImageDicomBufferise(f, 5), "test2");
	}

	public BufferedImage chargeImageDicomBufferise(File file, int value) throws IOException {
		Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");// spécifie l'image
		ImageReader readers = iter.next();// on se déplace dans l'image dicom
		DicomImageReadParam param1 = (DicomImageReadParam) readers.getDefaultReadParam();// return DicomImageReadParam
		// Adjust the values of Rows and Columns in it and add a Pixel Data attribute
		// with the bytearray from the DataBuffer of the scaled Raster
		ImageInputStream iis = ImageIO.createImageInputStream(file);
		readers.setInput(iis, false);// sets the input source to use the given ImageInputSteam or otherObject
		BufferedImage image = readers.read(value, param1);// BufferedImage image =reader.read(frameNumber, param);
															// frameNumber = int qui est l'imageIndex
		System.out.println(image);// affichage au terminal des caractères de l'image
		readers.dispose();// Releases all of the native sreen resources used by this Window,
							// itssubcomponents, and all of its owned children
		return image;
	}

	public void sauverImage(BufferedImage image, String nomImage) throws IOException {
		File nomfichier = new File("target\\classes\\images\\saved_or_converted\\" + nomImage + ".png");// ou
																													// jpg
		// image = ;
		ImageIO.write(removeAlphaChannel(image), "PNG", nomfichier);// ou JPG
	}

	private BufferedImage removeAlphaChannel(BufferedImage img) {
		if (!img.getColorModel().hasAlpha()) {
			return img;
		}

		BufferedImage target = createImage(img.getWidth(), img.getHeight(), false);
		Graphics2D g = target.createGraphics();
		// g.setColor(new Color(color, false));
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return target;
	}

	private BufferedImage createImage(int width, int height, boolean hasAlpha) {
		return new BufferedImage(width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
	}

}