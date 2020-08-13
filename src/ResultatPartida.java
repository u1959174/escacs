/**
 *  @file ResultatPartida.java
 *  @brief Enum ResultatPartida
 */

/** 
 *  @class ResultatPartida
 *  @brief Enum que conté tots els possibles resultats d'una Partida
 *  @note és un Enum.
 *  @author Eloi Quintana Ferrer
 *  @author u1963141
 */

public enum ResultatPartida {
	BlanquesGuanyen {
            @Override
            public String toString() {
              return "BLANQUES GUANYEN";
            }
          },
	NegresGuanyen {
            @Override
            public String toString() {
              return "NEGRES GUANYEN";
            }
          },
        Taules {
            @Override
            public String toString() {
              return "TAULES";
            }
          },
        PartidaAjornada {
            @Override
            public String toString() {
              return "PARTIDA AJORNADA";
            }
          };
        
        /**
         * @post donada una string aquesta la compara amb cada un dels possibles resultats de partida.
	 * @param str Conté la representació en format string d'un possible valor d'aquest ennum.
	 * @return El Resultat de l'string.
	 */
	public static ResultatPartida obtenirResultat(String str) {
		if(str.toLowerCase().equals(ResultatPartida.BlanquesGuanyen.toString()))
			return ResultatPartida.BlanquesGuanyen;
                else if(str.toLowerCase().equals(ResultatPartida.NegresGuanyen.toString()))
			return ResultatPartida.NegresGuanyen;
                else if(str.toLowerCase().equals(ResultatPartida.Taules.toString()))
			return ResultatPartida.Taules;
                else if(str.toLowerCase().equals(ResultatPartida.PartidaAjornada.toString()))
			return ResultatPartida.PartidaAjornada;
		else
			return null;
	}
}
