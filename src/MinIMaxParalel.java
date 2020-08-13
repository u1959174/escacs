import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 *  @file MinIMaxParalel.java
 *  @brief Classe MinIMaxParalel
 */

/** 
 *  @class MinIMaxParalel
 *  @brief Classe que conté l'algoritme MinIMax aplicat als Escacs en mode Paralel
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public class MinIMaxParalel extends RecursiveTask<Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>>> implements MinIMax{
	
	/**
	 * Color del jugador que mou en aquest nivell de la IA.
	 */
	private Color colorQueMou;

	/**
	 * Moviment que s'aplica a quest nivell. Si és el primer nivell serà null.
	 */
	private Pair<Peca,Pair<Posicio,Moviment>> movimentAplicat;
	
	/**
	 * Profunditat d'aquest nivell.
	 */
	private int profunditatActual;
	
	/**
	 * Profunditat màxima a la que la IA calcularà la millor jugada.
	 */
	private int profunditatTotalIA = 4;
	
	/**
	 * Referencia al tauler a partir del qual s'ha de calcular l'algorisme.
	 */
	private Tauler tauler;
	
	/**
	 * @brief Constructor per defecte.
	 * @param tauler Tauler sobre el qual s'operarà.
	 * @param colorQueMou Color del qual se'n calcularà la millor jugada.
	 */
	public MinIMaxParalel(Tauler tauler, Color colorQueMou) {
		this.tauler = tauler;
		this.colorQueMou = colorQueMou;
		
	}
	

	/**
	 * @brief Constructor per la crida recursiva.
	 * @param tauler Tauler sobre el qual s'operarà.
	 * @param colorQueMou Color del qual se'n calcularà la millor jugada.
	 * @param profunditatActual Profunditat d'aquest nivell.
	 * @param movimentAplicat Moviment que s'aplica en aquest nivell.
	 */
	private MinIMaxParalel(Tauler tauler, Color colorQueMou, int profunditatActual, Pair<Peca,Pair<Posicio,Moviment>> movimentAplicat) {
		this.tauler = tauler;
		this.colorQueMou = colorQueMou;
		this.profunditatActual = profunditatActual;
		this.movimentAplicat = movimentAplicat;
	}
	
	@Override
	public Jugada calcularMillorJugada() {		
		
		ForkJoinPool pool = new ForkJoinPool();
		Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> millorJugada = pool.invoke(new MinIMaxParalel(this.tauler,colorQueMou,0,null));
		Jugada res = new Jugada(millorJugada.second.first.onEstic(),millorJugada.second.second.first,null);

		return res;
		
	}

	private Collection<Pair<MinIMaxParalel,Pair<Peca,Pair<Posicio,Moviment>>>> splitAndFork(Collection<Pair<Peca,Pair<Posicio,Moviment>>> possiblesJugades) {
    	Collection<Pair<MinIMaxParalel,Pair<Peca,Pair<Posicio,Moviment>>>> subNivells = new ArrayList<Pair<MinIMaxParalel,Pair<Peca,Pair<Posicio,Moviment>>>>();
    	//Per totes les jugades possibles
		for( Pair<Peca,Pair<Posicio,Moviment>> jugadaAct : possiblesJugades) {
			
			//1.Generar un tauler igual per fer la jugada de prova
			Tauler taulerAmbAquestaJugada = this.tauler.deepCopy();
			//2. Comprovar la validesa de la jugada
			boolean jugadaValida = ferIValidarJugada(taulerAmbAquestaJugada, jugadaAct, colorQueMou);
			
			//3. Fer la crida recursiva per obtenir la millor jugada a partir d'aquest nivell.
			if(jugadaValida) { 
				int novaProfunditat = profunditatActual+1;

				MinIMaxParalel nouNivell =  new MinIMaxParalel(taulerAmbAquestaJugada, Color.canviaColor(colorQueMou), novaProfunditat, jugadaAct);
				//Crida recursiva
				Pair<MinIMaxParalel,Pair<Peca,Pair<Posicio,Moviment>>> nouElement = new Pair<MinIMaxParalel,Pair<Peca,Pair<Posicio,Moviment>>>(nouNivell,jugadaAct);
				subNivells.add(nouElement);
				nouNivell.fork();
				
				}
				
			}
		return subNivells;
    }
	
	@Override
	protected Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> compute() {
	
		Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> millorJugada = null;
	
		Integer millorValor = Integer.MIN_VALUE;
		if(this.colorQueMou.equals(Color.Negres))
			millorValor = Integer.MAX_VALUE;
		
		boolean maximitzar = this.colorQueMou.equals(Color.Blanques);
		boolean fulla = false; //Serà true quan en un node que no està a la màxima profunditat, no se'n pugui crear més fills. Altrament dit, no hi hagi cap subJugada possible.
		
		//CAS recursiu
		if(profunditatActual != profunditatTotalIA) {
			//Calculem les possibles subjugades
			Collection<Pair<Peca,Pair<Posicio,Moviment>>> possiblesJugades = this.tauler.possiblesMovimentsJugador(colorQueMou);
			
			//Per cada subjugada fem un thread i en demanem el calcul.
			//Pair<SubNivell,Pair<Jugada que provoca aquest subnivell>>
			Collection<Pair<MinIMaxParalel,Pair<Peca,Pair<Posicio,Moviment>>>> subNivellsAmbJugada = splitAndFork(possiblesJugades);
			
			//Esperem els subnivells i busquem el que té el valor que més ens interessa(màxim o mínim).
			for(Pair<MinIMaxParalel,Pair<Peca,Pair<Posicio,Moviment>>> subNivellAmbJugadaActual : subNivellsAmbJugada) {
				
				Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> resultatSubNivellActual = subNivellAmbJugadaActual.first.join();
				Integer valorCandidata = resultatSubNivellActual.first; //Valor de la millor jugada passada pels subnivells
				//Si la jugada és millor que la que teniem la canviem per l'actual.	
				if(maximitzar) {
					if(millorValor < valorCandidata) {
						millorJugada = new Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>>(valorCandidata,subNivellAmbJugadaActual.second);
						millorValor = valorCandidata;
					}
				}
				else {
					if(millorValor > valorCandidata) {
						millorJugada = new Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>>(valorCandidata,subNivellAmbJugadaActual.second);
						millorValor = valorCandidata;
					}
						
				}
			}
			
			if(millorJugada == null)
				fulla = true;
				
		}
		
		if(profunditatActual == profunditatTotalIA || fulla) {
			millorJugada = computeDirectly();
			
		}
		return millorJugada;

	}
    
    //CAS BASE
    protected Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> computeDirectly() {
		//Cas base retornem el valor d'aquesta jugada.
		
		
		Integer valorJugada = this.tauler.evaluarTauler();

		Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>> millorJugada = new Pair<Integer,Pair<Peca,Pair<Posicio,Moviment>>>(valorJugada,movimentAplicat);
		
		
		return millorJugada;
    }
}
