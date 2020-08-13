import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
/**
 *  @file Huma.java
 *  @brief Classe Huma
 */

/** 
 *  @class Huma
 *  @brief Classe que representa un Jugador controlat per un Humà. Fa les interaccions entre un Humà i el contolador a través del terminal.
 *  @author Martí Madrenys Masferrer
 *  @author u1953866
 */
public class Huma implements Jugador {

	/**
	 * @post S'ha demanat al Jugador si quantes jugades vol desfer i s'ha passat la petició al controlador. Si la petició de desfer jugades és vàlida es retorna true, altrament false.
	 * @return Un booleà que indica si la petició s'ha realitzat.
	 */
	public static boolean desferJugada() {
		Scanner scanner = new Scanner(System.in);
		int numJugades;
		
		System.out.println("Quantes jugades vols desfer?");
		
		try {numJugades = scanner.nextInt();}
		catch(InputMismatchException e) {
			System.out.println("[ERROR]Format incorrecte, s'esperava un enter positiu.");
			return false;
		}
		finally {scanner.nextLine();}
		
		try {
			Escacs.desferJugada(numJugades);
			return true;
		}
		catch(IllegalArgumentException e) { 
			System.out.println(e.getMessage());
			return false;
		}
		
		
	}
	
	/**
	 * Nom del jugador
	 */
	private String nom;
	
	/**
	 * Indica si el jugador té pendent decidir si accepta o no taules.
	 */
	private boolean taulesPendentsAcceptacio;

	public Huma(String nom) {
		this.nom = nom;
		taulesPendentsAcceptacio = false;
	}

	@Override
	public Jugada jugar(boolean modeText) {
		Jugada res = new Jugada(null, null, null); //Jugada Buida
		Scanner scanner = new Scanner(System.in);
		
		//CAS: Tenim taules pendents.
		if(taulesPendentsAcceptacio) {
			
			///////////////////////////////
			/*Tractem les taules pendents*/
			///////////////////////////////
			
			res = gestionarTaulesPendents(modeText);
			return res;
		}
		
		//CAS: No hi han taules pendents
		boolean acabat = false;
		while(!acabat) {
			
			///////////////////
			/*MOSTRAR OPCIONS*/
			///////////////////
			
			mostrarOpcions(modeText);
			
			/////////////////
			/*LLEGIR INPUT */
			/*I EXECUTAR-LO*/
			/////////////////
			
			int select;
			try {
				select = scanner.nextInt();
			}
			catch(InputMismatchException e) {
				
				select = 999;
			}
			finally {
				scanner.nextLine();
			}
			switch(select) {
			
			//Jugar(text) o Retornar(grafic)
			case 1:
				try {
					if(modeText) {
						res =  ferJugada(); //si juguem ja podem retornar.
					}
					else {
						res = null;//En mode gràfic 1 =  retornar del menu text. Retornem null doncs no s'ha fet cap jugada.
					}
					acabat = true;
				}catch(IllegalArgumentException e) {
					System.out.println("Error de format de la posicio!");
					acabat = false;
				}
				catch(StringIndexOutOfBoundsException e) {
					System.out.println("Error de format de la posicio!");
					acabat = false;
				}
				break;
			
			//Rendir-se
			case 2:
				res = rendirse();
				acabat = true;
				break;	
				
			//Demanar taules
			case 3:
				res = demanarTaules();
				acabat = true;
				break;
				
			//Guardar partida
			case 4:	
				guardarPartida();
				acabat = false;
				break;
				
			//Desfer jugada
			case 5:
				if(Escacs.potDesfer())
					acabat = desferJugada();
				else {
					acabat = false;
					System.out.println("Error de selecció" + System.lineSeparator());		
				}
				break;
				
			//Refer jugada
			case 6: 
				if(Escacs.potRefer())
					acabat = referJugada();
				else {
					acabat = false;
					System.out.println("Error de selecció" + System.lineSeparator());		
				}
				
				break;
				
			//Ajornar partida	
			case 7: 
				res =  ajornarPartida();
				acabat = true;
				break;
				
			//Ajornar partida
			case 8: 
				res =  acceptarTaules();
				acabat = true;
				break;
			
			//Input desconegut.
			default:
				System.out.println("Error de selecció" + System.lineSeparator());
				acabat = false;
				break;

			}
		}
		return res;
	}

