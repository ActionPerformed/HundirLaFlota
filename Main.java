package HundirLaFlota;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Hundir la flota
 * 
 * El juego permite jugar contra una IA simple, mediante tableros de 10x10 casillas
 * Se divide en dos fases, la de colocación de pizas y la de juego
 *  
 * dimensionTablero: la dimensión de los tableros de juego
 * 
 * orientacionBarco: Determina la orientación del barco en la fase de colocación.
 *                  Varía de true a false con la rueda del ratón. 
 * 
 * colision: True si un barco se solapa con otro o se hayan contiguos
 * faseColocarPiezas: True si aun no ha empezado la fase de disparos
 * 
 * tableroDefensa: matriz de botones que determina el tablero donde el jugador coloca sus barcos
 * tableroAtaque: matriz de botones que determina el tablero donde el jugador realiza disparos
 * 
 * btColocaBarco: botones para la eleccion y colocacion de barcos
 * numColocaBarcos: determina cuantos barcos se colocaran de cada tipo
 * dimBarco: determina la dimension del barco que se está colocando (0 si no se está colocando ninguno)
 * 
 * barcosIA: tablero de defensa de la IA. True si hay barco, false si no
 * 
 * btEmpiezaJuego: permite pasar a la fase de disparos, finaliza la de colocacion de piezas
 * btLimpiaTablero: resetea el juego
 * 
 * lbImpactosJugador: Numero de impactos restantes del jugador
 * lbImpactosIA: Número de impactos restantes de la IA 
 * 
 * @author ActionPerformed
 * @version 1.4
 */
public class Main extends javax.swing.JFrame implements ActionListener, MouseListener, MouseWheelListener{

    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuItCargar;
    private javax.swing.JMenuItem menuItFinalizar;
    private javax.swing.JMenuItem menuItGuardar;
    private javax.swing.JMenuItem menuItNuevo;
    private javax.swing.JMenuItem menuItSalir;
    private javax.swing.JMenu menuJuego;
    
    private final int dimensionTablero = 10;
    
    private final Tablero tableroDefensa = new Tablero(dimensionTablero,"Tablero de defensa");
    private final Tablero tableroAtaque = new Tablero(dimensionTablero,"Tablero de ataque");
    
    private boolean orientacionBarco; 
    private boolean colision; 
    private boolean faseColocarPiezas; 
    private boolean turnoJugador;
    
    private final JButton[] btColocaBarco = new JButton[4];
    private final JButton btEmpiezaJuego = new JButton();
    private final JButton btLimpiaTablero = new JButton();
    
    private final JLabel lbImpactosJugador = new JLabel();
    private final JLabel lbImpactosIA = new JLabel();
    private final JLabel lbBackground = new JLabel();
    
    private int[] numColocaBarco;
    private int dimBarco;
    private int impactosJugador;
    
    private int impactosIA;
    private boolean[][] barcosIA = new boolean[dimensionTablero][dimensionTablero];
    private List<int[]> tableroAtaqueIA;
    private List<int[]> tableroAtaqueIAPrioritario;
    
    /**
     * Construye el objeto que se lanzará para iniciar el juego
     * initcomponens(), crearTableros() y crearMenuBarcos() generarán la interface,
     * mientras que initMoreComponents() inicializará los valores de juego 
     */
    public Main() {
        initComponents();
        crearTableros();
        crearMenuBarcos(); 
        initMoreComponents();
    }
    
