package org.openjfx.JavaFXLungEcho;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ThreadPenteTraitementImage implements Runnable{

	private boolean type;// Selon le type on gère pente gauche ou pente droite
	private ArrayList<ArrayList<Integer>> pente;
	private float gOmega;
	private float newHeight;
	private int midWidth;
	private float penteGauche;
	private float h0;
	private float z;
	private int dOmega;
	private float penteDroite;

	// Constructeur pente gauche
	public ThreadPenteTraitementImage(boolean type ,ArrayList<ArrayList<Integer>> pentee,float gOmega, int midWidth, float newHeight, float penteGauche) {
		super();
		this.type = type;
		pente = pentee;
		if(type==true)
		{
			this.gOmega = gOmega;
			this.newHeight = newHeight;
			this.midWidth = midWidth;
			this.penteGauche = penteGauche;
			
		}else {
			this.h0 = gOmega;
			this.dOmega = midWidth;
			this.z = newHeight;
			this.penteDroite = penteGauche;
		}
	}







	@Override
	public void run() {
		if (type == true) {
			// On a la liste de tous les points qui sont sur la pente gauche
			ArrayList<ArrayList<Float>> pointsPenteGaucheTemp = new ArrayList<ArrayList<Float>>();
			ArrayList<Float> aG0 = new ArrayList<Float>(2);
			aG0.add((float) gOmega);
			aG0.add((float) newHeight);
			pointsPenteGaucheTemp.add(aG0);
			float prevGX = gOmega;
			float prevGY = newHeight;
			for (int x = 1; x < (int) midWidth - gOmega; x++) {				
				float newGY = prevGY + penteGauche;
				if ((int)prevGY - (int)newGY >= 2) { //Si entre x et x+1 on passe de y à y+2 alors on ajoute aussi y+1
					ArrayList<Float> a2 = new ArrayList<Float>(2);
					a2.add(prevGX + 1);
					a2.add(prevGY - 1);
					pointsPenteGaucheTemp.add(a2);
				}
				ArrayList<Float> a = new ArrayList<Float>(2);
				a.add(prevGX + 1);
				a.add(newGY);
				pointsPenteGaucheTemp.add(a);
				prevGX += 1;
				prevGY = newGY;
			}
			for (ArrayList<Float> aGTemp : pointsPenteGaucheTemp) {
				ArrayList<Integer> aG = new ArrayList<Integer>(2);
				float aGX = aGTemp.get(0);
				float aGY = aGTemp.get(1);
				aG.add((int) aGX);
				aG.add((int) aGY);
				pente.add(aG);
			}
		} else {

			// On va créer une liste temporaire de flotants car la pente est un float
			ArrayList<ArrayList<Float>> pointsPenteDroiteTemp = new ArrayList<ArrayList<Float>>();
			ArrayList<Float> aD0 = new ArrayList<Float>(2);
			aD0.add((float) z);
			aD0.add((float) h0);
			pointsPenteDroiteTemp.add(aD0);
			float prevDX = z;
			float prevDY = h0;
			for (int x = 1; x < (int) dOmega - z; x++) {
				float newDY = prevDY + penteDroite;
				if ((int)newDY - (int)prevDY >= 2) { //Si entre x et x+1 on passe de y à y+2 alors on ajoute aussi y+1
					ArrayList<Float> a2 = new ArrayList<Float>(2);
					a2.add(prevDX + 1);
					a2.add(prevDY + 1);
					pointsPenteDroiteTemp.add(a2);
				}
				ArrayList<Float> a = new ArrayList<Float>(2);
				a.add(prevDX + 1);
				a.add(newDY);
				pointsPenteDroiteTemp.add(a);
				prevDX += 1;
				prevDY = newDY;
			}
			// On va maintenant faire la vraie liste des points où on va recast en int car
			// pixel : (int x, int y)
			for (ArrayList<Float> aDTemp : pointsPenteDroiteTemp) {
				ArrayList<Integer> aD = new ArrayList<Integer>(2);
				float aDX = aDTemp.get(0);
				float aDY = aDTemp.get(1);
				aD.add((int) aDX);
				aD.add((int) aDY);
				pente.add(aD);
			}
		}

	}
}
