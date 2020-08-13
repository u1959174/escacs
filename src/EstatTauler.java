/**
 *  @file EstatTauler.java
 *  @brief Ennum EstatTauler
 */

/** 
 *  @class EstatTauler
 *  @brief Ennum amb els possibles estats del Tauler en un momemnt determinat de la partida.
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public enum EstatTauler {
    ESCAC_I_MAT {
        @Override
        public String toString() {
          return "S'ha posat en escac i mat al rival.";
        }
    },
    ESCAC_SIMPLE {
        @Override
        public String toString() {
          return "S'ha posat en escac al rival.";
        }
      },
    NORMALITAT {
        @Override
        public String toString() {
          return "L'estat actual del tauler no presenta cap anomalia.";
        }
      },
    OFEGAT {
        @Override
        public String toString() {
          return "S'ha ofegat al rival.";
        }
      };
	
}
