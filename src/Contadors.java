/**
 *  @file Contadors.java
 *  @brief Classe Contadors
 */

/** 
 *  @class Contadors
 *  @brief Gestiona els dos contadors per a cada color. Innacció i Escacs seguits.
 *  @author Eloi Quintana
 *  @author u1963141
 */

public class Contadors {
    
    /**
     * Contador de Inaccio de Jugador Blanc
     */
    private int contadorInaccioBlanques;

    /**
     * Contador de Inaccio de Jugador Negre
     */
    private int contadorInaccioNegres;

    /**
     * Contador de Escacs de Jugador Blanc
     */
    private int contadorEscacBlanques;

    /**
     * Contador de Enrocs de Jugador Negre
     */
    private int contadorEscacNegres;
    
    
    /**
    * @pre -
    * @Post S'han assignat els valors dels contadors ls valor 0;
    */
    public Contadors(){
        this.contadorEscacBlanques   = 0;
        this.contadorEscacNegres     = 0;
        this.contadorInaccioBlanques = 0;
        this.contadorInaccioNegres   = 0;
    }
    
    /**
    * @pre -
    * @Post S'han assignat els valors dels contadors als valors dels parametres;
    */
    public Contadors(int contadorInaccioBlanques, int contadorInaccioNegres, int contadorEscacBlanques, int contadorEscacNegres){
        this.contadorEscacBlanques   = contadorEscacBlanques;
        this.contadorEscacNegres     = contadorEscacNegres;
        this.contadorInaccioBlanques = contadorInaccioBlanques;
        this.contadorInaccioNegres   = contadorInaccioNegres;
    }
    
    /**
    * @pre El color es Blanques o Negres
    * @Post S'ha sumat/restat el valor d'entrada de Escac
    * @param val Valor en la operació.
    * @param col Color on s'aplicarà l'operació
    */
    public void modificarContadorEscac(int val, Color col){
        if(col == Color.Blanques)
            contadorEscacBlanques += val;
        else
            contadorEscacNegres += val;   
    }
    
    /**
    * @pre El color es Blanques o Negres
    * @Post S'ha sumat/restat el valor d'entrada de Inacció
    * @param val Valor en la operació.
    * @param col Color on s'aplicarà l'operació
    */
    public void modificarContadorInaccio(int val, Color col){
        if(col == Color.Blanques)
            contadorInaccioBlanques += val;
        else
            contadorInaccioNegres += val;
    }
    
    /**
    * @pre El color es Blanques o Negres
    * @Post s'ha retornat el valor del contador de Escac del color donat.
    * @return enter del contador seleccionat.
    * @param col Color on s'aplicarà l'operació
    */
    public int obtenirContadorEscac(Color col){
        if(col == Color.Blanques)
            return contadorEscacBlanques;
        else
            return contadorEscacNegres;
    }
    
    /**
    * @pre El color es Blanques o Negres
    * @Post s'ha retornat el valor del contador d'inaccio del color donat.
    * @return enter del contador seleccionat.
    * @param col Color on s'aplicarà l'operació
    */
    public int obtenirContadorInaccio(Color col){
        if(col == Color.Blanques)
            return contadorInaccioBlanques;
        else
            return contadorInaccioNegres;
    }
    
    /**
    * @pre El color es Blanques o Negres
    * @Post s'ha posat a valor 0 el contador d'escac del color donat.
    * @param col Color on s'aplicarà l'operació
    */
    public void posarAZeroContadorEscac(Color col){
        if(col == Color.Blanques)
            contadorEscacBlanques = 0;
        else
            contadorEscacNegres = 0;
    }
    
    /**
    * @pre El color es Blanques o Negres
    * @Post s'ha posat a valor 0 el contador d'inaccio del color donat.
    * @param col Color on s'aplicarà l'operació
    */
    public void posarAZeroContadorInaccio(Color col){
        if(col == Color.Blanques)
            contadorInaccioBlanques = 0;
        else
            contadorInaccioNegres = 0;
    }
    
    /**
    * @pre -
    * @Post s'ha realitzat una còpia de valors de l'objecte
    * @return retorna un nou objecte amb copia realitzada.
    */
    public Contadors deepCopy(){
        return new Contadors(contadorInaccioBlanques, contadorInaccioNegres, contadorEscacBlanques, contadorEscacNegres);
    }
    
}
