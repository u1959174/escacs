/**
 *  @file MovimentDiagonal.java
 *  @brief Classe MovimentDiagonal
 */

/** 
 *  @class MovimentDiagonal
 *  @brief Implementació de Moviment per MovimentDiagonal.
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */
import static java.lang.Math.abs;
public class MovimentDiagonal implements Moviment {
    /**
    * @brief Moviment diagonal d'una peça.
    */
    
    /**
    * @brief Indica si el moviment és exclusivament inicial
    */
    private boolean inicial;
    
    /**
    * @brief Indica el límit de dstància del moviment, 0 si és infinit
    */
    private int distancia;
    
    /**
    * @brief Indica la direcció el sentit del moviment. 1 positiu, -1 negatiu i 0 en ambdos sentits respecte la A.
    * @note També pot ser 0 si el moviment és d'una distància determinada.
    */
    private int sentit;
    
    /**
    * @brief Cert si la diagonal és dreta, fals és esquerra.
    */
    private boolean dreta;
    
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
     * @pre c i d són enters
     * @brief Constructor.
     * @param a Component A.
     * @param b Component B.
     * @param c Component C.
     * @param d Component D.
     * @param inicial Indica si el moviment és inicial.
     * @post Crea un moviment de tipus diagonal.
     * @throws IllegalArgumentException Quan no es compleix la pre.
    */
    public MovimentDiagonal(String a, String b, String c, String d, boolean inicial) throws IllegalArgumentException {
        this.inicial = inicial;
        if (!esEnter(c) || !esEnter(d)) throw new IllegalArgumentException("Format incorrecte a (" + a + "," + b + ")");
        else {
            matar = Integer.parseInt(c);
            saltar = Integer.parseInt(d);
        }
        boolean error = false;
        if (a.equals(b)) {
            dreta = true;
            error = traduirAB(a,b);
        }
        else if ((a.equals("n") && b.equals("-n")) || (a.equals("-n") && b.equals("n")) || (a.equals("a") && b.equals("-a")) || abs(Integer.parseInt(a)) == abs(Integer.parseInt(b))) {
            dreta = false;
            error = traduirAB(a,b);
        }
        else error = true;
        if (error) throw new IllegalArgumentException("Format incorrecte a (" + a + "," + b + ")");
    }
    
    @Override
    public boolean esMoviment(Posicio origen, Posicio desti, boolean primer, TipusPeca peca_final, boolean peca_mig, boolean exacte) {
        if (inicial && !primer || !origen.movimentDiagonal(desti) || (peca_final != null && matar == 0) || (peca_final == null && matar == 2) || (peca_mig && saltar == 0)) return false;
        else {
            boolean diagonal_dreta = desti.compareFila(origen) == desti.compareColumna(origen);
            int valor = desti.compareFila(origen);
            switch (sentit) {
                case 0:
                    return !(diagonal_dreta ^ dreta) && (distancia == 0 || (!exacte && distancia >= abs(valor) || (exacte && distancia == valor)));
                case 1:
                    return !(diagonal_dreta ^ dreta) && (valor > 0 && (distancia == 0 || (!exacte && distancia >= valor) || (exacte && distancia == valor)));
                case -1:
                    return !(diagonal_dreta ^ dreta) && (valor < 0 && (distancia == 0 || (!exacte && distancia*(-1) <= valor) || (exacte && distancia*(-1) == valor)));
                default:
                    return false;
            }
        }
    }
    
    public boolean equals(MovimentDiagonal mov) {
        return distancia == mov.distancia && sentit == mov.sentit && dreta == mov.dreta;
    }
    
    private boolean traduirAB(String a, String b) {
        boolean error = false;
        Integer sentit_a = traduirParametre(a);
        Integer sentit_b = traduirParametre(b);
        if ((sentit_a != null || esEnter(a)) && (sentit_b != null || esEnter(b))) {
            if (esEnter(a) && esEnter(b)) {
                int num_a = Integer.parseInt(a);
                distancia = abs(num_a);
                if (num_a > 0) sentit = 1;
                else if (num_a < 0) sentit = -1;
                else error = true;
            }
            else if (sentit_a != null && sentit_b != null) {
                if (sentit_a == 1 && abs(sentit_b) == 1) { sentit = 1; distancia = 0; }
                else if (sentit_a == -1 && abs(sentit_b) == 1) { sentit = -1; distancia = 0; }
                else if (sentit_a == 0 && sentit_b == 0) { sentit = 0; distancia = 0; }
                else error = true;
            }
            else error = true;
        }
        else error = true; 
        return error;
    }
    
    @Override
    public String diguemA(){
        if (dreta) {
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
        if (dreta) {
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
        else {
            if (distancia != 0) {
                String res = String.valueOf(distancia);
                if (sentit == 1) res = "-" + res;
                return res;
            }
            else {
                switch (sentit) {
                    case 0:
                        return "-a";
                    case 1:
                        return "-n";
                    default:
                        return "n";
                }
            }
        }
    }
    
    @Override
    public boolean esEnroc() {
        return false;
    }
}
