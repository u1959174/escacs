
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *  @file Partida.java
 *  @brief Classe Escacs
 */

/** 
 *  @class Partida
 *  @brief Motor de el conjunt de Escacs. Gestiona les jugades, el tauler, les peces. Executa també les jugades.
 *  @note Motor del Joc. ../projecte-2020-a4/dataset/jugades.json
 *  @author Eloi Quintana Ferrer
 *  @author u1963141
 */

public class Partida {
    
    /**
     * Indica si la partida esta acabada o no.
     */
    private boolean acabada;
    
    /**
     * Cotnadors de escacs i inaccio per a cada color.
     */
    private Contadors contador;
    
    /**
     * Indica el color de quin jugador és el guanyador.
     */
    private Color guanyador;

    /**
     * Indica la posició de la jugada actual de l'Array de les jugades.
     */
    private int jugadaActual = 0;
    
    /**
     * Indica la posició de la jugada en la qual es pot recuperar 
     * realitzant un refer.
     */
    private int jugadaFinal = 0;

    /**
     * índex de les jugades realitzades.
     */
    private ArrayList<Jugada> jugades;
    
    /**
     * Nombre màxim de escacs seguits.
     */
    private int maxEscacs;
    
    /**
     * Nombre màxim de jugades en inacció.
     */
    private int maxInaccio;

    /**
     * Indica el resultat final de la partida.
     */
    private ResultatPartida resultatFinal;

    /**
     * Indica el resultat final de la partida en detall.
     */
    private ResultatJugada resultatFinalDetall;

    /**
     * Ruta completa del fitxer de regles de la partida
     */
    private String rutaFitxerRegles;
    
    /**
     * Guarda la classe Tauler que s'esta utilitzant actualment.
     */
    private Tauler t;
    
    
    /**
     * Index dels tipus de peces que tenim.
     */
    private HashMap<String,TipusPeca> tipus;

    /**
     * Indica el torn actual de la partida
     */
    private Color torn;

    

    /**
    * @post Crea la partida, executa totes les jugades passades.
    * @param t Objecte tauler
    * @param tipus Un hashmap on String es el nom del tipusPeca.
    * @param Array de jugades, aquestes però s'han d'afegir i comprovar per a la partida.
    * @note La partida ha estat començada.
    */
    public Partida(Tauler t, HashMap<String,TipusPeca> tipus, ArrayList<Jugada> j, int maxInaccio, int maxEscacs) throws ExcepcioG{
        acabada   = false;
        this.t    = t;
        this.tipus= tipus;
        jugades = new ArrayList<Jugada>();
        this.torn = Color.Blanques;

        contador = new Contadors();

        this.maxEscacs = maxEscacs;
        this.maxInaccio = maxInaccio;
 
        //Carraguem una jugada d'inici que sera el tauler base.
        canviarTorn(Color.Blanques);
        jugadaActual = -1;
        
        Jugada jugadaInicial = new Jugada(ResultatJugada.Inicial,Color.Negres);
        afegirJugada(jugadaInicial,null);
        jugadaInicial.finalitzarJugada(t,acabada,resultatFinal,contador,null,guanyador());
         
        Contadors nouContador = contador.deepCopy();
        this.t = t.deepCopy();
        contador = nouContador;
        
        //Realitzem les jugades carregades del json
        realitzarJugades(j);
    }

    /**
     * @post Crea una partida inicial.
     * @param t Objecte tauler
     * @param tipus Un hashmap on String es el nom del tipusPeca i tipusPeca.
     * @note La partida esta inicialitzada nova.
    */
    public Partida(Tauler t, HashMap<String,TipusPeca> tipus, int maxInaccio, int maxEscacs)throws ExcepcioG{
        //carraguem les variables passades
        acabada   = false;
        this.t    = t;
        this.tipus= tipus;

        this.maxEscacs = maxEscacs;
        this.maxInaccio = maxInaccio;
        jugades = new ArrayList<Jugada>();
        contador = new Contadors();
        this.torn = Color.Blanques;
        
        //Carraguem una jugada d'inici que sera el tauler base.
        jugadaActual = -1;
        Jugada jugadaInicial = new Jugada(ResultatJugada.Inicial,Color.Negres);
        afegirJugada(jugadaInicial,null);
        jugadaInicial.finalitzarJugada(t,acabada,resultatFinal,contador,null,guanyador()); 
        Contadors nouContador = contador.deepCopy();
        this.t = t.deepCopy();
        contador = nouContador;

    }
    
