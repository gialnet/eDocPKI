package es.redmoon.utils;

import org.bouncycastle.tsp.TSPAlgorithms;

/**
 * Class for define and filter algorithm formats supported by TimeStamp.
 * @author Ángel L. Garcia <angel@redmoon.es>
 * @version 0.1
 *
 */
public class DigestAlgorithm {	
	/**
	 * Get the correct type for digest algorithm.
	 * @param hashType The type.
	 * @return The type for digest or null in case of error.
	 */
	public static String getAlgorithm(String hashType){
		String algoritmo = hashType.toUpperCase();
		// SHA-1
		if (algoritmo.equals("SHA1") || algoritmo.equals("SHA.1") || algoritmo.equals("SHA_1") || algoritmo.equals("SHA-1")){
			return TSPAlgorithms.SHA1;
		}
		// SHA-2
		else if (algoritmo.equals("SHA2") || algoritmo.equals("SHA.2") || algoritmo.equals("SHA_2") || algoritmo.equals("SHA-2")){
			return TSPAlgorithms.SHA256;
		}
		// SHA-224
		else if (algoritmo.equals("SHA224") || algoritmo.equals("SHA.224") || algoritmo.equals("SHA_224") || algoritmo.equals("SHA-224")){
			return TSPAlgorithms.SHA224;
		}
		// SHA-256
		else if (algoritmo.equals("SHA256") || algoritmo.equals("SHA.256") || algoritmo.equals("SHA_256") || algoritmo.equals("SHA-256")){
			return TSPAlgorithms.SHA256;
		}
		// SHA-384
		else if (algoritmo.equals("SHA384") || algoritmo.equals("SHA.384") || algoritmo.equals("SHA_384") || algoritmo.equals("SHA-384")){
			return TSPAlgorithms.SHA384;
		}
		// SHA-512
		else if (algoritmo.equals("SHA512") || algoritmo.equals("SHA.512") || algoritmo.equals("SHA_512") || algoritmo.equals("SHA-512")){
			return TSPAlgorithms.SHA512;
		}
		// Algortihm not supported
		else 
			return null;	
	}

}
