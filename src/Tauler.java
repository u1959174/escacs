import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
/**
 *  @file Tauler.java
 *  @brief Classe Tauler
 */

/** 
 *  @class Tauler
 *  @brief Classe que representa un tauler d'Escacs. Contenidor de Caselles. Conté alguns mètodes de suport relacionats amb el tauler
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public class Tauler {
	/**
	 * Conjunt de parells de peces que en un estat del tauler(que pot no ser l'actual) amenacen al rei Blanc i el moviment que ho permet.
	 */
	private ArrayList<Pair<Peca,Moviment>> atacantsAlReiBlanc;
	
	/**
	 *  Conjunt de parells de peces que en un estat del tauler(que pot no ser l'actual) amenacen al rei Negre i el moviment que ho permet.
	 */
	private ArrayList<Pair<Peca,Moviment>> atacantsAlReiNegre;
	
	/**
	 * Caselles del tauler en format matriu.
	 * L'acces és caselles[fila][columna]
	 * La posició 0 del contenidor no es fa servir.
	 */
	private ArrayList<ArrayList<Casella>> caselles;
	
	/**
	 * Nombre de columnes del tauler
	 */
	private int columnes; 
	
	/**
	 * Nombre de files del tauler
	 */
	private int files; 
	
	/**
	 * Index de les peces de color blanc. La clau és el nom del tipus de la peça ex "REI" i el valor és una llista de les peces d'aquell color i tipus.
	 */
	private HashMap<String,List<Peca>> indexBlanques;
	
	/**
	 * Index de les peces de color negre. La clau és el nom del tipus de la peça ex "REI" i el valor és una llista de les peces d'aquell color i tipus.
	 */
	private HashMap<String,List<Peca>> indexNegres;
	
	/**
	 * Conjunt de posicions amenaçades per les peces blanques. Pot no estar actualitzat.
	 */
	private ArrayList<Posicio> taulerAmenacesBlanques;
	
	/**
	 * Conjunt de posicions amenaçades per les peces negres. Pot no estar actualitzat.
	 */
	private ArrayList<Posicio> taulerAmenacesNegres;
	
	/**
	 * @post Crea el tauler a partir del nombre de files i columnes que ha de tenir.
	 * @param files Files del tauler
	 * @param columnes Columnes del tauler
	 */
	public Tauler(int files, int columnes) {
		this.files = files;
		this.columnes = columnes;
		
		this.caselles= new ArrayList<ArrayList<Casella>>(files+1);
		for (int i = 0; i < files+1; i++) {
			caselles.add(new ArrayList<Casella>(columnes+1));
		}
		for (ArrayList<Casella> fila : caselles) {
			for (int i = 0; i < columnes+1; i++) {
				fila.add(new Casella());
			}
		}
		
		taulerAmenacesBlanques = new ArrayList<Posicio>();
		taulerAmenacesNegres = new ArrayList<Posicio>();
		
		indexBlanques = new HashMap<String,List<Peca>>();
		indexNegres = new HashMap<String,List<Peca>>();
		
		atacantsAlReiBlanc = new ArrayList<Pair<Peca,Moviment>>();
		atacantsAlReiNegre = new ArrayList<Pair<Peca,Moviment>>();
	}
	
	
	/**
	 * @pre La Posicio pos existeix al tauler. No pot haver-hi al tauler cap referència a la mateixa peça que es vol afegir.
	 * @post S'ha afegit la peça del tauler. Si ja hi havia cap peça a aquella Posicio s'ha sobreescrit. La peça afegida sap a quin tauler està i a quina posició d'aquest tauler.
	 * @param pos Posicio on s'afegirà la peça.
	 * @param peca peça que s'afegira.
	 */
	public void afegirPeca(Posicio pos, Peca peca) {
		if(pos.fila() < 1 || pos.columna() < 1)
			System.err.println("[possible][ERROR]: S'està afegint una peça a posicions invàlides del tauler. Posició: {" + pos.fila() +"," + pos.columna() +"}");
		Peca anterior = caselles.get(pos.fila()).get(pos.columna()).tePeca();
		if(anterior != null)
			this.eliminarPeca(pos);
		caselles.get(pos.fila()).get(pos.columna()).agafaPeca(peca);
		peca.canviarDePosicio(pos); //Indiquem a la peça on està.
		peca.initTauler(this); //Passem la referencia al tauler 
		afegirIndex(peca);

	}
	
	/**
	 * @pre posInicialA i posInicialB són dues posicions diferents que fan referencia a dues Peces diferents al tauler. Les dues peces estan en aquest tauler.
	 * @param posInicialA Posicio actual/inicial de la peça A de l'enrroc.
	 * @param posInicialB Posicio actual/inicial de la peça B de l'enrroc.
	 * @return Un pair de Posicions amb les posicions de destí que hauríen de prendre les peces després d'un teoric enrroc. Si l'enrroc no és vàlid segons els moviements dels tipus es retorna null. First->posFinalA, Second->posFinalB.
	 */
	public Pair<Posicio,Posicio> calcularEnrroc(Posicio posInicialA, Posicio posInicialB){
		Peca pecaA = this.tensPeca(posInicialA);
		Peca pecaB = this.tensPeca(posInicialB);
		Pair<Posicio,Posicio> res;
		
		if(pecaA == null || pecaB == null)
			//Si falta alguna de les peces no es poden enrrocar
			return null;
		
		//Les peces són rivals -> No es pot fer l'enrroc
		if(!pecaA.color().equals(pecaB.color()))
			return null;
		
		//Comprovem si les pecesA i B s'havíen mogut.
		boolean primer = pecaA.quieta() && pecaB.quieta();
		
		//Comprovem si hi han peces al mig
		boolean pecesAlMig = this.pecesAlMig(posInicialA, posInicialB, false, false).size() > 0;
		
		//Busquem l'enrroc.
		Moviment enrroc = pecaA.tipus().provaMoviment(posInicialA, posInicialB, primer, pecaA.color().equals(Color.Blanques), pecaB.tipus(), pecesAlMig);
		if(enrroc == null)
		//Si no existeix l'enrroc entre aquestes dues peces es retorna null
			return null;
		
		if(!enrroc.esEnroc())
		//Si el moviment retornat no és enrroc retornem null
			return null;
			
		//L'enrroc és correcte -> Calculem el resultat(posicions finals) de fer-lo.
		int difCaselles = posInicialB.columna() - posInicialA.columna();
		int mig = difCaselles/2;
		
		//Si és imparell anirem a la posicio del mig +1 (o menys 1 si vas a l'esquerra)
		if(difCaselles %2 != 0) {
			if(mig < 0) {
				mig--;
			}
			else {
				mig++;
			}
		}
			
		int colFinalA = posInicialA.columna() + mig;
		int colFinalB = colFinalA +1 ;
		
		if(mig > 0)
			colFinalB -= 2;
		
		//Retornem les posicions calculades
		Posicio posFinalA = new Posicio(posInicialA.fila(),colFinalA);
		Posicio posFinalB = new Posicio(posInicialB.fila(),colFinalB);
		
		res = new Pair<Posicio,Posicio>(posFinalA,posFinalB);
		
		return res;
	}


	/**
	 * @return Una copia profunda d'aquest tauler s'hi ha aplicat un eix de simetria horitzontal.
	 * @note No es té en compte que el rei i la reina estan a la mateixa columna. 
	 */
	public Tauler canviarPerspectiva() {
		Tauler res = new Tauler(this.files,this.columnes);
		
		for(Entry<String, List<Peca>> tipusActual : indexBlanques.entrySet()) {
			for(Peca pecaAct: tipusActual.getValue()) {
				Posicio novaPos = calcularPosicioCanviPrespectiva(pecaAct);
				Peca novaPeca = pecaAct.canviarPrespectiva();
				res.afegirPeca(novaPos, novaPeca);
			}
		}
		
		for(Entry<String, List<Peca>> tipusActual : indexNegres.entrySet()) {
			for(Peca pecaAct: tipusActual.getValue()) {
				Posicio novaPos = calcularPosicioCanviPrespectiva(pecaAct);
				Peca novaPeca = pecaAct.canviarPrespectiva();
				res.afegirPeca(novaPos, novaPeca);
			}
		}
			
		return res;
	}


	/**
	 * @pre original != null.
	 * @return Una ref a una copia del tauler.
	 * @note Respecte les estructures internes només s'en copien els índexs. Les altres es recalculen cada cop que es pregunta l'estat del tauler i per tant, no cal.
	 */
	public Tauler deepCopy() {
		Tauler res = new Tauler(this.dimensions().second,this.dimensions().first);
		
		//Copiem les blanques:
		for(List<Peca> tipusAct: indexBlanques.values()) {
			for(Peca pecaAct : tipusAct) {
				Peca copia = pecaAct.deepCopy();
				res.afegirPeca(copia.onEstic(), copia);
			}
		}
		
		//Copiem les negres:
		for(List<Peca> tipusAct: indexNegres.values()) {
			for(Peca pecaAct : tipusAct) {
				Peca copia = pecaAct.deepCopy();
				res.afegirPeca(copia.onEstic(), copia);
			}
		}
		
		return res;
	}


	/**
	 * @pre: S'ha inicialitzat el Tauler
	 * @return: Un pair amb les dimensions first->columnes, seccond->files.
	 */
	public Pair<Integer,Integer> dimensions(){
		
		Pair<Integer,Integer> res = new Pair<Integer, Integer>(this.columnes,this.files);
		return res;
	}
	
	/**
	 * @param pos Posició que es comprova.
	 * @return Un booleaà que serà true si pos es dins el tauler i false altrament
	 */
	public boolean dinsTauler(Posicio pos) {
		int filaCheck = pos.fila();
		int columnaCheck = pos.columna();
		if(this.files >= filaCheck && this.columnes >= columnaCheck) {
			if(filaCheck > 0 && columnaCheck > 0) {
				return true;
			}
		}
	 		
		return false;			
	}

	
	/**
	 * @param colorPeces Color de les peces que es consulten.
	 * @return Una coleccio de totes les peces del colorPeces que conté el tauler.
	 */
	public Collection<Peca> donamPeces(Color colorPeces){
		Collection<Peca> res = new ArrayList<Peca>();
		
		if(colorPeces.equals(Color.Blanques)) {
			for(List<Peca> tipusAct : indexBlanques.values()) {
				res.addAll(tipusAct);
			}
		}
		else {
			for(List<Peca> tipusAct : indexNegres.values()) {
				res.addAll(tipusAct);
			}
		}
		return res;
	}

	/**
	 * @pre La Posicio existeix al tauler.
	 * @post S'ha eliminat la peça del tauler. Si no hi havia cap peça en aquella Posicio no s'ha modificat. La peça ja no sap on es.
	 * @param pos Posicio on s'eliminarà la peça.
	 */
	public void eliminarPeca(Posicio pos) {
		Peca pecaAEliminar = caselles.get(pos.fila()).get(pos.columna()).tePeca();
		if(pecaAEliminar != null) {
			caselles.get(pos.fila()).get(pos.columna()).treuPeca();
			pecaAEliminar.canviarDePosicio(null);
			borrarIndex(pecaAEliminar);
		}
	}


	/**
	 * @param posicioAComprovar Posició que es comprovarà.
	 * @param color Color que s'agafarà de referència a la comprovació.
	 * @return un booleà que indicarà si la posició està al la última fila del tauler tenint en compte la prespectiva de cada jugador.
	 * @note Prespectiva: En la prespectiva de les negres l'última fila serà 0 mentre que per les blanques l'última serà la més gran.
	 */
	public boolean esFinal(Posicio posicioAComprovar, Color color) {
		if(color.equals(Color.Blanques)) {
			if(posicioAComprovar.fila() == this.files)
				return true;
		}
		else {
			if(posicioAComprovar.fila() == 1)
				return true;
		}
		return false;
	}

	/**
	 * @pre torn El color torn ha de ser el de l'últim jugador que ha fet una jugada. Aquesta jugada ha d'haver sigut ja aplicada al tauler.
	 * @param torn Color del jugador que ACABA de fer el torn.
	 * @return Un EstatTauler amb la informació de l'estat del tauler. Fent referència a qui acaba de jugar. Per exemple si tenim un escac, aquest l'ha provocat el jugador de color "torn".
	 */
	public EstatTauler estat(Color torn) {
		EstatTauler res = EstatTauler.NORMALITAT;
		if(hiHaEscac(Color.canviaColor(torn))) {
			res = EstatTauler.ESCAC_SIMPLE;
			if(hiHaEscacIMat(Color.canviaColor(torn)))
				res = EstatTauler.ESCAC_I_MAT;
		}
		else {
			if(hiHaTaulesPerOfegat(torn))
				res = EstatTauler.OFEGAT;
		}
		return res;
	}
	
	/**
	 * @brief Evalua aquest tauler indicant la superioritat d'un jugador en vers l'altre. El valor absolut indica la superioritat i el signe a favor de quin jugador és aquesta. Positiu->Blanques, Negatiu->Negres.
	 * @post Es garanteix que l'estat del tauler no ha canviat. S'ha retornat el valor de l'evaluació.
	 * @note Per fer aquesta evaluació s'han utilitzat diversos factors com la protecció del rei, el control del centre i desenvolupament així com les peces restants i el seu valor.
	 */
	public int evaluarTauler() {
		int puntuacioBlanques = 0;
		int puntuacioNegres = 0;
		float mitjanaFilesBlanques = 0;
		float mitjanaFilesNegres = 0;
		int valorReiBlanc = valorarPosicioRei(this.buscarPeca("REI", Color.Blanques));
		int valorReiNegre = valorarPosicioRei(this.buscarPeca("REI", Color.Negres));
		
		//Valorem Peces restants
		int i = 0;
		for(List<Peca> tipusActual : indexBlanques.values()) {
			for(Peca pecaActual : tipusActual) {
				puntuacioBlanques += pecaActual.tipus().diguemValor();	
				mitjanaFilesBlanques += pecaActual.onEstic().fila();
				i++;
			}
		}
		mitjanaFilesBlanques = mitjanaFilesBlanques/i;
		i = 0;
		for(List<Peca> tipusActual : indexNegres.values()) {
			for(Peca pecaActual : tipusActual) {
				puntuacioNegres += pecaActual.tipus().diguemValor();
				mitjanaFilesNegres += pecaActual.onEstic().fila();
				i++;
			}
		}
		mitjanaFilesNegres = files - (mitjanaFilesNegres/i) +1;
		
		float mitjanaDesenvolupament = mitjanaFilesBlanques - mitjanaFilesNegres;
		int mitjanaAInt = (int) (mitjanaDesenvolupament*100);
		
		int valorReis = (valorReiBlanc-valorReiNegre)*100;
		int puntuacioPeces = (puntuacioBlanques-puntuacioNegres)*100;
		
		int resultat = valorReis + puntuacioPeces+ mitjanaAInt;
		
		return resultat;
	}

	/**
	 * @pre posInicial i posFinal != null i dins el tauler. 
	 * @param posInicial Posició inicial a partir de la que es fa el càlcul.
	 * @param posFinal Posició final a partir de la que es fa el càlcul.
	 * @param filaPrimer En els casos en que el moviment sigui combinat, indica si el desplaçament es comença per la fila o no. Altrament no es fa servir i es pot deixar qualsevol valor booleà.
	 * @param incloureExtrems Si és true s'inclouran a la consulta les posicions Inicial i Final, altrament aquestes no es comprovaran
	 * @return Una colecció de Pair de totes les posicions entre posInicial i posFinal(incloses o no en funcio del parametre incloureExtrems) que estiguin ocupades per una Peca juntament amb aquesta última. First ->Posicio, Second -> Peca
	 */
	public Collection<Pair<Posicio,Peca>> pecesAlMig(Posicio posInicial, Posicio posFinal, boolean filaPrimer, boolean incloureExtrems){
	
		Collection<Posicio> casellesAComprovar = casellesEntrePosicions(posInicial, posFinal,filaPrimer);
		ArrayList<Pair<Posicio,Peca>> res = new ArrayList<Pair<Posicio,Peca>>();
		
		for (Posicio posicio : casellesAComprovar) {
			
			//Si s'inclouen els extrems o si no s'inclouen els extrems pero la posicio no es la inicial ni la final llavors s'ha de comprovar
			if(incloureExtrems || ( !( posicio.equals(posInicial)) && !(posicio.equals(posFinal)))) {
				Peca candidata = tensPeca(posicio);
				if(candidata != null) {
					//Si hi ha peça l'afegim a la col·leccio.
					Pair<Posicio,Peca> nouElement = new Pair<Posicio,Peca>(posicio,candidata);
					res.add(nouElement);
				}
			}
			
		}
		
		return res;
	}


	/**
	 * @pre posInicial i posFinal != null i dins el tauler. 
	 * @param posInicial Posició inicial a partir de la que es fa el càlcul.
	 * @param posFinal Posició final a partir de la que es fa el càlcul.
	 * @param filaPrimer En els casos en que el moviment sigui combinat, indica si el desplaçament es comença per la fila o no. Altrament no es fa servir i es pot deixar qualsevol valor booleà.
	 * @param colorAComprovar Color que han de tenir les peces.
	 * @return Una colecció de Pair de totes les posicions entre posInicial i posFinal(posInicial i posFinal incloses) que estiguin ocupades per una Peca del colorAComprovar juntament amb aquesta última. First ->Posicio, Second -> Peca
	 * @note No conforndre amb PecesAlMig() que no té en compte el color.
	 */
	public Collection<Pair<Posicio,Peca>> pecesAlMigColorConcret(Posicio posInicial, Posicio posFinal, boolean filaPrimer, Color colorAComprovar){
	
		Collection<Posicio> casellesAComprovar = casellesEntrePosicions(posInicial, posFinal,filaPrimer);
		ArrayList<Pair<Posicio,Peca>> res = new ArrayList<Pair<Posicio,Peca>>();
		
		for (Posicio posicio : casellesAComprovar) {
			Peca candidata = tensPeca(posicio);
			if(candidata != null) {
				if(colorAComprovar.equals(candidata.color())) {
					Pair<Posicio,Peca> nouElement = new Pair<Posicio,Peca>(posicio,candidata);
					res.add(nouElement);
				}
			}
		}
		
		return res;
	}

	/**
	 * @param colorJugador Color del jugador que ha de moure
	 * @return Un conjunt de moviments que pot fer el jugador i que poden no ser vàlids si deixen el rei en escac. El conjunt de moviments s'interpreta com first->peça que mou, Second->moviment que fa on second.first és el destí del moviment i second.second és el moviment aplicat per anar-hi.
	 * @note No confondre amb possiblesMovimentsValids(Peca) que si que assegura la validesa total de cada moviment
	 */
	public Collection<Pair<Peca,Pair<Posicio,Moviment>>> possiblesMovimentsJugador(Color colorJugador){
		//Per cada peça del jugador calculem els moviments possibles sense tenir en compte la validesa i els recopilem en un collection que retornem
		Collection<Pair<Peca,Pair<Posicio,Moviment>>> res =  new ArrayList<Pair<Peca,Pair<Posicio,Moviment>>>();
		HashMap<String,List<Peca>> indexPecesDelJugador = indexBlanques;
		if(colorJugador.equals(Color.Negres))
			indexPecesDelJugador = indexNegres;
		
		for (Entry<String, List<Peca>> tipusActual : indexPecesDelJugador.entrySet()) {
			for(Peca pecaActual : tipusActual.getValue()) {
				Collection<Pair<Posicio,Moviment>> possiblesMovimentsDeLaPeca = pecaActual.possiblesMoviments();
				for(Pair<Posicio,Moviment> movAct : possiblesMovimentsDeLaPeca) {
					Pair<Peca,Pair<Posicio,Moviment>> nouElement =  new Pair<Peca,Pair<Posicio,Moviment>>(pecaActual,movAct);
					res.add(nouElement);
				}
			}
		}
		return res;
	}


	/**
	 * @pre La Posicio existeix al tauler.
	 * @param pos Posicio on es fa la consulta
	 * @return La peça a la Posicio demanada. Si no hi ha peça es retorna null.
	 */
	public Peca tensPeca(Posicio pos) {		
		return caselles.get(pos.fila()).get(pos.columna()).tePeca();
		
	}


	/**
	 * @post: Mostra tot el tauler en un format comprensible. Es mostren les files i columnes així com la representació de cada peça al tauler.
	 */
	@Override
	public String toString() {
		String separador = "-"; 
		
		//Capçalera
		String res = "Tauler d'escacs: " + System.lineSeparator();
		
		res += IntStream.range(0,6+4*columnes).mapToObj(p -> separador).collect(Collectors.joining(""));	
		res += System.lineSeparator();
		//Tauler
		for (int i = files; i > 0; i--) {
			res += i + "    | ";
			for (int j = 1; j < columnes+1; j++) {
				Peca pecaActual = caselles.get(i).get(j).tePeca();
				String representacio = " ";
				if(pecaActual != null)
					representacio = pecaActual.toString();
				res +=  representacio + " | ";
			}
			
			res += System.lineSeparator();
			res += IntStream.range(0,6+4*columnes).mapToObj(p -> separador).collect(Collectors.joining(""));	
			res += System.lineSeparator();
		}
		//Peu del tauler
		res +="       ";
		for (int i = 1; i < columnes +1; i++) {
			char fila = 'a' - 1;
			fila += i;
			res +=  fila + "   "; 
		}
		res += System.lineSeparator();
		res += IntStream.range(0,6+4*columnes).mapToObj(p -> separador).collect(Collectors.joining(""));	
		res += System.lineSeparator();
		return res;
		
	}


	/**
	 * @pre el rei rival està al tauler.
	 * @post S'ha actualitzat el conjunt de posicions amenaçades per les peces del Color peces i s'ha actualitzat el conjunt de peces que ataquen al rei rival.
	 * @param peces Color de les peces de les quals s'han d'actualitzar les posicions amenaçades.
	 */
	private void actualitzarAmenaces(Color peces) {
		
		Collection<Posicio> taulerAmenaces = taulerAmenacesBlanques; //Posicions Amenaçades per les peces de color peces.
		Collection<Pair<Peca,Moviment>> atacantsAlRei = atacantsAlReiBlanc; //Peces del color peces que ataquen al rei rival.
		HashMap<String,List<Peca>> pecesAmenacants = indexBlanques; //Peces color peces.
		Posicio posReiRival = buscarPeca("REI", Color.canviaColor(peces));
		
		if(peces.equals(Color.Negres)) {
			taulerAmenaces = taulerAmenacesNegres;
			atacantsAlRei = atacantsAlReiNegre;
			pecesAmenacants = indexNegres;
		}
		//Reset dels índexs
		taulerAmenaces.clear();
		atacantsAlRei.clear();
		
		//Per cada peça aliada mirem les posicions on pot atacar i les afegim al tauler d'amenaces. Si pot atacar el rei rival, afegim la peça a atacantsAlRei.
		for(List<Peca> tipusActual : pecesAmenacants.values()) {
			for(Peca pecaActual : tipusActual) {
				Collection<Pair<Posicio,Moviment>> possiblesMoviments = pecaActual.possiblesMoviments();
				for(Pair<Posicio,Moviment> pairAct : possiblesMoviments) {
					taulerAmenaces.add(pairAct.first);
					if(pairAct.first.equals(posReiRival))
						atacantsAlRei.add(new Pair<Peca,Moviment>(pecaActual,pairAct.second)); //Una peça no pot tenir dos moviments que tinguin el mateix destí.
				}		
			}
		}
		//Finalment eliminem els duplicats.
		taulerAmenaces = taulerAmenaces.stream().distinct().collect(Collectors.toList()); //En 1 sola línia (Elegantíssim) :D
	}


	/**
	 * @pre Peca != null.
	 * @post S'ha afegit la peca als índexs.
	 * @param peca Peca a afegir als índexs.
	 */
	private void afegirIndex(Peca peca) {
		
		//Referència a l'índex que hem de modificar segons el color de la "peca".
		HashMap<String,List<Peca>> indexAModificar = indexBlanques; 
		if(peca.color().equals(Color.Negres))
			indexAModificar = indexNegres;
		
		
		if(!indexAModificar.containsKey(peca.tipus().toString())) {
			//CAS la primera vegada que entrem una peça d'aquest tipus.
			//Es crea una llista amb un únic element, la nova "peca".
			List<Peca> novaLlista = Stream.of(peca).collect(Collectors.toList());
			indexAModificar.put(peca.tipus().toString(),novaLlista);
		}
		else {
			//CAS ja hi havia una peça del mateix tipus i color.
			//Afegim la peca a la llista ja existent.
			indexAModificar.get(peca.tipus().toString()).add(peca);
		}
	}


	/**
	 * @post S'ha borrat la Peca dels índexs
	 * @param pecaAEliminar Peca a eliminar dels índexs.
	 */
	private void borrarIndex(Peca pecaAEliminar) {
		//Referència a l'índex que hem de modificar segons el color de la "peca".
		HashMap<String,List<Peca>> indexAModificar = indexBlanques; 
		if(pecaAEliminar.color().equals(Color.Negres))
			indexAModificar = indexNegres;
				
		if(!indexAModificar.containsKey(pecaAEliminar.tipus().toString()))
			//CAS la peca no estava als índex
			return;
		
		indexAModificar.get(pecaAEliminar.tipus().toString()).remove(pecaAEliminar);
	}

	/**
	 * 
	 * @param nomPeca Nom de la peça a buscar
	 * @param color Color de la peça a buscar
	 * @return La primera posicio trobada d'una casella que conté una peça de nom "nomPeca". Si no es troba retorna null;
	 */
	private Posicio buscarPeca(String nomPeca, Color color) {
		
		Posicio res;
		
		//Referència a l'índex que hem de modificar segons el color de la "peca".
		HashMap<String,List<Peca>> indexColor = indexBlanques; 
		if(color.equals(Color.Negres))
			indexColor = indexNegres;
		
		try {
			res = indexColor.get(nomPeca).get(0).onEstic();
		}
		catch(IndexOutOfBoundsException e) {
			res = null; //En cas que no quedi cap peça a la llista
		}
		catch(NullPointerException e) {
			res = null; //En cas que no hi hagi cap llista amb aquell tipus de peça
		}
	
		return res;
		
	}
		


	/**
	 * @param pecaAct Peca sobre la que s'aplicarà el calcul de la nova posició
	 * @return La posicio a la que hauria d'anar la pecaAct si aquesta es canvies de prespectiva tenint en compte les dimensions d'aquest tauler.
	 */
	private Posicio calcularPosicioCanviPrespectiva(Peca pecaAct) {
		int fila = this.files - pecaAct.onEstic().fila() + 1;
		int columna = pecaAct.onEstic().columna();
		
		return new Posicio(fila,columna);
	}
	
	/**
	 * @param posInicial Posicio inicial del moviment
	 * @param posFinal Posicion final del moviment
	 * @param filaPrimer En els casos en que el moviment sigui combinat, indica si el desplaçament es comença per la fila o no.
	 * @return El conjunt de caselles entre les dues posicions(inicial i final incloses).
	 */
	private Collection<Posicio> casellesEntrePosicions(Posicio posInicial, Posicio posFinal, boolean filaPrimer) {
		//Afegir mov incial i final. Si són iguals retornar-ne només un.
		ArrayList<Posicio> res = new ArrayList<>();
		res.add(posInicial);
		if(posInicial.equals(posFinal))
			return res;
		
		//CAS DIAGONAL
		if(posInicial.movimentDiagonal(posFinal)) 
			casellesEntrePosicionsDiagonals(res,posInicial,posFinal);
					
		
		//CAS Moviment rectilini
		else if(posInicial.movimentRectilini(posFinal))
			casellesEntrePosicionsRectilinies(res, posInicial,posFinal);
		
		//CAS Moviment Combinat
		else {
			//Moviment Combinat: separem en dos moviments rectilinis i trieem quin es fa primer.
			
			Posicio parcial = new Posicio(posInicial.fila(),posFinal.columna());
			if(!filaPrimer) 
				parcial = new Posicio(posFinal.fila(),posInicial.columna());
			
			casellesEntrePosicionsRectilinies(res, posInicial,parcial);
			casellesEntrePosicionsRectilinies(res, parcial,posFinal);
			
		}
		
			
		
			return res;
	}
	
	/**
	 * @pre El desplaçament ha de ser diagonal.
	 * @post S'actualitza res amb les caselles que hi ha en el rang diagonal (posInicial, posFinal]
	 * @param res Conjunt de posicions del tauler que hi ha entre les dues posicions inicial i final
	 * @param posInicial Posicio inicial del moviment
	 * @param posFinal Posicio final del moviment
	 */
	private void casellesEntrePosicionsDiagonals(Collection<Posicio> res, Posicio posInicial, Posicio posFinal) {
		
		int difFiles = Math.abs(posInicial.compareFila(posFinal));
		
		Posicio filaMesBaixa = posInicial;
		Posicio filaMesAlta = posFinal;
		if(posInicial.compareFila(posFinal) > 1) {
			filaMesBaixa = posFinal;
			filaMesAlta = posInicial;
		}
		//CAS \
		if(filaMesBaixa.compareColumna(filaMesAlta) > 1) {
			for(int i = 1; i < difFiles; i++) {
				res.add(new Posicio(filaMesBaixa.fila()+i,filaMesBaixa.columna()-i));
			}
			res.add(posFinal);
		}
		
		//CAS /
		else {
			for(int i = 1; i < difFiles; i++) {
				res.add(new Posicio(filaMesBaixa.fila()+i,filaMesBaixa.columna()+i));
			}
			res.add(posFinal);
			
		}
		
	}
	
	/**
	 * @pre El desplaçament ha de ser rectilini.
	 * @post S'actualitza res amb les caselles que hi ha en el rang rectilini (posInicial, posFinal].
	 * @param res Conjunt de posicions del tauler que hi ha entre les dues posicions inicial i final.
	 * @param posInicial Posicio inicial del moviment.
	 * @param posFinal Posicio final del moviment.
	 */
	private void casellesEntrePosicionsRectilinies(Collection<Posicio> res, Posicio posInicial, Posicio posFinal) {
		int difFiles = Math.abs(posInicial.compareFila(posFinal));
		int difColumnes = Math.abs(posInicial.compareColumna(posFinal));
		
		Posicio posPetita = posInicial;
		if(posInicial.compareTo(posFinal) > 1) {
			posPetita = posFinal;
		}
		if(posInicial.compareFila(posFinal) == 0) {
			//Moviment horitzontal
			for(int i = 1; i < difColumnes; i++) {
				res.add(new Posicio(posPetita.fila(),posPetita.columna()+i));
			}
			res.add(posFinal);
		}
		
		//CAS VERTICAL
		else {
			
			for(int i = 1; i < difFiles; i++) {
				res.add(new Posicio(posPetita.fila()+i,posPetita.columna()));
			}
			res.add(posFinal);
		}
	}
	
	/**
	 * @pre Hi ha el rei del colorRei al tauler.
	 * @post S'ha actualitzat el conjunt de posicions amenaçades pel rival de colorRei i s'ha retornat un booleà en funció de si el Rei de colorRei està en escac.
	 * @param colorRei Color color del rei que podria estar amenaçat.
	 * @return Un booleà que diu si el rei de colorRei està en escac.
	 */
	private boolean hiHaEscac(Color colorRei) {
		//Actualitzem el tauler d'amenaces
		this.actualitzarAmenaces(Color.canviaColor(colorRei));
		
		//Busquem la posició del rei possiblement amenaçat:
		Posicio posReiAmenacat = this.buscarPeca("REI", colorRei); //El tipus del rei sempre serà "REI" segons l'enunciat.
		
		//Mirem si la posició del rei està dins el conjunt de peces amenaçades pel rival:
		Collection<Posicio> posicionsAmenacades = this.taulerAmenacesNegres; //Ref al conjunt de posicions amenaçades pel rival/
		if(colorRei.equals(Color.Negres))
			posicionsAmenacades = this.taulerAmenacesBlanques;
		
		if(posicionsAmenacades.contains(posReiAmenacat))
			//CAS La posició del rei està dins el conjunt de les peces amenaçades i, per tant, està en escac.
			return true;
		else
			//CAS La posició del rei NO està dins el conjunt de les peces amenaçades i, per tant, NO està en escac.
			return false;
	}


	/**
	 * @pre Hi ha el rei del color colorRei al tauler. S'ha comprovat que el rei colorRei està en escac.
	 * @post S'ha retornat un booleà en funció de si el Rei de colorRei està en escac i mat.
	 * @param colorRei Color color del rei que podria estar en mat.
	 * @return Un booleà que diu si el rei de colorRei està en escac i mat.
	 * @version 1.
	 */
	private boolean hiHaEscacIMat(Color colorRei) {
		
		//Si el rei es pot moure a una posició no amenaçada -> NO hi ha mat
		if(reiTeMovimentSenseEscac(buscarPeca("REI", colorRei)))
			return false;
		//Si hi ha alguna jugada(les provem totes) que deixa el rei sense escac -> NO hi ha mat
		if(hiHaJugadaQueEvitaMat(colorRei))
			return false;
		//Si no hi ha cap jugada possible que deixi el rei sense escac -> SI hi ha mat
		return true;
	}
	
	/**
	 * @pre el Rei de colorRei està en escac i està al tauler.
	 * @param colorRei Color del rei que esta en escac.
	 * @return Un booleà que diu si s'ha trobat, o no, una jugada que evitaria el mat.
	 */
	private boolean hiHaJugadaQueEvitaMat(Color colorRei) {
		HashMap<String,List<Peca>> pecesAliades = indexBlanques;
		if(colorRei.equals(Color.Negres)) {
			pecesAliades = indexNegres;
		}
		
		//Per cada peça aliada prova tots els moviments a veure si n'hi ha algun que eviti l'escac
		for (Entry<String, List<Peca>> tipusActual : pecesAliades.entrySet()) {
			List<Peca> conjuntPecesActual =  tipusActual.getValue();
			//Cal copiar les referencies a les Peces de la llista conjuntPecesActual ja que tot i que no s'en modificarà el contingut, si que se'n modificarà l'ordre i per tant no s'hi pot iterrar directament
			List<Peca> conjuntPecesActualSafeIteration = new ArrayList<Peca>(conjuntPecesActual);
			for(Peca pecaAct : conjuntPecesActualSafeIteration) {
				if(possiblesMovimentsValids(pecaAct).size() > 0)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * @param torn Color de qui té el torn i pot estar ofegat.
	 * @return true si alguna peça de color torn pot fer algún movment vàlid. Altrament false.
	 */
	private boolean hiHaMovimentValid(Color torn) {
		//Comprovem si alguna de les peces té moviment vàlid
		
		//Ref al tauler a actualitzar i al conjunt de peces amenaçants.
		HashMap<String,List<Peca>> pecesAliades = indexBlanques;
		if(torn.equals(Color.Negres)) {
			pecesAliades = indexNegres;
		}
		
		//Per cada peça aliada mirem si existeix algun moviment que no deixi el rei en escac.
		for (Entry<String, List<Peca>> tipusActual : pecesAliades.entrySet()) {
			for(Peca pecaActual : tipusActual.getValue()) {
				if(possiblesMovimentsValids(pecaActual).size() > 0)
					return true;
			}
		}
		return false;
		
	}
	
	/**
	 * @pre Hi ha el rei del color contrari  al color torn al tauler. S'han comprobat que no hi ha escac al rival de torn.
	 * @param torn Color del jugador que ha fet la última jugada i que podria provocar l'afogat.
	 * @return Un booleà que serà true si el jugador que no té el torn, està afogat.
	 * @note Com diu el "@param torn", cal que la jugada que hauria de provocar l'ofegat s'hagi fet ja per comprovar si ara el rei rival ha quedat ofegat.
	 */
	private boolean hiHaTaulesPerOfegat(Color torn) {
	
		//Si existeix algun moviment vàlid d'alguna de les peces aliades, no estem ofegats
		return !hiHaMovimentValid(Color.canviaColor(torn));
	}

	/**
	 * @param colorAmenaca Color de les peces que amenacen.
	 * @return Una col·lecció de Peces de colorAmenaca que poden matar a una Peca a la Posicio objectiu
	 */
	private Collection<Peca> pecesAtacants(Posicio objectiu, Color colorAmenaca){
		
		ArrayList<Peca> res = new ArrayList<Peca>();
		
		//Ref al tauler a actualitzar i al conjunt de peces amenaçants.
		HashMap<String,List<Peca>> pecesAmenacants = indexBlanques;
		if(colorAmenaca.equals(Color.Negres)) {
			pecesAmenacants = indexNegres;
		}
		
		for (Entry<String, List<Peca>> tipusActual : pecesAmenacants.entrySet()) {
			for(Peca pecaActual : tipusActual.getValue()) {
				if(pecaActual.potAnar(objectiu)!= null)
					res.add(pecaActual);
			}
		}
				
		return res;	
	}

	/**
	 * @pre Peca està al tauler i coneix la seva posicio.
	 * @param peca Peça que es vol comprovar.
	 * @return Un conjunt de posicions de possibles desplaçaments VALIDS(legal segons les regles dels escacs ) que pot fer la peça a posPeca en l'estat actual del tauler.
	 */
	private Collection<Pair<Posicio,Moviment>> possiblesMovimentsValids(Peca peca){
		Posicio posPeca = peca.onEstic();
		
		ArrayList<Pair<Posicio,Moviment>> res = new ArrayList<Pair<Posicio,Moviment>>();
		Peca pecaAMoure = this.tensPeca(posPeca);
		
		Collection<Pair<Posicio,Moviment>> possiblesMovimentsDeLaPeca = pecaAMoure.possiblesMoviments();
		//Per cada possible moviment que pot fer la peça mirem si és vàlid segons les rregles generals dels escacs.
		for(Pair<Posicio,Moviment> pairAct : possiblesMovimentsDeLaPeca) {
			
			if(simularMoviment(peca.onEstic(),pairAct)) {
				res.add(pairAct);
			}
		}
		
		return res;
	}
	
	/**
	 * @pre posRei és una Posició que fa referència a una casella que conté un Rei amenaçat. El tauler d'amenaces del rival està actualitzat.
	 * @param posRei: Posició del rei amenaçat.
	 * @return Un booleà que ens informa si el rei que està en escac té algun moviment valid que evités l'escac.
	 */
	private boolean reiTeMovimentSenseEscac(Posicio posRei) {
		
		return possiblesMovimentsValids(this.tensPeca(posRei)).size() > 0;
	}
	
	/**
 	 * @param peca Posicio de le peça que farà el moviment
	 * @param moviment Conjunt de Posicio desti i Moviment a aplicar a la simulacio
	 * @return Un booleà que determina si el moviment és o no vàlid.
	 */
	private boolean simularMoviment(Posicio peca, Pair<Posicio, Moviment> moviment) {		//Part 1: Crear un tauler de proves.
		Tauler testMoviment = this.deepCopy();
		Peca pecaASimular = testMoviment.tensPeca(peca);
		//Part 2: Aplicar el moviment
		Contadors cont = new Contadors(0,0,0,0);
		try {
			Partida.realitzarJugada(testMoviment, pecaASimular, moviment, cont,pecaASimular.color());
		} catch (ExcepcioG e) {
			//La jugada no era vàlida: retornem false
			return false;
		}
		
		//Part 3: Validar-lo	
		return !testMoviment.hiHaEscac(pecaASimular.color());
	
	}
	

	/**
	 * @brief Donada la columna on està el rei, calcula el valor que té tenir-lo en aquesta posició. Es valor molt més tenir-lo als extrems del tauler.
	 * @param posRei Posicio on està el rei.
	 * @return El valor que té tenir el rei en aquesta posició.
	 */
	private int valorarPosicioRei(Posicio posRei){

		int columnaAct = posRei.columna();
		int filaAct = posRei.columna();
		
		int valorFila = 0;
		int valorColumna = 0;
		
		
		float migC = (float) columnes/2;
	    int mig2C =  columnes/2;
	    float difC = mig2C - migC;
	    if(difC == 0){
	        int p1C = mig2C + 1;
	        int p2C = mig2C - 1;

	        if(p1C == columnaAct) valorColumna =  0;
	        else if(p2C == columnaAct) valorColumna = 0;
	        else if(columnaAct > p1C) valorColumna = (columnaAct-p1C)*10;
	        else valorColumna = (p2C-columnaAct+1)*10;

	    }
	    else{
	        int p1 = columnaAct - mig2C;
	        p1 = (Math.abs(p1)+1) * 10;
	        valorColumna= Math.abs(p1);
	    }
	    
		float migF = (float) files/2;
	    int mig2F =  files/2;
	    float difF = mig2F - migF;
	    if(difF == 0){
	        int p1F = mig2F + 1;
	        int p2F = mig2F - 1;

	        if(p1F == filaAct) valorFila =  0;
	        else if(p2F == filaAct) valorFila = 0;
	        else if(filaAct > p1F) valorFila = (filaAct-p1F)*10;
	        else valorFila = (p2F-filaAct+1)*10;

	    }
	    else{
	        int p1 = filaAct - mig2F;
	        p1 = (Math.abs(p1)+1) * 10;
	        valorFila= Math.abs(p1);
	    }
	return valorColumna - valorFila;
	}
	
}