    /**
     * @pre cap dels elements es null
     * @post donat els parametres s'ha classificat la jugada i s'ha gestionat el tauler. 
     * @param tauler tauler a realitzar la jugada
     * @param tmpOrigen peca d'origen
     * @param a pair de posicio i moviment
     * @param cont objecte contadors
     * @param tornE torn actual del que realitza la jugada
     * @throws ExcepcioG
    */
    public static void realitzarJugada(Tauler tauler, Peca tmpOrigen, Pair<Posicio,Moviment> a, Contadors cont, Color tornE) throws ExcepcioG{ 
        Integer tipusMoviment;
        tipusMoviment = a.second.diguemSaltar();

        Posicio origen = tmpOrigen.onEstic();
        Posicio desti = a.first;
        
        Peca tmpDesti = tauler.tensPeca(desti);
        Color colorContrari = Color.canviaColor(tmpOrigen.color());
        
        boolean pmig = false, pfinal = false;
        
        ///Mirem que la peca desti no sigui del mateix color ni invulnerable
        if(tmpDesti != null) pfinal = true;
        cont.modificarContadorInaccio(1,tornE); 
        if(a.second.esEnroc()){
            //Calculem Enroc
            Pair<Posicio,Posicio> calculEnroc = tauler.calcularEnrroc(origen,desti);
            if(calculEnroc == null) 
                throw new ExcepcioG(TipusExcepcioG.MOVIMENT_INCORRECTE);

            //Movem les peces
            tmpOrigen.moure();
            tmpDesti.moure();
            tauler.eliminarPeca(origen);
            tauler.eliminarPeca(desti);
            if(tauler.tensPeca(calculEnroc.first) != null && tauler.tensPeca(calculEnroc.second) != null){
                throw new ExcepcioG(TipusExcepcioG.MOVIMENT_INCORRECTE);
            }
            else{
                tauler.afegirPeca(calculEnroc.first, tmpOrigen);
                tauler.afegirPeca(calculEnroc.second, tmpDesti);
            }

        }
        else{
            ArrayList<Pair<Posicio,Peca>> pecesAlMigTotes = (ArrayList<Pair<Posicio,Peca>>) tauler.pecesAlMig(origen, desti, false, true);
            //Posicio final no sigui Buida
            if(tmpDesti != null){
                if(tmpDesti.color() == tmpOrigen.color()){
                    throw new ExcepcioG(TipusExcepcioG.PECA_MATEIX_EQUIP);
                }
            }
                
            //Comprobem les peces al mig
            int totalPecesAlMig = pecesAlMigTotes.size();
            if(pfinal){
                if(totalPecesAlMig-2 > 0)
                    pmig = true;
            }
            else{
                if(totalPecesAlMig-1 > 0)
                    pmig = true;
            }
            
            ArrayList<Pair<Posicio,Peca>> pecesAlMig = (ArrayList<Pair<Posicio,Peca>>) tauler.pecesAlMigColorConcret(tmpOrigen.onEstic(), a.first, false, colorContrari);
            ///Mirem que la peca desti no sigui del mateix color ni invulnerable
            if(tmpDesti != null){
                if(tmpDesti.color() == tornE)
                    throw new ExcepcioG(TipusExcepcioG.MATAR_MATEIX_EQUIP);
                if(tmpDesti.tipus().esInvulnerable())
                    throw new ExcepcioG(TipusExcepcioG.PECA_INVULNERABLE);
            }

            //Tipus de moviment
            if(tipusMoviment != null){
                //Si hi ha peces al mig i la peca no pot saltar, mov incorrecte.
                if(tipusMoviment == 0 && pmig)
                    throw new ExcepcioG(TipusExcepcioG.MOVIMENT_INCORRECTE);
                //Elimina nomes la peca del desti
                else if(tipusMoviment == 1 && tmpDesti != null)
                    tauler.eliminarPeca(desti);
                //Elimina Peces del mig i desti si existeix
                else if(tipusMoviment == 2 && pmig)
                    eliminarPeces(pecesAlMig,tauler,cont,tornE);
            }
            else{
                if(tmpDesti != null)
                    tauler.eliminarPeca(desti);
            }
            
            
            //Si la peca desti existeix, actualitzem el contador de Inaccio
            if(tmpDesti != null)
                cont.posarAZeroContadorInaccio(tornE);
            
            //Movem la peca d'origen al desti.
            tmpOrigen.moure();
            tauler.eliminarPeca(origen);
            tauler.afegirPeca(desti, tmpOrigen);
        }
    }
    
