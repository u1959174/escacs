import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

/**
 *  @file MotorCPU.java
 *  @brief Classe MotorCPU
 */

/** 
 *  @class MotorCPU
 *  @brief Mòdul de suport de la CPU.
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */

public class MotorCPU {
    private static int VALOR_ENROC = 8; //valor arbitrari
    private static int VALOR_DIVISOR = 2; //valor arbitrari
    private static HashMap<String,Jugada> coneixement = new HashMap<String,Jugada>();
    
    /**
     * @param taulerActual Tauler amb l'estat actual.
     * @param llista
     * @param torn Color del jugador que tocaria moure.
     * @return La jugada que tocaria fer si l'estat Ã©s conegut. Null si no es troba o no és moviment o escac.
     */
    public static Jugada esEstatConegut(Tauler taulerActual, Color torn) {
        Jugada jugada = coneixement.get(taulerActual.toString());
        if (jugada != null) {
            Collection<ResultatJugada> resultats = jugada.obtenirResultatsJugada();
            if (resultats.size() != 0) {
                boolean temp = resultats.contains(ResultatJugada.Escac);
                if (!temp) temp |= resultats.contains(ResultatJugada.EscacIMat);
                if (!temp) temp |= resultats.contains(ResultatJugada.Promocio);
                if (!temp) return null;
            }
            if (torn.equals(Color.Negres)) {
                Posicio origen_invertit = jugada.obtenirOrigen().canviPrespectiva(taulerActual.dimensions().second);
                Posicio desti_invertit = jugada.obtenirDesti().canviPrespectiva(taulerActual.dimensions().second);
                return new Jugada(origen_invertit,desti_invertit,jugada.obtenirPecaOrigen(),jugada.obtenirPecaDesti());
            }
            else return jugada;
        }
        else return null;
    }

    /**
     * @param taulerActual Tauler amb l'estat actual.
     * @param jug Jugada que s'evalua.
     * @return Retorna el valor de la jugada. Valor negatiu si Ã©s a favor de les negres.
     * @post taulerActual no queda modificat.
     * @deprecated
     */
    public static int valorarJugada(Tauler taulerActual, Jugada jug) {
        Peca peca_desti = taulerActual.tensPeca(jug.obtenirDesti());
        Peca peca_origen = taulerActual.tensPeca(jug.obtenirOrigen());
        int res = 0;
        
        res -= (peca_origen.tipus().diguemValor()/VALOR_DIVISOR);
        
        if (peca_desti != null) {
            if (taulerActual.calcularEnrroc(jug.obtenirOrigen(),jug.obtenirDesti()) != null && jug.obtenirDesti().compareFila(jug.obtenirOrigen()) == 0) return VALOR_ENROC;
            else res += peca_desti.tipus().diguemValor();
        }
        
        Collection<Pair<Posicio,Peca>> peces_mig = taulerActual.pecesAlMigColorConcret(jug.obtenirOrigen(), jug.obtenirDesti(), false, Color.canviaColor(peca_origen.color()));
        for (Pair<Posicio,Peca> parella: peces_mig) {
            res += parella.second.tipus().diguemValor();
        }
            
        if (peca_origen.color() == Color.Negres) return -res;
        else return res;
    }

    /**
     * @brief Càrrega del coneixement a partir d'una ruta de fitxer de coneixemnt.
     * @param rutaFitxerConeixement La ruta del fitxer amb les partides de prova.
     * @post Carrega les llistes de taulers i jugades.
     */
    public static void carregarConeixement(String rutaFitxerConeixement) {
        try {
            Collection<Partida> partides = Json.carregarConeixement(rutaFitxerConeixement);
            for (Partida partida : partides) {
                Integer n = null;
                if (!partida.acabada()) {
                    System.out.println(partida.obtenirTauler());
                    System.out.println("Qui esta  en una posicio avantatjosa? (Blanques:1,Negres:0)");
                    Scanner s = new Scanner(System.in);
                    if (s.hasNextInt()) n = s.nextInt();
                }
                else {
                    if (partida.guanyador().equals(Color.Blanques)) n = 1;
                    else n = 0;
                }
                
                for (Jugada jugada : partida.obtenirJugades()) {
                    Tauler tauler = jugada.obtenirTauler();
                    if (n != null) {
                        if (n == 0) coneixement.put(tauler.canviarPerspectiva().toString(),jugada);
                        else if (n == 1) coneixement.put(tauler.toString(),jugada);
                    }
                }
            }
        }
        catch (IOException | ExcepcioG e) {
            if (e instanceof IOException) System.out.println("ERROR: No s'ha pogut obrir el fitxer.");
            else System.out.println(e);
        }
    }
}
