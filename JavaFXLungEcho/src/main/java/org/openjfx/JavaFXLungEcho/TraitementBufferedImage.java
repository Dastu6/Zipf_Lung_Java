package org.openjfx.JavaFXLungEcho;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
// TODO: Auto-generated Javadoc

/**
 * The Class TraitementBufferedImage.
 */
public class TraitementBufferedImage {
	
	/** The buff img. */
	public BufferedImage buffImg;
	
	/** The grey pixels levels. */
	public int[][] greyPixelsLevels;
	
	/** The minimum grey intensity before treatment. */
	public int minimumGreyIntensityBeforeTreatment;
	
	/** The seuil detect debut. */
	public int seuil_detect_debut;
									
	/** The seuil detect fin. */
	public int seuil_detect_fin;
	
	/** The grey matrix only sonogram. */
	public int[][] greyMatrixOnlySonogram;
	
	/** The boolean zipf matrix. Will be used during the Zipf process to only use the sonogram pixels on the crop image */
	public boolean[][] booleanZipfMatrix;
	
	/** The echography image. */
	public BufferedImage echographyImg;
	
	/** The array pente gauche. */
	public int[] array_pente_gauche;
	
	/** The array pente droite. */
	public int[] array_pente_droite;
	
	/** The array courbe haute. */
	public int[] array_courbe_haute;
	
	/** The array courbe basse gauche. */
	public int[] array_courbe_basse_gauche;
	
	/** The array courbe basse droite. */
	public int[] array_courbe_basse_droite;
	
	/** The g omega. */
	public int gOmega;
	
	/** The new height. */
	public int newHeight;
	
	/** The mid width. */
	public int midWidth;
	
	/** The z. */
	public int z;
	
	/** The h 0. */
	public int h0;
	
	/** The d omega. */
	public int dOmega;
	
	/** The prev HGY. */
	public float prevHGY;
	
	/** The prev BGY. */
	public float prevBGY;
	
	/** The h 2. */
	public int h2;
	
	/**
	 * Thread buff imageto pixel matrix.
	 * This will store the pixel matrix from the selected frame of the dicom image but by using threads if possible
	 * @param isDicom the is dicom
	 * @param bufferImg the buffer img
	 */
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
	/**
	 * Buffered image to pixel matrix.
	 * This will store the pixel matrix from the selected frame of the dicom image
	 * @param bufferImg the buffer img
	 */
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
	/**
	 * Pixel has black pixel above.
	 * Will check if a pixel from the given matrix has a direct full black pixel above it
	 * @param matrix the matrix
	 * @param x the x
	 * @param y the y
	 * @return true, if successful
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public boolean pixelHasBlackPixelAbove(int[][] matrix, int x, int y) throws IllegalArgumentException {
		int row = y;
		if (row < 1 || x < 0) {
			throw new IllegalArgumentException("Trying to check for a pixel that doesn't exist");
		}
		if (matrix[row - 1][x] <= seuil_detect_debut) {
			return true;
		}
		return false;
	}
	
	/**
	 * Pixel has black pixel below.
	 * Will check if a pixel from the given matrix has a direct full black pixel below it
	 * @param matrix the matrix
	 * @param x the x
	 * @param y the y
	 * @return true, if successful
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public boolean pixelHasBlackPixelBelow(int[][] matrix, int x, int y) throws IllegalArgumentException {
		if (y < 1 || x < 0) {
			throw new IllegalArgumentException("Trying to check for a pixel that doesn't exist");
		}
		if (matrix[y + 1][x] <= 2 * minimumGreyIntensityBeforeTreatment) {
			return true;
		}
		return false;
	}
	
	/**
	 * Slope.
	 * Given the 2 points (x1,y1) and (x2,y2), this will return the slope factor between them
	 * @param x1 the first point X value
	 * @param y1 the first point Y value
	 * @param x2 the second point X value
	 * @param y2 the second point Y value
	 * @return the float
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public float slope(int x1, int y1, int x2, int y2) throws IllegalArgumentException {
		if (x2 == x1) {
			throw new IllegalArgumentException("Slope method can't take similar X values");
		}
		float p = (float) (y2 - y1) / (float) (x2 - x1);
		return p;
	}
	/**
	 * Buffered image to sonogram.
	 * This will create a new PNG file which would be a crop image of the dicom one, with its edges fitting
	 * the sonogram area.
	 * This method also enhance the contrast of the sonogram without losing information.
	 * @throws Exception the exception
	 */
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
		//On va vérifier qu'on est bien sur le trait et pas sur le triangle
		int recadrage = h2;
		while(recadrage>0 && greyPixelsLevels[recadrage][gOmega] < seuil_detect_debut) {
			recadrage--;
		}
		newHeight = h2 - h0 + 1;
		// Courbe du bas droite : ( , ) -> ( , )
		// Courbe du bas gauche : ( , ) -> ( , )
		float penteGauche = slope(gOmega, newHeight, midWidth, h0); // On part du point le plus à gauche
		float penteDroite = slope(z, h0, dOmega, newHeight); // On part du point le plus à gauche
		///////////////// PENTES ///////////////////////::
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
		ArrayList<ArrayList<Float>> pointsCourbeBasseGaucheTemp = new ArrayList<ArrayList<Float>>();
		ArrayList<Float> aBG0 = new ArrayList<Float>(2);
		aBG0.add((float) gOmega);
		aBG0.add((float) recadrage);
		pointsCourbeBasseGaucheTemp.add(aBG0);
		float prevBGX = gOmega;
		prevBGY = recadrage;
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
		ArrayList<ArrayList<Integer>> pointsCourbeBasseDroite = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Float> aBDTemp : pointsCourbeBasseDroiteTemp) {
			ArrayList<Integer> aBD = new ArrayList<Integer>(2);
			float aBDX = aBDTemp.get(0);
			float aBDY = aBDTemp.get(1);
			aBD.add((int) aBDX);
			aBD.add((int) aBDY);
			pointsCourbeBasseDroite.add(aBD);
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
		
		PenteBufferedImage pentegauche = new PenteBufferedImage(pointsPenteGauche,gOmega,h0);
		PenteBufferedImage pentedroite = new PenteBufferedImage(pointsPenteDroite,gOmega,h0);
		
		PenteBufferedImage courbehaute = new PenteBufferedImage(pointsCourbeHaute,gOmega,h0);
		PenteBufferedImage courbebassedroite = new PenteBufferedImage(pointsCourbeBasseDroite,gOmega,h0);
		PenteBufferedImage courbebassegauche = new PenteBufferedImage(pointsCourbeBasseGauche,gOmega,h0);
	
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
		
		 
		 int nbThreadPosition = Model.getInstance().nbThreadPosition;
			
		 for(int th = 0; th < nbThreadPosition; th++)
			{
				ThreadSonoBooleanMatrix temp = new ThreadSonoBooleanMatrix
						 (th, booleanZipfMatrix,pentegauche,pentedroite,courbehaute,courbebassegauche,courbebassedroite,
								 gOmega,newHeight,midWidth,h0,z,dOmega,(int)prevHGY,(int)prevBGY, h2);
				 temp.run();
			}		
		
		long endTime = System.nanoTime();
		// obtenir la différence entre les deux valeurs de temps nano
		long timeElapsed = endTime - startTime;
		long milliTimeElapsed = timeElapsed / 1000000;
		System.out.println("Execution time in milliseconds: " + milliTimeElapsed);
	}
}