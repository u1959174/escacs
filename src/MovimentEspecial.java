/**
 *  @file MovimentEspecial.java
 *  @brief Classe MovimentEspecial
 */

/** 
 *  @class MovimentEspecial
 *  @brief Implementació de Moviment per MovimentEspecial (enroc rectilini).
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */
public class MovimentEspecial implements Moviment {
    /**
    * @brief Indica si s'ha de fer amb les peces no mogudes.
    */
    private boolean quiets;
    
    /**
    * @brief Indica si l'espai entre les dues peces no ha d'estar ocupa per cap altra.
    */
    private boolean buit;
    
    /**
    * @brief L'altra peça amb la que es fa l'enroc
    */
    private TipusPeca peca;
    
    /**
     * @brief Constructor.
     * @param quiets Peces han d'estar quietes.
     * @param buit Espai buit entre peces.
     * @param peca TipusPeca de l'altra peca.
     * @post Crea un moviment de tipus especial, l'enroc.
    */
    public MovimentEspecial(boolean quiets, boolean buit, TipusPeca peca) {
        this.quiets = quiets;
        this.buit = buit;
        this.peca = peca;
    }
    
    /**
     * @Pre El tipus de peça amb la que es vol fer l'intercanvi és compatible.
    */
    @Override
    public boolean esMoviment(Posicio origen, Posicio desti, boolean primer, TipusPeca peca_final, boolean peca_mig, boolean exacte) {
        if ((peca_final != null && peca_final == peca) && desti.compareFila(origen) == 0 && !(peca_mig && buit) && !(!primer && quiets)) return true;
        else return false;
    }
    
    public boolean equals(MovimentEspecial mov) {
        return quiets == mov.quiets && buit== mov.buit && peca == mov.peca;
    }
    
    @Override
    public String diguemA(){
        return null;
    }
    
    @Override
    public String diguemB(){
        return null;
    }
    
    @Override
    public Integer diguemSaltar() {
        return null;
    }
    
    @Override
    public boolean esEnroc() {
        return true;
    }
}
