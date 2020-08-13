import java.io.File;
import java.net.URISyntaxException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *  @file PecaGrafica.java
 *  @brief Classe PecaGrafica
 */

/** 
 *  @class PecaGrafica
 *  @brief Peça gràfica que s'utilitza per la interfície gràfica.
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */

public class PecaGrafica extends ImageView {
    /**
    * @brief Costat de la peca.
    */
    private int pixels;
    
    /**
    * @brief Peca representada.
    */
    private Peca peca;
    
    /**
    * @brief Coordenades del cursor.
    */
    private double mouseX, mouseY;
    
    /**
    * @brief Coordenades antigues.
    */
    private double oldX, oldY;

    /**
    * @return Retorna la peca representada.
    */
    public Peca peca() {
        return peca;
    }

    /**
    * @return Retorna la antiga coordenada X.
    */
    public double oldX() {
        return oldX;
    }
    
    /**
    * @return Retorna la antiga coordenada Y.
    */
    public double oldY() {
        return oldY;
    }

    /**
    * @brief Constructor.
    * @param peca Peca que es representara.
    * @param pixels Amplada de la icona.
    * @param x Coordenada x de la peca.
    * @param y Coordenada y de la peca.
    */
    public PecaGrafica(Peca peca, int pixels, int x, int y) {
        this.peca = peca;
	this.pixels = pixels;
        move(x,y); // posicionament de la peÃ§a a la pantalla
        try {
            setImage(generaIcona());
        }
        catch (Exception e) {
            System.out.println("Hi ha alguna imatge que no s'ha pogut carregar correctament");
        }
        setOnMousePressed((MouseEvent e) -> {  // hi afegim un EventHandler anÃ²nim (funciÃ³ lambda) 
           mouseX = e.getSceneX();
           mouseY = e.getSceneY();
        } );
        setOnMouseDragged((MouseEvent e) -> {  // EventHandler per moure la peÃ§a quan l'arrosseguem
           relocate(oldX + e.getSceneX() - mouseX, oldY + e.getSceneY() - mouseY);
        } );
    }

    /**
    * @pre x > 0 i y > 0.
    * @param x Coordenada x de on es vol moure.
    * @param y Coordenada y de on es vol moure.
    * @post Mou la peca a les coordenades indicades.
    */
    public void move(int x, int y) {
        oldX = x * pixels; 
        oldY = y * pixels;
        relocate(oldX, oldY);
    }

    /**
    * @post Mou la peca a la coordenada antiga.
    */
    public void abortMove() {
        relocate(oldX, oldY);
    }
    
    /**
    * @return La icona que es visualitza per pantalla.
    * @throws URISyntaxException Quan no s'ha trobat la imatge.
    */
    private Image generaIcona() throws URISyntaxException {
        File imageFile = null;
        switch (peca.color()) {
            case Blanques:
                imageFile = new File(peca.tipus().diguemRutaBlanca());
                return new Image(imageFile.toURI().toString());
            case Negres:
                imageFile = new File(peca.tipus().diguemRutaNegra());
                return new Image(imageFile.toURI().toString());
            default:
                return null;
        }
    }
}
