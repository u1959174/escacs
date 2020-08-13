import java.util.Collection;

/**
 *  @file MinIMaxSequencial.java
 *  @brief Classe MinIMaxSequencial
 */

/** 
 *  @class MinIMaxSequencial
 *  @brief Classe que conté l'algoritme MinIMax aplicat als Escacs en mode Seqüencial
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */

public class MinIMaxSequencial implements MinIMax{

	/**
	 * Color del jugador que mou en aquest nivell de la IA.
	 */
	private Color colorQueMou;

	/**
	 * Moviment que s'aplica a quest nivell. Si és el primer nivell serà null
	 */
	private Pair<Peca,Pair<Posicio,Moviment>> movimentAplicat;
	
	/**
	 * Profunditat d'aquest nivell
	 */
	private int profunditatActual;
	
	/**
	 * Profunditat màxima a la que la IA calcularà la millor jugada.
	 */
	private int profunditatTotalIA = 3;
	
	/**
	 * Referencia al tauler a partir del qual s'ha de calcular l'algorisme.
	 */
	private Tauler tauler;
	
	/**
	 * @brief Constructor per defecte.
	 * @param tauler Tauler sobre el qual s'operarà
	 * @param colorQueMou Color del qual se'n calcularà la millor jugada.
	 */
	public MinIMaxSequencial(Tauler tauler, Color colorQueMou) {
		this.tauler = tauler;
		this.colorQueMou = colorQueMou;
		
	}
	

	/**
	 * @brief Constructor per la crida recursiva
	 * @param tauler Tauler sobre el qual s'operarà
	 * @param taulerPrevi Tauler del nivell anterior.
	 * @param colorQueMou Color del qual se'n calcularà la millor jugada.
	 * @param profunditatActual Profunditat d'aquest nivell
	 * @param movimentAplicat Moviment que s'aplica en aquest nivell
	 * @post Es garenteix que taulerPrevi no es modificarà.
	 */
	private MinIMaxSequencial(Tauler tauler, Color colorQueMou, int profunditatActual, Pair<Peca,Pair<Posicio,Moviment>> movimentAplicat) {
		this.tauler = tauler;
		this.colorQueMou = colorQueMou;
		this.profunditatActual = profunditatActual;
		this.movimentAplicat = movimentAplicat;
	}
	
	@Override
	public Jugada calcularMillorJugada() {		
		this.profunditatActual = 0; //Inicialitzem la profunditat
		Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> millorJugada = iCalcularMillorJugada();
		Jugada res =  new Jugada(millorJugada.second.first.onEstic(),millorJugada.second.second.first,null);

		return res;
		
	}
	
	/**
	 * @pre Com a mínim hem de tenir una jugada vàlida possible.
	 * @param profunditat Profunditat que té actualment la IA
	 * @param movimentAplicat moviment que s'ha aplicat en aquest nivell.
	 * @return la millor jugada possible segons l'algoritme MiniMax i el seu valor en un pair<ValorJugada,<PosicioInicialJugada,PosicioFinalJugada>>
	 */
	private Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> iCalcularMillorJugada(){
		
		
		Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> millorJugada = null;
	
		Integer millorValor = Integer.MIN_VALUE;
		if(this.colorQueMou.equals(Color.Negres))
			millorValor = Integer.MAX_VALUE;
		
		boolean maximitzar = this.colorQueMou.equals(Color.Blanques);
		boolean fulla = false; //Serà true quan en un node que no està a la màxima profunditat, no se'n pugui crear més fills. Altrament dit, no hi hagi cap subJugada possible.
		
		if(profunditatActual != profunditatTotalIA) {
			//Cas recursiu: per cada jugada possible en aquest nivell l'apliquem i fem crida recursiva
			Collection<Pair<Peca,Pair<Posicio,Moviment>>> possiblesJugades = this.tauler.possiblesMovimentsJugador(colorQueMou);
			
			//Per totes les jugades possibles
			for( Pair<Peca,Pair<Posicio,Moviment>> jugadaAct : possiblesJugades) {
				
				//1.Generar un tauler igual per fer la jugada de prova
				Tauler taulerAmbAquestaJugada = this.tauler.deepCopy();
				//2. Comprovar la validesa de la jugada
				boolean jugadaValida = ferIValidarJugada(taulerAmbAquestaJugada, jugadaAct, colorQueMou);
				
				//3. Fer la crida recursiva per obtenir la millor jugada a partir d'aquest nivell.
				if(jugadaValida) { 
					int novaProfunditat = profunditatActual+1;
					MinIMaxSequencial nouNivell =  new MinIMaxSequencial(taulerAmbAquestaJugada, Color.canviaColor(colorQueMou), novaProfunditat, jugadaAct);
					//Crida recursiva
					Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> candidata = nouNivell.iCalcularMillorJugada();
					
					
					Integer valorCandidata = candidata.first; //Valor de la millor jugada passada pels subnivells
					//Si la jugada és millor que la que teniem la canviem per l'actual.	
					if(maximitzar) {
						if(millorValor < valorCandidata) {
							millorJugada = new Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>>(valorCandidata,jugadaAct);
							millorValor = valorCandidata;
						}
					}
					else {
						if(millorValor > valorCandidata) {
							millorJugada = new Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>>(valorCandidata,jugadaAct);
							millorValor = valorCandidata;
						}
					}
					
				}
					
			}
			if(millorJugada == null)
				fulla = true;
				
		}
		
		if(profunditatActual == profunditatTotalIA || fulla) {
			//Cas base retornem el valor d'aquesta jugada.
				
			Integer valorJugada = this.tauler.evaluarTauler();
			if(movimentAplicat.second.second.esEnroc()) {
				//Matar una reina estàndard serien 90punts. Fer un enrroc el valorem en 45.
				if(maximitzar)
					valorJugada +=45;
				else
					valorJugada -=45;
			}
			millorJugada = new Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>>(valorJugada,movimentAplicat);
			
		}
		return millorJugada;
	}

}