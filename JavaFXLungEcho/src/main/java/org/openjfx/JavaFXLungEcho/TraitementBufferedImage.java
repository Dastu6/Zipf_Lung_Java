package org.openjfx.JavaFXLungEcho;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TraitementBufferedImage {
	public BufferedImage buffImg;
	public int[][] greyPixelsLevels;
	public int minimumGreyIntensityBeforeTreatment;
	public int seuil_detect_debut; // Car ce n'est pas forcément la valeur min on peut passer de 2 à 4 mais il n'y
									// a pas d'info echo
	public int seuil_detect_fin;
	public int[][] greyMatrixOnlySonogram;
	public boolean[][] booleanZipfMatrix;
	public BufferedImage echographyImg;
	public int[] array_pente_gauche;
	public int[] array_pente_droite;
	public int[] array_courbe_haute;
	public int[] array_courbe_basse_gauche;
	public int[] array_courbe_basse_droite;
	public int gOmega;
	public int newHeight;
	public int midWidth;
	public int z;
	public int h0;
	public int dOmega;
	public float prevHGY;
	public float prevBGY;
	public int h2;

	public TraitementBufferedImage() {

	}

	// Fonction qui va permettre de stocker la matrice de pixels d'une des frames de
	// l'image dicom
	// à la différence de l'autre celle-ci parallélise le calcul via des thread
	public void ThreadBuffImagetoPixelMatrix(boolean isDicom, BufferedImage bufferImg) {
		int widthImg = bufferImg.getWidth();
		int heightImg = bufferImg.getHeight();
		if (isDicom) {

			greyPixelsLevels = new int[heightImg][widthImg];
			int nbThread = Model.getInstance().nbThreadTraitement;
			for (int i = 0; i < nbThread; i++) {
				ThreadTraitementImage temp = new ThreadTraitementImage(greyPixelsLevels, nbThread, bufferImg, i);
				temp.run();
			}
		} else {
			BufferedImageToPixelMatrix(bufferImg);
			greyMatrixOnlySonogram = greyPixelsLevels;
		}
	}

	// Fonction qui va permettre de stocker la matrice de pixels d'une des frames de
	// l'image dicom
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
				// Utilisation de la norme rec 709
				greyPixelsLevels[i][j] = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
				if (min > greyPixelsLevels[i][j]) {
					min = greyPixelsLevels[i][j]; // Le minimum est 2
				}
			}
		}
		minimumGreyIntensityBeforeTreatment = min;
	}

	// Fonction qui va regarder si un pixel d'une matrice possède un pixel non nul
	// au dessus de lui
	public boolean pixelHasBlackPixelAbove(int[][] matrix, int x, int y) throws IllegalArgumentException {
		int row = y;
		if (row < 1 || x < 0) {
			throw new IllegalArgumentException("Trying to check for a pixel that doesn't exist");
		}
		// while (row > 1) {
		if (matrix[row - 1][x] <= seuil_detect_debut) {
			return true;
		}
		// row--;
		// }
		return false;
	}

	public boolean pixelHasBlackPixelBelow(int[][] matrix, int x, int y) throws IllegalArgumentException {
		if (y < 1 || x < 0) {
			throw new IllegalArgumentException("Trying to check for a pixel that doesn't exist");
		}
		if (matrix[y + 1][x] <= 2 * minimumGreyIntensityBeforeTreatment) {
			return true;
		}
		return false;
	}

	// Fonction qui va calculer la pente entre (x1,y1) et (x2,y2)
	public float slope(int x1, int y1, int x2, int y2) throws IllegalArgumentException {
		if (x2 == x1) {
			throw new IllegalArgumentException("Slope method can't take similar X values");
		}
		float p = (float) (y2 - y1) / (float) (x2 - x1);
		return p;
	}

	public void ThreadBufferedImageToSonogram() {

	}

	// Fonction qui va créer un nouveau fichier PNG pour ne garder que la zone de
	// l'échographie
	// Cette fonction améliore également le contraste de l'échographie
	public void BufferedImageToSonogram() throws Exception {
		long startTime = System.nanoTime();
		seuil_detect_debut = 10;
		seuil_detect_fin = minimumGreyIntensityBeforeTreatment;
		int oldWidth = buffImg.getWidth();
		int oldHeight = buffImg.getHeight();
		// Etape Omega : Trouver les triangles de la courbe du bas
		// On va se baser sur la barre de graduation et regarder chercher la 1ère barre
		// puis mesurer l'écart avec la précédente
		// dès qu'un écart va être plus petit ou bien qu'une barre a une épaisseur plus
		// élevée (cas où le triangle se superpose)
		// on a trouvé le triangle
		int hOmega_diff = 0;
		int hOmega = 0;
		int triangle_grey_value = 0;
		// Suppression de la bande blanche si elle existe

		// Ici on trouve le triangle de droite : 1er point jusqu'au dernier
		// (anciennement étape 4)
		for (int i = 0; i < oldHeight; i++) {
			if (greyPixelsLevels[i][oldWidth - 1] >= seuil_detect_debut
					&& greyPixelsLevels[i + 1][oldWidth - 1] >= seuil_detect_debut) {
				triangle_grey_value = greyPixelsLevels[i][oldWidth - 1];
				hOmega = i;
				hOmega_diff++;
			}
		}
		dOmega = oldWidth - 1;
		gOmega = 0;
		while (greyPixelsLevels[hOmega][gOmega] != triangle_grey_value) { // On a ici le second triangle
			gOmega++;
		}

		// Etape 0 : trouver le milieu de l'image actuelle et descendre jusqu'au premier
		// pixel de l'image.
		midWidth = (int) (((oldWidth - 1) - gOmega) / 2);
		h0 = 0;

		while (greyPixelsLevels[h0][midWidth] <= seuil_detect_debut && h0 < oldHeight) { // La boucle s'arrête lorsqu'on
																							// trouve un pixel non noir
																							// (donc un point de l'écho)
			h0++;
		}

		// Etape 2 : Trouver le point le plus bas de l'échographie
		h2 = oldHeight - 1;
		while (greyPixelsLevels[h2][midWidth] < seuil_detect_debut) { // On part du bas et on remonte
			h2--;
		}
		// Etape P : On va calculer les pentes et contours
		z = dOmega + gOmega - midWidth + 1;
		int newWidth = dOmega - gOmega + 1;
		newHeight = h2 - h0 + 1;
		// Courbe du bas droite : ( , ) -> ( , )
		// Courbe du bas gauche : ( , ) -> ( , )
		float penteGauche = slope(gOmega, newHeight, midWidth, h0); // On part du point le plus à gauche
		float penteDroite = slope(z, h0, dOmega, newHeight); // On part du point le plus à gauche

		///////////////// PENTES///////////////////////::

		// On a la liste de tous les points qui sont sur la pente gauche
//		ArrayList<ArrayList<Float>> pointsPenteGaucheTemp = new ArrayList<ArrayList<Float>>();
//		ArrayList<Float> aG0 = new ArrayList<Float>(2);
//		aG0.add((float)gOmega); aG0.add((float)newHeight); 
//		pointsPenteGaucheTemp.add(aG0);
//		float prevGX = gOmega; float prevGY = newHeight;
//		for (int x = 1; x < (int)midWidth-gOmega; x++) {
//			ArrayList<Float> a = new ArrayList<Float>(2);
//			a.add(prevGX+1); a.add((float) (prevGY+penteGauche));
//			pointsPenteGaucheTemp.add(a);
//			prevGX += 1; prevGY = (float) (prevGY+penteGauche);
//		}
//		ArrayList<ArrayList<Integer>> pointsPenteGauche = new ArrayList<ArrayList<Integer>>();
//		for (ArrayList<Float> aGTemp : pointsPenteGaucheTemp) {
//			ArrayList<Integer> aG = new ArrayList<Integer>(2);
//			float aGX = aGTemp.get(0); float aGY = aGTemp.get(1);
//			aG.add((int)aGX); aG.add((int)aGY);
//			pointsPenteGauche.add(aG);
//		}
//		
//		//On va créer une liste temporaire de flotants car la pente est un float
//		ArrayList<ArrayList<Float>> pointsPenteDroiteTemp = new ArrayList<ArrayList<Float>>();
//		ArrayList<Float> aD0 = new ArrayList<Float>(2);
//		aD0.add((float)z); aD0.add((float)h0); 
//		pointsPenteDroiteTemp.add(aD0);
//		float prevDX = z; float prevDY = h0;
//		for (int x = 1; x < (int)dOmega-z; x++) {
//			ArrayList<Float> a = new ArrayList<Float>(2);
//			a.add(prevDX+1); a.add((float)(prevDY+penteDroite));
//			pointsPenteDroiteTemp.add(a);
//			prevDX += 1; prevDY = (float)(prevDY+penteDroite);
//		}
//		//On va maintenant faire la vraie liste des points où on va recast en int car pixel : (int x, int y)
//		ArrayList<ArrayList<Integer>> pointsPenteDroite = new ArrayList<ArrayList<Integer>>();
//		for (ArrayList<Float> aDTemp : pointsPenteDroiteTemp) {
//			ArrayList<Integer> aD = new ArrayList<Integer>(2);
//			float aDX = aDTemp.get(0); float aDY = aDTemp.get(1);
//			aD.add((int)aDX); aD.add((int)aDY);
//			pointsPenteDroite.add(aD);
//		}

		ArrayList<ArrayList<Integer>> pointsPenteGauche = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> pointsPenteDroite = new ArrayList<ArrayList<Integer>>();

		ThreadPenteTraitementImage gauche = new ThreadPenteTraitementImage(true, pointsPenteGauche, (float) gOmega,
				midWidth, (float) newHeight, penteGauche);
		ThreadPenteTraitementImage droite = new ThreadPenteTraitementImage(false, pointsPenteDroite, (float) h0, dOmega,
				(float) z, penteDroite);
		gauche.run();

		droite.run();

		/////////////////// COURBES///////////////////////////////
		// On va calculer la courbe haute de la zone de l'échographie : d'abord partie
		/////////////////// gauche puis droite
		ArrayList<ArrayList<Float>> pointsCourbeHauteGaucheTemp = new ArrayList<ArrayList<Float>>();

		ArrayList<Float> aHG0 = new ArrayList<Float>(2); // Point gauche pente haute
		aHG0.add((float) midWidth);
		aHG0.add((float) h0);
		pointsCourbeHauteGaucheTemp.add(aHG0);
		float prevHGX = midWidth;
		prevHGY = h0;
		for (int x = 1; x < (int) ((z - midWidth) / 2); x++) {
			ArrayList<Float> a = new ArrayList<Float>(2);
			int countX = 1;
			int countY = 0;
			// On va se décaler d'un vers la droite en abscisse et tant qu'il y a un pixel
			// nul au dessus on descend d'un en ordonée
			// Puis une fois que le pixel au dessus est non nul, on conserve ce pixel là
			while (greyPixelsLevels[(int) prevHGY + countY][(int) prevHGX + countX] <= seuil_detect_debut) {
				countY++;
			}
			a.add(prevHGX + countX);
			a.add(prevHGY + countY);
			pointsCourbeHauteGaucheTemp.add(a);
			prevHGX += countX;
			prevHGY += countY;
		}
		ArrayList<ArrayList<Float>> pointsCourbeHauteDroiteTemp = new ArrayList<ArrayList<Float>>();
		ArrayList<Float> aHD0 = new ArrayList<Float>(2);
		aHD0.add((float) z);
		aHD0.add((float) h0);
		pointsCourbeHauteDroiteTemp.add(aHD0);
		float prevHDX = z;
		float prevHDY = h0;
		while (prevHDX > prevHGX) {
			ArrayList<Float> a = new ArrayList<Float>(2);
			int countX = 1;
			int countY = 0;
			while (greyPixelsLevels[(int) prevHDY + countY][(int) prevHDX - countX] <= seuil_detect_debut) {
				countY++;
			}
			a.add(prevHDX - countX);
			a.add(prevHDY + countY);
			pointsCourbeHauteDroiteTemp.add(a);
			prevHDX -= countX;
			prevHDY += countY;
		}

		ArrayList<ArrayList<Integer>> pointsCourbeHaute = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Float> aHGTemp : pointsCourbeHauteGaucheTemp) {
			ArrayList<Integer> aHG = new ArrayList<Integer>(2);
			float aHGX = aHGTemp.get(0);
			float aHGY = aHGTemp.get(1);
			aHG.add((int) aHGX);
			aHG.add((int) aHGY);
			pointsCourbeHaute.add(aHG);
		}
		for (ArrayList<Float> aHDTemp : pointsCourbeHauteDroiteTemp) {
			ArrayList<Integer> aHD = new ArrayList<Integer>(2);
			float aHDX = aHDTemp.get(0);
			float aHDY = aHDTemp.get(1);
			aHD.add((int) aHDX);
			aHD.add((int) aHDY);
			pointsCourbeHaute.add(aHD);
		}

		ArrayList<ArrayList<Float>> pointsCourbeBasseGaucheTemp = new ArrayList<ArrayList<Float>>();
		ArrayList<Float> aBG0 = new ArrayList<Float>(2);
		aBG0.add((float) gOmega);
		aBG0.add((float) newHeight);
		pointsCourbeBasseGaucheTemp.add(aBG0);
		float prevBGX = gOmega;
		prevBGY = newHeight;
		while (prevBGX < midWidth) {
			ArrayList<Float> a = new ArrayList<Float>(2);
			int countX = 1;
			int countY = 0;
			while (greyPixelsLevels[(int) prevBGY + countY][(int) prevBGX + countX] > seuil_detect_debut) {
				countY++;
			}
			a.add(prevBGX + countX);
			a.add(prevBGY + countY);
			pointsCourbeBasseGaucheTemp.add(a);
			prevBGX += countX;
			prevBGY += countY;
		}

		ArrayList<ArrayList<Float>> pointsCourbeBasseDroiteTemp = new ArrayList<ArrayList<Float>>();
		ArrayList<Float> aBD0 = new ArrayList<Float>(2);
		aBD0.add((float) dOmega);
		aBD0.add((float) newHeight);
		pointsCourbeBasseDroiteTemp.add(aBD0);
		float prevBDX = dOmega;
		float prevBDY = newHeight;
		int limit = midWidth + (int) ((z - midWidth) / 2);
		while (prevBDX > limit) {
			ArrayList<Float> a = new ArrayList<Float>(2);
			int countX = 1;
			int countY = 0;
			while (greyPixelsLevels[(int) prevBDY + countY][(int) prevBDX - countX] > seuil_detect_debut) {
				countY++;
			}
			a.add(prevBDX - countX);
			a.add(prevBDY + countY);
			pointsCourbeBasseDroiteTemp.add(a);
			prevBDX -= countX;
			prevBDY += countY;
		}

		ArrayList<ArrayList<Integer>> pointsCourbeBasseGauche = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Float> aBGTemp : pointsCourbeBasseGaucheTemp) {
			ArrayList<Integer> aBG = new ArrayList<Integer>(2);
			float aBGX = aBGTemp.get(0);
			float aBGY = aBGTemp.get(1);
			aBG.add((int) aBGX);
			aBG.add((int) aBGY);
			pointsCourbeBasseGauche.add(aBG);
		}
		ArrayList<ArrayList<Integer>> pointsCourbeBasseDroite = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Float> aBDTemp : pointsCourbeBasseDroiteTemp) {
			ArrayList<Integer> aBD = new ArrayList<Integer>(2);
			float aBDX = aBDTemp.get(0);
			float aBDY = aBDTemp.get(1);
			aBD.add((int) aBDX);
			aBD.add((int) aBDY);
			pointsCourbeBasseDroite.add(aBD);
		}

		// Conversion ArrayList ArrayList vers int[]
		int taille_pente_gauche = pointsPenteGauche.size();
		int taille_pente_droite = pointsPenteDroite.size();
		int taille_courbe_haute = pointsCourbeHaute.size();
		int taille_courbe_basse_gauche = pointsCourbeBasseGauche.size();
		int taille_courbe_basse_droite = pointsCourbeBasseDroite.size();
		array_pente_gauche = new int[2 * taille_pente_gauche];
		array_pente_droite = new int[2 * taille_pente_droite];
		array_courbe_haute = new int[2 * taille_courbe_haute];
		array_courbe_basse_gauche = new int[2 * taille_courbe_basse_gauche];
		array_courbe_basse_droite = new int[2 * taille_courbe_basse_droite];
		for (int pg = 0; pg < taille_pente_gauche; pg++) {
			array_pente_gauche[2 * pg] = pointsPenteGauche.get(pg).get(0);
			array_pente_gauche[2 * pg + 1] = pointsPenteGauche.get(pg).get(1);
		}
		for (int pd = 0; pd < taille_pente_droite; pd++) {
			array_pente_droite[2 * pd] = pointsPenteDroite.get(pd).get(0);
			array_pente_droite[2 * pd + 1] = pointsPenteDroite.get(pd).get(1);
		}
		for (int ch = 0; ch < taille_courbe_haute; ch++) {
			array_courbe_haute[2 * ch] = pointsCourbeHaute.get(ch).get(0);
			array_courbe_haute[2 * ch + 1] = pointsCourbeHaute.get(ch).get(1);
		}
		for (int cbg = 0; cbg < taille_courbe_basse_gauche; cbg++) {
			array_courbe_basse_gauche[2 * cbg] = pointsCourbeBasseGauche.get(cbg).get(0);
			array_courbe_basse_gauche[2 * cbg + 1] = pointsCourbeBasseGauche.get(cbg).get(1);
		}
		for (int cbd = 0; cbd < taille_courbe_basse_droite; cbd++) {
			array_courbe_basse_droite[2 * cbd] = pointsCourbeBasseDroite.get(cbd).get(0);
			array_courbe_basse_droite[2 * cbd + 1] = pointsCourbeBasseDroite.get(cbd).get(1);
		}

		// Etape 6 : calcul de la nouvelle image
		greyMatrixOnlySonogram = new int[newHeight][newWidth];
		booleanZipfMatrix = new boolean[newHeight][newWidth]; // Matrice qui va sauvegarder si le point doit être traité
																// par Zipf
		echographyImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

		int nbThreadTraitement = Model.getInstance().nbThreadTraitement;
		for (int i = 0; i < nbThreadTraitement; i++) {
			ThreadSonoTraitementImage temp = new ThreadSonoTraitementImage(nbThreadTraitement, i, echographyImg,
					greyMatrixOnlySonogram, greyPixelsLevels, pointsPenteGauche, pointsPenteDroite, pointsCourbeHaute,
					pointsCourbeBasseGauche, pointsCourbeBasseDroite, h0, gOmega, dOmega, h2);
			temp.run();
		}

		long endTime = System.nanoTime();

		// obtenir la différence entre les deux valeurs de temps nano
		long timeElapsed = endTime - startTime;
		long milliTimeElapsed = timeElapsed / 1000000;

		System.out.println("Execution time in milliseconds: " + milliTimeElapsed);
	}
}
