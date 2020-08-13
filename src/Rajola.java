import javafx.scene.image.ImageView;

/**
 *  @file Rajola.java
 *  @brief Classe Rajola
 */

/** 
 *  @class Rajola
 *  @brief Rajola gràfica que s'utilitza per la interfície gràfica.
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */

public class Rajola extends ImageView {
	
	/**
         * @brief Constructor de Rajola.
         * @param imatge Imatge que representa la rajola.
	 * @param amplada Número de pixels que tindrà la rajola.
	 */
	public Rajola(javafx.scene.image.Image imatge, int amplada) {
            setImage(imatge);
            setFitWidth(amplada);
            setPreserveRatio(true);
            setSmooth(true);
            setCache(true);
	}
}
