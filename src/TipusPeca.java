import java.util.ArrayList;
import java.util.Collections;

/**
 *  @file TipusPeca.java
 *  @brief Classe TipusPeca
 */

/** 
 *  @class TipusPeca
 *  @brief Un tipus de peça configurable.
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */

public class TipusPeca {
    /**
     * @brief Tipus de peça, configuració.
    */
    
    /**
    * @brief Nom de la peça
    */
    private String nom;
    
    /**
    * @brief Símbol de la peça en majúscula
    */
    private char simbol;
    
    /**
    * @brief Valor de la peça
    */
    private int valor;
    
    /**
    * @brief Regles extra de la peça
    */
    boolean promocio, invulnerabilitat;
    
    /**
    * @brief Moviments de la peça
    */
    private ArrayList<Moviment> moviments;
    
    /**
    * @brief Rutes de les icones.
    */
    private String ruta_blanca;
    private String ruta_negra;
    
    /**
     * @param nom El nom.
     * @param simbol El símbol.
     * @param valor El valor.
     * @param promocio Indica si té promoció o no.
     * @param invulnerabilitat Indica si la peça és invulnerable.
     * @pre char majúscula i valor >= 0
     * @post Crea un tipus de peça amb nom i valors entrats.
     * @exception IllegalArgumentException Quan se salta la precondició.
    */
    public TipusPeca(String nom, char simbol, int valor, boolean promocio, boolean invulnerabilitat, String ruta_blanca, String ruta_negra) throws IllegalArgumentException {
        if (valor < 0) throw new IllegalArgumentException("Valor de peça negatiu.");
        else if (Character.isLowerCase(simbol)) throw new IllegalArgumentException("Nom de peça no majúscula.");
        moviments = new ArrayList<Moviment>();
        this.promocio = promocio;
        this.invulnerabilitat = invulnerabilitat;
        this.simbol = simbol;
        this.nom = nom;
        this.valor = valor;
        this.ruta_blanca = ruta_blanca;
        this.ruta_negra = ruta_negra;
    }
    
    /**
     * @return Retorna la ruta de la icona blanca.
    */
    public String diguemRutaBlanca() {
        return ruta_blanca;
    }
    
    /**
     * @return Retorna la ruta de la icona negra.
    */
    public String diguemRutaNegra() {
        return ruta_negra;
    }
    
    /**
     * @return Retorna si té promoció.
    */
    public boolean diguemPromocio() {
        return promocio;
    }
    
    /**
     * @return Retorna la invulnerabilitat de la peça.
    */
    public boolean esInvulnerable() {
        return invulnerabilitat;
    }
    
    /**
     * @param mov Moviment a inserir.
     * @param inicial Indica si el moviment és exclusivament inicial o no.
     * @post Carrega el moviment al a configuració de la peça.
     * @exception DOBLE_MOVIMENT Si s'ha trobat un moviment amb el mateix vector  de desplaçament.
     * @throws ExcepcioG Quan el moviment ja existeix.
    */
    public void carregarMoviment(Moviment mov, boolean inicial) throws ExcepcioG {        
        if (!provaMoviment(mov)) moviments.add(mov);
        else throw new ExcepcioG(TipusExcepcioG.DOBLE_MOVIMENT);
    }
    
    /**
     * @return Retorna el valor.
    */
    public int diguemValor() {
        return valor;
    }
    
    /**
     * @return Retorna el símbol.
    */
    public char diguemSimbol() {
        return simbol;
    }
    
    /**
     * @return Retorna el nom.
    */
    private String diguemNom() {
        return nom;
    }

    /**
     * @param origen Posició d'origen.
     * @param desti Posició destí.
     * @param primer Indica si el el moviment és el primer de la partida.
     * @param peca_final Referència al tipus de peça de la posició destí.
     * @param peca_mig Indica si hi ha alguna peça enmig de la trajectòria.
     * @note Retorna primer els enrocs.
     * @return Retorna el moviment que fa el desplaçament.
    */
    public Moviment provaMoviment(Posicio origen, Posicio desti, boolean primer, boolean blanques, TipusPeca peca_final, boolean peca_mig) {
        if (!blanques) origen.swap(desti);
        Collections.reverse(moviments);
        for (Moviment mov : moviments) {
            if (mov.esMoviment(origen,desti,primer,peca_final,peca_mig,false)) {
                if (!blanques) origen.swap(desti); Collections.reverse(moviments); return mov;
            }
        }
        if (!blanques) origen.swap(desti); Collections.reverse(moviments); return null;
    }
    
    /**
     * @return Cert si el moviment està repetit amb el mateix vector de desplaçament, fals altrament.
    */
    public boolean provaMoviment(Moviment mov) {
        int i = 0;
        boolean trobat = false;
        while (!trobat && i < moviments.size()) {
            if (mov.mateixVector(moviments.get(i))) trobat = true;
            else i++;
        }
        return trobat;
    }
    
    @Override
    public String toString() {
        return diguemNom();
    }
}
