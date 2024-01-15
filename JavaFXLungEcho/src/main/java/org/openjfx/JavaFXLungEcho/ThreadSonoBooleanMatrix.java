package org.openjfx.JavaFXLungEcho;

/**
 * 
 * @author Antoine Delenclos Cette classe correspond à un thread qui permet de
 *         recalculer une image selon l'image de base ainsi que d'autre
 *         paramètre
 */
public class ThreadSonoBooleanMatrix implements Runnable {

	private int nThread;

	private PenteBufferedImage penteGauche;
	private PenteBufferedImage pointsPenteDroite;
	private PenteBufferedImage pointsCourbeHaute;
	private PenteBufferedImage pointsCourbeBasseGauche;
	private PenteBufferedImage pointsCourbeBasseDroite;
	private int height_min;
	private int height_max;
	private int width_min;
	private int width_max;
	private boolean[][] booleanZipf;
	public int gOmega;
	public int dOmega;
	public int h0;
	public int h2;

	public ThreadSonoBooleanMatrix(int nThread, boolean[][] booleanZipf, PenteBufferedImage PenteGauche,
			PenteBufferedImage pointsPenteDroite, PenteBufferedImage pointsCourbeHaute,
			PenteBufferedImage pointsCourbeBasseGauche, PenteBufferedImage pointsCourbeBasseDroite, int gOmega,
			int newHeight, int midWidth, int h0, int z, int dOmega, int prevHGY, int prevBGY, int h2) {
		super();
		this.nThread = nThread;
		this.penteGauche = PenteGauche;
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
		case 0: // Partie supérieur gauche : pente gauche
			this.height_min = h0;
			this.height_max = newHeight;
			this.width_min = gOmega;
			this.width_max = midWidth;
			break;
		case 1: // Partie supérieur au dessus de la courbe haute
			this.height_min = h0;
			this.height_max = prevHGY;
			this.width_min = midWidth;
			this.width_max = z;
			break;
		case 2: // Partie centrale
			this.height_min = prevHGY;
			this.height_max = newHeight+h0;
			this.width_min = midWidth;
			this.width_max = z;
			break;
		case 3: // Partie supérieur droite : pente droite
			this.height_min = h0;
			this.height_max = newHeight;
			this.width_min = z;
			this.width_max = dOmega;
			break;
		case 4: // Partie inférieure gauche
			this.height_min = newHeight;
			this.height_max = prevBGY;
			this.width_min = gOmega;
			this.width_max = midWidth;
			break;
		case 5: // Partie inférieure droite
			this.height_min = newHeight;
			this.height_max = prevBGY;
			this.width_min = z;
			this.width_max = dOmega;
		}
		this.height_min -= h0;
		this.height_max -= h0;
		this.width_min -= gOmega;
		this.width_max -= gOmega;
	}

	@Override
	public void run() {
		for (int i = height_min; i < height_max; i++) {
			int tempIndex = -1;
			switch (nThread) {
	
							case 0: // Partie supérieur gauche : pente gauche
							tempIndex = penteGauche.lookFornearY(i, 2);
								break;
							case 3: // Partie supérieur droite : pente droite
								tempIndex = pointsPenteDroite.lookFornearY(i, 2);
								break;
							case 4: // Partie inférieure gauche : courbe basse gauche
								tempIndex = pointsCourbeBasseGauche.lookFornearY(i, 2);
								break;
							case 5: // Partie inférieure droite : courbe basse droite
								tempIndex = pointsCourbeBasseDroite.lookFornearY(i, 2);

								break;
							}
			for (int j = width_min; j < width_max; j++) {
				int x = j;
				int y = i;
				switch (nThread) {

				case 0: // Partie supérieur gauche : pente gauche
					// Pour chaque point : chercher si le point de la pente est avant ou après (sur
					// une même ligne)
					if (tempIndex != -1) {
						if (x > penteGauche.Points.get(tempIndex).x)
							booleanZipf[i][j] = true;
					}
					break;
				case 1: // Partie supérieur au dessus de la courbe haute
				
					tempIndex = pointsCourbeHaute.lookFornearX(j, 2);
					if (tempIndex != -1) {
						if (y > pointsCourbeHaute.Points.get(tempIndex).y)
							booleanZipf[i][j] = true;
					}
					break;
				case 2: // Partie centrale
					booleanZipf[i][j] = true;
					break;
				case 3: // Partie supérieur droite : pente droite
					if (tempIndex != -1) {
						if (x < pointsPenteDroite.Points.get(tempIndex).x) {
							booleanZipf[i][j] = true;
						}
					}
					break;
				case 4: // Partie inférieure gauche : courbe basse gauche
					if(tempIndex!=-1)
					{	
						if (x > pointsCourbeBasseGauche.Points.get(tempIndex).x) {
							booleanZipf[i][j] = true;
						}
					}
					break;
				case 5: // Partie inférieure droite : courbe basse droite
					if(tempIndex!=-1)
					{
						if (x < pointsCourbeBasseDroite.Points.get(tempIndex).x) {
							booleanZipf[i][j] = true;
						}
					}
					break;
				}
			}
		}
	}

}