	@Override
	public void preguntarTaules() {
		this.taulesPendentsAcceptacio = true;
	}

	@Override
	public TipusPeca promocionar() {
		System.out.println("Jugador " + nom +" pots promocionar una peça, tria quin tipus de peça nova vols!");
		ArrayList<TipusPeca> tipusPerTriar = Escacs.tipusDisponibles();
		boolean acabat = false;
		TipusPeca res = null;
		
		while(!acabat) {
			int i = 0;
			int posRei = 0;
			for(TipusPeca tipusAct : tipusPerTriar) {
				if(!tipusAct.toString().contains("REI")) {
					System.out.println(i+"- " + tipusAct.toString());
					i++;
				}
				else {
					posRei = i;
				}
			}
			Scanner scanner =  new Scanner(System.in);
			int seleccio =  scanner.nextInt();
			scanner.nextLine();
			if(seleccio < 0 || seleccio >= tipusPerTriar.size()) {
				System.out.println("[ERROR]: El número entrat no coincideix amb cap Tipus, repeteix la seleccio");
			}
			else {
				acabat = true;
				if(seleccio > posRei)
					seleccio++;
				res = tipusPerTriar.get(seleccio);
			}
		}
		return res;
	}

	@Override
	public String toString() {
		return this.nom;
	}

	/**
	 * @post S'ha retornat una jugada que indica que les taules s'han rebutjat.
	 * @return Una jugada que té com a resultat TRebutjades.
	 */
	private Jugada acceptarTaules() {
		return new Jugada(ResultatJugada.TAcceptades,null);
	}

	/**
	 * @post S'ha retornat una jugada que indica la petició d'ajornament i s'ha informat als jugadors de l'Ajornament.
	 * @return Una jugada que té com a resultat Ajornament.
	 */
	private Jugada ajornarPartida() {
		System.out.println("Ajornant Partida i tornant al menú principal");
		return new Jugada(ResultatJugada.Ajornament,null);
	}
	
	/**
	 * @post S'ha retornat una jugada que indica la petició de taules.
	 * @return Una jugada que té com a resultat Taules Solicitades.
	 */
	private Jugada demanarTaules() {
		return new Jugada(ResultatJugada.TSollicitades,null);
	}
	
	/**
	 * @post S'ha demanat la jugada a fer al jugador, aquesta s'ha enregistrat i retornat en una Jugada.
	 * @return Una jugada que conté el moviment que vol fer el Jugador.
	 * @throws IllegalArgumentException Quan el format que ha entrat el jugador no era l'esperat.
	 * @throws StringIndexOutOfBoundsException Quan el format que ha entrat el jugador no era l'esperat.
	 */
	private Jugada ferJugada() throws IllegalArgumentException, StringIndexOutOfBoundsException {
		System.out.println("Entra la posició de la peça que vols morue");
		System.out.println("Format columnaFila EX: \"a3\" -> Fila=3 columna=a ");
		
		Scanner scanner = new Scanner(System.in);
		Jugada res = null;

		Posicio origen = parseString(scanner.nextLine());
		System.out.println("Entra la posicó on vols moure la peça");
		Posicio desti = parseString(scanner.nextLine());
		
		res= new Jugada(origen,desti,null);
				
		return res;
	}

