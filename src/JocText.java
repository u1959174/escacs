import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *  @file JocText.java
 *  @brief Classe JocText
 */
/** 
 *  @class JocText
 *  @brief Classe controladora del joc en mode text/terminal. Controla el flux de la partida a traves del motor i s'encarrega de demanar els moviments als jugadors.
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public class JocText{

	/**
	 * Jugador que controla les peces blanques.
	 */
	private static Jugador jugadorBlanques;

	/**
	 * Jugador que controla les peces negres.
	 */
	private static Jugador jugadorNegres;

	/**
	 * Partida del Joc, motor del joc.
	 */
	private static Partida partida;

	/**
	 * Color del jugador que té el torn.
	 */
	private static Color torn; 

	/**
	 * @brief Entrada el programa quan s'executa el mode text/terminal
	 * @param args Arguments del programa.
	 */
	public static void main(String[] args) {
		//Iniciar/Carregar Partida i Jugadors
		inicialitzarDades();
	
		//Començar/Continuar la partida
		do {
			ferPartida();
	
			//Donar avis de victoria
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
	
		} while(gestorPartidaAcabada());		
	
	
	}

	/**
	 * @pre Es poden desfer numJugades.
	 * @param numJugades Numero de jugades que es volen desfer.
	 * @post S'han desfet numJugades.
	 * @throws IllegalArgumentException quan numJugades no és un número vàlid de jugades a desfer.
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
	 * @post S'ha guardat la Partida segons el format especificat als requeriments a la ruta escollida. També s'ha donat l'opcio de cancelar el guardat.
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
	 * @return El tauler actual sobre el que es fa la partida
	 */
	public static Tauler obtenirTauler() {
		return partida.obtenirTauler();

	}


	/**
	 * @return Un booleà que indica si es poden o no desfer jugades en aquest estat de la partida.
	 */
	public static boolean potDesfer() {
		return partida.potDesferJugada() >= 1;
	}

	/**
	 * @return Un booleà que indica si es poden o no refer jugades en aquest estat de la partida.
	 */
	public static boolean potRefer() {
		return partida.potReferJugada() >= 1;
	}


	/**
	 * @pre Hi ha alguna peça promocionable del jugador amb torn.
	 * @post S'ha preguntat al jugador amb torn a quina peça es vol promocionar. S'ha retornat el tipusPeca el qual el jugador ha escollit per fer la promoció.
	 * @return El TipusPeca que ha triat el jugador que podria promocionar. Es garanteix que el tipus retornat no serà el REI.
	 */
	public static TipusPeca promocionar() {
		Jugador jugadorAmbTorn = jugadorBlanques;
		if(torn.equals(Color.Negres))
			jugadorAmbTorn = jugadorNegres;

		return jugadorAmbTorn.promocionar();
	}

	/**
	 * @pre Es poden refer numJugades.
	 * @param numJugades Numero de jugades que es volen refer.
	 * @post S'han refet numJugades.
	 * @throws IllegalArgumentException quan numJugades no és un número vàlid de jugades a refer.
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
	 * @return Un conjunt de TipusPeca amb tots els TipusPeca en aquesta partida.
	 */
	public static ArrayList<TipusPeca> tipusDisponibles(){
		ArrayList<TipusPeca> res = new ArrayList<TipusPeca>(partida.obtenirNomsTipusPeca());
		return res;
	}


	/**
	 * @param rutaFitxerConeixement Ruta al fitxer de coneixement que es vol carregar.
	 * @post S'ha carregat el coneixement a la IA si no ho ha hagut errors al buscar i carregar els fitxers. Si hi ha hagut errors s'ha informat.
	 */
	private static void carregarConeixement(String rutaConeixement){
		MotorCPU.carregarConeixement(rutaConeixement);

	}

	/**
	 * @pre rutaFitxerJugades és la ruta d'un fitxer de regles en el format especificat en l'enunciat del projecte i extensió JSON.
	 * @param rutaFitxerJugades fitxer de jugades.
	 * @throws ExcepcioG Quan el fitxer està en un estat incorrecte.
	 * @throws IOException Quan hi ha algun error al obrir el fitxer, per exemple que no es troba o no tenim els permisos.
	 * @post carrega la partida.
	 */
	private static void carregarPartida(String rutaFitxerJugades) throws IOException, ExcepcioG {
		partida = Json.crearPartidaComencada(rutaFitxerJugades);
		torn = partida.torn();
	}

	/**
	 * @brief Es demana quins jugadors jugaran la partida, els seus noms i qui els controla.
	 * @post S'ha demanat quin tipus de jugador controlarà cada jugador.
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
					scaner.nextLine();
				}

			}

		}

	}

	/**
	 * @brief Controlador del joc. Va preguntant als jugadors a cada torn què volen fer i va ordenant al motor les seves peticions.
	 * @post La partida s'ha acabat.
	 */
	private static void ferPartida() {

		while(!partida.acabada()) {
			System.out.println(partida.tauler());
			torn = partida.torn();
			Jugador jugadorAmbTorn = jugadorBlanques;
			Jugador jugadorSenseTorn = jugadorNegres;
			if(torn.equals(Color.Negres)) {
				jugadorAmbTorn = jugadorNegres;
				jugadorSenseTorn = jugadorBlanques;
			}
			Jugada jugada = jugadorAmbTorn.jugar(true);
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
				//CAS: S'ha retornat un resultat això vol dir que s'ha ajornat, hi ha hagut rendicio etc.
				ResultatJugada resultatJugada = conjuntResultats.iterator().next(); 
				try {
					switch(resultatJugada) {
					case Ajornament:
						partida.ferJugada(ResultatJugada.Ajornament);
						guardarPartida();
						break;
					case Rendicio:
						partida.ferJugada(ResultatJugada.Rendicio);
						break;
					case TAcceptades:
						partida.ferJugada(ResultatJugada.TAcceptades);
						break;
					case TSollicitades:
						partida.ferJugada(ResultatJugada.TSollicitades);
						jugadorSenseTorn.preguntarTaules();
						break;
					case TRebutjades:
						partida.canviarTorn(Color.canviaColor(partida.torn()));
						break;
					default:
						System.err.println("[ERROR]: El jugador ha retornat una jugada invàlida.");
						break;
					}
				}
				//CAS altrament s'han defet o refet jugades, no cal tractar res ja que s'ha fet en un altre mètode (desfer o refer).
				catch(ExcepcioG e1) {
					System.out.println("[ERROR]: " + e1.getTipus().toString());

				}

			}

		}	
	}

	/**
	 * @brief Fa la gestió del les opcions que es poden triar quan una partida està acabada.
	 * @post S'ha gestionat una partida que ha finalitzat. L'usuari ha tingut l'opcio de defer jugades, guardar i sortir. En el cas de que continuar sigui una opció vàlida, també s'ha ofert aquesta opció.
	 * @return Un booleà que serà true quan el jugador ha desfet jugades i, per tant vol continuar amb la partida. Altrament false.
	 */
	private static boolean gestorPartidaAcabada() {
		boolean res = false;
		boolean sortirMenu = false;
		Scanner scanner = new Scanner(System.in);
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
						torn = partida.torn();
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
		return res;
	}


	/**
	 * @post S'han demanat al jugador les dades per començar la partida i aquestes s'han carregat.
	 * @version 2. S'afegeix l'opció de sortir del programa.
	 * @version 3. S'afegeix l'opcio de carregar coneixament.
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

					carregarConeixement(rutaConeixement);
					System.out.println("S'ha finalitzat la càrrega de dades!");

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

			//Finalment demanem els jugadors

		}
		demanarJugadors();
	}

	/**
	 * @pre rutaFitxerConfig és la ruta d'un fitxer de regles en el format especificat en l'enunciat del projecte i extensió JSON.
	 * @param rutaFitxerConfig fitxer de configuració.
	 * @throws ExcepcioG Quan el fitxer està en un estat incorrecte.
	 * @throws IOException Quan hi ha algun error al obrir el fitxer, per exemple que no es troba o no tenim els permisos.
	 * @post inicialitza la partida.
	 */
	private static void novaPartida(String rutaFitxerConfig) throws IOException, ExcepcioG {
		partida = Json.crearPartidaNova(rutaFitxerConfig);
		torn = Color.Blanques;		

	}
}
