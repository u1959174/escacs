/**
 *  @file TipusExcepcioG.java
 *  @brief Enum TipusExcepcioG
 */

/** 
 *  @class TipusExcepcioG
 *  @brief Enum que conté tots els possibles tipus d'execpcio G.
 *  @note és un Enum.
 *  @author Eloi Quintana Ferrer
 *  @author u1963141
 */

public enum TipusExcepcioG {
        MOVIMENT_PROVOCA_ESCAC_I_MAT {
            @Override
            public String toString() {
              return "El moviment deixa en Escac i Mat el teu rei.";
            }
          },
        MOVIMENT_PROVOCA_ESCAC {
            @Override
            public String toString() {
              return "El moviment deixa en Escac el teu rei.";
            }
          },
	MOVIMENT_INCORRECTE {
            @Override
            public String toString() {
              return "La peça que vols moure no pot fer aquest moviment.";
            }
          },
        POSICIO_BUIDA {
            @Override
            public String toString() {
              return "La posició d'origen no hi ha cap peça.";
            }
          },
	PECA_MATEIX_EQUIP {
            @Override
            public String toString() {
              return "Estas atacant a la peces del teu equip.";
            }
          },
        PECA_DIFERENT_EQUIP {
            @Override
            public String toString() {
              return "La peca seleccionada es de l'equip contrari";
            }
          },
        MATAR_MATEIX_EQUIP {
            @Override
            public String toString() {
              return "Estas atacant a la peces del teu equip.";
            }
          },
        FITXER_REGLES_NO_VALID {
            @Override
            public String toString() {
              return "El fitxer de regles és invàlid.";
            }
          },
        PECA_INVULNERABLE {
            @Override
            public String toString() {
              return "S'intenta matar a una peca Invulnerable.";
            }
          },
        DOBLE_MOVIMENT {
            @Override
            public String toString() {
              return "S'ha trobat un moviment duplicat.";
            }
          },
        FITXER_INVALID {
            @Override
            public String toString() {
              return "El format del fitxer és invàlid.";
            }
          },
        DESCONEGUT {
            @Override
            public String toString() {
              return "No s'ha pogut completar la jugada.";
            }
        };
}