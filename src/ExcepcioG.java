/**
 *  @file ExcepcioG.java
 *  @brief Classe ExcepcioG
 */

/** 
 *  @class ExcepcioG of Exception
 *  @brief Classe ExepcioG que gestiona el tipus de ExepcionsG (TipusExcepcioG).
 *  @note S'utilitza a Partida i Json.
 *  @author Eloi Quintana Ferrer
 *  @author u1963141
 */

public class ExcepcioG extends Exception{
    /**
     * Tipus d'exepció
     */
    private final TipusExcepcioG exc;
    
    /**
    * @post s'ha cridat a la classe mare d'exepció
    * @param t Posició d'origen de la peça.
    */
    ExcepcioG(TipusExcepcioG t){
        super(t.toString());
        exc = t;
    }
    
    /**
    * @post retorna exc
    * @return retorna el tipus d'exepció.
    */
    public TipusExcepcioG getTipus(){
        return exc;
    }
}



