package org.openjfx.JavaFXLungEcho;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TraitementZipf {
	public int[][] greyMatrix;
	public int motifSize; // motif x motif
	public int motifSizeX;
	public int motifSizeY;
	public int seuilPixelDifferenceDetection;
	public int recouvrement;
	public boolean specificOrientation;
	public boolean ascendSortingMap;
	public ConcurrentHashMap<String, Integer> mapMotifNombreOccurence;
	public HashMap<String, Integer> mapSortedCodedMotifOccurence;
	public boolean[][] ZipfOrNot;

	
	public TraitementZipf(int[][] matrix, int seuil, boolean specifOrientation, boolean orderSortingMap, int motifX, int motifY,
			boolean[][] booleanZipf) { //Il faut lui passer une matrice d'identité (greyMatrixOnlySonogram dans traitbuffer)
		greyMatrix = matrix.clone();
		motifSize = 3;
		motifSizeX = motifX;
		motifSizeY = motifY;
		recouvrement = 0;
		seuilPixelDifferenceDetection = seuil;
		specificOrientation = specifOrientation;
		ascendSortingMap = orderSortingMap;
		mapMotifNombreOccurence = new ConcurrentHashMap<String,Integer>();
		ZipfOrNot = booleanZipf;
	}

	// Permet de convertir un nombre d'une base vers une autre base
	public static int convertNumberBaseToBase(int number, int base, int new_base) {
		return Integer.parseInt(Integer.toString(Integer.parseInt(String.valueOf(number), base), new_base));
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
			//System.out.println(index);
			Coded.add(index); // On ajoute dans le motif coded l'indice du rang de motif plus ou moins (qui
								// est dans stock)
		}
		//System.out.println(Coded);
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

	public void printMapValuesAndKeys(HashMap<String, Integer> map) {
		System.out.println(map.keySet());
		System.out.println(map.values());
	}

	public void printMapValuesAndKeys(ConcurrentHashMap<String, Integer> map) {
		System.out.println("Eh ho");
		System.out.println(map.keySet());
		System.out.println(map.values());
	}
	public void sortMapByOccurence() {
		Stream<HashMap.Entry<String, Integer>> sortedMapStream;
		if (ascendSortingMap) {
			sortedMapStream = mapMotifNombreOccurence.entrySet().stream().sorted(Map.Entry.comparingByValue());
		} else {
			sortedMapStream = mapMotifNombreOccurence.entrySet().stream()
					.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
		}
		mapSortedCodedMotifOccurence = sortedMapStream.collect(
				Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (intK, intV) -> intK, LinkedHashMap::new));
	}

	public void motifMapFromGreyMatrix() {
		
		 long startTime = System.nanoTime();
		
		 long endTime = System.nanoTime();
		 
	        // obtenir la différence entre les deux valeurs de temps nano
	        long timeElapsed = endTime - startTime;
	        long milliTimeElapsed = timeElapsed / 1000000;
	      
	        System.out.println("Execution time in milliseconds: " + milliTimeElapsed);
	        System.out.println("Execution time in seconds : " + milliTimeElapsed / 1000);
		
		int number_row = greyMatrix.length;
		int number_col = greyMatrix[0].length;
		int max_row_iteration = number_row / motifSizeX; // On va enlever les quelques pixels qui dépassent
		int max_col_iteration = number_col / motifSizeY; // pour ne pas lire dans de mauvais endroits de la mémoire
		int corner_limitX = (motifSizeX - 1) / 2; // On rogne l'image pour être sûr que chaque pixel parcouru puisse
													// être traité
		int corner_limitY = (motifSizeY - 1) / 2;
		System.out.println("row : "+max_row_iteration+" col : "+max_col_iteration);
		// mais qui ne recouvrent pas entièrement l'image
		for (int i = corner_limitX; i < max_row_iteration; i++) {
			for (int j = corner_limitY; j < max_col_iteration; j++) { // Pour chaque pixel on va maintenant regarder son
																		// voisinage
				
					int[] listMotif = new int[motifSizeX * motifSizeY];
					int count = 0;
					boolean check = true;
					for (int ki = i-corner_limitX; ki <= i+corner_limitX; ki++) {
						for (int kj = j-corner_limitY; kj <= j+corner_limitY; kj++) {
							if (ki == i-corner_limitX && kj == j-corner_limitY && !ZipfOrNot[ki][kj]) {
								check = false;
							}
							//if (check) {
								listMotif[count] = greyMatrix[ki][kj];
								count++;
							//}
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
	//Fonction qui réalise les motifs nécessaire pour la loi de zipf et qui remplit la hashmap
	// cette fonction utilise des threads et réalise sur toutes l'images. C'est donc appliquable
	//Sur toutes les images, pour les images .dcm avec des contours particuliers il faudra utiliser une autre méthode
	public void motifThreadMapFromGreyMatrix() {
		int maxThread = Model.getInstance().nbThreadTraitement;
		for(int i=0;i<maxThread;i++)
		{
			ThreadTraitementZipf temp = new ThreadTraitementZipf
					(maxThread, i, seuilPixelDifferenceDetection,
							specificOrientation, greyMatrix, motifSizeX, motifSizeY, mapMotifNombreOccurence);
			temp.run();
		}
		
	}
	
	//Dicom's version
	public void motifThreadMapFromGreyMatrixDicom() {
		int maxThread = Model.getInstance().nbThreadTraitement;
		for(int i=0;i<maxThread;i++)
		{
			ThreadTraitementZipf2 temp = new ThreadTraitementZipf2
					(maxThread, i, seuilPixelDifferenceDetection,
							specificOrientation, greyMatrix, motifSizeX, motifSizeY, mapMotifNombreOccurence,ZipfOrNot);
			temp.run();
		}
		printMapValuesAndKeys(mapMotifNombreOccurence);
	}
	
	
	public void printbooleanZipf() {
		for(int i=0;i<ZipfOrNot.length;i++)
		{
			System.out.print("i : "+i+ " ");
			for(int j=0;j<ZipfOrNot[i].length;j++)
			{
				if(ZipfOrNot[i][j])
					System.out.print(" "+ 1+" ");
				else
					System.out.print(" "+ 0+" ");
			}
			System.out.print("\n");
		}
	}
	
}