    /**
    * @pre -
    * @post s'han eliminat les peces del tauler donades i s'ha posat el contador d'inaccio a 0.
    * @param mig conte el conjunt de peces i posicions a eliminar del tauler.
    */
    private static void eliminarPeces(ArrayList<Pair<Posicio,Peca>> mig, Tauler tau, Contadors cont, Color tornE){
        for(Pair<Posicio,Peca> p : mig){
            if(!p.second.tipus().esInvulnerable() && !p.second.color().equals(tornE)){
                tau.eliminarPeca(p.first);
                cont.posarAZeroContadorInaccio(tornE);
            }
        }
    }

    /**
     * @pre -
     * @post ha retornat un bolea de l'atribut acabada.
     * @return acabada, cert si la partida esta acabada, fals altrament.
     */
    public boolean acabada(){
        return acabada;
    }

    
    /**
    * @pre -
    * @Post s'ha canviat el color del torn
    */
    public void canviarTorn(Color torn){
        this.torn = torn;
    }

    /**
    * @pre j != null
    * @post s'ha comprobat al jugada j
    * @param j Jugada a analitzar
    * @throws ExcepcioG si la jugada no es vàlida
    */
    public void comprobarJugada(Jugada j) throws ExcepcioG{
        comprobarJugada(j,null);
    }
    
    /**
     * @pre -
     * @post Es crea una jugada.
     * @param desti posicio destí per a crear la jugada.
     * @param origen Posico d'origen per crear la jugada.
     * @throws ExcepcioG si la jugada és invalida.
     */
    public Jugada crearJugada(Posicio origen, Posicio desti) throws ExcepcioG{ 
        if(t.dinsTauler(origen) && t.dinsTauler(desti)){
            //Inicialitzem
            ArrayList<Pair<Posicio,Peca>> pecesAlMigTotes = (ArrayList<Pair<Posicio,Peca>>) t.pecesAlMig(origen, desti, false, true);
            boolean pmig = false, pfinal = false;
            Peca tmpOrigen = t.tensPeca(origen);
            Peca tmpDesti = t.tensPeca(desti);

            //Posicio Inici sigui Buida
            if(tmpOrigen == null)
                throw new ExcepcioG(TipusExcepcioG.POSICIO_BUIDA);
            
            ///Mirem que la peca desti no sigui del mateix color ni invulnerable
            if(tmpDesti != null) pfinal = true;
            
            //Es crea la jugada
            Jugada e = new Jugada(origen, desti, tmpOrigen, tmpDesti);

            //Comprobem les peces al mig
            int totalPecesAlMig = pecesAlMigTotes.size();
            if(pfinal){
                if(totalPecesAlMig-2 > 0)
                    pmig = true;
            }
            else{
                if(totalPecesAlMig-1 > 0)
                    pmig = true;
            }

            //Comprobacions i aplicacio del Moviment
            boolean esTornBlanques = true;
            if(torn() == Color.Negres) esTornBlanques=false;

            Moviment mov;
            if(tmpDesti != null)
               mov = tmpOrigen.tipus().provaMoviment(origen,desti,tmpOrigen.quieta(),esTornBlanques,tmpDesti.tipus(),pmig);
            else
               mov = tmpOrigen.tipus().provaMoviment(origen,desti,tmpOrigen.quieta(),esTornBlanques,null,pmig); 

            //Comprovacions del moviment
            if(mov == null)
                throw new ExcepcioG(TipusExcepcioG.MOVIMENT_INCORRECTE);
            
            //Es realitza la jugada.
            this.realitzarJugada(tmpOrigen,new Pair(desti,mov));
            
            return e;
        }
        else{
            return null;
        }
    }
    
    
    /**
     * @pre Hi ha una jugada per desfer
     * @post s'ha desfet la jugada actual.
     */
    public void desferJugada(){
        if(jugades.get(jugadaActual).obtenirResultatsJugada() != null){
           if(jugades.get(jugadaActual).obtenirResultatsJugada().size() >= 1){
                if(jugades.get(jugadaActual).obtenirResultatsJugada().toArray()[0] == ResultatJugada.TAcceptades){
                    desferJugadaFinal();
                    desferJugadaFinal();
                }
                else if(jugades.get(jugadaActual-1).obtenirResultatsJugada().contains(ResultatJugada.TSollicitades)){
                    desferJugadaFinal();
                    this.torn = Color.canviaColor(torn());
                    
                }
                else{
                    desferJugadaFinal();
                }
            }
            else{
                desferJugadaFinal();
            } 
        }
        else{
            desferJugadaFinal();
        } 
        
    }

