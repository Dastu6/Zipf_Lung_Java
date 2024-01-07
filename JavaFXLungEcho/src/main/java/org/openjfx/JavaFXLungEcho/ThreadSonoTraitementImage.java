package org.openjfx.JavaFXLungEcho;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * 
 * @author Esteban Fernandes
 *Cette classe correspond à un thread qui permet de recalculer une image selon
 *l'image de base ainsi que d'autre paramètre
 */
public class ThreadSonoTraitementImage implements Runnable {

	private int maxThread;
	private int nbThread;
	private BufferedImage newImage;
	private int[][] newPixelLevel;
	private int[][] oldPixelLevel;

	private ArrayList<ArrayList<Integer>> pointsPenteGauche;
	private ArrayList<ArrayList<Integer>> pointsPenteDroite;
	private ArrayList<ArrayList<Integer>> pointsCourbeHaute;
	private ArrayList<ArrayList<Integer>> pointsCourbeBasseGauche;
	private ArrayList<ArrayList<Integer>> pointsCourbeBasseDroite;
	private int h0;
	private int gOmega;
	private int dOmega;
	private int h2;

	public ThreadSonoTraitementImage(int maxThread, int nbThread, BufferedImage newImage, int[][] newPixelLevel,
			int[][] oldPixelLevel, ArrayList<ArrayList<Integer>> pointsPenteGauche,
			ArrayList<ArrayList<Integer>> pointsPenteDroite, ArrayList<ArrayList<Integer>> pointsCourbeHaute, ArrayList<ArrayList<Integer>> pointsCourbeBasseGauche, ArrayList<ArrayList<Integer>> pointsCourbeBasseDroite, int h0, int gOmega, int dOmega, int h2) {
		super();
		this.maxThread = maxThread;
		this.nbThread = nbThread;
		this.newImage = newImage;
		this.newPixelLevel = newPixelLevel;
		this.oldPixelLevel = oldPixelLevel;
		this.pointsPenteGauche = pointsPenteGauche;
		this.pointsPenteDroite = pointsPenteDroite;
		this.pointsCourbeHaute = pointsCourbeHaute;
		this.pointsCourbeBasseGauche = pointsCourbeBasseGauche;
		this.pointsCourbeBasseDroite = pointsCourbeBasseDroite;
		this.h0 = h0;
		this.gOmega = gOmega;
		this.dOmega = dOmega;
		this.h2 = h2;
	}

	@Override
	public void run() {
		int oldHeight = oldPixelLevel.length;
		int oldWidth = oldPixelLevel[0].length;
		int height_min = (oldHeight / maxThread) * nbThread;
		int height_max = (oldHeight / maxThread) * (nbThread + 1);
		if (nbThread == maxThread - 1)
			height_max = oldHeight;
		int i_sono = 0;
		int j_sono = 0;
		for (int i = height_min; i < height_max; i++) {
			for (int j = 0; j < oldWidth; j++) {
				if (j >= gOmega && j <= dOmega && i >= h0 && i <= h2) { // on est dans la zone de l'echographie
					i_sono = i - h0;
					j_sono = j - gOmega;
					newPixelLevel[i_sono][j_sono] = oldPixelLevel[i][j];
					Color greyRGBColor = new Color(newPixelLevel[i_sono][j_sono], newPixelLevel[i_sono][j_sono],
							newPixelLevel[i_sono][j_sono]);
					int greyRGB = greyRGBColor.getRGB();
					// Si l'image est dans les points de l'échographie
					ArrayList<Integer> point = new ArrayList<Integer>(2); // Le point actuel
					point.add(j); //x
					point.add(i); //y
					if (pointsPenteGauche.contains(point) || pointsPenteDroite.contains(point) || pointsCourbeHaute.contains(point) || pointsCourbeBasseGauche.contains(point) || pointsCourbeBasseDroite.contains(point)) {
						greyRGBColor = new Color(255, 0, 0);
						greyRGB = greyRGBColor.getRGB();
					}
					newImage.setRGB(j_sono, i_sono, greyRGB);
				}
			}
		}
		//System.out.println("Je suis le thread numero "+nbThread+" et je remplis de la hauteur "+height_min+" à "+height_max);
	}

}
