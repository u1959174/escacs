import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 *  @file Jugada.java
 *  @brief Classe Jugada
 */

/** 
 *  @class Jugada
 *  @brief Conte tota la informacio de una Jugada i part de les dades de una partida.
 *  @author Eloi Quintana Ferrer
 *  @author u1963141
 */

public class Jugada {
    
    /**
     * Indica si la partida esta acabada o no.
     */
    private boolean acabada;
    
    /**
     * Cotnadors de escacs i inaccio per a cada color.
     */
    private Contadors contador;
    
    /**
    * Posició de desti de la peça.
    */
    private Posicio desti;
    
    /**
    * Color del guanyador de la partida
    */
    private Color guanyador;
    
    /**
    * Posició d'origen de la peça.
    */
    private Posicio origen;
    
    /**
    * Peça del desti (posible peça del rival elimanada).
    */
    private Peca pecaDesti;
    
    /**
    * Peça de l'origen.
    */
    private Peca pecaOrigen;
    
    /**
    * Peça que s'ha promocionat si escau
    */
    private Peca pecaPromocionada;
    
    /**
     * Indica el resultat final de la partida.
     */
    private ResultatPartida resultatFinal;

    /**
    * Llista de resultats de la jugada en questio.
    */
    //private ResultatJugada resultat;
    private Queue<ResultatJugada> resultatsJugada;

    /**
     * Guarda la classe Tauler que s'esta utilitzant actualment.
     */
    private Tauler t;
    
    /**
    * El que realitza la jugada.
    */
    private Color tirador;
    
    /**
    * @pre
    * @Post constructor
    */
    public Jugada(){
    } 
    
    /**
    * @pre origen != null, desti != null, tirador != null, 
    * @pre origen i desti dins el tauler
    * @Post S'han guardat les variables d'entrada.
    * @param origen Posició d'origen de la peça.
    * @param desti Posició de desti de la peça.
    * @param tirador Tirador de la jugada.
    */
    public Jugada(Posicio origen, Posicio desti, Color tirador){
        this.origen = origen;
        this.desti = desti;
        this.tirador = tirador;
        this.resultatsJugada = new LinkedList<ResultatJugada>();
    } 
    
    /**
    * @pre origen != null, desti != null, pecaOrigen != null, 
    * @pre origen i desti dins el tauler
    * @Post S'han guardat les variables d'entrada.
    * @param origen Posició d'origen de la peça.
    * @param desti Posició de desti de la peça.
    * @param pecaOrigen Peça de l'origen.
    * @param pecaDesti Peça del desti (posible peça del rival elimanada).
    * @param resultat Resultat de la jugada.
    * @param eliminades Peces que s'han eliminat.
    */
    public Jugada(Posicio origen, Posicio desti, Peca pecaOrigen, Peca pecaDesti){
        this.origen = origen;
        this.desti = desti;
        this.pecaOrigen = pecaOrigen;
        this.pecaDesti = pecaDesti;
        this.resultatsJugada = new LinkedList<ResultatJugada>();
        this.tirador = pecaOrigen.color();
        this.t = null;
        this.acabada = false;
        this.resultatFinal = null;
        this.contador = null;
    } 
    
    /**
    * @pre resultat != null
    * @Post S'han guardat les variables d'entrada.
    * @param resultat Resultat de la Jugada.
    * @param tirador color del jugador que realitza la jugada
    */
    public Jugada(ResultatJugada resultat,Color tirador){
        this.resultatsJugada = new LinkedList<ResultatJugada>();
        afegirResultatJugada(resultat);
        this.tirador = tirador;
    } 


    /**
     * @pre res != null
     * @Post s'ha afegit a la llista el resultat de la jugada
     * @param t Tauler referenciat.
    */   
    public void afegirResultatJugada(ResultatJugada res){
        resultatsJugada.add(res);
    }
    
    /**
    * @pre t != null
    * @Post S'ha substituit la referencia de tauler amb la d'entrada
    * @param t Tauler referenciat.
    */
    public void canviTauler(Tauler t){
        this.t = t;
    }
    
    /**
     * @pre -
     * @post S'han assignat els parametrs d'entrada a les corresponents varialbes/referencies.
     * @param t Tauler referenciat.
     * @param partidaAcabada true si la partida es acabada, false altrament.
     * @param resultatFinal resultat de la partida
     * @param contador contadors de innacció i escac continu.
    */
    public void finalitzarJugada(Tauler t, boolean partidaAcabada, ResultatPartida resultatFinal, Contadors contador, Peca pecaPromocionada, Color guanyador){
        this.t = t;
        this.acabada = partidaAcabada;
        this.resultatFinal = resultatFinal;
        this.contador = contador;
        this.pecaPromocionada = pecaPromocionada;
        this.guanyador = guanyador;
    }
    
    /**
    * @return True si la partida esta acabada, false sino.
    */
    public boolean obtenirAcabada(){
        return acabada;
    }
    
    /**
    * @return Contadors de la partida.
    */    
    public Contadors obtenirContadors(){
        return contador;
    }
    
    /**
    * @return Posicio de desti
    */
    public Posicio obtenirDesti(){
        return desti;
    }
    
    /**
    * @return retorna el color del guanyador
    */
    public Color obtenirGuanyador(){
        return guanyador;
    }
    
    /**
    * @return Posicio d'origen
    */
    public Posicio obtenirOrigen(){
        return origen;
    }
    
    /**
    * @return peca de desti
    */
    public Peca obtenirPecaDesti(){
        return pecaDesti;
    }
    
    /**
    * @return peca d'origen
    */
    public Peca obtenirPecaOrigen(){
        return pecaOrigen;
    }
    
    /**
    * @pre -
    * @Post S'ha retornat el tipus de peca promocionada.
    */
    public TipusPeca obtenirPecaPromocionada(){
        if(pecaPromocionada == null){
            return null;
        }
        else{
            return pecaPromocionada.tipus();
        }
    }
    
    /**
    * @return el resultat final de la partida
    */
    public ResultatPartida obtenirResultatPartida(){
        return resultatFinal;
    }
    
    /**
    * @return Posicio d'origen
    */
    public Collection<ResultatJugada> obtenirResultatsJugada(){
        return resultatsJugada;
    }
    
    /**
    * @return Tauler una vegada aplicada la jugada.
    */
    public Tauler obtenirTauler(){
        return t;
    }
    
    /**
    * @return Color del Tirador
    */
    public Color obtenirTirador(){
        return tirador;
    }
}