    /**
     * @pre: t != null
     * @post: retorna l'atribut dimensions del tauler actual.
     * @return: Un pair amb les dimensions del tauler first->columnes, seccond->files.
     */
    public Pair<Integer,Integer> dimensions(){
        return t.dimensions();
    }

    /**
    * @pre origen != null
    * @Post s'ha creat una jugada i s'ha guardart a la llista de jugades
    * @param origen posició d'origen des d'on es vol moure una peça
    * @param desti posició d'origen
    */
    public Tauler ferJugada(Posicio origen, Posicio desti) throws ExcepcioG{
        
        return ferJugada(origen,desti,null,null);
    }
    
    /**
   * @pre resultat != null
   * @Post s'exectua una jugada especial donat un resultat de jugada.
   * @param resultat conte el resultat que tindra la jugada que es crea.
   */
   public boolean ferJugada(ResultatJugada resultat) throws ExcepcioG{
	Jugada jugadaTmp = null;
	ResultatPartida resultatPTmp = null;
	switch(resultat) {
	    case Ajornament:
	        acabarPartida(ResultatPartida.PartidaAjornada,resultat,Color.capColor);
	        break;
	    case Rendicio:
	        if(torn() == Color.Blanques){
	            acabarPartida(ResultatPartida.NegresGuanyen,resultat,Color.Negres);
	        }
	        else if(torn() == Color.Negres){
	            acabarPartida(ResultatPartida.BlanquesGuanyen,resultat,Color.Blanques);
	        }
	        break;
	    case TAcceptades:
	        acabarPartida(ResultatPartida.Taules,resultat,Color.capColor);
	        break;
	    case TSollicitades:
	        break;
	    default:
	        return false;
	}
	jugadaTmp = new Jugada(resultat,torn());
	
	//Es crea una copia del tauler i s'afageix a la jugada
	Tauler nouTauler = t.deepCopy();
	Contadors nouContador = contador.deepCopy();
	jugadaTmp.finalitzarJugada(t,acabada,resultatFinal,contador,null,guanyador());
	t = nouTauler;
	contador = nouContador;
	
	//Afegim la jugada i canviem de torn
	afegirJugada(jugadaTmp,null);
	canviarTorn(Color.canviaColor(torn()));
	return true;
   }
    
    /**
    * @pre -
    * @Post Retorna el Color del jugador del torn actual
    * @return Blanques si color Blanc, Negres si color Negre.
    */
    public Color guanyador(){
        return guanyador;
    }
    
    /**
     * @pre -
     * @param rutaFitxerRegles string que conte la nova ruta del fitxer de regles.
     */
    public void modificarRutaFitxerRegles(String rutaFitxerRegles){
        this.rutaFitxerRegles = rutaFitxerRegles;
    }

    /**
     * @pre: -
     * @return retorna una col·leccio de les jugades realitzades al llarg de la partida.
     */
    public Collection<Jugada> obtenirJugades(){
        return jugades;
    }
    
