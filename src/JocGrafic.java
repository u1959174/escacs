import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.InputMismatchException;
import static javafx.application.Application.launch;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *  @file JocGrafic.java
 *  @brief Classe JocGrafic
 */

/** 
 *  @class JocGrafic
 *  @brief Classe que gestiona la interfície gràfica del programa.
 *  @author Marc Cosgaya Capel
 *  @author u1959174
 */

public class JocGrafic extends Application {
    
    /**
    * @brief El conjunt de rajoles que s'han de representar.
    */
    private static Group rajoles = new Group();
    
    /**
    * @brief El conjunt de peces que s'han de representar.
    */
    private static Group peces = new Group();
    
    /**
    * @brief El nombre de pixels que fa cada rajola de costat.
    */
    private static int costatRajola;
    
    /**
    * @brief El tauler actual que s'ha de representar.
    */
    private static Tauler tauler_actual;
    
    /**
    * @brief L'escenari actual.
    */
    private static Stage escenari;
    
   /**
    * @brief Mètode d'entrada al programa.
    * @param args Arguments passats per paràmetre.
    * @post S'ha executat el programa en mode gràfic.
    */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        Parameters parameters = getParameters();
        List<String> rawArguments = parameters.getRaw();
        costatRajola = 50;
        
        inicialitzarDades();
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(crearContingut());
        escenari = primaryStage;
        escenari.setTitle("JOC D'ESCACS");
        escenari.setScene(scene);
        escenari.show();
        System.out.println("PER OPCIONS MOURE UNA FITXA AL MATEIX LLOC ON ESTA");
        if (jugadorBlanques instanceof CPU || jugadorNegres instanceof CPU) System.out.println("SI ES JUGA AMB CPU: AL TORN DE LA IA MOURE QUALSEVOL PEÇA");
        try { TimeUnit.SECONDS.sleep(3); }
        catch (InterruptedException e) { System.out.println("Interrupcio rebuda, saltant espera"); }
        mostrarEstatConsola();
        if (jugadorBlanques instanceof CPU) demanarAltres(jugadorBlanques); //CPU primer torn
    }
    
    /**
    * @brief Genera el contingut, rajoles i peces, a representar.
    * @pre fons1.png i fons2.png existents.
    * @return El contingut amb les rajoles i peces segons el tauler actual.
    */
    private static Parent crearContingut() {
        Image imatge1 = null;
        Image imatge2 = null;
        try {
    	    imatge1 = new Image("fons1.png");
            imatge2 = new Image("fons2.png");
    	}
    	catch (Exception e) {
    	    System.err.println("ERROR: imatge fons no trobada!");
    	    System.exit(-1);
    	}
        
        Pane root = new Pane();
        root.setPrefSize(partida.dimensions().first * costatRajola, partida.dimensions().second * costatRajola);
        root.getChildren().addAll(rajoles, peces); // podem afegir els grups encara que estiguin buits
        tauler_actual = partida.obtenirTauler();
        //rajoles
        for (int i = 1; i <= partida.dimensions().second; i++) { //files
            for (int j = 1; j <= partida.dimensions().first; j++) { //columnes
                Rajola rajola = null;
                if ((i % 2 != 0 && j % 2 != 0) || (i % 2 == 0 && j % 2 == 0)) rajola = new Rajola(imatge2,costatRajola);
                else rajola = new Rajola(imatge1,costatRajola);
                rajola.setX((j-1) * costatRajola);
                rajola.setY((i-1) * costatRajola);
                rajoles.getChildren().add(rajola); // els elements es visualitzaran en l'ordre en que s'afegeix
            }
        }
        //peces
        for (int i = 1; i <= partida.dimensions().second; i++) { //files
            for (int j = 1; j <= partida.dimensions().first; j++) { //columnes
                Peca f = tauler_actual.tensPeca(new Posicio(i,j));
                if (f != null) { // a la rajola hi ha alguna fitxa
                    PecaGrafica fitxa = creaPeca(f,j,invertirY(i)+1);
                    peces.getChildren().add(fitxa); // canviar
                }
            }
        }
        return root;
    }
    
    /**
    * @brief Crea la peca a representar.
    * @pre fons1.png i fons2.png existents.
    * @return La PecaGrafica amb les coordenades i peca real corresponents
    * amb els gestors d'esdeveniments.
    * @param peca Peca real a representar.
    * @param x Coordenada x de la peca.
    * @param y Coordenada y de la peca.
    */
    private static PecaGrafica creaPeca(Peca peca, int x, int y) {
        PecaGrafica p = new PecaGrafica(peca,costatRajola,x-1,y-1);
        p.setOnMouseReleased((MouseEvent e) -> {
            int newX = posTauler(p.getLayoutX());
            int newY = posTauler(p.getLayoutY());
            Posicio pNew = new Posicio(newY,newX);
            int oldX = posTauler(p.oldX());
            int oldY = posTauler(p.oldY());
            Posicio pOld = new Posicio(oldY,oldX);
            if (!aplicaMoviment(pOld,pNew)) p.abortMove();
        });
        return p;
    }

    /**
    * @return Fila o columna corresponent a pixel. 
    * @param pixel Pixel d'entrada.
    */
    private static int posTauler(double pixel) {
        return (int)(pixel + costatRajola / 2) / costatRajola;
    }

    /**
    * @brief Aplica el moviment d'una peca.
    * @note En el cas de que el jugador actual sigui una CPU, farà el seu
    * moviment.
    * @note Si origen == desti s'obre el menu d'opcions extra per consola.
    * @return Cert si s'ha fet un moviment, fals altrament.
    * @param origen Posicio d'origen.
    * @param desti Posicio de desti.
    */
    private static boolean aplicaMoviment(Posicio origen, Posicio desti) {
        try {
            if (jugadorBlanques instanceof CPU && partida.torn() == Color.Blanques) {
                demanarAltres(jugadorBlanques);
                return true;
            }
            else if (jugadorNegres instanceof CPU && partida.torn() == Color.Negres) {
                demanarAltres(jugadorNegres);
                return true;
            }
            else if (origen.equals(desti)) { //demanar opcions
                if (partida.torn().equals(Color.Blanques)) demanarAltres(jugadorBlanques);
                else if (partida.torn().equals(Color.Negres)) demanarAltres(jugadorNegres);
                return true;
            }
            else {
                Posicio nou_origen = new Posicio(invertirY(origen.fila()),origen.columna()+1);
                Posicio nou_desti = new Posicio(invertirY(desti.fila()),desti.columna()+1);
                String string_anterior = partida.obtenirTauler().toString();
                tauler_actual = partida.ferJugada(nou_origen,nou_desti);
                if (partida.acabada() && !gestorPartidaAcabada()) System.exit(0);
                actualitzarTaulerGrafic();
                mostrarEstatConsola();
                if (partida.torn().equals(Color.Blanques) && jugadorBlanques instanceof CPU) demanarAltres(jugadorBlanques);
                else if (partida.torn().equals(Color.Negres) && jugadorNegres instanceof CPU) demanarAltres(jugadorNegres);
                return !string_anterior.equals(tauler_actual.toString());
            }
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
    
    /**
    * @brief Demana les opcions i actualitza el tauler grafic.
    * @note Es pot acabar l'execució si s'ha acabat la partida.
    * @param jugador El jugador que demana les opcions.
    * @return La jugada que s'ha fet
    */
    private static Jugada demanarAltres(Jugador jugador) {
        Jugada jugada = null;
        jugada = jugador.jugar(false);
        if (jugada == null) {
            actualitzarTaulerGrafic();
            mostrarEstatConsola();
            return jugada;
        }
        Collection<ResultatJugada> conjuntResultats = jugada.obtenirResultatsJugada();
        if(conjuntResultats.size() == 0 && jugada.obtenirOrigen() != null) {
            //CAS: S'ha fet un moviment.
            try {
                partida.ferJugada(jugada.obtenirOrigen(),jugada.obtenirDesti());
            }
            catch(ExcepcioG e1) {
                System.out.println("[ERROR]: " + e1.getTipus().toString());
            }
        }
        else if(conjuntResultats.size() != 0){
            //CAS: S'ha retornat un resultat aixi vol dir que s'ha ajornat, hi ha hagut rendicio etc.
            ResultatJugada resultatJugada = conjuntResultats.iterator().next(); 
            try {
                switch(resultatJugada) {
                    case Ajornament:
                        partida.ferJugada(ResultatJugada.Ajornament);
                        guardarPartida();
                        System.out.println(partida.obtenirResultatPartidaDetall());
                        System.exit(0);
                    case Rendicio:
                        partida.ferJugada(ResultatJugada.Rendicio);
                        break;
                    case TAcceptades:
                        partida.ferJugada(ResultatJugada.TAcceptades);
                        break;
                    case TSollicitades:
                        partida.ferJugada(ResultatJugada.TSollicitades);
                        if (partida.torn().equals(Color.Blanques)) {
                            jugadorNegres.preguntarTaules();
                            return demanarAltres(jugadorNegres);
                        }
                        else if (partida.torn().equals(Color.Negres)) {
                            jugadorBlanques.preguntarTaules();
                            return demanarAltres(jugadorBlanques);
                        }
                        else throw new IllegalStateException("Torn indefinit.");
                    case TRebutjades:
                        partida.canviarTorn(Color.canviaColor(partida.torn()));
                        break;
                    default:
                        System.err.println("[ERROR]: El jugador ha retornat una jugada invalida.");
                        break;
                }
            }
            //CAS altrament s'han defet o refet jugades, no cal tractar res ja que s'ha fet en un altre metode (desfer o refer).
            catch(ExcepcioG e) {
                e.printStackTrace();
            }
        }
        if (partida.acabada() && !gestorPartidaAcabada()) System.exit(0);
        actualitzarTaulerGrafic();
        mostrarEstatConsola();
        return jugada;
    }
    
    /**
    * @brief Inversió de coordenada en l'eix de les Y.
    * @param n Nombre a invertir.
    * @return El nombre entrat invertit.
    */
    private static int invertirY(int n) {
        return partida.dimensions().second-n;
    }
    
    /**
    * @brief Mostra l'estat de la partida grafica per consola.
    */
    private static void mostrarEstatConsola() {
        System.out.println(partida.tauler());
        System.out.println("Torn:"+partida.torn());
    }
    
    /**
    * @brief Torna a generar el tauler gràfic.
    */
    private static void actualitzarTaulerGrafic() {
        rajoles.getChildren().clear();
        peces.getChildren().clear();
        Scene scene = new Scene(crearContingut());
        escenari.setScene(scene);
    }
    
    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    private static Partida partida;
    
    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    private static Jugador jugadorBlanques;
    
    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    private static Jugador jugadorNegres; 
    
    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    private static void carregarConeixement(String rutaConeixement) throws IOException, ExcepcioG {
        MotorCPU.carregarConeixement(rutaConeixement);
    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    private static void novaPartida(String rutaFitxerConfig) throws IOException, ExcepcioG {
        partida = Json.crearPartidaNova(rutaFitxerConfig);
    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    private static void carregarPartida(String rutaFitxerJugades) throws IOException, ExcepcioG {
        partida = Json.crearPartidaComencada(rutaFitxerJugades);
        if (partida.acabada() && !gestorPartidaAcabada()) {
            System.out.println(partida.obtenirResultatPartidaDetall());
            System.exit(0);
        }
    }
    
    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    public static void guardarPartida() {
        boolean acabat = false;
        Scanner scaner = new Scanner(System.in);
        while(!acabat) {
            System.out.println("[Gestor de guardat]: Indica la ruta de guardat completa. ex: \"E:\\profProPro\\millorsAlumnes\\grupA4\\jugades.json\"");
            System.out.println("[Gestor de guardat]: Indica \"cancelar\" per cancelar el guardat");
            String rutaGuardat = scaner.nextLine();
            if(!rutaGuardat.contains("cancelar")) {
                try {
                    Json.guardarPartida(partida, rutaGuardat);
                    System.out.println("[Gestor de guardat]: S'ha guardat la partida correctament.");
                    acabat = true;
                }
                catch(IOException e) {
                    System.out.println("[ERROR]: Hi ha hagut un error al guardar la partida. Comprova que el programa té els permisos!");
                }
                catch(ExcepcioG e) {
                    System.out.println("[ERROR]: " + e.getTipus().toString());
                }
            }
            else {
                System.out.println("[Gestor de guardat]: S'ha cancelat el guardat de la partida.");
                acabat = true;
            }
        }
    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    private static void demanarJugadors() {
        Scanner scaner = new Scanner(System.in);
        for (int i = 0; i < 2; i++) {
            boolean acabat = false;
            while(!acabat) {
                System.out.println("Entra les dades del jugador " + (i+1) + System.lineSeparator());
                System.out.println("1-Humà");
                System.out.println("2-CPU");
                try {
                    int select = scaner.nextInt();
                    scaner.nextLine();
                    System.out.println("Entra el nom del jugador " + (i+1) + System.lineSeparator());
                    String nom = scaner.nextLine();

                    //HUMA
                    if(select == 1) {
                        if(i == 0) {
                            jugadorBlanques = new Huma(nom);
                            acabat = true;
                        }else {
                            jugadorNegres = new Huma(nom);
                            acabat = true;
                        }
                    }

                    //CPU
                    else if (select == 2){
                        if(i == 0) {
                            jugadorBlanques = new CPU(nom,Color.Blanques);
                            acabat = true;
                        }else {
                            jugadorNegres = new CPU(nom,Color.Negres);
                            acabat = true;
                        }
                    }
                    else {
                        System.out.println("[ERROR]: Opció desconeguda.");
                        acabat = false;
                    }
                }
                catch(InputMismatchException e) {
                    System.out.println("[ERROR]: S'esperava un número.");
                }
            }
        }
    }


    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    public static boolean potDesfer() {
        return partida.potDesferJugada() >= 1;
    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    public static boolean potRefer() {
        return partida.potReferJugada() >= 1;
    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    public static void desferJugada(int numJugades) throws IllegalArgumentException{
        if(partida.potDesferJugada() >= numJugades && numJugades >= 1) {
            System.out.println("Desfent jugades...");
            for(int i = 1; i <= numJugades; i++) {
                partida.desferJugada();
            }
            System.out.println("S'han desfet correctament les jugades!");
            System.out.println(partida.tauler());
        }
        else if(numJugades < 1){
            throw new IllegalArgumentException("[ERROR]:No es pot desfer/refer menys d'una jugada");
        }
        else {
            throw new IllegalArgumentException("[ERROR]:El nombre màxim de jugades que es poden desfer és: " + partida.potDesferJugada());
        }
    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    public static void referJugada(int numJugades) throws IllegalArgumentException{
        if(partida.potReferJugada() >= numJugades && numJugades >= 1) {
            System.out.println("Refent jugades...");
            for(int i = 1; i <= numJugades; i++) {
                partida.referJugada();
            }
            System.out.println("S'han refet correctament les jugades!");
            System.out.println(partida.tauler());
        }
        else if(numJugades < 1){
            throw new IllegalArgumentException("[ERROR]:No es pot desfer/refer menys d'una jugada");
        }
        else {
            throw new IllegalArgumentException("[ERROR]:El nombre màxim de jugades que es poden refer és: " + partida.potReferJugada());
        }
    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    public static TipusPeca promocionar() {
        Jugador jugadorAmbTorn = jugadorBlanques;
        if(partida.torn().equals(Color.Negres)) jugadorAmbTorn = jugadorNegres;
        return jugadorAmbTorn.promocionar();
    }


    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    public static ArrayList<TipusPeca> tipusDisponibles(){
        ArrayList<TipusPeca> res = new ArrayList<TipusPeca>(partida.obtenirNomsTipusPeca());
        return res;
    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    public static Tauler obtenirTauler() {
        return partida.obtenirTauler();

    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    private static boolean gestorPartidaAcabada() {
        boolean res = false;
        boolean sortirMenu = false;
        Scanner scanner = new Scanner(System.in);
        
        Color colorGuanyador = partida.guanyador();
        System.out.println("Partida Acabada!!");

        Jugador jugadorGuanyador = jugadorBlanques;
        if(colorGuanyador.equals(Color.Negres))
            jugadorGuanyador = jugadorNegres;

        if(!colorGuanyador.equals(Color.capColor))
            System.out.println("Felicitats " + jugadorGuanyador + "!! Has guanyat!");
        else {
            System.out.println("No hi ha hagut guanyador perque la partida ha acabat degut a " + partida.obtenirResultatPartidaDetall());
        }
        
        while(!sortirMenu) {
            System.out.println(System.lineSeparator()+"La partida HA ACABAT. Indica què vols fer:");
            System.out.println("1- Guardar la partida");
            System.out.println("2- Desfer Jugades (es continuarà la partida)");
            System.out.println("3- Sortir del programa");
            if(partida.potContinuar()) {
                System.out.println("4- Continuar la partida.");
            }
            try {
                int select = scanner.nextInt();
                scanner.nextLine();
                switch(select) {

                    //Guardar Partida
                    case 1:
                        guardarPartida();
                        sortirMenu =  false;
                        break;

                    //Desfer Jugades
                    case 2:
                        sortirMenu = Huma.desferJugada();
                        res = sortirMenu;
                        break;
                    //Sortir
                    case 3:
                        sortirMenu =  true;
                        res = false;
                        break;
                    //Continuar
                    case 4:
                        if(partida.potContinuar()) {
                                desferJugada(1);
                                res = true;
                                sortirMenu = true;
                        }
                        else
                                System.out.println("[ERROR]: Opció desconeguda");
                        break;
                    //ERROR INPUT
                    default:
                        res = false;
                        sortirMenu = false;
                        System.out.println("[ERROR]: Opció desconeguda");
                        break;
                }
            }
            catch(InputMismatchException e) {
                System.out.println("[Error]: S'esperava un número");
                scanner.nextLine();
            }
        }
        if (!res) {
            Color guanyador = partida.guanyador();
            System.out.println("Felicitats " + guanyador + "!! Has guanyat!");
        }
        return res;
    }

    /**
    * @brief Adaptacio de JocText, es un placeholder per despres fer els menus
    * corresponents en grafic. Per la documentacio buscar-ho a JocText.
    */
    private static void inicialitzarDades() {
        Scanner scan = new Scanner(System.in);
        boolean carregat  = false;
        while(!carregat) {
            System.out.println("Indica que vols fer:");
            System.out.println("1-Iniciar una nova partida");
            System.out.println("2-Carregar una partida previa");
            System.out.println("3-Reforçar aprenentatge");
            System.out.println("4-Sortir");
            try {
                int select = scan.nextInt();
                scan.nextLine();
                switch (select) {

                //NOVA PARTIDA.
                case 1:
                    System.out.println("Iniciant una nova partida...");
                    System.out.println("Indica la ruta del fitxer de configuració");

                    String rutaFitxerConfig = scan.nextLine();
                    try {
                            novaPartida(rutaFitxerConfig);
                            carregat = true;
                    }
                    catch(IOException e) {
                            System.out.println("[ERROR]: Hi ha hagut un error al buscar el fitxer de regles. Comprova que el programa té els permisos!");
                    }
                    catch(ExcepcioG e) {
                            System.out.println("[ERROR]: " + e.getTipus().toString());
                    }
                    break;
                    //CARREGAR PARTIDA PRÈVIA
                case 2:
                    System.out.println("Indica la ruta del fitxer de guardat");
                    String rutaFitxerPartidaPrevia = scan.nextLine();
                    try {
                        carregarPartida(rutaFitxerPartidaPrevia);
                        carregat = true;
                    }
                    catch(IOException e) {
                        System.out.println("[ERROR]: Hi ha hagut un error al buscar el fitxer de jugades. Comprova que el programa té els permisos!");
                    }
                    catch(ExcepcioG e) {
                        System.out.println("[ERROR]: " + e.getTipus().toString());
                    }
                    break;
                    //NOVES DADES
                case 3:
                    System.out.println("Indica la ruta del fitxer de dades");
                    String rutaConeixement = scan.nextLine();
                    try {
                        carregarConeixement(rutaConeixement);
                        System.out.println("S'han carregat les dades correctament!");
                    }
                    catch(IOException e) {
                        System.out.println("[ERROR]: Hi ha hagut un error al buscar el fitxer de jugades. Comprova que el programa té els permisos!");
                    }
                    catch(ExcepcioG e) {
                        System.out.println("[ERROR]: " + e.getTipus().toString());
                    }
                    carregat = false;
                    break;
                    //SORTIR DEL PROGRAMA
                case 4:
                    System.out.println("Sortint del programa...");
                    System.exit(0);
                    break;
                    //ERROR INPUT
                default:
                    System.out.println("Error, input erroni!");
                }
            }
            catch(InputMismatchException e) {
                System.out.println("[Error]: S'esperava un número");
                scan.nextLine();
            }
        }
        //Finalment demanem els jugadors
        demanarJugadors();
    }
}