	/**
	 * @pre Hi han taules pendents d'acceptar o rebutjar per part d'aquest jugador.
	 * @param modeText Booleà que indica si estem en mode Text(true) o gràfic(false)
	 * @return Una jugada que conté la decisió d'acceptar o rebutjar les taules per part d'aquest jugador.
	 */
	private Jugada gestionarTaulesPendents(boolean modeText) {
		Scanner scanner = new Scanner(System.in);
		Jugada res = null;
		

		System.out.println( System.lineSeparator() + this.toString()+  ", l'oponent t'ha solicitat taules, les acceptes? [s/n].");
		
		taulesPendentsAcceptacio = false;
		String resposta = scanner.nextLine();
		
		//CAS: Taules Acceptades
		if(resposta.matches("s")) {
			System.out.println("Les taules s'accepten");
			res =  acceptarTaules(); 
		}
		//CAS: Taules Rebutjades
		else if(resposta.matches("n")) {
			System.out.println("Les taules es rebutjen");
			res = rebutjarTaules();
		}
		//CAS: Input Incorrecte
		else{
			System.out.println("Entrada desconeguda, les taules es rebutjen...");
			res = rebutjarTaules();
		}
		return res;
	}


	/**
	 * @post El jugador ha tingut la opció de guardar la partida.
	 */
	private void guardarPartida() {
		Escacs.guardarPartida();
	}
	
	/**
	 * @post S'ha mostrat al jugador les opcions que té disponibles en aquest estat i mode de la partida.
	 * @param modeText Booleà que indica si estem en mode Text(true) o gràfic(false).
	 * @note Les opcions mostrades són 1-Jugar(text)/Sortir(gràfic), 2-Rendir-se, 3-Demanar Taules, 4-Guardar, 5-Desfer, 6-Refer, 7-Ajornar.
	 */
	private void mostrarOpcions(boolean modeText) {
		System.out.println("És el teu torn " + this.toString());
		System.out.println("");
		if(modeText) {
			System.out.println("1- Jugar");
		}
		else {
			System.out.println("1- Tornar a l'aplicació");
		}
		System.out.println("2- Rendir-se");
		System.out.println("3- Demanar Taules");
		System.out.println("4- Guardar Partida");
		if(Escacs.potDesfer()) {
			System.out.println("5- Desfer Jugada");
		}
		if(Escacs.potRefer()) {
			System.out.println("6- Refer Jugada");
		}
		System.out.println("7- Ajornar Partida");
		
	}
	
	/**
	 * @pre pos està en format algebraic @see Posicio::Posicio(char columna, int fila).
	 * @param pos String representatiu d'una posició.
	 * @return Una instància de Posicio inicialitzada a partir del paràmtre pos.
	 * @throws IllegalArgumentException: En cas que pos no estigui en el format especificat.
	 */
	private Posicio parseString(String pos) throws IllegalArgumentException{
	
		char columna = pos.charAt(0);
		int fila = 0;
		for(int i = 1; i<pos.length();i++) {
			fila *= 10;
			fila += pos.charAt(i) - 48;
		}
		
		if(columna < 'a' || columna > 'p' || fila < 0 || fila > 16)
			throw new IllegalArgumentException("[Error] La columna o fila resultant no està dins els paràmetres");
		return new Posicio(columna,fila);
	}
	
	/**
	 * @post S'ha retornat una jugada que indica que les taules s'han rebutjat.
	 * @return Una jugada que té com a resultat TRebutjades.
	 */
	private Jugada rebutjarTaules() {
		return new Jugada(ResultatJugada.TRebutjades,null);
	}
	
	/**
	 * @post S'ha demanat al Jugador si quantes jugades vol desfer i s'ha passat la petició al controlador. Si la petició de desfer jugades és vàlida es retorna true, altrament false.
	 * @return Un booleà que indica si la petició s'ha realitzat.
	 */
	private boolean referJugada() {
		Scanner scanner = new Scanner(System.in);
		int numJugades;
		
		System.out.println("Quantes jugades vols refer?");
		
		try {numJugades = scanner.nextInt();}
		catch(InputMismatchException e) {
			scanner.nextLine();
			System.out.println("[ERROR]Format incorrecte, s'esperava un enter positiu.");
			return false;
		}
		finally {scanner.nextLine();}
		
		try {
			Escacs.referJugada(numJugades);
			return true;
		}
		catch(IllegalArgumentException e) { 
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	/**
	 * @post S'ha retornat una jugada que conté la rendició.
	 * @return Una jugada que té com a resultat Rendició.
	 */
	private Jugada rendirse() {
		return new Jugada(ResultatJugada.Rendicio,null);
	}
}