    /**
     * @pre: -
     * @return: retorna una coleccio de tots els noms dels tipus de peca que tenim carregats.
     */
    public Collection<TipusPeca> obtenirNomsTipusPeca(){
        return tipus.values();
    }
    
    /**
     * @pre: -
     * @return: retorna el resultat de la partida.
     */
    public ResultatPartida obtenirResultatPartida(){
        return resultatFinal;
    }
    
    /**
     * @pre -
     * @post Retorna el resultat de la partida en detall
     * @return resultatFinalDetall, un tipus de ResultatJugada
     */
    public ResultatJugada obtenirResultatPartidaDetall(){
        return resultatFinalDetall;
    }


    //GESTIO JUGADES


    /**
     * @pre -
     * @return ruta del fitxer de regles
     */
    public String obtenirRutaFitxerRegles(){
        return rutaFitxerRegles;
    }

    /**
     * @return el tauler actual
     * @pre -
     * @Post ha realizat el retorn
     */
    public Tauler obtenirTauler(){
        return t;
    }
    
    /**
     * @pre existeix el tauler inicial
     * @Post ha retornat el tauler inicial
     * @return el tauler inicial
     */
    public Tauler obtenirTaulerInicial(){
        return jugades.get(0).obtenirTauler();
    }

    /**
     * @pre -
     * @Post indica si la partida pot continuar
     * @return true si una partida pot continuar, false altrament
     */
    public boolean potContinuar(){
        if(jugades.get(jugadaActual).obtenirResultatsJugada().size() >= 1){
            if(jugades.get(jugadaActual).obtenirResultatsJugada().toArray()[0] == ResultatJugada.Ajornament){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    /**
     * @pre -
     * @post realitza el calcul de jugades x desfer
     * @return nombre de jugades per desfer.
    */
    public int potDesferJugada(){
        return jugadaActual;
    }

    /**
      * @pre -
      * @Post calcula el nombre de jugada que pot refer
      * @return nombre de jugades disponibles per refer
    */
    public int potReferJugada(){
        return jugadaFinal - jugadaActual;
    }

    /**
     * @pre j no esta buit
     * @post donat els parametres s'ha classificat la jugada i s'ha gestionat el tauler. 
     * @param tmpOrigen peca d'origen
     * @param a pair de posicio i moviment del desti
     * @throws ExcepcioG
     * @comment metode que crida el metode estatic de partida però amb les dades de la partida actual.
    */
    public void realitzarJugada(Peca tmpOrigen, Pair<Posicio,Moviment> a) throws ExcepcioG{ 
        realitzarJugada(t,tmpOrigen,a,contador,torn());
    }
    
    /**
     * @pre Hi ha una jugada per refer.
     * @Post Torna a fer la jugada següent.
     */
    public void referJugada(){
        //Augmantem la Jugada
        jugadaActual++;
        //Carraguem la jugada.
        carregarJugada(jugadaActual);
    }
    
    /**
     * @post obte el tauler actual en format text.
     * @return El tauler en format String.
     */
    public String tauler() {
        return this.t.toString();
    }

    /**
    * @pre -
    * @post Retorna el Color del jugador del torn actual
    * @return Blanques si color Blanc, Negres si color Negre.
    */
    public Color torn(){
        return torn;
    }
    
    /**
     * @pre partida no acabada
     * @post s'ha marcat la partida com acabada i es guarda el guanyador
     */
    private void acabarPartida(ResultatPartida resultatFinal, ResultatJugada resultatFinalDetall, Color guanyador){
        this.resultatFinal = resultatFinal;
        this.resultatFinalDetall = resultatFinalDetall;
        this.guanyador = guanyador;
        this.acabada = true;
    }
    
    /**
     * @pre j != null
     * @Post s'ha afegit la jugada a la llista de jugades a una posicio donada o no
     * @param j Jugada afegir.
     * @param pos Posicio de la jugada a fegir, null per afegir al final
     */
    private void afegirJugada(Jugada j, Integer pos) throws ExcepcioG{
        if(pos != null){
            //Control del index a fora
            jugades.set(pos,j);
            jugadaActual = pos;
        }
        else{
            jugadaActual++;
            if(jugadaActual < jugades.size()){
                jugades.set(jugadaActual, j);
            }
            else if(jugadaActual == jugades.size()){
                jugades.add(j);
            }
            else{
                throw new ExcepcioG(TipusExcepcioG.DESCONEGUT);
            }
            
            jugadaFinal = jugadaActual;
        } 
    }

    /**
     * @pre pos ha d'estar dins el size de jugades
     * @post S'ha carregat a la partida en joc la jugada previament introduïda.
     * @param pos posicio de l'array que s'agafara la jugada a aplicar.
     */
    private void carregarJugada(int pos){
        //Obtenim la jugada a desfer
        Jugada e = jugades.get(pos);
        
        //Obtenim els parametres variables de la partida guardats a la Jugada.
        this.t = e.obtenirTauler().deepCopy();
        this.contador = e.obtenirContadors().deepCopy();
        this.acabada = e.obtenirAcabada();
        this.resultatFinal = e.obtenirResultatPartida();
        this.torn = Color.canviaColor(e.obtenirTirador());
        this.guanyador = e.obtenirGuanyador();
    }

    /**
    * @pre j != null
    * @post s'ha comprobat(escac,..i mat, contadors..) i aplicat una jugada.
    * @param j Jugada a analitzar
    * @param tipusPecaPromocio Tipus de peca a promocionar amb la peca desti.
    * @throws ExcepcioG
    */
    private void comprobarJugada(Jugada j,TipusPeca tipusPecaPromocio) throws ExcepcioG{
        switch(t.estat(torn())) {
            case OFEGAT:
                j.afegirResultatJugada(ResultatJugada.TReiOfegat);
                acabarPartida(ResultatPartida.Taules,ResultatJugada.TReiOfegat,Color.capColor);
                break;
            case ESCAC_SIMPLE:
                j.afegirResultatJugada(ResultatJugada.Escac);

                int contEscacSimple = contador.obtenirContadorEscac(torn());
                contador.modificarContadorEscac(1, torn());
                break;
            case ESCAC_I_MAT:
                j.afegirResultatJugada(ResultatJugada.EscacIMat);
                if(torn() == Color.Negres){
                    acabarPartida(ResultatPartida.NegresGuanyen,ResultatJugada.EscacIMat,torn());
                }
                else{
                    acabarPartida(ResultatPartida.BlanquesGuanyen,ResultatJugada.EscacIMat,torn());
                }
                break;
            default:
                
                contador.posarAZeroContadorEscac(torn());
                
                //continua
         }
        switch(t.estat(Color.canviaColor(torn()))) {
            case ESCAC_SIMPLE:
                throw new ExcepcioG(TipusExcepcioG.MOVIMENT_PROVOCA_ESCAC);
            case ESCAC_I_MAT:
                throw new ExcepcioG(TipusExcepcioG.MOVIMENT_PROVOCA_ESCAC_I_MAT);
            default:
                //continua
         }

        //Taules per inaccio
        if(contador.obtenirContadorInaccio(Color.Blanques) == maxInaccio){
            j.afegirResultatJugada(ResultatJugada.TInaccio);
            acabarPartida(ResultatPartida.Taules,ResultatJugada.TInaccio,Color.capColor);
        }
        if(contador.obtenirContadorInaccio(Color.Negres) == maxInaccio){
            j.afegirResultatJugada(ResultatJugada.TInaccio);
            acabarPartida(ResultatPartida.Taules,ResultatJugada.TInaccio,Color.capColor);
        }
        
        //Taules per escac continu
        if(contador.obtenirContadorEscac(Color.Blanques) == maxEscacs){
            j.afegirResultatJugada(ResultatJugada.TEscacContinu);
            acabarPartida(ResultatPartida.Taules,ResultatJugada.TEscacContinu,Color.capColor);
        }
        if(contador.obtenirContadorEscac(Color.Negres) == maxEscacs){
            j.afegirResultatJugada(ResultatJugada.TEscacContinu);
            acabarPartida(ResultatPartida.Taules,ResultatJugada.TEscacContinu,Color.capColor);
        }
        
        //Promocio de la peca
        Peca novaPecaPromocionada = null;
        Peca pdestiprom = t.tensPeca(j.obtenirDesti());
        if(pdestiprom != null){
            if(t.esFinal(pdestiprom.onEstic(), pdestiprom.color()) && !pdestiprom.promocionada() && pdestiprom.tipus().diguemPromocio()){
                TipusPeca noutipusdepeca = null;
                if(tipusPecaPromocio == null){
                    noutipusdepeca = Escacs.promocionar();
                }
                else{
                    noutipusdepeca = tipusPecaPromocio;
                }
                
                novaPecaPromocionada = pdestiprom.hasPromocionat(noutipusdepeca);
                t.afegirPeca(pdestiprom.onEstic(), novaPecaPromocionada);
                j.afegirResultatJugada(ResultatJugada.Promocio);
            }
        }
        
        //Es crea una copia del tauler i s'afageix a la jugada
        Tauler nouTauler = t.deepCopy();
        Contadors nouContador = contador.deepCopy();
        j.finalitzarJugada(t,acabada,resultatFinal,contador,novaPecaPromocionada,guanyador());
        t = nouTauler;
        contador = nouContador;
    }
    
    /**
     * @pre Hi ha una jugada per desfer
     * @Post Desfà la jugada actual, i si a mes aquesta son taules acceptades desfa 2 jugades.
     */
    private void desferJugadaFinal(){
        //Recol·loquem el index de jugades
        jugadaActual--;
        //Carreguem la jugada
        carregarJugada(jugadaActual);
    } 
    
    /**
    * @pre posicio és vàlida, i origen != null
    * @Post es cra una jugada i es guarda a la posicó de l'array de jugades donada.
    * @param origen posició d'origen des d'on es vol moure una peça
    * @param desti posició d'origen
    * @param posicio posició on es guardara la jugada al l'array de jugades.
    */
    private Tauler ferJugada(Posicio origen, Posicio desti, Integer posicio, TipusPeca pecaPromocio) throws ExcepcioG{
        
        Color tornInicial = torn();
        
        if(t.tensPeca(origen) == null){
            throw new ExcepcioG(TipusExcepcioG.POSICIO_BUIDA);
        }
        if(t.tensPeca(origen).color() != torn()){
            throw new ExcepcioG(TipusExcepcioG.PECA_DIFERENT_EQUIP);
        }
        Jugada tt = null;
        try{
            tt = crearJugada(origen,desti);
        }
        catch(ExcepcioG g){
            Jugada jtmp = new Jugada();
            afegirJugada(jtmp,posicio);
            desferJugada();
            jugadaFinal--;
            canviarTorn(tornInicial);
            throw new ExcepcioG(g.getTipus());
        }
        if(tt != null){
            try{
                comprobarJugada(tt,pecaPromocio);
            }
            catch(ExcepcioG g){
                Jugada jtmp = new Jugada();
                afegirJugada(jtmp,posicio);
                desferJugada();
                jugadaFinal--;
                throw new ExcepcioG(g.getTipus());
            }
            afegirJugada(tt,posicio);
            canviarTorn(Color.canviaColor(torn()));
            return t;
        }
        else{
            canviarTorn(tornInicial);
            throw new ExcepcioG(TipusExcepcioG.DESCONEGUT);
        }
        
    }
    
    
     /**
    * @pre j no esta buit, el color dels tiradors de les jugades son correctes(no es tindras en compte)
    * @Post donat un conjunt de jugades, s'executa cada una per ordre ascendent.
    * @param j conte el conjunt de jugades a executar.
    */
    private void realitzarJugades(ArrayList<Jugada> j) throws ExcepcioG {
        for (Jugada tmp : j) {
            canviarTorn(tmp.obtenirTirador());
            if(tmp.obtenirOrigen() == null){
                ferJugada((ResultatJugada) tmp.obtenirResultatsJugada().toArray()[0]);
            }
            else{
                if(tmp.obtenirPecaPromocionada() != null){
                    ferJugada(tmp.obtenirOrigen(), tmp.obtenirDesti(),null,tmp.obtenirPecaPromocionada());
                }
                else{
                    ferJugada(tmp.obtenirOrigen(), tmp.obtenirDesti());
                }
            }
        }
    }

}
