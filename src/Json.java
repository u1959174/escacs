import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *  @file Json.java
 *  @brief Classe Json
 */

/** 
 *  @class Json
 *  @brief Gestiona tota la lectura i la generació dels fitxers Json i la creació dels objectes que aquest està involucrat.
 *  @author Eloi Quintana
 *  @author u1963141
 */

public class Json {
    
    /**
     * @post -
     * @comment Constructor Constructor buid
     * @throws IOException En la lectura del fitxxer
     * @throws ExcepcioG en diversos casos
     */
    public Json() throws IOException, ExcepcioG{
    }
    
    /**
     * @pre -
     * @post S'ha creat una nova partida a partir de les dades del fitxer.
     * @return partida Es retorna la Partida creada
     * @param fitxer ruta del fitxer de regles
    */
    public static Partida crearPartidaNova(String fitxer)throws IOException, ExcepcioG{
        //Lectura i Comprobacio de Fitxers
        JSONObject obj = null;
        
        obj = lecturaFitxers(fitxer,false);


        //Mida Tauler
        int nFiles = lecturaEnter(obj,"nFiles");
        int nCols  = lecturaEnter(obj,"nCols");

        //Comprobacions Tauler
        comprobarMidaTauler(nFiles,nCols);

        //Maxims de Tirades
        int maxInaccio = lecturaEnter(obj,"limitTornsInaccio");
        int maxEscacs = lecturaEnter(obj,"limitEscacsSeguits");

        //Creacio del tauler
        Tauler t = new Tauler(nFiles, nCols);

        //Peces
        HashMap<String,TipusPeca> tipus = new HashMap<String,TipusPeca>();
        lecturaPeces(obj,tipus);

        //Posicions
        lecturaPosicions(obj, t, tipus, nFiles, nCols);

        //Enrocs
        lecturaEnrocs(tipus,obj);

        //Es crear i retorna la partida
        Partida p = new Partida(t, tipus, maxInaccio, maxEscacs);
        p.modificarRutaFitxerRegles(fitxer);
        return p;
    }
    
    /**
     * @pre -
     * @post S'ha creat una nova partida a partir de les dades del fitxer amb jugades introduïdes.
     * @return Es retorna Partida
     * @param fitxer ruta del fitxer de relges
     * @throws java.io.IOException
     * @throws ExcepcioG
    */
    public static Partida crearPartidaComencada(String fitxer) throws IOException, ExcepcioG{
        //Lectura i Comprobacio de Fitxers
        JSONObject obj = lecturaFitxers(fitxer,true);

        //Mida Tauler
        int nFiles = lecturaEnter(obj,"nFiles");
        int nCols  = lecturaEnter(obj,"nCols");
        
        //Comprobacions Tauler
        comprobarMidaTauler(nFiles,nCols);
        
        //Maxims de Tirades
        int maxInaccio = lecturaEnter(obj,"limitTornsInaccio");
        int maxEscacs = lecturaEnter(obj,"limitEscacsSeguits");
        
        //Creacio del tauler
        Tauler t = new Tauler(nFiles, nCols);
        
        //Peces
        HashMap<String,TipusPeca> tipus = new HashMap<String,TipusPeca>();
        lecturaPeces(obj,tipus);
        
        //Enrocs
        lecturaEnrocs(tipus,obj);
        
        //Posicions
        lecturaPosicionsRegles(obj, "posIniBlanques", t, tipus, Color.Blanques);
        lecturaPosicionsRegles(obj, "posIniNegres", t, tipus, Color.Negres);
        
        //Tirades
        ArrayList<Jugada> j = new ArrayList<Jugada>();
        lecturaTirades(obj, j,tipus);
        
        //Torn i resultat
        Color torn = Color.obtenirColor(lecturaString(obj,"proper_torn"));
        ResultatPartida resultatFinal = ResultatPartida.obtenirResultat(lecturaString(obj,"resultat_final")) ;
        
        //Es crear i retorna la partida
        Partida p = new Partida(t, tipus, j, maxInaccio, maxEscacs);
        p.modificarRutaFitxerRegles(obj.getString("fitxerRegles"));
        return p;
    }
    