    /**
     * Generamos los tableros de juego y sus listener asociados
     */
    public void crearTableros(){
       
        add(tableroDefensa);
        tableroDefensa.setBounds(10, 10, 400, 400);
        add(tableroAtaque);
        tableroAtaque.setBounds(425, 10, 400, 400);
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                tableroDefensa.getBtCasilla()[i][j].addActionListener(this);
                tableroDefensa.getBtCasilla()[i][j].addMouseWheelListener(this);
                tableroDefensa.getBtCasilla()[i][j].addMouseListener(this);
                tableroAtaque.getBtCasilla()[i][j].addActionListener(this);
            }
        }
    }

    /**
     * Generamos el resto de botones del tablero y sus listener
     */
    public void crearMenuBarcos(){
        for (int i = 0; i < btColocaBarco.length; i++) {
            btColocaBarco[i] = new JButton();
            add(btColocaBarco[i]);
            btColocaBarco[i].addActionListener(this);
            btColocaBarco[i].setBounds(50+110*(i%2), 425+40*(i/2), 100, 30);
        }
        
        add(btEmpiezaJuego);
        btEmpiezaJuego.addActionListener(this);
        btEmpiezaJuego.setBounds(270,425,100,30);
        btEmpiezaJuego.setText("Jugar");
        
        add(btLimpiaTablero);
        btLimpiaTablero.addActionListener(this);
        btLimpiaTablero.setBounds(270,465,100,30);
        btLimpiaTablero.setText("Limpiar");
        
        add(lbImpactosJugador);
        lbImpactosJugador.setBounds(500,425,200,30);
        
        add(lbImpactosIA);
        lbImpactosIA.setBounds(500,465,200,30);
    }

    /**
     * Inicializa las variables de juego: numero de impactos de cada jugador, 
     * numero de barcos a colocar, tablero de la IA, texto y color de los botones...
     */
    public void initMoreComponents(){
        impactosJugador=10;
        impactosIA=10;
        numColocaBarco = new int[]{4, 3, 2, 1};
        turnoJugador = true; //será false en los chequeos posteriores al turno del jugador
        faseColocarPiezas=true;
        orientacionBarco=true;
        colision=false;
        dimBarco=0;
        
        
        add(lbBackground);
        lbBackground.setBounds(0,0,850,600);
        lbBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/background.png")));
        
        tableroAtaqueIA = new ArrayList<>();
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                tableroAtaqueIA.add(new int[] {i,j});
            }
        }
        
        tableroAtaqueIAPrioritario = new ArrayList<>();
        
        //Activamos y añadimos texto a los botones de colocación de barcos
        for (int i = 0; i < btColocaBarco.length; i++) {
            btColocaBarco[i].setEnabled(true);
            btColocaBarco[i].setText(numColocaBarco[i]+" x Barco ["+(i+1)+"]");
        } 
        
        //empiezaJuego será true cuando se hayan colocado todos los barcos
        btEmpiezaJuego.setEnabled(false);
        btLimpiaTablero.setEnabled(true);
        
        menuItGuardar.setEnabled(false);
        
        //Inicializamos los colores de las casillas de los diferentes tableros
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                tableroDefensa.getBtCasilla()[i][j].setBackground(Libs.colorAgua);
                tableroAtaque.getBtCasilla()[i][j].setBackground(null);
                barcosIA[i][j]=false;
            }
        }
        
        //Añadimos texto inicial a los marcadores
        lbImpactosIA.setText("Barcos vivos IA: "+impactosIA);
        lbImpactosJugador.setText("Barcos vivos jugador: "+impactosJugador);
        
        // Creamos el tablero de defensa de la IA
        barcosIA=LibsIA.colocaBarcosIA(barcosIA);
    }
    
    /**
     * Si el barco proyectado sobre el tablero no produce colisiones, este método (llamado
     * desde actionPerformed) fija el barco a partir de la casilla (i,j) que se ha clicado,
     * con la orientación indicada previamente
     * 
     * Una vez se situa el barco, actualiza el numero de barcos restantes por colocar
     * y el texto del botón asociado a ese tipo de barco. Si ya no quedasen más barcos
     * de ese tipo, el boton pasaria a estado disabled
     * 
     * Una vez se coloca el último barco, este método activa el boton de jugar, que permite
     * dar comienzo a la fase de disparos
     * 
     * @param i
     * @param j
     */
    public void colocaBarco(int i, int j){
        if(!colision){
            if (orientacionBarco) {
                for (int k = i; k < Math.min(tableroDefensa.getBtCasilla().length, i+dimBarco); k++) {
                    tableroDefensa.getBtCasilla()[k][j].setBackground(Libs.colorBarco);
                }
            }else{
                for (int k = j; k < Math.min(tableroDefensa.getBtCasilla().length, j+dimBarco); k++) {
                    tableroDefensa.getBtCasilla()[i][k].setBackground(Libs.colorBarco);
                }
            }
            numColocaBarco[dimBarco-1]--;
            btColocaBarco[dimBarco-1].setText(numColocaBarco[dimBarco-1]+" x Barco ["+dimBarco+"]");
            if (numColocaBarco[dimBarco-1]<=0) {
                btColocaBarco[dimBarco-1].setEnabled(false);
                dimBarco=0;
            }
            if (!btColocaBarco[3].isEnabled()
             && !btColocaBarco[2].isEnabled()
             && !btColocaBarco[1].isEnabled()
             && !btColocaBarco[0].isEnabled()){
                btEmpiezaJuego.setEnabled(true);
            }
        }
    }
    
    
    
    /**
     * checkGame comprueba si se han dado las condiciones de victoria o derrota,
     * y se encarga de identificar el turno del jugador
     *
     */
    public void checkGame(){
        turnoJugador=!turnoJugador;
        if (!turnoJugador && impactosIA<=0){
            JOptionPane.showMessageDialog(this, "HAS GANADO");
            initMoreComponents();
        }else if (turnoJugador && impactosJugador<=0){
            JOptionPane.showMessageDialog(this, "HAS PERDIDO");
            initMoreComponents();
        }
    }
    
    /**
     *  juegaIA realiza los disparos de la IA
     * 
     */
    public void juegaIA(){
        //disparo
        int[] casillaDisparada = new int[2];
        
        if (!tableroAtaqueIAPrioritario.isEmpty()) {
            casillaDisparada = tableroAtaqueIAPrioritario.get((int)(Math.random()*tableroAtaqueIAPrioritario.size()));
        }else{
            casillaDisparada = tableroAtaqueIA.get((int)(Math.random()*tableroAtaqueIA.size()));
        }
        
        int i = casillaDisparada[0];
        int j = casillaDisparada[1];
        
        if (tableroDefensa.getBtCasilla()[i][j].getBackground().equals(Libs.colorAgua)){
            tableroDefensa.getBtCasilla()[i][j].setBackground(Libs.colorAguaTocado);
            Libs.borraValor(tableroAtaqueIA,casillaDisparada);
            Libs.borraValor(tableroAtaqueIAPrioritario,casillaDisparada);
        }else if (tableroDefensa.getBtCasilla()[i][j].getBackground().equals(Libs.colorBarco)){
            tableroDefensa.getBtCasilla()[i][j].setBackground(aciertoIA(i,j));
            
            //quito la casilla de la listas de futuros disparos
            Libs.borraValor(tableroAtaqueIA,casillaDisparada);
            Libs.borraValor(tableroAtaqueIAPrioritario,casillaDisparada);
            
            //y sus diagonales, puesto que no van a tener barcos.
            //Si no existen, no debería pasar nada
            Libs.borraValor(tableroAtaqueIA,new int[] {i-1,j-1});
            Libs.borraValor(tableroAtaqueIA,new int[] {i-1,j+1});
            Libs.borraValor(tableroAtaqueIA,new int[] {i+1,j-1});
            Libs.borraValor(tableroAtaqueIA,new int[] {i+1,j+1});
            
            Libs.borraValor(tableroAtaqueIAPrioritario,new int[] {i-1,j-1});
            Libs.borraValor(tableroAtaqueIAPrioritario,new int[] {i-1,j+1});
            Libs.borraValor(tableroAtaqueIAPrioritario,new int[] {i+1,j-1});
            Libs.borraValor(tableroAtaqueIAPrioritario,new int[] {i+1,j+1});
            
        }
        checkGame();
    }
    
    /**
     * muestraSolucion descubre el tablero de juego de la IA 
     * y finaliza la partida, desactivando todos los botones salvo el
     * de limpiar
     */
    public void muestraSolucion(){
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                if (barcosIA[i][j]){
                    tableroAtaque.getBtCasilla()[i][j].setBackground(Libs.colorTocado);;
                }
            }
        }
        faseColocarPiezas=false;
        for (int i = 0; i < btColocaBarco.length; i++) {
            btColocaBarco[i].setEnabled(false);
        }
        btEmpiezaJuego.setEnabled(false);
    }
    
    @Override
    public void mouseClicked(MouseEvent e){}
    @Override
    public void mousePressed(MouseEvent e){}
    @Override
    public void mouseReleased(MouseEvent e){}

    /**
     * mouseEntered realizará acciones SOLO sobre el tabledo de defensa del 
     * jugador, si nos hallamos en la fase de colocar piezas y si además se ha 
     * seleccionado un barco previamente
     * 
     * En tal caso, al colocar el puntero sobre una casilla, creará la sombra 
     * del barco seleccionado, con la orientacion correspondiente.
     * 
     * La sombra de este barco será:
     * 
     * - verde, si se hallase dentro del tablero de juego completamente 
     * y no colisionase con otros barcos ya colocados.
     * 
     * - rojo, en caso contrario, manteniendo en todo caso el color de las casillas
     * donde ya hubiera barcos anteriormente
     * 
     * @param e 
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (dimBarco>0 && faseColocarPiezas) {
            for (int i = 0; i < dimensionTablero; i++) {
                for (int j = 0; j < dimensionTablero; j++) {
                    if (e.getSource()==tableroDefensa.getBtCasilla()[i][j]){
                        if (orientacionBarco) { // Código apra barcos en vertical
                            if (i+dimBarco>dimensionTablero){
                                colision=true;
                                for (int k = i; k < dimensionTablero; k++) {
                                    if(tableroDefensa.getBtCasilla()[k][j].getBackground()!=Libs.colorBarco){
                                        tableroDefensa.getBtCasilla()[k][j].setBackground(Color.RED);
                                    }
                                }
                            }else{
                                colision=false;
                                for (int k = Math.max(0,i-1); k < Math.min(i+dimBarco+1,dimensionTablero); k++) {
                                    for (int l = Math.max(0,j-1); l < Math.min(j+2,dimensionTablero); l++) {
                                        if(tableroDefensa.getBtCasilla()[k][l].getBackground()==Libs.colorBarco){
                                            colision=true;
                                        }
                                    }
                                }
                                if(colision){
                                    for (int k = i; k < i+dimBarco; k++) {
                                        if(tableroDefensa.getBtCasilla()[k][j].getBackground()!=Libs.colorBarco){
                                            tableroDefensa.getBtCasilla()[k][j].setBackground(Color.RED);
                                        }
                                    }
                                }else{
                                    for (int k = i; k < i+dimBarco; k++) {
                                        if(tableroDefensa.getBtCasilla()[k][j].getBackground()!=Libs.colorBarco){
                                            tableroDefensa.getBtCasilla()[k][j].setBackground(Color.GREEN);
                                        }
                                    }
                                }
                            }
                        }else{ //Código apra barcos en horizontal
                            if (j+dimBarco>dimensionTablero){
                                colision=true;
                                for (int k = j; k < dimensionTablero; k++) {
                                    if(tableroDefensa.getBtCasilla()[i][k].getBackground()!=Libs.colorBarco){
                                        tableroDefensa.getBtCasilla()[i][k].setBackground(Color.RED);
                                    }
                                }
                            }else{
                                colision=false;
                                for (int k = Math.max(0,j-1); k < Math.min(j+dimBarco+1,dimensionTablero); k++) {
                                    for (int l = Math.max(0,i-1); l < Math.min(i+2,dimensionTablero); l++) {
                                        if(tableroDefensa.getBtCasilla()[l][k].getBackground()==Libs.colorBarco){
                                            colision=true;
                                        }
                                    }
                                }
                                if(colision){
                                    for (int k = j; k < j+dimBarco; k++) {
                                        if(tableroDefensa.getBtCasilla()[i][k].getBackground()!=Libs.colorBarco){
                                            tableroDefensa.getBtCasilla()[i][k].setBackground(Color.RED);
                                        }
                                    }
                                }else{
                                    for (int k = j; k < j+dimBarco; k++) {
                                        if(tableroDefensa.getBtCasilla()[i][k].getBackground()!=Libs.colorBarco){
                                            tableroDefensa.getBtCasilla()[i][k].setBackground(Color.GREEN);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
           
    }

    /**
     * mouseExited funciona solo en el tablero de defensa del jugador, 
     * siempre y cuando nos hallemos en la fase de colocar piezas y además
     * hallamos seleccionado un barco previamente para colocar
     * 
     * mouseExited limpiará las casillas sombreadas que genera mouseEntered, 
     * dejando siempre intactas las que determinan la colocación de barcos
     * anteriores
     * 
     * @param e 
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (dimBarco>0 && faseColocarPiezas) {
           for (int i = 0; i < dimensionTablero; i++) {
                for (int j = 0; j < dimensionTablero; j++) {
                    if (e.getSource()==tableroDefensa.getBtCasilla()[i][j]){
                        for (int k = i; k < Math.min(tableroDefensa.getBtCasilla().length, i+dimBarco); k++) {
                            if(tableroDefensa.getBtCasilla()[k][j].getBackground()!=Libs.colorBarco){
                                tableroDefensa.getBtCasilla()[k][j].setBackground(Libs.colorAgua);
                            }
                        }
                        for (int k = j; k < Math.min(tableroDefensa.getBtCasilla().length, j+dimBarco); k++) {
                            if(tableroDefensa.getBtCasilla()[i][k].getBackground()!=Libs.colorBarco){
                                tableroDefensa.getBtCasilla()[i][k].setBackground(Libs.colorAgua);
                            }
                        }
                    }
                }
            } 
        }
    }
    
    /**
     * actionPerformed SOLO realizará acciones si nos hallamos en el turno
     * del jugador
     * 
     * Si clicamos:
     * 
     * el boton btLimpiaTablero --> resetearemos el juego
     * 
     * el boton btEmpiezaJuego --> entraremos en la fase de disparos
     * 
     * 
     * Mientras nos hallemos en la fase de colocación de piezas:
     * 
     * - btColocarBarco[] definirá la dimensión del barco a tantear
     * 
     * - tableroDefensa[i][j] llamará al método colocarBarco sobre esa posición,
     *          siempre que previamente hayamos definido la dimensión de dicho
     *          barco
     * 
     * Mientras nos hallemos en la fase de disparos:
     * 
     * - tableroAtaque[i][j] generará un disparo sobre esa posición, siempre que
     *          no se haya disparado previamente sobre dicha casilla. Tras esto,
     *          se llamará a checkGame() y posteriormente se realizará la jugada
     *          de la IA y nu nuevo checkGame()
     * 
     * 
     * @param evt 
     */
    @Override
    public void actionPerformed (ActionEvent evt){
        if (turnoJugador) {
            if (evt.getSource()==btLimpiaTablero){
                menuItNuevoActionPerformed(evt); //Reseteamos el tablero
            }else if (evt.getSource()==btEmpiezaJuego){
                btEmpiezaJuego.setEnabled(false);
                menuItGuardar.setEnabled(true);
                faseColocarPiezas = false;
            }

            if (faseColocarPiezas) { //fase de colocar piezas
                for (int i = 0; i < btColocaBarco.length; i++) {
                    if (evt.getSource()==btColocaBarco[i]){
                        dimBarco=i+1;
                    }
                }
                if(dimBarco>0){
                    for (int i = 0; i < dimensionTablero; i++) {
                        for (int j = 0; j < dimensionTablero; j++) {
                            if (evt.getSource()==tableroDefensa.getBtCasilla()[i][j]) {
                                colocaBarco(i,j);
                            }
                        }
                    }
                }
            }else{ //fase de disparos
                for (int i = 0; i < dimensionTablero; i++) {
                    for (int j = 0; j < dimensionTablero; j++) {
                        if (evt.getSource()==tableroAtaque.getBtCasilla()[i][j] 
                         && tableroAtaque.getBtCasilla()[i][j].getBackground()!= Libs.colorAgua
                         && tableroAtaque.getBtCasilla()[i][j].getBackground()!= Libs.colorTocado
                         && tableroAtaque.getBtCasilla()[i][j].getBackground()!= Libs.colorHundido) {
                            if (barcosIA[i][j]) {
                                tableroAtaque.getBtCasilla()[i][j].setBackground(aciertoJugador(i,j));
                            }else{
                                tableroAtaque.getBtCasilla()[i][j].setBackground(Libs.colorAgua);
                            }
                            checkGame();

                            if (!faseColocarPiezas){ //Con esto evitamos que la maquina juege si ya terminó la partida
                                Timer jugadaIA;
                                int delay = 750; //milisegundos
                                ActionListener accionIA = new ActionListener() {
                                    public void actionPerformed(ActionEvent evt) {
                                        juegaIA();
                                    }
                                };
                                jugadaIA = new Timer(delay, accionIA); 
                                jugadaIA.setRepeats(false);
                                jugadaIA.start();
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Dadas unas coordenadas (i,j) derivadas de un impacto, el metodo devuelve
     * un color según haya sido "tocado" o "hundido"
     * 
     * @param i
     * @param j
     * @return colorHundido si es "hundido", colorTocado si es "tocado"
     */
    public Color aciertoJugador(int i, int j){
        if (estaHundido(i,j)) {
            impactosIA--;
            lbImpactosIA.setText("Barcos vivos IA: "+impactosIA);
            return Libs.colorHundido;
        }else{
            return Libs.colorTocado;
        }
    }
    
    /**
     * Comprueba si el impacto es un "tocado" o un "hundido". 
     * 
     * Para ello, genera las cuatro coordenadas anexas a la casilla (i,j) indicada
     * por parámetro. Para cada una de ellas, recorre la semirrecta hasta que
     * 
     * a) se sale del tablero
     * b) encuentra una casilla de agua
     * c) encuentra una casilla de barco que aún no ha recibido impacto
     * 
     * Si se da c), el metodo termina y devuelve un "false" para indicar que el
     * barco aun tiene casillas sin impactar
     * 
     * Si de dan a) o b), prueba con la siguiente semirrecta. 
     * 
     * Si terminan las cuatro semirrectas sin devolver un "false", el barco se considera
     * "hundido" y devuelve un "true"
     *
     * @param i
     * @param j
     * @return true si está hundido, false si no.
     */
    public boolean estaHundido(int i,int j){
               
        int[] casillaDer = {i,j+1};
        int[] casillaIzq = {i,j-1};
        int[] casillaArr = {i-1,j};
        int[] casillaAbj = {i+1,j};
        
        while (casillaDer[1]<dimensionTablero && barcosIA[casillaDer[0]][casillaDer[1]] ){
            if (tableroAtaque.getBtCasilla()[casillaDer[0]][casillaDer[1]].getBackground()!=Libs.colorTocado){
                return false;
            }
            casillaDer[1]++;
        }
        
        while (casillaIzq[1]>=0 && barcosIA[casillaIzq[0]][casillaIzq[1]] ){
            if (tableroAtaque.getBtCasilla()[casillaIzq[0]][casillaIzq[1]].getBackground()!=Libs.colorTocado){
                return false;
            }
            casillaIzq[1]--;
        }
        
        while (casillaAbj[0]<dimensionTablero && barcosIA[casillaAbj[0]][casillaAbj[1]] ){
            if (tableroAtaque.getBtCasilla()[casillaAbj[0]][casillaAbj[1]].getBackground()!=Libs.colorTocado){
                return false;
            }
            casillaAbj[0]++;
        }
        
        while (casillaArr[0]>=0 && barcosIA[casillaArr[0]][casillaArr[1]] ){
            if (tableroAtaque.getBtCasilla()[casillaArr[0]][casillaArr[1]].getBackground()!=Libs.colorTocado){
                return false;
            }
            casillaArr[0]--;
        }
        
        
        return true;
    }

    public Color aciertoIA(int i, int j){
        
        //Añado sus extensiones horizontales-verticales a la lista prioritaria
        //si aun están en la lista inicial
            
        if (Libs.borraValor(tableroAtaqueIA,new int[] {i,j+1})) {
            tableroAtaqueIAPrioritario.add(new int[] {i,j+1});
        }
        if (Libs.borraValor(tableroAtaqueIA,new int[] {i,j-1})) {
            tableroAtaqueIAPrioritario.add(new int[] {i,j-1});
        }
        if (Libs.borraValor(tableroAtaqueIA,new int[] {i+1,j})) {
            tableroAtaqueIAPrioritario.add(new int[] {i+1,j});
        }
        if (Libs.borraValor(tableroAtaqueIA,new int[] {i-1,j})) {
            tableroAtaqueIAPrioritario.add(new int[] {i-1,j});
        }
            
        if (LibsIA.estaHundidoIA(tableroDefensa.getBtCasilla(),i,j)) {
            impactosJugador--;
            lbImpactosJugador.setText("Barcos vivos Jugador: "+impactosJugador);
            tableroAtaqueIAPrioritario.clear();
            return Libs.colorHundido;
        }else{
            return Libs.colorTocado;
        }
    }
    
    /**
     * mouseWheelMoved cambia la orientación de la sombra del barco que se quiere
     * colocar, de vertical a horizontal y viceversa. Con cada uso, llama a los
     * métodos mouseExited() y mouseEntered() para limpiar el tablero
     * 
     * @param e 
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (faseColocarPiezas) {
            orientacionBarco=!orientacionBarco;
            mouseExited(e);
            mouseEntered(e);
        }        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor. 
     * 
     * I DO WHAT I WANT ~ r/firstworldanarchists
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        menuJuego = new javax.swing.JMenu();
        menuItNuevo = new javax.swing.JMenuItem();
        menuItCargar = new javax.swing.JMenuItem();
        menuItGuardar = new javax.swing.JMenuItem();
        menuItFinalizar = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hundir la flota");
        setMinimumSize(new java.awt.Dimension(850, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));

        menuJuego.setText("Juego");

        menuItNuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK));
        menuItNuevo.setText("Nuevo juego");
        menuItNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItNuevoActionPerformed(evt);
            }
        });
        menuJuego.add(menuItNuevo);

        menuItCargar.setText("Cargar juego...");
        menuItCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItCargarActionPerformed(evt);
            }
        });
        menuJuego.add(menuItCargar);

        menuItGuardar.setText("Guardar juego...");
        menuItGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItGuardarActionPerformed(evt);
            }
        });
        menuJuego.add(menuItGuardar);

        menuItFinalizar.setText("Finalizar juego");
        menuItFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItFinalizarActionPerformed(evt);
            }
        });
        menuJuego.add(menuItFinalizar);
        menuJuego.add(jSeparator1);

        menuItSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        menuItSalir.setText("Salir");
        menuItSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItSalirActionPerformed(evt);
            }
        });
        menuJuego.add(menuItSalir);

        menuBar.add(menuJuego);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 883, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 828, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItFinalizarActionPerformed(java.awt.event.ActionEvent evt) {
        muestraSolucion();
    }

/**
 * Abandona el juego
 * 
 * @param evt 
 */    
    private void menuItSalirActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

/**
 * Reinicia el juego
 * 
 * @param evt 
 */
    private void menuItNuevoActionPerformed(java.awt.event.ActionEvent evt) {
        initMoreComponents();
    }

/**
 * Ejecuta el menú de guardar partida. El archivo destino deberá tener extensión
 * .hdf para que el programa ejecute el guardado
 * 
 * @param evt 
 */
    private void menuItGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        File fichero;
        JFileChooser eligeFichero;
        FileNameExtensionFilter filter;
        
        eligeFichero = new JFileChooser();        
        filter = new FileNameExtensionFilter("Partidas de Hundir La Flota (.hdf)", "hdf");
        eligeFichero.setFileFilter(filter);
        int seleccion = eligeFichero.showSaveDialog(null);
        
        if (seleccion == JFileChooser.APPROVE_OPTION){
            fichero = eligeFichero.getSelectedFile();
            if (!fichero.getName().endsWith(".hdf")) {
               JOptionPane.showMessageDialog(this, "Debe guardar el fichero en un formato válido (.hdf)");
               menuItGuardarActionPerformed(evt);
            }else{
                try {
                    guardarDatos(fichero);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex){
                    //Aqui deberia añadir algo y eso
                }
            }
        }
    }

/**
 * Ejecuta el menú de cargar partida. Por defecto, el programa buscará archivos
 * con extensión .hdf
 * 
 * @param evt 
 */ 
    private void menuItCargarActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser eligeFichero;        
        eligeFichero = new JFileChooser();
        FileNameExtensionFilter filter;
        File fichero;
        
        filter = new FileNameExtensionFilter("Partidas de Hundir La Flota (.hdf)", "hdf");
        eligeFichero.setFileFilter(filter);
        int seleccion = eligeFichero.showOpenDialog(null);
        
        if (seleccion == JFileChooser.APPROVE_OPTION){            
            fichero = eligeFichero.getSelectedFile();
            try {
                cargarDatos(Libs.cargaRegistro(fichero));
                faseColocarPiezas=false;
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "El fichero indicado no existe");
                menuItCargarActionPerformed(evt);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    
    /**
     * guardarDatos recorre las diferentes variables de juego y las codifica,
     * para posteriormente grabar su valor en el fichero indicado mediante el uso
     * de grabarDatos
     * 
     * @param fichero fichero donde se guardarán los datos
     * @throws Exception 
     */
    
    public void guardarDatos(File fichero) throws Exception{
        //Tablero defensa del jugador
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                if (tableroDefensa.getBtCasilla()[i][j].getBackground()==Libs.colorAgua) {
                    Libs.grabarDatos(fichero, 0);
                }else if (tableroDefensa.getBtCasilla()[i][j].getBackground()==Libs.colorBarco){
                    Libs.grabarDatos(fichero, 1);
                }else if (tableroDefensa.getBtCasilla()[i][j].getBackground()==Libs.colorTocado){
                    Libs.grabarDatos(fichero, 2);
                }else if (tableroDefensa.getBtCasilla()[i][j].getBackground()==Libs.colorAguaTocado){
                    Libs.grabarDatos(fichero, 3);
                }else if (tableroDefensa.getBtCasilla()[i][j].getBackground()==Libs.colorHundido){
                    Libs.grabarDatos(fichero, 5);
                }else{
                    throw new Exception();
                }
            }
        }
        
        //Tablero ataque del jugador
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                if (tableroAtaque.getBtCasilla()[i][j].getBackground()==Libs.colorAgua) {
                    Libs.grabarDatos(fichero, 0);
                }else if (tableroAtaque.getBtCasilla()[i][j].getBackground()==Libs.colorTocado){
                    Libs.grabarDatos(fichero, 2);
                }else if (tableroAtaque.getBtCasilla()[i][j].getBackground()==Libs.colorHundido){
                    Libs.grabarDatos(fichero, 5);
                }else{
                    Libs.grabarDatos(fichero, 4);
                }
            }
        }
        
        //Tablero defensa IA
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                if (barcosIA[i][j]) {
                    Libs.grabarDatos(fichero, 1);
                }else{
                    Libs.grabarDatos(fichero, 0);
                }
            }
        }
        
        //turno de juego
        
        if (turnoJugador) {
            Libs.grabarDatos(fichero, 1);
        }else{
            Libs.grabarDatos(fichero, 0);
        }
        
        //puntos de los jugadores
        Libs.grabarDatos(fichero, impactosJugador);
        Libs.grabarDatos(fichero, impactosIA);
        
        //tamaño y estado del tablero de tiradas de la IA
        Libs.grabarDatos(fichero,tableroAtaqueIA.size());
        for (int i = 0; i < tableroAtaqueIA.size(); i++) {
            Libs.grabarDatos(fichero,tableroAtaqueIA.get(i)[0]);
            Libs.grabarDatos(fichero,tableroAtaqueIA.get(i)[1]);
        }
        
        Libs.grabarDatos(fichero,tableroAtaqueIAPrioritario.size());
        for (int i = 0; i < tableroAtaqueIAPrioritario.size(); i++) {
            Libs.grabarDatos(fichero,tableroAtaqueIAPrioritario.get(i)[0]);
            Libs.grabarDatos(fichero,tableroAtaqueIAPrioritario.get(i)[1]);
        }
    }
    
    /**
     * cargarDatos descodifica el contenido de cargaRegistro y genera una partida
     * válida de Hundir La Flota, inciializando los valore y botones correspondientes
     * 
     * @param cargaRegistro array de enteros que posee los datos del juego a cargar
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception 
     */
    public void cargarDatos(int[] cargaRegistro) throws FileNotFoundException, IOException, Exception{
        //Tablero defensa del jugador
        int posicion=0;
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                if (cargaRegistro[posicion]==0) {
                    tableroDefensa.getBtCasilla()[i][j].setBackground(Libs.colorAgua);
                }else if (cargaRegistro[posicion]==1){
                    tableroDefensa.getBtCasilla()[i][j].setBackground(Libs.colorBarco);
                }else if (cargaRegistro[posicion]==2){
                    tableroDefensa.getBtCasilla()[i][j].setBackground(Libs.colorTocado);
                }else if (cargaRegistro[posicion]==3){
                    tableroDefensa.getBtCasilla()[i][j].setBackground(Libs.colorAguaTocado);
                }else if (cargaRegistro[posicion]==5){
                    tableroDefensa.getBtCasilla()[i][j].setBackground(Libs.colorHundido);
                }else{
                    throw new Exception();
                }
                posicion++;
            }
        }
        
        //Tablero ataque del jugador
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                if (cargaRegistro[posicion]==0) {
                    tableroAtaque.getBtCasilla()[i][j].setBackground(Libs.colorAgua);
                }else if (cargaRegistro[posicion]==2){
                    tableroAtaque.getBtCasilla()[i][j].setBackground(Libs.colorTocado);
                }else if (cargaRegistro[posicion]==5){
                    tableroAtaque.getBtCasilla()[i][j].setBackground(Libs.colorHundido);
                }else{
                    tableroAtaque.getBtCasilla()[i][j].setBackground(null);
                }
                posicion++;
            }
        }
        
        //Tablero defensa IA
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                if (cargaRegistro[posicion]==1) {
                    barcosIA[i][j]=true;
                }else{
                    barcosIA[i][j]=false;
                }
                posicion++;
            }
        }
        
        //turno de juego
        if (cargaRegistro[posicion]==1) {
            turnoJugador=true;
        }else{
            turnoJugador=false;
         }
        posicion++;
        
        //puntos de los jugadores
        impactosJugador=cargaRegistro[posicion];
        posicion++;
        impactosIA=cargaRegistro[posicion];
        posicion++;
                
        //cargo el tablero de la IA
        int dimTableroIA = cargaRegistro[posicion];
        posicion++;
        
        int posi;
        int posj;
        tableroAtaqueIA.clear();
        for (int i = 0; i < dimTableroIA; i++) {
            posi=cargaRegistro[posicion];
            posicion++;
            posj=cargaRegistro[posicion];
            posicion++;
            tableroAtaqueIA.add(new int[] {posi,posj});
        }
        
        int dimTableroIAPrioritario = cargaRegistro[posicion];
        posicion++;
        
        tableroAtaqueIAPrioritario.clear();
        for (int i = 0; i < dimTableroIAPrioritario; i++) {
            posi=cargaRegistro[posicion];
            posicion++;
            posj=cargaRegistro[posicion];
            posicion++;
            tableroAtaqueIAPrioritario.add(new int[] {posi,posj});
        }
        
        //Actualizo el interfaz
        lbImpactosJugador.setText("Barcos vivos jugador: "+impactosJugador);
        lbImpactosIA.setText("Barcos vivos IA: "+impactosIA);
        
        for (int i = 0; i < btColocaBarco.length; i++) {
            btColocaBarco[i].setEnabled(false);
        }
        
        btEmpiezaJuego.setEnabled(false);
        menuItGuardar.setEnabled(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
