import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 *  @file Fitxer.java
 *  @brief Classe Fitxer
 */

/** 
 *  @class Fitxer
 *  @brief Realitza tota la lectura de qualevol fitxer.
 *  @author Eloi Quintana Ferrer
 *  @author u1963141
 */

public final class Fitxer {
    
    /**
     * Indica la ruta del fitxer en questio
     */
    public final String nom;
    
    /**
     * Indica el contingut del fitxer
     */
    public String contingut;
    
    /**
     * @pre -
     * @Post s'ha inicialitzat el fitxer
     * @param nom ruta del fitxer a llegir
     * @param llegir true si s'ha de llegir el fitxer, false altrament
     * @return String contingut del fitxer
     */
    public Fitxer(String nom, boolean llegir) throws IOException{
        this.nom = nom;
        if(llegir){
            this.contingut = Llegir();
        }
        
    }
    
    /**
     * @pre -
     * @Post s'ha realitzat la lectura del fitxer
     * @return String contingut del fitxer
     */
    public String Llegir() throws IOException{
        contingut = llegirFitxer(nom);
        return contingut;
    };
    
    /**
     * @pre contingut != ""
     * @Post s'ha escrit al fitxer el contingut passat
     * @param contingut contingut a guardar
     */
    public void Escriure(String contingut) throws IOException{
        this.contingut = contingut;
        escriureFitxer(nom,contingut);
    };
    
    /**
     * @pre fitxer != null
     * @Post Retorna el fitxer demanat en format String.
     * @param fitxer Nom del fitxer a obtenir.
     * @return String : Contingut del fitxer
     */
    private static String llegirFitxer(String fitxer) throws IOException {
            //BufferedReader reader = new BufferedReader(new FileReader(new File(fitxer),"UTF8"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fitxer), "UTF-8"));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * @pre fitxer != null
     * @Post Reescriu/Crea un fitxer amb contingut del parametre
     * @param fitxer Nom del fitxer a obtenir.
     * @param contingut Nom del fitxer a obtenir.
     */
    private void escriureFitxer(String fitxer, String contingut) throws IOException {
            File myFoo = new File(fitxer);
            FileWriter fooWriter = new FileWriter(myFoo, false);                                          
            fooWriter.write(contingut);
            fooWriter.close();
    }
}