    /**
     * @pre -
     * @post donat un fitxr s'ha llegit i retornat una Array de HasMap<String tauler, Tauler t>
     * @param rutaFitxerConeixement ruta del fitxer de coneixement
     * @return una Collection de partides aplicades contingudes al fitxer de la rutaFitxerConeixement
     * @throws java.io.IOException
     * @throws ExcepcioG
    */
    public static Collection<Partida> carregarConeixement(String rutaFitxerConeixement) throws IOException, ExcepcioG{
        
        Fitxer ftx1 = new Fitxer(rutaFitxerConeixement,false);
        rutaFitxerConeixement.replace("\\","/");
        String contingut = ftx1.Llegir();
        JSONObject obj = new JSONObject(contingut);
        Queue<Partida> llistaPartides = new LinkedList<Partida>();
        
        if(comprovarFitxerConeixement(obj)){
            JSONArray pos_inicial = obj.getJSONArray("coneixements");
            for (int j = 0; j < pos_inicial.length(); j++) {
                String fitxertmpConeixement = pos_inicial.getString(j);
                fitxertmpConeixement.replace("\\","/");
                Partida partidaConeixement = crearPartidaComencada(fitxertmpConeixement);
                llistaPartides.add(partidaConeixement);
            }
        }
        else{
            throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);  
        }
        
