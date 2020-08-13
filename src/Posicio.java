/**
 *  @file Posicio.java
 *  @brief Classe Posicio
 */

/** 
 *  @class Posicio
 *  @brief Classe que representa una posició d'un tauler d'escacs
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public class Posicio implements Comparable<Posicio>{
	/**
	 * Coordenades de la Posicio
	 */
	private int columna; //Columna de la posició

	private int fila; //Fila de la posició
	
	/**
	 * @post Crea una posició a partir de les seva notació algebraica. Més info: https://www.ajedrez365.com/2007/10/la-notacin-de-las-partidas-de-ajedrez.html
	 * @param fila Enter que representa la fila.
	 * @param columna Caràcter que representa la columna de la posició.
	 */
	public Posicio(char columna, int fila) throws IllegalArgumentException{
		this.fila = fila;
		this.columna = columna - 96;
	}
	
	/**
	 * @post Crea una posició a partir de les seves coordenades fila i columna.
	 * @param fila Fila en format enter
	 * @param columna Columna en format enter
	 */
	public Posicio(int fila, int columna){
		this.fila = fila;
		this.columna = columna;
	}
	
	/**
	 * @post Crea una nova Posició a partir d'una altre i un desplaçament.
	 * @param antiga Posició antiga/original a partir de la qual es crea la nova
	 * @param despFila Desplaçament de files.
	 * @param despColumna Desplaçament de columnes.
	 * @note Els desplaçaments poden ser negatius per indicar que es redueix el valor de la coordenada a la nova Posició.
	 */
	public Posicio(Posicio antiga, int despFila, int despColumna) {
		this.fila = antiga.fila()+despFila;
		this.columna = antiga.columna()+despColumna;
	}
	
	/**
	 * @param files Nombre de files del tauler.
	 * @post S'ha retornat una copia d'aquesta Posicio però canviada de prespectiva. Es garanteix que l'estat d'aquesta Posicio no variarà.
	 * @note Canviar de prespectiva vol dir que s'aplica un eix de simetria horitzontal.
	 */
	public Posicio canviPrespectiva(int files) {
		int novaFila = files - this.fila() +1;
		Posicio res = new Posicio(novaFila,this.columna());
		return res;
	}


	/** 
	 * @return La columna de la posició.
	 */
	public int columna() {
		return this.columna;
	}
	
	/**
	 * 
	 * @param o Posicio amb la que es compararà.
	 * @return Un enter positiu, negatiu, o 0, en funció de si la columna d'aquest objecte és més gran, més petit o igual al passat per paràmetre 'o'.
	 * @note No confondre amb el compareTo. Aquest mètode només compara la columna dels objectes aixi doncs: a5 < h5 i a2 == a3 (retorna 0).
	 */
	public int compareColumna(Posicio o) {
		return (this.columna()-o.columna());

	}
	
	/**
	 * @param o Posicio amb la que es compararà.
	 * @return Un enter positiu, negatiu, o 0, en funció de si la fila d'aquest objecte és més gran, més petit o igual al passat per paràmetre 'o'.
	 * @note No confondre amb el compareTo. Aquest mètode només compara la fila dels objectes aixi doncs: a5 == h5(retorna 0) i a2 < a3.
	 */
	public int compareFila(Posicio o) {
		return (this.fila() - o.fila());

	}

	/**
	 * @note La comparació es fa primer en funcio de la fila i en cas de ser iguals en funció de la columna.
	 * @note La ordenació, per tant, és de dalt a baix i de dreta a esquerra.
	 * @note Exemples a1 < a8, b5 > a5, h3 == h3 (retorna 0) @see Comparable.
	 * @param o Posicio amb la que es compararà
	 */
	@Override
	public int compareTo(Posicio o) {
		if(compareFila(o) != 0)
			return (compareFila(o));
		else 
			return (compareColumna(o));
	}
	
	/**
	 * @post: Compara la fila i la columna entre this i o. Si son iguals retorna true, altrament false.
	 */
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof Posicio))
			return false;
		else {
			Posicio cmp = (Posicio) o;
			if(this.fila() == cmp.fila() && this.columna() == cmp.columna())
				return true;
			else
				return false;
		}
	}
	
	/**
	 * @return La fila de la posició.
	 */
	public int fila() {
		return this.fila;
	}
	
	/**
	 * @pre posInicial i posFinal ! null
	 * @param posInicial Posició inicial a partir de la que es fa el càlcul.
	 * @param posFinal Posició final a partir de la que es fa el càlcul.
	 * @return Si el desplaçament entre posInicial i posFinal és diagonal
	 */
	public boolean movimentDiagonal( Posicio posFinal) {
		//Un moviment serà diagonal quan la diferència entre files == diferència entre columnes.
		Integer difFiles = Math.abs(posFinal.fila() - this.fila());
		Integer difColumnes = Math.abs(posFinal.columna() - this.columna()); 
		return difFiles.compareTo(difColumnes) == 0;
	}
	
	/**
	 * @pre posInicial i posFinal ! null
	 * @param posInicial Posició inicial a partir de la que es fa el càlcul.
	 * @param posFinal Posició final a partir de la que es fa el càlcul.
	 * @return Si el desplaçament entre posInicial i posFinal és rectilini
	 */
	public boolean movimentRectilini(Posicio posFinal) {
		return((compareFila(posFinal)== 0) || (compareColumna(posFinal) == 0));
	}
	
	/**
	 * @pre o != null.
	 * @param o Posicio amb la que es canviarà.
	 * @post S'han intercanviat els valors entre this i o.
	 */
	public void swap(Posicio o) {
		int filaTemp = this.fila();
		int colTemp = this.columna();
		
		this.fila = o.fila();
		this.columna = o.columna();
		
		o.fila = filaTemp;
		o.columna = colTemp;
	}
	
	/**
	 * @returns La representació algebraica de la posició. Ex "a3" en format string.	
	 */
	@Override
	public String toString() {
		char lletra = (char)(this.columna() + 96);
		Integer filaInteger = this.fila;
		String res = lletra + filaInteger.toString();
		return res;
	}

}
