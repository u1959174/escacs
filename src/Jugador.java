/**
 *  @file Huma.java
 *  @brief Inerfície Huma
 */

/** 
 *  @class Huma
 *  @brief Interfície que generalitza un Jugador d'escacs.
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public interface Jugador {
	
	/**
	 * @param modeText Indica si el jugador juga en mode text. En aquest cas el jugador té opcions diferents a mode text.
	 * @post S'ha demanat al jugador quina jugada vol fer i s'ha retornat una jugada que conté la seva desició.
	 * @return La Jugada que s'ha fet. Null si en mode grafic s'ha demanat sortir.
	 * @note La Jugada pot contenir un moviment, pot contenir una desició diferent de moure una peça com per exemple: petició de taules o rendició.
	 *  També pot contenir una desició buida indicant que s'han desfet o refet jugades i pot ser null en cas que en mode grafic s'hagi sortit del menú.
	 */
	public Jugada jugar(boolean modeText);
	
	/**
	 * @post El jugador sap que li han demanat taules.
	 */
	public void preguntarTaules();
	
	/**
	 * @pre Hi ha alguna peca a promocionar.
	 * @return Retorna la peca a la que es desitja promocionar. Es garanteix que el TipusPeca retornat no serà el tipus REI.
	 */
	public TipusPeca promocionar();
	
	/**
	 * @return El nom del Jugador.
	 */
	@Override
	public String toString();
}