        return llistaPartides;
    }

    /**
     * @pre -
     * @Post S'ha creat una nova partida a partir de les dades del fitxer amb jugades introduïdes.
     * @Return Es retorna Partida
     * @param p
     * @param fitxer
     * @throws java.io.IOException
    */
    public static void guardarPartida(Partida p, String fitxer) throws IOException, ExcepcioG{
        String partida = generarJSONJugades(p);
        
        Fitxer ftx = new Fitxer(fitxer,false);
        ftx.Escriure(partida);
        
    }
    
    /**
    * @pre -
    * @Post S'ha generat el contingut de un objecte JSON.
    * @Return Objecte JSON
    */
    private static String generarJSONJugades(Partida p){
        JSONObject obj = new JSONObject();

        obj.put("resultat_final", Json.generarStringResultatPartida(p));
        obj.put("tirades", Json.generarTiradesPartida(p));
        obj.put("proper_torn",p.torn().toString());
        obj.put("posIniNegres", generarPosIni(p,Color.Negres));
        obj.put("posIniBlanques", generarPosIni(p,Color.Blanques));
        obj.put("fitxerRegles",p.obtenirRutaFitxerRegles());
        
        return obj.toString();
    }
    
    /**
    * @pre -
    * @Post s'ha generat l'array de posicions inicials de la partida p del color col.
    * @Return Objecte array JSON
    */
    private static JSONArray  generarPosIni(Partida p,Color col){
        JSONArray posInic = new JSONArray();
        for(Peca pe : p.obtenirTaulerInicial().donamPeces(col)){
            JSONObject objectePosicio = new JSONObject();
            objectePosicio.put("pos", pe.onEstic().toString());
            objectePosicio.put("tipus", pe.tipus().toString());
            objectePosicio.put("moguda", !pe.quieta());
            posInic.put(objectePosicio);
        }
        return posInic;
    }
    
    /**
    * @pre -
    * @Post s'ha generat l'array de posicions inicials de la partida p del color col.
    * @Return Objecte array JSON
    */
    private static JSONArray  generarTiradesPartida(Partida p){
        JSONArray tirades = new JSONArray();
        for(Jugada j : p.obtenirJugades()){
            boolean guardar = true;
            if(j.obtenirResultatsJugada()!= null){
                if(j.obtenirResultatsJugada().contains(ResultatJugada.Inicial) || j.obtenirResultatsJugada().contains(ResultatJugada.TRebutjades)){
                    guardar = false;
                }       
            }
            if(guardar){
                String ori = "";
                String dst = "";
                if(j.obtenirOrigen() != null){
                    ori = j.obtenirOrigen().toString();
                }
                if(j.obtenirDesti() != null){
                    dst = j.obtenirDesti().toString();
                }
                JSONObject objecteJugada = new JSONObject();
                objecteJugada.put("torn", j.obtenirTirador().toString().toUpperCase());
                objecteJugada.put("origen", ori);
                objecteJugada.put("desti", dst);
                objecteJugada.put("resultat", Json.generarStringResultatJugada(j));
                tirades.put(objecteJugada);
            }
        }
        return tirades;
    }
    
    /**
    * @pre p != null
    * @Post s'ha generat un string amb el resultat de la partida si no es null
    * @Return Es retorna l'string amb el resultat
    */
    private static String generarStringResultatPartida(Partida p){
        if(p.obtenirResultatPartida() == null)return "";
        else return p.obtenirResultatPartida().toString();
    }
    
    /**
    * @pre -
    * @Post s'ha generat un string amb tots els resultats d'una partida
    * @Return Es retorna l'string amb tots els resultats
    * @param j jugada en la que s'aplicara el mètode
    */
    private static String generarStringResultatJugada(Jugada j){
        String s = "";
        boolean primer = true;
        for(ResultatJugada res : j.obtenirResultatsJugada()){
            if(!primer){
                s = s + ", ";
            }
            else{
                primer = false;
            }
            s = s + res.toString();
            if(res == ResultatJugada.Promocio){
                s = s + ": (" + j.obtenirPecaOrigen().tipus().toString();
                s = s + "-" + j.obtenirPecaPromocionada().toString() + ")";
            }
        }
        return s;
    }
    
    /**
    * @pre fil i col > 0
    * @Post S'ha comprobat que fil i col esta disns els paràmetres establerts
    */
    private static void comprobarMidaTauler(int fil,int col) throws ExcepcioG{
        if(fil > 16) throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
        if(col > 16) throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
    }
    
    /**
    * @pre -
    * @Post S'ha classificat el fitxer d'entrada en el format que es, i s'ha transformat a objecte JSON.
    * @Return Es retorna l'Objecte
    * @param fitxer ruta del fitxer de jugades o regles
    * @param esJugades indica si el fitxer es de jugades o de regles
    */
    private static JSONObject lecturaFitxers(String fitxer, boolean esJugades)throws IOException, ExcepcioG{
        Fitxer ftx2, ftx1 = new Fitxer(fitxer,false);
        String contingut = ftx1.Llegir();
        JSONObject obj = new JSONObject(contingut);
        if(comprovarFitxerJugades(obj) && esJugades){
            ftx2 = new Fitxer(obj.getString("fitxerRegles"),false);
            String contingutftx2 = ftx2.Llegir();
            JSONObject obj2 = new JSONObject(contingutftx2);
            if(!comprovarFitxerRegles(obj2)){
                throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
            }
            for(String key : JSONObject.getNames(obj2))
            {
              obj.put(key, obj2.get(key));
            }
            
            return obj;
            
        }
        else if(comprovarFitxerRegles(obj) && !esJugades){
            return obj;
        }
        else throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
    }

    /**
    * @pre -
    * @Post Ha coprovat que existeixen tots els objectes a l'objecte d'entrada.
    * @param obj Objecte a comprovar
    * @Return True si conte els objectes especificats, false altrament
    */
    private static boolean comprovarFitxerJugades(JSONObject obj) {
        return obj.has("fitxerRegles") && obj.has("posIniBlanques") && obj.has("posIniNegres") && obj.has("proper_torn") && obj.has("tirades")&& obj.has("resultat_final");
    }
    
    /**
    * @pre -
    * @Post Ha coprovat que existeixen tots els objectes a l'objecte d'entrada.
    * @param obj Objecte a comprovar
    * @Return True si conte els objectes especificats, false altrament
    */
    private static boolean comprovarFitxerRegles(JSONObject obj) {
        return obj.has("nFiles") && obj.has("nCols") && obj.has("peces") && obj.has("posInicial") && obj.has("limitEscacsSeguits")&& obj.has("limitTornsInaccio")&& obj.has("enrocs");
    }
    
    /**
    * @pre -
    * @Post Ha coprovat que existeixen tots els objectes a l'objecte d'entrada.
    * @param obj Objecte a comprovar
    * @Return True si conte els objectes especificats, false altrament
    */
    private static boolean comprovarFitxerConeixement(JSONObject obj) {
        return obj.has("coneixements");
    }
    
    /**
    * @pre -
    * @Post ha extret l'enter amb amb nom (nom)
    * @param nom Nom de l'objecte a extreure
    * @Return l'enter amb nom (nom)
    */
    private static int lecturaEnter(JSONObject obj, String nom){
        return obj.getInt(nom);
    }
    
    /**
    * @pre -
    * @Post ha extret l'String amb amb nom (nom)
    * @param nom Nom de l'objecte a extreure
    * @Return l'String amb nom (nom)
    */
    private static String lecturaString(JSONObject obj, String nom){
        return obj.getString(nom);
    }
    
    /**
    * @pre -
    * @Post Ha traduit un arraylist de Strings a un tipus de Moviment
    * @param mov : ArrayList de String que conte els 4 paràmetres de moviment.
    * @param inicial : True si el moviment es inicial, false altrament.
    * @author Marc Cosgaya
    */
    private static Moviment traduirMoviment(ArrayList<String> mov, boolean inicial) throws IllegalArgumentException {
        Moviment res;
        String a = mov.get(0);
        String b = mov.get(1);
        if (a.equals("0") ^ b.equals("0")) 
            res = new MovimentRectilini(a,b,mov.get(2),mov.get(3),inicial);
        else if (a.equals(b) || (a.equals("n") && b.equals("-n")) || (a.equals("-n") && b.equals("n")) || (a.equals("a") && b.equals("-a")) || (a.equals("b") && b.equals("-b")) || abs(Integer.parseInt(a)) == abs(Integer.parseInt(b)))
            res = new MovimentDiagonal(a,b,mov.get(2),mov.get(3),inicial);
        else
            res = new MovimentCombinat(a,b,mov.get(2),inicial);
        return res;
    }

    /**
    * @pre -
    * @Post HashMap conte ara tots els tipus de peces de l'object obj.
    * @param obj Objecte a extreure les peces.
    * @param tipus Conte els Tipus de peces ordenats pel seu Nom.
    */
    private static void lecturaPeces(JSONObject obj, HashMap<String,TipusPeca> tipus) throws ExcepcioG {
        JSONArray arr_peces = obj.getJSONArray("peces");
        int valmaxim = 0;
        int valrei = 0;
        for (int i = 0; i < arr_peces.length(); i++){
            TipusPeca value = tipus.get(arr_peces.getJSONObject(i).getString("nom"));
            if (value != null) {
               throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
            }

            TipusPeca pecaTmp = new TipusPeca(
                    arr_peces.getJSONObject(i).getString("nom"),
                    (char) arr_peces.getJSONObject(i).getString("simbol").charAt(0),
                    arr_peces.getJSONObject(i).getInt("valor"),
                    (boolean) arr_peces.getJSONObject(i).getBoolean("promocio"), 
                    (boolean) arr_peces.getJSONObject(i).getBoolean("invulnerabilitat"),
                    arr_peces.getJSONObject(i).getString("imatgeBlanca"),
                    arr_peces.getJSONObject(i).getString("imatgeNegra")
                    
            );
            if(arr_peces.getJSONObject(i).getString("nom").equals("REI")){
                valrei = arr_peces.getJSONObject(i).getInt("valor");
            }
            else{
                if(valmaxim < arr_peces.getJSONObject(i).getInt("valor")){
                    valmaxim = arr_peces.getJSONObject(i).getInt("valor");
                }
            }
            
            lecturaMovimentsPeca(pecaTmp, arr_peces.getJSONObject(i), "moviments", false);
            lecturaMovimentsPeca(pecaTmp, arr_peces.getJSONObject(i), "movimentsInicials", true);
            
            tipus.put(arr_peces.getJSONObject(i).getString("nom"),pecaTmp);
        }
        if(valrei < valmaxim){
            throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
        }
    }
    
    /**
    * @pre -
    * @Post s'han carregat tots els moviments de l'objecte json a TipusPeca
    * @param pecaTmp TipusPeca on es guardaran els moviments
    * @param obj Objecte json de on es llegeixen els moviment
    * @param inicial true si el moviment es de tipus inicial, false altrament
    */
    private static void lecturaMovimentsPeca(TipusPeca pecaTmp, JSONObject obj, String nom, boolean inicial){
        JSONArray tmp_arr = obj.getJSONArray(nom);
        for (int j = 0; j < tmp_arr.length(); j++) {
            try{
                ArrayList<String> lst = new ArrayList<String>();
                for (int k = 0; k < tmp_arr.getJSONArray(j).length(); k++){
                    lst.add((String) tmp_arr.getJSONArray(j).get(k).toString().replaceAll("\\s",""));
                }
                pecaTmp.carregarMoviment(traduirMoviment(lst,inicial), inicial);
            }
            catch (ExcepcioG g){
                //Segons escrit del moodle, obviar moviments invalids.
            }
        }
    }
    
    /**
    * @pre tipus ja s'ha inicialitzat.
    * @Post s'han llegit tots els moviments enrocs de l'objecte json i s'han carregat al hasmap
    * @param tipus es el hasmap ja inicialitzat on es guarden els objecte llegits a obj
    * @param obj es el objecte on s'extreuen els moviments enrocs
    */
    private static void lecturaEnrocs(HashMap<String,TipusPeca> tipus, JSONObject obj) throws ExcepcioG{
        JSONArray arr = obj.getJSONArray("enrocs");
        for (int i = 0; i < arr.length(); i++){
            TipusPeca pa = tipus.get(arr.getJSONObject(i).getString("peçaA"));
            TipusPeca pb = tipus.get(arr.getJSONObject(i).getString("peçaB"));
            
            pa.carregarMoviment(
                    new MovimentEspecial(
                        (boolean) arr.getJSONObject(i).getBoolean("quiets"), 
                        (boolean) arr.getJSONObject(i).getBoolean("buitAlMig"), 
                        pb
                    ), 
                    (boolean) arr.getJSONObject(i).getBoolean("quiets")
            );
            pb.carregarMoviment(
                    new MovimentEspecial(
                        (boolean) arr.getJSONObject(i).getBoolean("quiets"), 
                        (boolean) arr.getJSONObject(i).getBoolean("buitAlMig"), 
                        pa
                    ), 
                    (boolean) arr.getJSONObject(i).getBoolean("quiets")
            );
            
        }
    }
    
    /**
    * @pre -
    * @Post s'han llegit les posicions inicials del json obj
    * @param obj Objecte json on s'extreurean les posicions inicials
    * @param tipus hashmap on es guarden tots els tipus de peca
    * @param nFiles numero de files del tauler
    * @param nCols numero de columnes del tauler
    */
    private static void lecturaPosicions(JSONObject obj, Tauler t, HashMap<String,TipusPeca> tipus, int nFiles, int nCols) throws ExcepcioG{
        JSONArray pos_inicial = obj.getJSONArray("posInicial");
        int nreis = 0;
        for (int j = 0; j < pos_inicial.length(); j++) {
            TipusPeca ptmp = tipus.get(pos_inicial.getString(j));
            if(ptmp == null){
                throw new ExcepcioG(TipusExcepcioG.FITXER_REGLES_NO_VALID);
            }
            if(t.tensPeca(new Posicio((j/nFiles+1),(j%nCols+1))) != null){
                throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
            }
            
            t.afegirPeca(new Posicio((j/nFiles+1),(j%nCols+1)), new Peca(ptmp, Color.Blanques));
            t.afegirPeca(new Posicio(nFiles-(j/nFiles),(j%nCols+1)), new Peca(ptmp, Color.Negres));
            if(pos_inicial.getString(j).equals("REI")){
                nreis += 1;
                if(nreis > 1){
                    throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
                }
            }
        }
    }
    
    /**
    * @pre -
    * @Post S'ha classificat el fitxer d'entrada en el format que es, i s'ha transformat a objecte JSON.
    * @param obj Objecte json on s'extreurean les posicions de les peces
    * @param nom nom de l'objecte a extreure
    * @param t Tauler on s'afegiran les peces en questio
    * @param tipus index on es guarden tots els tipus de peces disponibles
    */
    private static void lecturaPosicionsRegles(JSONObject obj, String nom, Tauler t, HashMap<String,TipusPeca> tipus, Color color) throws ExcepcioG{
        JSONArray posIni = obj.getJSONArray(nom);
        int nreis = 0;
        for (int i = 0; i < posIni.length(); i++){
            t.afegirPeca(
                new Posicio(
                    (char) posIni.getJSONObject(i).getString("pos").charAt(0),
                    (int) Integer.parseInt(posIni.getJSONObject(i).getString("pos").replaceAll("[^\\d.]", ""))
                ), 
                new Peca(tipus.get(posIni.getJSONObject(i).getString("tipus")),color)
            );
            if(posIni.getJSONObject(i).getString("tipus") == "REI"){
                nreis += 1;
                if(nreis > 1){
                    //nº de reis superior al permes
                    throw new ExcepcioG(TipusExcepcioG.FITXER_REGLES_NO_VALID);
                }
            }
        }
    }
    
    /**
    * @pre j ja inicialitzat.
    * @Post S'han llegit totes les jugades del obj json.
    * @param obj Objecte json on s'extreurean les tirades o jugades
    * @param j index on es guardaran les jugades extretes
    */
    private static void lecturaTirades(JSONObject obj, ArrayList<Jugada> j,HashMap<String,TipusPeca> tipus) throws ExcepcioG{
        JSONArray arr = obj.getJSONArray("tirades");
        for (int i = 0; i < arr.length(); i++){
            Jugada tmp = null;
            if(comprovarTirada(arr.getJSONObject(i))){
                if(arr.getJSONObject(i).getString("origen").isEmpty()){
                    //ajornament,rendicio,taulessolicitades,itaules acceptades.
                    String sresultat = lecturaString(arr.getJSONObject(i),"resultat");
                    ResultatJugada resultatJugada = null;
                    if(sresultat.contains("TAULES ACCEPTADES")){
                        resultatJugada = ResultatJugada.TAcceptades;
                    }
                    else if(sresultat.contains("TAULES SOL·LICITADES")){
                        resultatJugada = ResultatJugada.TSollicitades;
                    }
                    else if(sresultat.contains("AJORNAMENT")){
                        resultatJugada = ResultatJugada.Ajornament;
                    }
                    else if(sresultat.contains("RENDICIO")){
                        resultatJugada = ResultatJugada.Rendicio;
                    }
                    else{
                        throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
                    }

                    tmp = new Jugada(resultatJugada,
                    Color.obtenirColor(lecturaString(arr.getJSONObject(i),"torn")));
                }
                else{               
                    tmp = new Jugada(
                        new Posicio(
                            (char) arr.getJSONObject(i).getString("origen").charAt(0),
                            (int) Integer.parseInt(arr.getJSONObject(i).getString("origen").replaceAll("[^\\d.]", ""))
                        ),
                        new Posicio(
                            (char) arr.getJSONObject(i).getString("desti").charAt(0),
                            (int) Integer.parseInt(arr.getJSONObject(i).getString("desti").replaceAll("[^\\d.]", ""))
                        ),
                        Color.obtenirColor(lecturaString(arr.getJSONObject(i),"torn"))
                    );
                    String sresultat = lecturaString(arr.getJSONObject(i),"resultat");
                    if(sresultat.contains("PROMOCIO")){
                        String s = sresultat.substring(sresultat.indexOf("(") + 1);
                        s = s.substring(0, s.indexOf(")"));

                        String[] res3 = s.split("-");
                        tmp.afegirResultatJugada(ResultatJugada.Promocio);
                        tmp.finalitzarJugada(null, false, null, null, new Peca(tipus.get(res3[1]),Color.obtenirColor(lecturaString(arr.getJSONObject(i),"torn"))), null);
                    }
                }
                j.add(tmp);
            }
            else{
                throw new ExcepcioG(TipusExcepcioG.FITXER_INVALID);
            }
        } 
    }
    
    /**
    * @pre -
    * @Post Ha coprovat que existeixen tots els objectes a l'objecte d'entrada.
    * @param obj Objecte a comprovar
    * @Return True si conte els objectes especificats, false altrament
    */
    private static boolean comprovarTirada(JSONObject obj) {
        return obj.has("torn") && obj.has("resultat") && obj.has("origen") && obj.has("desti");
    }
}


