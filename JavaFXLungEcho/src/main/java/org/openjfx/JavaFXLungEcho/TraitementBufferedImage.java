package org.openjfx.JavaFXLungEcho;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class TraitementBufferedImage {
	public BufferedImage buffImg;
	public int[][] greyPixelsLevels;
	public int minimumGreyIntensityBeforeTreatment;
	public int seuil_detect_debut; //Car ce n'est pas forcément la valeur min on peut passer de 2 à 4 mais il n'y a pas d'info echo
	public int seuil_detect_fin;
	public int[][] greyMatrixOnlySonogram;
	public BufferedImage echographyImg;
	public TraitementBufferedImage() {
		
	}
	
	//Fonction qui va permettre de stocker la matrice de pixels d'une des frames de l'image dicom
	public void BufferedImageToPixelMatrix(BufferedImage bufferImg) {
		int widthImg = bufferImg.getWidth();
		int heightImg = bufferImg.getHeight();
		greyPixelsLevels = new int[heightImg][widthImg];
		int min = 256;
		for (int i = 0; i < heightImg; i++) {
			for (int j = 0; j < widthImg; j++) {
				int col = bufferImg.getRGB(j, i);
				int red = col & 0xff0000 >> 16;
				int green = col & 0xff00 >> 8;
				int blue = col & 0xff;
				//Utilisation de la norme rec 709
				greyPixelsLevels[i][j] = (int)(0.2126 * red + 0.7152 * green + 0.0722 * blue);
				if (min > greyPixelsLevels[i][j]) {
					min = greyPixelsLevels[i][j]; //Le minimum est 2
				}
			}
		}
		minimumGreyIntensityBeforeTreatment = min;
	}
	
	//Fonction qui va regarder si un pixel d'une matrice possède un pixel non nul au dessus de lui
	public boolean pixelHasNonBlackPixelAbove(int[][] matrix, int x, int y) {
		int row = y;
		while (row > 1) {
			if (greyPixelsLevels[row][x] != 2*minimumGreyIntensityBeforeTreatment) {
				return true;
			}
			row--;
		}
		return false;
	}
	
	//Fonction qui va créer un nouveau fichier PNG pour ne garder que la zone de l'échographie
	//Cette fonction améliore également le contraste de l'échographie
	public void BufferedImageToSonogram() {
		seuil_detect_debut = 10;
		seuil_detect_fin = minimumGreyIntensityBeforeTreatment;
		int oldWidth = buffImg.getWidth();
		int oldHeight = buffImg.getHeight();
		//Etape Omega : Trouver les triangles de la courbe du bas
		//On va se baser sur la barre de graduation et regarder chercher la 1ère barre puis mesurer l'écart avec la précédente
		//dès qu'un écart va être plus petit ou bien qu'une barre a une épaisseur plus élevée (cas où le triangle se superpose)
		//on a trouvé le triangle
		int hOmega_diff = 0;
		int hOmega = 0;
		int triangle_grey_value = 0;
		//Suppression de la bande blanche si elle existe
		
		//Ici on trouve le triangle de droite : 1er point jusqu'au dernier (anciennement étape 4)
		for (int i = 0; i< oldHeight; i++) {
			if (greyPixelsLevels[i][oldWidth-1] >= seuil_detect_debut && greyPixelsLevels[i+1][oldWidth-1] >= seuil_detect_debut) {
				triangle_grey_value = greyPixelsLevels[i][oldWidth-1];
				hOmega = i;
				hOmega_diff++;
			}
		}
		int dOmega = oldWidth-1;
		int gOmega = 0;
		while (greyPixelsLevels[hOmega][gOmega] != triangle_grey_value) { //On a ici le second triangle
			gOmega++;
		}

		//Etape 0 : trouver le milieu de l'image actuelle et descendre jusqu'au premier pixel de l'image.
		int midWidth = (int)(((oldWidth-1)-gOmega)/2);
		int h0 = 0;
		
		while (greyPixelsLevels[h0][midWidth] <= seuil_detect_debut && h0 < oldHeight) { //La boucle s'arrête lorsqu'on trouve un pixel non noir (donc un point de l'écho)
			h0++;
		}
		
		//Etape 2 : Trouver le point le plus bas de l'échographie
		int h2 = oldHeight-1;
		while (greyPixelsLevels[h2][midWidth] < seuil_detect_debut) { //On part du bas et on remonte
			h2--;
		}
		
		//Etape 6 : calcul de la nouvelle image 
		int newWidth = dOmega - gOmega + 1;
		int newHeight = h2 - h0 + 1;
		greyMatrixOnlySonogram = new int[newHeight][newWidth];
		int i_sono = 0;
		int j_sono = 0;
		echographyImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < oldHeight; i++) {
			for (int j = 0; j < oldWidth - 1; j++) {
				if (j >= gOmega && j <= dOmega && i >= h0 && i <= h2) { //on est dans la zone de l'echographie
					i_sono = i - h0;
					j_sono = j - gOmega;
					greyMatrixOnlySonogram[i_sono][j_sono] = greyPixelsLevels[i][j];
					Color greyRGBColor = new Color(greyMatrixOnlySonogram[i_sono][j_sono],greyMatrixOnlySonogram[i_sono][j_sono],greyMatrixOnlySonogram[i_sono][j_sono]);
					int greyRGB = greyRGBColor.getRGB();
					echographyImg.setRGB(j_sono ,i_sono , greyRGB);
				}
			}
		}
	}
}
