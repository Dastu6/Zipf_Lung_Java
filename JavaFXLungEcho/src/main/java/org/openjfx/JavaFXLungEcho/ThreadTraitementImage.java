package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
/**
 *  
 * @author Esteban Fernandes
 *
 */
public class ThreadTraitementImage implements Runnable {
	private int[][] returnpixellevel;
	private BufferedImage bufferImg;
	private int maxThread;
	private int nbThread;

	public ThreadTraitementImage(int[][] returnpixellevel, int nbMaxThread, BufferedImage buffImg, int nbThread) {
		this.returnpixellevel = returnpixellevel;
		maxThread = nbMaxThread;
		this.bufferImg = buffImg;
		this.nbThread = nbThread;
	}

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
				//Utilisation de la norme rec 709
				returnpixellevel[i][j] = (int)(0.2126 * red + 0.7152 * green + 0.0722 * blue);
			}
		}
		System.out.println("Je suis le thread numero "+nbThread+" et je remplis de la hauteur "+height_min+" à "+height_max);
	}

}
