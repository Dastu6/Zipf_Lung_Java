package org.openjfx.JavaFXLungEcho;

public final class Model {
	//Le mot clé volatile est utile dans le cas d'un multi-threading
	//On crée un singleton afin de toujours avoir qu'une instanciation du modèle
	private static volatile Model instance;
	//Méthode thread-safe pour récupérer l'instance du singleton
	 public static Model getInstance() {
	        // The approach taken here is called double-checked locking (DCL). It
	        // exists to prevent race condition between multiple threads that may
	        // attempt to get singleton instance at the same time, creating separate
	        // instances as a result.
	        //
	        // It may seem that having the `result` variable here is completely
	        // pointless. There is, however, a very important caveat when
	        // implementing double-checked locking in Java, which is solved by
	        // introducing this local variable.
	        //
	        // You can read more info DCL issues in Java here:
	        // https://refactoring.guru/java-dcl-issue
		 Model result = instance;
	        if (result != null) {
	            return result;
	        }
	        synchronized(Model.class) {
	            if (instance == null) {
	                instance = new Model();
	            }
	            return instance;
	        }
	    }
	
	 //Constructeur par défaut, le seul jamais appelé dans la classe
	 private Model() {
		 
	 }
	 
	 //Ici on peut mettre nos attributs et méthode de la classe
	 DicomLoader dicomLoader;
	 
	 TraitementBufferedImage traitement;
	 
	 
	 
}
