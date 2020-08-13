/**
 *  @file Moviment.java
 *  @brief Classe Moviment
 */

/** 
 *  @class Moviment
 *  @brief Interfície d'un Moviment d'un TipusPeca.
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */
public interface Moviment {
    /**
     * @brief Comprova si un desplaçament és el Moviment.
     * @param origen Posició d'origen.
     * @param desti Posició destí.
     * @param primer Indica si el el moviment és el primer de la partida.
     * @param peca_final Referència al tipus de peça de la posició destí.
     * @param peca_mig Indica si hi ha alguna peça en la trajectòria.
     * @param exacte Demanar si la distància ha de ser exacte.
     * @return Retorna cert si amb origen-desti és compleix el moviment, fals altrament.
    */
    public boolean esMoviment(Posicio origen, Posicio desti, boolean primer, TipusPeca peca_final, boolean peca_mig, boolean exacte);
    
    /**
     * @brief Tradueix els paràmetres perquè puguin ser interpretats.
     * @param param Coordenada A o B del moviment a configuració.
     * @return Retorna param en format int.
    */
    default Integer traduirParametre(String param) {
        if (param.equals("a") || param.equals("-a") | param.equals("b") || param.equals("-b")) return 0;
        else if (param.equals("n")) return 1;
        else if (param.equals("-n")) return -1;
        else return null;
    }
    
    /**
     * @brief Comprova si una string és un nombre enter.
     * @param entrada String d'entrada.
     * @return Retorna cert si entrada és enter, fals altrament.
    */
    default boolean esEnter(String entrada) {
        try {
            Integer.parseInt(entrada);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * @brief Comprova si un Moviment té el mateix vector de desplaçament que un altre.
     * @param mov Moviment amb el que es vol comparar.
     * @return Cert si el vector de desplaçament és igual.
     * @note serveix per diferenciar els vectors combinats dels diagonals i rectilinis en temps de càrrega
    */
    default boolean mateixVector(Moviment mov) {
        String aquest_a = diguemA();
        String aquest_b = diguemB();
        String aquell_a = mov.diguemA();
        String aquell_b = mov.diguemB();
        if (aquest_a != null && aquell_a != null && aquest_b != null && aquell_b != null) {
            return (conte(aquest_a,aquell_a) && conte(aquest_b,aquell_b)) || (conte(aquell_a,aquest_a) && conte(aquell_b,aquest_b));
        }
        else return false;
    }
    
    /**
     * @brief Traducció del paràmetre A.
     * @return El valor del paràmetre A traduit. Null si no el té.
    */
    public String diguemA();
    
    /**
     * @brief Traducció del paràmetre B.
     * @return El valor del paràmetre B traduit. Null si no el té.
    */
    public String diguemB();
    
    /**
     * @brief Diu el valor de saltar.
     * @return Retorna 0 si no pot saltar, 1 si pot, 2 si mata saltant, null si no té l'atribut.
    */
    public Integer diguemSaltar();
    
    /**
     * @brief Indica si un paràmetre conté a un altre.
     * @param a Component A|B d'un moviment.
     * @param b Component A|B d'un moviment.
     * @pre a i b han de ser tots dos component A o component B.
     * @return Cert si a conté b.
     */
    default boolean conte(String a, String b) {
        boolean temp = a == "a" || a == "b" || a == "-a" || a == "-b";
        if (a.equals(b)) return true;
        else if (esEnter(b)) {
            if (Integer.parseInt(b) > 0) {
                return a == "n" || temp;
            }
            else if (Integer.parseInt(b) < 0) {
                return a == "-n" || temp;
            }
            else return false;
        }
        else return temp && (b == "n" || b == "-n");
    }
    
    /**
     * @brief Diu si el Moviment és de tipus enroc.
     * @return Cert si el Moviment és enroc, fals altrament.
    */
    public boolean esEnroc();
}
