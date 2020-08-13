/**
 *  @file Casella.java
 *  @brief Classe Casella
 */

/** 
 *  @class Casella
 *  @brief Una casella d'un Tauler. Classe contenidora. Pot contenir una Peça.
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public class Casella {
	/**
	 * Peça que conté la casella. Per defecte una casella no té cap peça i aquest paràmetre serà = null.
	 */
	private Peca peca;
	
	/**
	 * @post Crea una casella buida(sense cap peça).
	 */
	public Casella() {
		peca = null;
	}
	
	/**
	 * @post La casella ara conté la peça. Si hi havia una peça prèviament ha quedat sobreescrita.
	 * @param novaPeca: Peça que s'afegirà.
	 */
	public void agafaPeca(Peca novaPeca) {
		this.peca = novaPeca;
	}
	
	/**
	 * @return Retorna la peça que conté la casella. En cas de no contenir cap peça retorna null.
	 */
	public Peca tePeca() {
		return this.peca;
	}
	
	/**
	 * @post S'ha eliminat la peça de la casella si n'hi havia, altrament no s'ha modificat.
	 */
	public void treuPeca() {
		this.peca = null;
	}
	
}
