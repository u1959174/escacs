import java.util.ArrayList;

/**
 *  @file CPU.java
 *  @brief Classe CPU
 */

/** 
 *  @class CPU
 *  @brief Classe que representa un Jugador controlat per l'ordinador.
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */

public class CPU implements Jugador {

	/**
	 * Color de les peces de la CPU.
	 */
	private Color color;
	
	/**
	 * Nom de la CPU.
	 */
	private String nom;
	
	/**
	 * Indica si el jugador té pendent decidir si accepta o no taules.
	 */
	private boolean taulesPendentsAcceptacio;
	
	/**
	 * @brief Crea un jugador controlat per ordinador
	 * @param nom Nom de la CPU
	 * @param color Color de les peces de la CPU
	 */
	public CPU(String nom, Color color) {
		this.nom = "BOT- " +nom;
		this.color = color;
		this.taulesPendentsAcceptacio = false;
	}
	
 
	@Override
	public Jugada jugar(boolean potMoure) {
		if(taulesPendentsAcceptacio) {
			return new Jugada(ResultatJugada.TRebutjades,null); //Tal i com va dir en Miquel, la CPU mai es rendeix.
		}
		Tauler tauler = Escacs.obtenirTauler();
		
		Jugada jugadaConeguda = MotorCPU.esEstatConegut(tauler, color);
		Jugada jugadaAFer = null;
		if(jugadaConeguda == null) {
			System.out.println("["+nom+"]"+ ":Booop Bip 404: Aquesta jugada no em sona, és igual, et guanyaré igualment. Muahahaha!");
			MinIMax IA = new MinIMaxSequencial(tauler, color);
			//MinIMax IA = new MinIMaxParalel(tauler, color); //Opcio de millorar el rendiment de la IA aplicant divideix i venç amb paralelització. No està 100% provat i per tant no es fa servir.
			jugadaAFer = IA.calcularMillorJugada();
		
		}
		else {
			System.out.println("["+nom+"]"+ ":Bip Bop Bip, Sóc més llest que tu!");
			jugadaAFer = new Jugada(jugadaConeguda.obtenirOrigen(),jugadaConeguda.obtenirDesti(),null); //Modifiquem la jugada per tal que estigui en el format demanat a les precondicions.
		}
		System.out.println("["+nom+"]"+ ":Jugada que faig: "+jugadaAFer.obtenirOrigen() + " " + jugadaAFer.obtenirDesti());
		return jugadaAFer;
	}

	
	@Override
	public void preguntarTaules() {
		this.taulesPendentsAcceptacio = true;
		
	}
	
	
	/**
	 * @note En el cas de la CPU, si no estem "enganxats" a un conjunt de jugades, triarem la peça amb més valor per promocionar.
	 * @note Altres opcions vàlides: En cas de promoció sense estar "enganxats" mirar quina peça ens dóna més valor a llarg termini.
	 */
	@Override
	public TipusPeca promocionar() {
		Tauler tauler = Escacs.obtenirTauler(); //Reiniciem el tauler
		
		TipusPeca res = null;
		
		//Jugada coneguda -> Promocionem a la peça coneguda.
		Jugada jugadaAFer = MotorCPU.esEstatConegut(tauler, color);
		if(jugadaAFer != null)
		 res = jugadaAFer.obtenirPecaPromocionada();
		
		//Jugada desconeguda -> Promocionem al tipus amb màxim valor (sempre que no sigui el rei).
		if(res == null){
			int maxValor = 0;
			ArrayList<TipusPeca> tipusDisponibles = Escacs.tipusDisponibles();
			for(TipusPeca tipusAct : tipusDisponibles) {
				if(tipusAct.diguemValor() > maxValor && !tipusAct.toString().contains("REI")) {
					res = tipusAct;
				}
			}
		}
		return res;
	}

	@Override
	public String toString() {
		return this.nom;
	}
}
