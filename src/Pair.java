/**
 *  @file Pair.java
 *  @brief Classe Pair
 */

/** 
 *  @class Pair
 *  @brief Classe Contenidora de dos objectes genèrics.
 */

public class Pair<X,Y> {
	
    public X first; //Primer element.
    public Y second; //Segon element.
    
	public Pair(X first, Y second) {
		
	   this.first = first; 
	   this.second = second;
    }
	
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof Pair)) {
			return false;
		}
		else {
			Pair<X,Y> cmp = (Pair<X,Y>) o;
			if(this.first.equals(cmp.first) && this.second.equals(cmp.second))
				return true;
		}
		return false;

	}
	
	/**
	 * @pre: Cap dels components del Pair no pot ser null.
	 * @returns: Retorna un string a partir de la representació en string dels components del pair en el format: "first, second"
	 */
	@Override
	public String toString() {
		String res = "{" + this.first.toString() + ", " + this.second.toString() + "}";
		return res;
	}
}
