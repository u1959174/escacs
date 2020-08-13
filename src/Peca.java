import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *  @file Peca.java
 *  @brief Classe Peca
 */

/** 
 *  @class Peca
 *  @brief Classe que representa una Peca dels escacs.
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public class Peca implements Comparable<Peca>{

	/**
	 * Color de la peça.
	 */
	private Color color;
	
	/**
	 * Indica si la peça s'ha mogut(true) o no(false) de posicio.
	 */
	private boolean moguda;
	
	/**
	 * Posició on està la Peca.
	 */
	private Posicio posicio;
	
	/**
	 * Indica si la peça ja s'ha promocionat i per tant no ho podrà tornar a fer.
	 */
	private boolean promocionada;
	
	/**
	 * Tauler on es la Peça
	 */
	private Tauler tauler;
	
	/**
	 * Tipus de la Peca
	 */
	private TipusPeca tipusPeca;
	
	/**
	 * @post crea una peça a partir del tipus i el color.
	 * @param tipus Tipus de la Peça.
	 * @param color Color de la Peça.
	 */
	public Peca(TipusPeca tipus, Color color) {
		this.tipusPeca = tipus;
		this.color = color;
		moguda = false;
		posicio = null;
		promocionada = false;
	}
	
	/**
	 * @post La peça sap a quina posició està.
	 * @param novaPos Nova posició de la Peça. Pot ser null, indicant que la peça ja no està al tauler
	 */
	public void canviarDePosicio(Posicio novaPos) {
		this.posicio = novaPos;
	}
	
	/**
	 * @return Una copia profunda d'aquesta peça amb el color canviat. Important: NO s'ha canviat la posició i es manté el tauler.
	 */
	public Peca canviarPrespectiva() {
		Peca res = this.deepCopy();
		res.color = Color.canviaColor( this.color());
		return res;
	}
	
	/**
	 * @return El color de la Peça
	 */
	public Color color() {
		return this.color;
	}
	
	/**
	 * @brief Compara les peces a partir de la seva posicio. En cas de que alguna no tingui posicio assignada, es fa servir la posicio fora del tauler 0,0.
	 */
	@Override
	public int compareTo(Peca o) {
		if(this == o)
			return 0; //Mateixa ref.
		if(o instanceof Peca) {
			Posicio pos1;
			Posicio pos2;
				try {
					pos1 = this.onEstic();
				}
				catch(IllegalStateException e) {
					pos1 = new Posicio(0,0);
				}
				
				try {
					pos2 = ((Peca) o).onEstic();
				}
				catch(IllegalStateException e) {
					pos2 = new Posicio(0,0);
				}
				
				return pos1.compareTo(pos2);

		}
		throw new InvalidParameterException("[ERROR]: S'ha intentat comparar Posicio amb una altre classe");
	}
	
	/**
	 * @return Una referencia a una copia d'aquesta peça
	 */
	public Peca deepCopy() {
		Peca res = new Peca(this.tipus(),this.color());
		if(!this.quieta())
			res.moure();
		if(this.promocionada())
			res.promocionada = true;
		res.canviarDePosicio(new Posicio(this.onEstic().fila(),this.onEstic().columna()));
		res.initTauler(this.tauler);
		return res;
	}
	
	/**
	 * @note: En cas d'haver tirat enrrere una jugada, aquest mètode permet tornar la peça a l'estat de no-moguda.
	 * @post: La peça sap que no s'ha mogut.
	 */
	public void desfer() {
		this.moguda = false;
	}
	
	/**
	 * @brief Dues peces son iguals si tenen el mateix nom, color i Posicio o bé les o és referencia a this.
	 * @note Si alguna de les Peces no té Posició assignada, es fa servir la posicio fora del tauler 0,0.
	 */
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true; //Mateixa ref.
		if(o instanceof Peca) {
			
			//Check 1: mateix color
			if(!this.color().equals(((Peca) o).color())) {
				return false;
			}
			
			//Check 2: mateix nom
			int compNoms = this.tipus().toString().compareTo(((Peca) o).tipus().toString());
			if(compNoms != 0)
				return compNoms == 0;
			//Check 3 mateixaPosicio
			else {
				Posicio pos1;
				Posicio pos2;
					try {
						pos1 = this.onEstic();
					}
					catch(IllegalStateException e) {
						pos1 = new Posicio(0,0);
					}
					
					try {
						pos2 = ((Peca) o).onEstic();
					}
					catch(IllegalStateException e) {
						pos2 = new Posicio(0,0);
					}
				return pos1.compareTo(pos2) == 0;
			}
				
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * @return Una nova peça del mateix color i amb el tipus nouTipus. Aquesta peça no esta encara al tauler, no sap on està.
	 */
	public Peca hasPromocionat(TipusPeca nouTipus) {
		Peca res = new Peca(nouTipus, this.color());
		if(!this.quieta())
			res.moure();
		res.promocionada = true;
		return res;
	}
	
	/**
	 * @post La Peça sap a quin tauler està.
	 * @param ref Tauler on està la peça
	 */
	public void initTauler(Tauler ref) {
		this.tauler = ref;
	}
	
	/**
	 * @post: La peça sap que s'ha mogut.
	 */
	public void moure() {
		this.moguda = true;
	}

	/**
	 * @pre la Peça sap a quina posició està. Se li pot dir a través de Peca::canviarDePosicio(Posicio novaPos).
	 * @return La posicio on està la Peça
	 * @throws IllegalStateException Si la peça encara no sabia a quina posicio estava.
	 */
	public Posicio onEstic() throws IllegalStateException{
		if(this.posicio != null)
			return this.posicio;
		else
			throw new IllegalStateException("[ERROR]: La Peça encara no sap on està!");
	}
	
	/**
	 * @pre La peça sap on es. Se li pot dir a traves de canviarDePosicio(). La peça està en un tauler.
	 * @return Una col·lecció de les posicions i el moviment associat on aquesta peça pot anar tenint en compte l'estat actual del tauler. No es comprova la validesa de la jugada.
	 * @note Aquest mètode no s'ha de confondre amb Tauler::possiblesMovimetsValids, que a diferència d'aquest comprova la validesa de la jugada.
	 */
	public Collection<Pair<Posicio,Moviment>> possiblesMoviments(){
		
		Pair<Integer,Integer> dimensions = this.tauler.dimensions();
		Collection<Pair<Posicio,Moviment>> res = new ArrayList<Pair<Posicio,Moviment>>();
		
		//Per cada casella del tauler mirem si la peça hi pot anar.
		for(int fila = 1; fila <= dimensions.second; fila++) {
			for(int columna = 1; columna <= dimensions.first; columna++) {
				
				Posicio casellaActual = new Posicio(fila,columna);
				Moviment possibleMoviment = this.potAnar(casellaActual);
				if(possibleMoviment != null) {
					Pair<Posicio,Moviment> nouElement = new Pair<Posicio,Moviment>(casellaActual,possibleMoviment);
					res.add(nouElement);
				}
			}
		}
		return res;
	}
	
	/**
	 * @pre La peça sap on es. posAnar posició dins el tauler.  La peça està en un tauler.
	 * @param posAnar Posicio on es consulta si la peça pot anar.
	 * @note No es comprova si el fet de desplaçar-se pot deixar el rei en escac -> moviment invàlid
	 * @return Un Moviment que serà null quan aquest sigui impossible. En cas de ser possible es retorna el propi moviment.
	 */
	public Moviment potAnar(Posicio posAnar) {
		
		boolean pecesAlMig = false;
		Peca pecaADesti = this.tauler.tensPeca(posAnar);
		TipusPeca TPecaFinal = null;

		if(pecaADesti != null) {
			TPecaFinal = pecaADesti.tipus();
		}

		if(onEstic().movimentDiagonal(posAnar) || onEstic().movimentRectilini(posAnar))
			//nomes cal calcular-ho per moviments rectilinis i diagonals ja que en cas de combinats pot saltar(enunciat) = es indiferent si hi han peces al mig
			pecesAlMig = this.tauler.pecesAlMig(this.onEstic(), posAnar, true,false).size() > 0; 
		
		

		Moviment mov;

		mov = this.tipus().provaMoviment(this.onEstic(), posAnar, this.quieta(), this.color().equals(Color.Blanques),TPecaFinal , pecesAlMig);
		
		if(pecaADesti != null && mov != null) {
			if(pecaADesti.color().equals(this.color()) && !mov.esEnroc()) {
				//Si el moviment mata es fa sobre una peça aliada sense ser enrroc llavors el moviment és ilegal.
				mov = null; 
			}
		}
		
		return mov;
	}
	
	/**
	 * @return Un booleà que indica si la peça ja s'ha promocionat.
	 */
	public boolean promocionada() {
		return this.promocionada;
	}
	
	/**
	 *
	 * @return Un booleà en funció de si la peça s'ha mogut
	 */
	public boolean quieta() {
		return !this.moguda;
	}
	
	/**
	 * 
	 * @return El tipus de la peça.
	 */
	public TipusPeca tipus() {
		return this.tipusPeca;
	}
	
	/**
	 * @returns Un String amb la representació de la peça en funció del seu color.
	 */
	@Override
	public String toString() {
		
		Character representacio  = Character.toUpperCase(tipus().diguemSimbol());
		if (color().equals(Color.Negres)) {
			representacio = Character.toLowerCase(tipus().diguemSimbol());
		}
		return representacio.toString();
	}

}