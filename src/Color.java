/**
 *  @file Color.java
 *  @brief Ennum Color
 */

/** 
 *  @class Color(ennum)
 *  @brief Color d'una peça que pot ser blanca o negra. Adicionalment s'hi afegeix un element neutre(capColor).
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */

public enum Color {
	Blanques {
            @Override
            public String toString() {
              return "Blanques";
            }
          },
	capColor,
	Negres {
            @Override
            public String toString() {
              return "Negres";
            }
          }; //Element neutre, el fem servir per exemple en els casos en que una partida acaba en taules.
	
	/**
	 * @brief Donat un color et retorna el color contrari.
	 * @pre El color actual no pot ser el neutre.
	 * @param actual Color actual.
	 * @return El color oposat a l'actual.
	 */
	public static Color canviaColor(Color actual) {
		if(actual.equals(Blanques))
			return Color.Negres;
		else
			return Color.Blanques;
	}
        
     /**
     * @brief Transforma un String al color que representa. 
     * @pre colorEnString Conté la representació en format string d'un possible valor d'aquest ennum. No pot ser el color neutre.
	 * @param colorEnString String que conte el nom del color.
	 * @return El Color al que correspon colorEnString.
	 */
	public static Color obtenirColor(String colorEnString) {
		if(colorEnString.toLowerCase().equals("blanques"))
			return Color.Blanques;
		else
			return Color.Negres;
	}
}

