/**
 *  @file MovimentCombinat.java
 *  @brief Classe MovimentCombinat
 */

/** 
 *  @class MovimentCombinat
 *  @brief Implementació de Moviment per MovimentCombinat.
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */
public class MovimentCombinat implements Moviment {
    /**
    * @brief Moviment combinat d'una peça.
    */
    
    /**
    * @brief Moviment Rectilini horitzontal/vertical que fa.
    */
    private MovimentRectilini moviment_horitzontal;
    private MovimentRectilini moviment_vertical;
    
    /**
     * @pre c és enter.
     * @brief Constructor.
     * @param a Component A.
     * @param b Component B.
     * @param c Component C.
     * @param inicial Indica si el moviment és inicial.
     * @post Crea un moviment de tipus combinat.
     * @throws IllegalArgumentException Quan no es compleix la pre.
    */
    public MovimentCombinat(String a, String b, String c, boolean inicial) throws IllegalArgumentException {
        if (!esEnter(c))
            throw new IllegalArgumentException("Format incorrecte a (" + a + "," + b + ")");
        else {
            moviment_horitzontal = new MovimentRectilini("0",b,c,"1",inicial);
            moviment_vertical = new MovimentRectilini(a,"0",c,"1",inicial);
        }
    }
    
    @Override
    public boolean esMoviment(Posicio origen, Posicio desti, boolean primer, TipusPeca peca_final, boolean peca_mig, boolean exacte) {
        if (moviment_horitzontal.diguemInicial() && !primer || (peca_final != null && moviment_horitzontal.diguemMatar() == 0) || (peca_final == null && moviment_horitzontal.diguemMatar() == 2)) return false;
        else {
            Posicio nou_origen_horitzontal = new Posicio(desti.fila(),origen.columna());
            Posicio nou_origen_vertical = new Posicio(origen.fila(),desti.columna());
            return moviment_horitzontal.esMoviment(nou_origen_horitzontal,desti,primer,peca_final,false,true) && moviment_vertical.esMoviment(nou_origen_vertical,desti,primer,peca_final,false,true);
        }
    }
    
    public boolean equals(MovimentCombinat mov) {
        return moviment_horitzontal.equals(mov.moviment_horitzontal) && moviment_vertical.equals(mov.moviment_vertical);
    }
    
    @Override
    public String diguemA(){
        return moviment_vertical.diguemA();
    }
    
    @Override
    public String diguemB(){
        return moviment_horitzontal.diguemB();
    }
    
    @Override
    public Integer diguemSaltar() {
        return null;
    }
    
    @Override
    public boolean esEnroc() {
        return false;
    }
}
