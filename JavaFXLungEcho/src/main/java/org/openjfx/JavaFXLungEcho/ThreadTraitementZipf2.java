package org.openjfx.JavaFXLungEcho;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadTraitementZipf2 implements Runnable {

	private int maxThread;
	private int nbThread;
	private int seuilPixelDifferenceDetection;
	private boolean specificOrientation;
	private int[][] greyMatrix;
	private int motifSizeX;
	private int motifSizeY;
	private ConcurrentHashMap<String, Integer> mapMotifNombreOccurence;
	private boolean[][] zipfOrNot;

	public ThreadTraitementZipf2(int maxThread, int nbThread, int seuilPixelDifferenceDetection,
			boolean specificOrientation, int[][] greyMatrix, int motifSizeX, int motifSizeY,
			ConcurrentHashMap<String, Integer> mapMotifNombreOccurence, boolean[][] zipfOrNot) {
		super();
		this.maxThread = maxThread;
		this.nbThread = nbThread;
		this.seuilPixelDifferenceDetection = seuilPixelDifferenceDetection;
		this.specificOrientation = specificOrientation;
		this.greyMatrix = greyMatrix;
		this.motifSizeX = motifSizeX;
		this.motifSizeY = motifSizeY;
		this.mapMotifNombreOccurence = mapMotifNombreOccurence;
		this.zipfOrNot = zipfOrNot;
	}

	@Override
	public void run() {
		int width = greyMatrix.length;
		int length = greyMatrix[0].length;
		int max_row_iteration = width / motifSizeX; // On va enlever les quelques pixels qui dépassent
		int max_col_iteration = length / motifSizeY; // pour ne pas lire dans de mauvais endroits de la mémoire
		int endWidth = (max_row_iteration / maxThread) * (nbThread + 1);
		// être traité
		for (int i = 0; i < endWidth; i++) {
			for (int j = 0; j < max_col_iteration; j++) { // Pour chaque pixel on va maintenant regarder son
																		// voisinage
				int[] listMotif = new int[motifSizeX * motifSizeY];
				int count = 0;
				boolean check = true;
				int newI = i*motifSizeX;
				int newJ = j*motifSizeY;
				for (int ki = newI ; ki < newI + motifSizeX; ki++) {
					for (int kj = newJ; kj < newJ + motifSizeY; kj++) {
						if (zipfOrNot[ki][kj] == false) {
							check = false;
							break;
						}
						listMotif[count] = greyMatrix[ki][kj];
						count++;
					}
					if (check == false)
						break;
				}
				if (check != false) {
					ArrayList<Integer> codedMotif = codeMotif(listMotif);
					String strCodedMotif = codedMotifToString(codedMotif);
					if (mapMotifNombreOccurence.containsKey(strCodedMotif)) { // Si le motif est déjà présent dans notre
																				// image
						int old_value = mapMotifNombreOccurence.get(strCodedMotif); // Alors on augmente son nombre
																					// d'occurence de 1
						mapMotifNombreOccurence.replace(strCodedMotif, old_value + 1);
					} else {
						mapMotifNombreOccurence.put(strCodedMotif, 1);
					}
				}
			}
		}

	}

	public ArrayList<Integer> codeMotif(int[] motif) {
		int len = motif.length;
		ArrayList<Integer> Stock = new ArrayList<>(); // On stocke toutes les valeurs du motif
		// Nouvelle méthode
		for (int i = 0; i < len; i++) {
			ArrayList<Integer> Seuils = new ArrayList<>(); // On va stocker l'ensemble [motif-seuil; motif+seuil]
			int ensemble_seuil_bas = motif[i] - seuilPixelDifferenceDetection; // On vérifie c'est ensemble soit bien
																				// dans [0;255]
			if (ensemble_seuil_bas < 0) {
				ensemble_seuil_bas = 0;
			}
			int ensemble_seuil_haut = motif[i] + seuilPixelDifferenceDetection;
			if (ensemble_seuil_haut > 255) {
				ensemble_seuil_haut = 255;
			}
			for (int s = ensemble_seuil_bas; s <= ensemble_seuil_haut; s++) {
				Seuils.add(s);
			}
			if (Collections.disjoint(Stock, Seuils)) { // True si rien en commun
				Stock.add(motif[i]);
			}
		}
		Collections.sort(Stock); // Les stocks sont maintenant triés
		ArrayList<Integer> Coded = new ArrayList<>();
		for (int i = 0; i < len; i++) {
			int index;
			if (Stock.indexOf(motif[i]) != -1) { // Si la valeur du pixel existe déjà alors on l'ajoute
				index = Stock.indexOf(motif[i]);
			} else { // Sinon on va prendre l'index de la valeur qui est la plus proche de lui
				int seeked = motif[i];
				index = Stock.indexOf(Stock.stream().min(Comparator.comparingInt(k -> Math.abs(k - seeked)))
						.orElseThrow(() -> new NoSuchElementException("Pas de valeur dans le motif")));
			}
			// System.out.println(index);
			Coded.add(index); // On ajoute dans le motif coded l'indice du rang de motif plus ou moins (qui
								// est dans stock)
		}
		// System.out.println(Coded);
		if (!specificOrientation) { // Soit on veut avoir l'orientation des motifs soit on s'en moque
			Collections.sort(Coded);
		}
		return Coded;
	}

	public String codedMotifToString(ArrayList<Integer> motif) {
		String StrMotif = "";
		int len = motif.size();
		for (int i = 0; i < len; i++) {
			StrMotif += motif.get(i);
		}
		return StrMotif;
	}
}
