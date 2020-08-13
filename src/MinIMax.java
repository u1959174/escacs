/**
 *  @file MinIMax.java
 *  @brief Interfície MinIMax
 */

/** 
 *  @class MinIMax
 *  @brief Interfície que coné els mètodes que implementa l'algoritme MinIMax adaptat al joc dels escacs.
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public interface MinIMax {
	
	/**
	 * @pre Hi ha com a mínim una jugada vàlida a fer.
	 * @param colorQueMou Color del qual es calcularà la millor jugada possible.
	 * @return La millor jugada calculada amb aquest algoritme i el tauler actual del joc. La jugada és vàlida segons les regles d'aquest joc.
	 */
	public Jugada calcularMillorJugada();
	
	/**
	 * @param taulerAmbAquestaJugada Tauler sobre el que se li aplicara la jugada.
	 * @param jugadaAct Jugada que s'aplicarà al tauler. Pair<PecaQueMou,Pair<Posicio destí, Moviment aplicat per anar a desti>>.
	 * @param colorQueMou Color que fa el moviment.
	 * @return Un booleà que indica si la jugada que s'ha fet es vàlida o no.
	 * @post La jugada s'ha aplicat al tauler si aquesta era vàlida. Altrament no es garanteix aquesta aplicació.
	 */
	public default boolean ferIValidarJugada(Tauler taulerAmbAquestaJugada, Pair<Peca,Pair<Posicio,Moviment>> jugadaAct, Color colorQueMou) {
		boolean jugadaValida = true;
		try {
			Peca pecaQueEsMou = taulerAmbAquestaJugada.tensPeca(jugadaAct.first.onEstic());
			Pair<Posicio, Moviment> desplacament =  new Pair<Posicio,Moviment>(new Posicio(jugadaAct.second.first.fila(),jugadaAct.second.first.columna()),jugadaAct.second.second);
			Partida.realitzarJugada(taulerAmbAquestaJugada, pecaQueEsMou, desplacament, new Contadors(),colorQueMou);
		}
		catch(ExcepcioG e) { 
				//En cas d'excepcio normal en fer la jugada la declarem no valida.
				jugadaValida = false;
		}
		catch(Exception e) {
				//En cas d'excepcio no habitual
				System.err.println("[ERROR]: Error desconegut en fer una jugada a l'algoritme MiniMax, info:");
				System.err.println(e.getMessage());
				jugadaValida = false;
		}
			EstatTauler estat = taulerAmbAquestaJugada.estat(Color.canviaColor(colorQueMou));
		if(estat.equals(EstatTauler.ESCAC_SIMPLE) || estat.equals(EstatTauler.ESCAC_I_MAT)) {
				//Si despres de fer la Jugada hi ha escac en contra la jugada no es valida
				jugadaValida = false;
		}
		return jugadaValida;
	}
}
