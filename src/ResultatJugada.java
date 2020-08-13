/**
 *  @file ResultatJugada.java
 *  @brief Enum ResultatJugada
 */

/** 
 *  @class ResultatJugada
 *  @brief Conte tots els possibles estats d'una Jugada
 *  @note S'utilitza a Jugada
 *  @author Eloi Quintana Ferrer
 *  @author u1963141
 */

public enum ResultatJugada {
	Escac {
            @Override
            public String toString() {
              return "ESCAC";
            }
          },
	EscacIMat {
            @Override
            public String toString() {
              return "ESCAC I MAT";
            }
          },
        TReiOfegat {
            @Override
            public String toString() {
              return "TAULES PER REI OFEGAT";
            }
          },
        TEscacContinu {
            @Override
            public String toString() {
              return "TAULES PER ESCAC CONTINU";
            }
          },
        TInaccio {
            @Override
            public String toString() {
              return "TAULES PER INACCIÓ";
            }
          },
        TSollicitades {
            @Override
            public String toString() {
              return "TAULES SOL·LICITADES";
            }
          },
        TAcceptades {
            @Override
            public String toString() {
              return "TAULES ACCEPTADES";
            }
          },
        TRebutjades {
        	  @Override
              public String toString() {
                return "TAULES REBUTJADES";
              }
          },
        Rendicio {
            @Override
            public String toString() {
              return "RENDICIO";
            }
          },
        Inicial {
            @Override
            public String toString() {
              return "INICIAL";
            }
          },
        Promocio {
            @Override
            public String toString() {
              return "PROMOCIO";
            }
          },
        Ajornament {
            @Override
            public String toString() {
              return "AJORNAMENT";
            }
          };
}
