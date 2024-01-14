package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
// TODO: Auto-generated Javadoc
/**
 *  
 * @author Esteban Fernandes & Antoine Delenclos
 *
 */
public class ThreadTraitementImage implements Runnable {
	
	/** The matrix of pixels levels. */
	private int[][] returnpixellevel;
	
	/** The buffer image. */
	private BufferedImage bufferImg;
	
	/** The max thread. */
	private int maxThread;
	
	/** The thread number. */
	private int nbThread;

	/**
	 * Instantiates a new thread traitement image.
	 *
	 * @param returnpixellevel the pixel levels
	 * @param nbMaxThread the nb max thread
	 * @param buffImg the buff image
	 * @param nbThread the thread number
	 */
	public ThreadTraitementImage(int[][] returnpixellevel, int nbMaxThread, BufferedImage buffImg, int nbThread) {
		this.returnpixellevel = returnpixellevel;
		maxThread = nbMaxThread;
		this.bufferImg = buffImg;
		this.nbThread = nbThread;
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		int widthImg = bufferImg.getWidth();
		int heightImg = bufferImg.getHeight();
		int height_min = (heightImg / maxThread) * nbThread;
		int height_max = (heightImg / maxThread) * (nbThread + 1);
		if (nbThread == maxThread - 1)
			height_max = heightImg;

		for (int i = height_min; i < height_max; i++) {
			for (int j = 0; j < widthImg; j++) {
				int col = bufferImg.getRGB(j, i);
				int red = col & 0xff0000 >> 16;
				int green = col & 0xff00 >> 8;
				int blue = col & 0xff;
				//Utilisation de la norme rec 709 pour une meilleure représentation des nuances de gris
				returnpixellevel[i][j] = (int)(0.2126 * red + 0.7152 * green + 0.0722 * blue);
			}
		}
		System.out.println("Je suis le thread numero "+nbThread+" et je remplis de la hauteur "+height_min+" à "+height_max);
	}

}
