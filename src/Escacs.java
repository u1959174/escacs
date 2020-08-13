import javafx.application.Application;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *  @file Escacs.java
 *  @brief Classe Escacs
 */

/** 
 *  @class Escacs
 *  @brief Classe d'entrada al programa. Pregunta en quin mode es vol executar el joc: la versio gràfica o text/terminal.
 *  @note També conté els mètodes generals que tenen els dos modes per tal de "generalitzar" els missatges(crides a mètodes) al mode.
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */

public class Escacs {

	/**
	 * @brief Booleà que indica quin mode s'ha inicialitzat. True-> Text, False->Grafic. Per defecte es deixa en mode terminal.
	 */
	private static boolean mode = true;
	
	/**
	 * @pre Es poden desfer numJugades.
	 * @post S'han desfet numJugades.
	 * @param numJugades Número de jugades que es volen desfer.
	 * @throws IllegalArgumentException Quan numJugades no és un número vàlid de jugades a desfer.
	 */
	public static void desferJugada(int numJugades) throws IllegalArgumentException{
		if(mode) {
			JocText.desferJugada(numJugades);
		}
		else {
			JocGrafic.desferJugada(numJugades);
		}
	}
	
	/**
	 * @post S'ha guardat la Partida segons el format especificat als requeriments a la ruta escollida. També s'ha donat l'opcio de cancelar el guardat.
	 */
	public static void guardarPartida() {
		if(mode) {
			JocText.guardarPartida();
		}
		else {
			JocGrafic.guardarPartida();
		}
	}
	
	/**
	 * @brief Mètode d'entrada al programa. Demana en quin mode es vol executar el joc i l'executa.
	 * @param args Arguments passats per paràmetre.
	 * @post S'ha executat el programa en el mode seleccionat per terminal.
	 */
	public static void main(String[] args) {
		System.out.println("[ESCACS]: BENVINGUT SIGUIS AL JOC DELS ESCACS {VERSIO FINAL}");
		System.out.println("[ESCACS]: Programa fet per Marc Cosgaya, Martí Madrenys i Eloi Quintana"+ System.lineSeparator());

		System.out.println("[ESCACS]: Indica en quin mode vols executar el joc: 1-Terminal | 2-Gràfic | 3-Sortir");
		boolean acabat = false;
		Scanner scanner = new Scanner(System.in);
		while(!acabat) {
			try {
				int seleccio = scanner.nextInt();
				scanner.nextLine();
				if(seleccio == 1) {
					mode = true;
					acabat = true;
				}
				else if(seleccio == 2) {
					mode = false;
					acabat = true;
				}
				else if(seleccio == 3) {
					System.out.println("Adeu!");
					System.exit(0);
				}
				else {
					System.out.println("[ERROR]: S'esperava un número.");
					acabat = false;
				}
			}
			catch(InputMismatchException e) {
				System.out.println("[ERROR]: S'esperava un número.");
				scanner.nextLine();
			}
		}
		if(!mode) {
			
			Application.launch(JocGrafic.class, args);
			return;
		}
		else {
			
			JocText.main(args);
		}
	}
	
	/**
	 * @return El tauler actual sobre el que es fa la partida.
	 */
	public static Tauler obtenirTauler() {
		if(mode) {
			return JocText.obtenirTauler();
		}
		else {
			return JocGrafic.obtenirTauler();
		}
	}
	
	/**
	 * @return Un booleà que indica si es poden o no desfer jugades en aquest estat de la partida.
	 */
	public static boolean potDesfer() {
		if(mode) {
			return JocText.potDesfer();
		}
		else {
			return JocGrafic.potDesfer();
		}
	}
	
	/**
	 * @return Un booleà que indica si es poden o no refer jugades en aquest estat de la partida.
	 */
	public static boolean potRefer() {
		if(mode) {
			return JocText.potRefer();
		}
		else {
			return JocGrafic.potRefer();
		}
	}
	
	/**
	 * @pre Hi ha alguna peça promocionable del jugador amb torn.
	 * @post S'ha preguntat al jugador amb torn a quina peça es vol promocionar. S'ha retornat el tipusPeca el qual el jugador ha escollit per fer la promoció.
	 * @return El TipusPeca que ha triat el jugador que podria promocionar. Es garanteix que el tipus retornat no serà el REI.
	 */
	public static TipusPeca promocionar() {
		if(mode) {
			return JocText.promocionar();
		}
		else {
			return JocGrafic.promocionar();
		}
	}
	
	/**
	 * @pre Es poden refer numJugades.
	 * @param numJugades Número de jugades que es volen refer.
	 * @post S'han refet numJugades.
	 * @throws IllegalArgumentException Quan numJugades no és un número vàlid de jugades a refer.
	 */
	public static void referJugada(int numJugades) throws IllegalArgumentException{
		if(mode) {
			JocText.referJugada(numJugades);
		}
		else {
			JocGrafic.referJugada(numJugades);
		}
	}
	
	/**
	 * @return Un conjunt de TipusPeca amb tots els TipusPeca en aquesta partida.
	 */
	public static ArrayList<TipusPeca> tipusDisponibles(){
		if(mode) {
			return JocText.tipusDisponibles();
		}
		else {
			return JocGrafic.tipusDisponibles();
		}
	}
}
	

