package org.openjfx.JavaFXLungEcho;

import java.util.ArrayList;

/**
 * Classe qui représente une pente par une liste de points (x et y)
 *  et des méthodes permettant de chercher efficacement certaines valeurs clefs
 * @author Esteban
 *
 */
public class PenteBufferedImage {
	public ArrayList<vect2Int> Points;
	public PenteBufferedImage() {
		Points = new ArrayList<vect2Int>();
	}
	
	public PenteBufferedImage(ArrayList<ArrayList<Integer>> f, int gOmega, int h0) {
		Points = new ArrayList<vect2Int>();
		for(int i=0;i<f.size();i++)
		{
			vect2Int temp = new vect2Int(f.get(i).get(0)-gOmega, f.get(i).get(1)-h0);
			Points.add(temp);
		}
	}
	public void addPoints(vect2Int point) {
		Points.add(point);
	}
	/**
	 * 
	 * @param x int we lookin for
	 * @return Index of the X value in the list
	 */
	public int lookForX(int x) {
		
		for(int i=0;i<Points.size();i++)
		{
			if(Points.get(i).x==x)
				return i;
		}
		
		return -1;
	}
	
	/**
	 * 
	 * @param y int we lookin for
	 * @return Index of the Y value in the list
	 */
	public int lookForY(int y) {
		
		return lookFornearY(y,0);
	}
public int lookFornearY(int y,int step) {
		int returnval = -1;
		for(int i=0;i<Points.size();i++)
		{
			if(Points.get(i).y==y)
				return i;
			else if(Math.abs(Points.get(i).y-y)<=step )
				returnval = i;
		}
		return returnval;
	}

public int lookFornearX(int x,int step) {
	int returnval = -1;
	for(int i=0;i<Points.size();i++)
	{
		if(Points.get(i).x==x)
			return i;
		else if(Math.abs(Points.get(i).x-x)<=step )
			returnval = i;
	}
	return returnval;
}

	
	
}
