import static java.lang.Math.abs;

/**
 *  @file MovimentRectilini.java
 *  @brief Classe MovimentRectilini
 */

/** 
 *  @class MovimentRectilini
 *  @brief Implementació de Moviment per MovimentRectilini.
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */

public class MovimentRectilini implements Moviment {
    /**
    * @brief Indica si el moviment és exclusivament inicial
    */
    private boolean inicial;
    
    /**
    * @brief Indica el límit de dstància del moviment, 0 si és infinit
    */
    private int distancia;
    
    /**
    * @brief Indica la direcció el sentit del moviment. 1 positiu, -1 negatiu i 0 en ambdos sentits.
    * @note També pot ser 0 si el moviment és d'una distància determinada.
    */
    private int sentit;
    
    /**
    * @brief Cert si el moviment és horitzontal, fals si és vertical
    */
    private boolean horitzontal;
    
    /**
    * @brief 0 si no pot saltar peces, 1 si pot, 2 mata les peces saltades
    */
    private int saltar;
    
    /**
    * @brief 0 si no pot matar la peça, 1 si pot, 2 ha de matar per fer el moviment
    */
    private int matar;
    
    @Override
    public Integer diguemSaltar() {
        return saltar;
    }
    
    /**
     * @return Cert si és horitzontal, fals altrament.
    */
    public boolean diguemHoritzontal() {
        return horitzontal;
    }
    
    /**
     * @return Cert si el moviment és inicial, fals altrament.
    */
    public boolean diguemInicial() {
        return inicial;
    }
    
    /**
     * @return Retorna matar. 0 si no pot, 1 si pot i 2 si ha.
    */
    public int diguemMatar() {
        return matar;
    }
    
    /**
     * @pre c i d són enters
     * @brief Constructor.
     * @param a Component A.
     * @param b Component B.
     * @param c Component C.
     * @param d Component D.
     * @param inicial Indica si el moviment és inicial.
     * @post Crea un moviment de tipus rectilini.
     * @throws IllegalArgumentException quan no es compleix la pre.
    */
    public MovimentRectilini(String a, String b, String c, String d, boolean inicial) throws IllegalArgumentException {
        this.inicial = inicial;
        
        if (!esEnter(c) || !esEnter(d)) throw new IllegalArgumentException("Format incorrecte a (" + a + "," + b + ")");
        else {
            matar = Integer.parseInt(c);
            saltar = Integer.parseInt(d);
        }
        
        boolean error = false;
        
        if (a.equals("0")) {
            horitzontal = true;
            error = traduirAoB(b);
        }
        else if (b.equals("0")) {
            horitzontal = false;
            error = traduirAoB(a);
        }
        else error = true;
        
        if (error) throw new IllegalArgumentException("Format incorrecte a (" + a + "," + b + ")");
    }
    
    @Override
    public boolean esMoviment(Posicio origen, Posicio desti, boolean primer, TipusPeca peca_final, boolean peca_mig, boolean exacte) {
        if (inicial && !primer || !origen.movimentRectilini(desti) || (peca_final != null && matar == 0) || (peca_final == null && matar == 2) || (peca_mig && saltar == 0)) return false;
        else if (horitzontal && desti.compareFila(origen) == 0) {
            int valor = desti.compareColumna(origen);
            switch (sentit) {
                case 0:
                    return distancia == 0 || (!exacte && distancia >= abs(valor)) || (exacte && distancia == abs(valor));
                case 1:
                    return valor > 0 && (distancia == 0 || (!exacte && distancia >= valor) || (exacte && distancia == valor));
                case -1:
                    return valor < 0 && (distancia == 0 || (!exacte && distancia*(-1) <= valor) || (exacte && distancia*(-1) == valor));
                default:
                    return false;
            }
        }
        else if (!horitzontal && desti.compareColumna(origen) == 0) {
            int valor = desti.compareFila(origen);
            switch (sentit) {
                case 0:
                    return distancia == 0 || (!exacte && distancia >= abs(valor)) || (exacte && distancia == abs(valor));
                case 1:
                    return distancia == 0 || valor > 0 && ((!exacte && distancia >= valor) || (exacte && distancia == valor));
                case -1:
                    return distancia == 0 || valor < 0 && ((!exacte && distancia*(-1) <= valor) || (exacte && distancia*(-1) == valor));
                default:
                    return false;
            }
        }
        else return false;
    }
    
    public boolean equals(MovimentRectilini mov) {
        return distancia == mov.distancia && sentit == mov.sentit && horitzontal == mov.horitzontal;
    }
    
    private boolean traduirAoB(String s) {
        boolean error = false;
        if (esEnter(s)) {
            int n = Integer.parseInt(s);
            distancia = abs(n);
            if (n > 0) sentit = 1;
            else if (n < 0) sentit = -1;
            else error = true;
        }
        else {
            Integer sentit_s = traduirParametre(s);
            if (sentit_s != null) {
                sentit = sentit_s;
                distancia = 0;
            }
            else error = true;
        }
        return error;
    }
    
    @Override
    public String diguemA(){
        if (horitzontal) return "0";
        else {
            if (distancia != 0) {
                String res = String.valueOf(distancia);
                if (sentit == -1) res = "-" + res;
                return res;
            }
            else {
                switch (sentit) {
                    case 0:
                        return "a";
                    case 1:
                        return "n";
                    default:
                        return "-n";
                }
            }
        }
    }
    
    @Override
    public String diguemB(){
        if (!horitzontal) return "0";
        else {
            if (distancia != 0) {
                String res = String.valueOf(distancia);
                if (sentit == -1) res = "-" + res;
                return res;
            }
            else {
                switch (sentit) {
                    case 0:
                        return "a";
                    case 1:
                        return "n";
                    default:
                        return "-n";
                }
            }
        }
    }
    
    @Override
    public boolean esEnroc() {
        return false;
    }
}
