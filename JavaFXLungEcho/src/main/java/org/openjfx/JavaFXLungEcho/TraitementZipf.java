package org.openjfx.JavaFXLungEcho;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TraitementZipf {
	public int[][] greyMatrix;
	public int motifSize; //motif x motif
	public int seuilPixelDifferenceDetection;
	public int recouvrement;
	public boolean specificOrientation;
	public boolean ascendSortingMap;
	public HashMap<String,Integer> mapMotifNombreOccurence;
	public HashMap<String,Integer> mapSortedCodedMotifOccurence;
	
	public TraitementZipf(int[][] matrix, int seuil, boolean specifOrientation, boolean orderSortingMap) { //Il faut lui passer une matrice d'identité (greyMatrixOnlySonogram dans traitbuffer)
		greyMatrix = matrix.clone();
		motifSize = 3;
		recouvrement = 0;
		seuilPixelDifferenceDetection = seuil;
		specificOrientation = specifOrientation;
		ascendSortingMap = orderSortingMap;
		mapMotifNombreOccurence = new HashMap<String,Integer>();
	}
	
	//Permet de convertir un nombre d'une base vers une autre base
	public static int convertNumberBaseToBase(int number, int base, int new_base) {
		return Integer.parseInt(Integer.toString(Integer.parseInt(String.valueOf(number), base), new_base));
	}
	
	public ArrayList<Integer> codeMotif(int[] motif) {
		int len = motif.length;
		ArrayList<Integer> Stock = new ArrayList<>(); //On stocke toutes les valeurs du motif
		//Nouvelle méthode
		for (int i = 0; i < len; i++) {
			ArrayList<Integer> Seuils = new ArrayList<>(); //On va stocker l'ensemble [motif-seuil; motif+seuil]
			int ensemble_seuil_bas = motif[i]-seuilPixelDifferenceDetection;
			if (ensemble_seuil_bas < 0) {
				ensemble_seuil_bas = 0;
			}
			int ensemble_seuil_haut = motif[i]+seuilPixelDifferenceDetection;
			if (ensemble_seuil_haut > 255) {
				ensemble_seuil_haut = 255;
			}
			for (int s = ensemble_seuil_bas; s <= ensemble_seuil_haut; s++) {
				Seuils.add(s);
			}
			if (Collections.disjoint(Stock, Seuils)) { //True si rien en commun
				Stock.add(motif[i]);
			}
		}
		Collections.sort(Stock); //Les stocks sont maintenant triés
		ArrayList<Integer> Coded = new ArrayList<>();	
		for (int i = 0; i < len; i++) {
			int index = Stock.indexOf(motif[i]);
			System.out.println(index);
			Coded.add(index); //On ajoute dans le motif coded l'indice du rang de motif plus ou moins (qui est dans stock)
		}
		System.out.println(Coded);
		if (!specificOrientation) { //Soit on veut avoir l'orientation des motifs soit on s'en moque
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
	
	public void printMapValuesAndKeys(HashMap<String,Integer> map) {
		System.out.println(map.keySet());
		System.out.println(map.values());
	}
	
	public void sortMapByOccurence() {
		Stream<HashMap.Entry<String,Integer>> sortedMapStream;
		if (ascendSortingMap) {
			sortedMapStream = mapMotifNombreOccurence.entrySet().stream().sorted(Map.Entry.comparingByValue());
		}
		else {
			sortedMapStream = mapMotifNombreOccurence.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
		}
		mapSortedCodedMotifOccurence = sortedMapStream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (intK, intV) -> intK, LinkedHashMap::new));
	}

	public void motifMapFromGreyMatrix() {
		int number_row = greyMatrix.length;
		int number_col = greyMatrix[0].length;
		int max_row_iteration = number_row/motifSize; //On va enlever les quelques pixels qui dépassent 
		int max_col_iteration = number_col/motifSize; //pour ne pas lire dans de mauvais endroits de la mémoire
		int corner_limit = (motifSize-1)/2; //Décide de faire le choix de prendre des motifs égaux
		//mais qui ne recouvrent pas entièrement l'image
		for (int i = corner_limit; i < max_row_iteration; i++) {
			for (int j = corner_limit; j < max_col_iteration; j++) { //Pour chaque pixel on va maintenant regarder son voisinage
				int[] listMotif = new int[motifSize * motifSize];
				int count = 0;
				for (int ki = i-corner_limit; ki <= i+corner_limit; ki++) {
					for (int kj = j-corner_limit; kj <= j+corner_limit; kj++) {
						listMotif[count] = greyMatrix[ki][kj];
						count++;
					}
				}
				ArrayList<Integer> codedMotif = codeMotif(listMotif);
				String strCodedMotif = codedMotifToString(codedMotif);
				if (mapMotifNombreOccurence.containsKey(strCodedMotif)){ //Si le motif est déjà présent dans notre image
					int old_value = mapMotifNombreOccurence.get(strCodedMotif); //Alors on augmente son nombre d'occurence de 1
					mapMotifNombreOccurence.replace(strCodedMotif, old_value+1);
				}
				else {
					mapMotifNombreOccurence.put(strCodedMotif, 1);
				}
			}
		}
	}
}
