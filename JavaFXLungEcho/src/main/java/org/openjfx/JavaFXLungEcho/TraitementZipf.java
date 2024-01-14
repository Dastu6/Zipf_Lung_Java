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

// TODO: Auto-generated Javadoc
/**
 * Cette classe est capable d'effectuer un traitement d'une loi de zipf appliqué à une image,
 * elle fonctionne grâce à des motifs.
 */
public class TraitementZipf {
	
	/** The grey matrix. */
	public int[][] greyMatrix;
	
	/** The motif size. */
	public int motifSize; // motif x motif
	
	/** The motif size X. */
	public int motifSizeX;
	
	/** The motif size Y. */
	public int motifSizeY;
	
	/** The seuil pixel difference detection. */
	public int seuilPixelDifferenceDetection;
	
	/** The recouvrement. */
	public int recouvrement;
	
	/** The specific orientation. */
	public boolean specificOrientation;
	
	/** The ascend sorting map. */
	public boolean ascendSortingMap;
	
	/** The map motif nombre occurence. */
	public ConcurrentHashMap<String, Integer> mapMotifNombreOccurence;
	
	/** The map sorted coded motif occurence. */
	public HashMap<String, Integer> mapSortedCodedMotifOccurence;
	
	/** The Zipf or not. */
	public boolean[][] ZipfOrNot;

	
	/**
	 * Instantiates a new traitement zipf.
	 *
	 * @param matrix the matrix
	 * @param seuil the threshold
	 * @param specifOrientation the specif orientation
	 * @param orderSortingMap the order sorting map
	 * @param motifX the motif X
	 * @param motifY the motif Y
	 * @param booleanZipf the boolean zipf
	 */
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
		if(booleanZipf!=null)
			ZipfOrNot = booleanZipf;
		else
			fillBooleanMatrix();
	}

	/**
	 * Convert number from base to a new base.
	 *
	 * @param number the number
	 * @param base the base
	 * @param new_base the new base
	 * @return the converted int number
	 */
	public static int convertNumberBaseToBase(int number, int base, int new_base) {
		return Integer.parseInt(Integer.toString(Integer.parseInt(String.valueOf(number), base), new_base));
	}

	/**
	 * Code motif.
	 * This code a pattern
	 * @param motif the pattern
	 * @return the array list of the coded pattern
	 */
	public ArrayList<Integer> codeMotif(int[] motif) {
		int len = motif.length;
		ArrayList<Integer> Stock = new ArrayList<>(); // On stocke toutes les valeurs du motif
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
			Coded.add(index); // On ajoute dans le motif coded l'indice du rang de motif plus ou moins (qui
								// est dans stock)
		}
		if (!specificOrientation) { // Soit on veut avoir l'orientation des motifs soit on s'en moque
			Collections.sort(Coded);
		}
		return Coded;
	}

	/**
	 * Coded motif to string.
	 * Will transform our pattern to use it as a String for other methods.
	 * @param motif the ArrayList containing the coded pattern
	 * @return the string of the coded pattern
	 */
	public String codedMotifToString(ArrayList<Integer> motif) {
		String StrMotif = "";
		int len = motif.size();
		for (int i = 0; i < len; i++) {
			StrMotif += motif.get(i);
		}
		return StrMotif;
	}

	/**
	 * Prints the map values and keys.
	 *
	 * @param map a HashMap<String, Integer>
	 */
	public void printMapValuesAndKeys(HashMap<String, Integer> map) {
		System.out.println(map.keySet());
		System.out.println(map.values());
	}
	
	/**
	 * Sort map by occurence.
	 */
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
	
	/**
	 * newTech
	 * Method that will code the patterns regarding the threshold & patterns values from constructor
	 */
	public void newTech() {
		int max_x = 0;							
		int max_y=0;
	    
		int width = greyMatrix.length;
		int length = greyMatrix[0].length;
		int max_row_iteration = width / motifSizeX; // On va enlever les quelques pixels qui dépassent
		int max_col_iteration = length / motifSizeY; // pour ne pas lire dans de mauvais endroits de la mémoire
		System.out.println("row : "+max_row_iteration+" col : "+max_col_iteration);
		// mais qui ne recouvrent pas entièrement l'image
		for (int i = 0; i < max_row_iteration; i++) {
			for (int j = 0; j < max_col_iteration; j++) { // Pour chaque pixel on va maintenant regarder son

				int[] listMotif = new int[motifSizeX * motifSizeY];
				int count = 0;
				boolean check = true;
				int newI = i*motifSizeX;
				int newJ = j*motifSizeY;
				for (int ki = newI ; ki < newI + motifSizeX; ki++) {
					for (int kj = newJ; kj < newJ + motifSizeY; kj++) {
						int temp = greyMatrix[ki][kj];
						if (ZipfOrNot[ki][kj] == false) {
							check = false;
							break;
						}
						listMotif[count] = temp;
						count++;
						if(max_y<kj)
							max_y=kj;
					}
					if (check == false)
						break;
					if(max_x<ki)
						max_x=ki;
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
		System.out.println("Max x : "+max_x+" and max y : "+max_y);
	}
	
	/**
	 * Printboolean zipf.
	 */
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
	
	
	/**
	 * Prints the grey matrix.
	 */
	public void printGreyMatrix() {
		for(int i=0;i<greyMatrix.length;i++)
		{
			System.out.print("i : "+i+ " ");
			for(int j=0;j<greyMatrix[i].length;j++)
			{
					System.out.print(" "+ greyMatrix[i][j]+" ");
			}
			System.out.print("\n");
		}
	}
	
	
	/**
	 * Fill boolean matrix.
	 */
	private void fillBooleanMatrix()
	{
		ZipfOrNot = new boolean[greyMatrix.length][greyMatrix[0].length];
		for(int i=0; i<ZipfOrNot.length; i++)
		{
		   for(int j=0; j<ZipfOrNot[i].length; j++)
		   {
			   ZipfOrNot[i][j] = true;
		   }	   
		}		  
	}
}