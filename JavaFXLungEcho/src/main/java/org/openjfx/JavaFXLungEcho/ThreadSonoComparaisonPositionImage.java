package org.openjfx.JavaFXLungEcho;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * 
 * @author Antoine Delenclos
 *Cette classe correspond à un thread qui permet de recalculer une image selon
 *l'image de base ainsi que d'autre paramètre
 */
public class ThreadSonoComparaisonPositionImage implements Runnable {

	private int nThread;
	private BufferedImage newImage;
	private int[][] newPixelLevel;

	private int[] pointsPenteGauche;
	private int[] pointsPenteDroite;
	private int[] pointsCourbeHaute;
	private int[] pointsCourbeBasseGauche;
	private int[] pointsCourbeBasseDroite;
	private int height_min;
	private int height_max;
	private int width_min;
	private int width_max;
	private boolean[][] booleanZipf;
	public int gOmega;
	public int dOmega;
	public int h0;
	public int h2;

	public ThreadSonoComparaisonPositionImage(int nThread, boolean[][] booleanZipf, int[] pointsPenteGauche,
			int[] pointsPenteDroite, int[] pointsCourbeHaute, int[] pointsCourbeBasseGauche, int[] pointsCourbeBasseDroite,
			int gOmega, int newHeight, int midWidth, int h0, int z, int dOmega, int prevHGY, int prevBGY, int h2) {
		super();
		this.nThread = nThread;
		this.pointsPenteGauche = pointsPenteGauche;
		this.pointsPenteDroite = pointsPenteDroite;
		this.pointsCourbeHaute = pointsCourbeHaute;
		this.pointsCourbeBasseGauche = pointsCourbeBasseGauche;
		this.pointsCourbeBasseDroite = pointsCourbeBasseDroite;
		this.booleanZipf = booleanZipf;
		this.gOmega = gOmega;
		this.dOmega = dOmega;
		this.h0 = h0;
		this.h2 = h2;
		switch (nThread) {
		case 0: //Partie supérieur gauche : pente gauche
			this.height_min = h0;
			this.height_max = newHeight;
			this.width_min = gOmega;
			this.width_max = midWidth;
			break;
		case 1: //Partie supérieur au dessus de la courbe haute
			this.height_min = h0;
			this.height_max = prevHGY;
			this.width_min = midWidth;
			this.width_max = z;
			break;
		case 2: //Partie centrale
			this.height_min = prevHGY;
			this.height_max = newHeight;
			this.width_min = midWidth;
			this.width_max = z;
			break;
		case 3: //Partie supérieur droite : pente droite
			this.height_min = h0;
			this.height_max = newHeight;
			this.width_min = z;
			this.width_max = dOmega;
			break;
		case 4: //Partie inférieure gauche
			this.height_min = newHeight;
			this.height_max = prevBGY;
			this.width_min = gOmega;
			this.width_max = midWidth;
			break;
		case 5: //Partie inférieure droite
			this.height_min = newHeight;
			this.height_max = prevBGY;
			this.width_min = z;
			this.width_max = dOmega;
		}
		this.height_min -= h0; this.height_max -= h0;
		this.width_min -= gOmega; this.width_max -= gOmega;
	}

	@Override
	public void run() {
		int i_sono = 0;
		int j_sono = 0;
		for (int i = height_min; i < height_max; i++) {
			for (int j = width_min; j < width_max; j++) {
				if (j >= gOmega && j <= dOmega && i >= h0 && i <= h2) { // on est dans la zone de l'echographie
					i_sono = i - h0;
					j_sono = j - gOmega;
				//if (j >= gOmega && j <= dOmega && i >= h0 && i <= h2) { // on est dans la zone de l'echographie
					/*i_sono = i - h0;
					j_sono = j - gOmega;
					newPixelLevel[i_sono][j_sono] = oldPixelLevel[i][j];
					Color greyRGBColor = new Color(newPixelLevel[i_sono][j_sono], newPixelLevel[i_sono][j_sono],
							newPixelLevel[i_sono][j_sono]);
					int greyRGB = greyRGBColor.getRGB();*/
					ArrayList<Integer> point = new ArrayList<Integer>(2); // Le point actuel
					point.add(j); //x
					point.add(i); //y
					int x = point.get(0); int y = point.get(1);
					switch (nThread) {
					
//int[] = { 0,0,0,1,0,2,0,3}
					
					case 0: //Partie supérieur gauche : pente gauche
						//Pour chaque point : chercher si le point de la pente est avant ou après (sur une même ligne)
						if (y>pointsPenteGauche[2*(x-width_min)+1]) { //Si le point est en dessous de la pente gauche
							booleanZipf[y][x] = true;
						}
						break;
					case 1: //Partie supérieur au dessus de la courbe haute
						if (y>pointsCourbeHaute[2*(x-width_min)+1]) {
							booleanZipf[y][x] = true;
						}
						break;
					case 2: //Partie centrale
						booleanZipf[y][x] = true;
						break;
					case 3: //Partie supérieur droite : pente droite
						if (y>pointsPenteDroite[2*(x-width_min)+1]) {
							booleanZipf[y][x] = true;
						}
						break;
					case 4: //Partie inférieure gauche : courbe basse gauche
						if (y<pointsCourbeBasseGauche[2*(x-width_min)+1]) {
							booleanZipf[y][x] = true;
						}
						break;
					case 5: //Partie inférieure droite : courbe basse droite
						if (y<pointsCourbeBasseDroite[2*(x-width_min)+1]) {
							booleanZipf[y][x] = true;
						}
						break;
					}
				}
			}
		}
		//System.out.println("Je suis le thread numero "+nbThread+" et je remplis de la hauteur "+height_min+" à "+height_max);
	}

}