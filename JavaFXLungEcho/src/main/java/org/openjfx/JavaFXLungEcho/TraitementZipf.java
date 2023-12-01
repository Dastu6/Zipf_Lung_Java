package org.openjfx.JavaFXLungEcho;

import java.util.Arrays;
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
	public HashMap<Integer,Integer> mapMotifNombreOccurence;
	
	public TraitementZipf(int[][] matrix) { //Il faut lui passer une matrice d'identité (greyMatrixOnlySonogram dans traitbuffer)
		greyMatrix = matrix.clone();
		motifSize = 3;
		recouvrement = 0;
		mapMotifNombreOccurence = new HashMap<Integer,Integer>();
	}
	
	//Permet de convertir un nombre d'une base vers une autre base
	public static int convertNumberBaseToBase(int number, int base, int new_base) {
		return Integer.parseInt(Integer.toString(Integer.parseInt(String.valueOf(number), base), new_base));
	}
	//Permet de coder un motif
	public int[] codeMotif(int[] motif) {
		int len = motif.length;
		int[] motif_code = new int[len];
		motif_code = motif.clone();
		Arrays.sort(motif_code);
		int old = motif[0];
		int count = 0;
		for (int i = 0; i < len; i++) {
			if (motif[i] != old) {
				old = motif[i];
				count++;
			}
			motif_code[i] = count;
		}
		return motif_code;
	}
	
	public int codedMotifToInt(int[] motif) {
		int IntMotif = 0;
		int len = motif.length;
		for (int i = 0; i < len; i++) {
			IntMotif += motif[i] * Math.pow(10,(len-1)-i);
		}
		return IntMotif;
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
				int[] codedMotif = codeMotif(listMotif);
				int intCodedMotif = codedMotifToInt(codedMotif);
				if (mapMotifNombreOccurence.containsKey(intCodedMotif)){ //Si le motif est déjà présent dans notre image
					int old_value = mapMotifNombreOccurence.get(intCodedMotif); //Alors on augmente son nombre d'occurence de 1
					mapMotifNombreOccurence.replace(intCodedMotif, old_value+1);
				}
				else {
					mapMotifNombreOccurence.put(intCodedMotif, 1);
				}
			}
		}
	}
	
	public void printMapValuesAndKeys(HashMap<Integer,Integer> map) {
		System.out.println(map.keySet());
		System.out.println(map.values());
	}
	
	public HashMap<Integer,Integer> sortMapByOccurence() {
		Stream<HashMap.Entry<Integer,Integer>> sortedMapStream;
		sortedMapStream = mapMotifNombreOccurence.entrySet().stream().sorted(Map.Entry.comparingByValue());
		HashMap<Integer,Integer> sortedMap;
		sortedMap = sortedMapStream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (intK, intV) -> intK, LinkedHashMap::new));
		return sortedMap;
	}
}
