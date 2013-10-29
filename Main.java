package HundirLaFlota;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * Hundir la flota
 * 
 * El juego permite jugar contra una IA simple, mediante tableros de 10x10 casillas
 * Se divide en dos fases, la de colocación de pizas y la de juego
 * 
 * Constantes:
 * 
 * dimensionTablero: la dimensión de los tableros de juego
 * colorAgua: color inicial del tablero del jugador
 * colorBarco: indica donde ha colocado un barco el jugador
 * colorTocado: Indica los disparos que han tenido exito
 * colorAguaTocado: Indica los disparos que no han tenido exito
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
 * empiezaJuego: permite pasar a la fase de disparos, finaliza la de colocacion de piezas
 * limpiaTablero: resetea el juego
 * 
 * lbLetraDef, lbLetraAt, lbNumeroDef, lbNumeroAt: etiquetas para marcar las casillas de juego
 * 
 * lbImpactosJugador: Numero de impactos restantes del jugador
 * lbImpactosIA: Número de impactos restantes de la IA 
 * 
 * @author ActionPerformed
 * @version 1.0
 */
public class Main extends javax.swing.JFrame implements ActionListener, MouseListener, MouseWheelListener{

    final int dimensionTablero = 10;
    final Color colorAgua = new Color(0x6f, 0x8e, 0xc2);
    final Color colorBarco = new Color(0x55, 0x55, 0x55);
    final Color colorTocado = new Color(0xc2, 0x23, 0x13);
    final Color colorAguaTocado = new Color(0xff, 0xff, 0xff);
    
    boolean orientacionBarco; 
    boolean colision; 
    boolean faseColocarPiezas; 
    
    JButton[][] tableroDefensa = new JButton[dimensionTablero][dimensionTablero];
    JButton[][] tableroAtaque = new JButton[dimensionTablero][dimensionTablero];
    
    JButton[] btColocaBarco = new JButton[4];
    int[] numColocaBarco;
    int dimBarco;
    
    int impactosJugador;
    int impactosIA;
    boolean turnoJugador;
    
    boolean[][] barcosIA = new boolean[dimensionTablero][dimensionTablero];
    
    JButton empiezaJuego = new JButton();
    JButton limpiaTablero = new JButton();
    
    String[] letra = {"A","B","C","D","E","F","G","H","I","J"};
    String[] numero = {"1","2","3","4","5","6","7","8","9","10"};
    
    JLabel[] lbLetraDef = new JLabel[dimensionTablero];
    JLabel[] lbNumeroDef = new JLabel[dimensionTablero];
    
    JLabel[] lbLetraAt = new JLabel[dimensionTablero];
    JLabel[] lbNumeroAt = new JLabel[dimensionTablero];
    
    JLabel lbImpactosJugador = new JLabel();
    JLabel lbImpactosIA = new JLabel();
    
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
        for (int i = 0; i < dimensionTablero; i++) {
            
            lbLetraDef[i] = new JLabel();
            add(lbLetraDef[i]);
            lbLetraDef[i].setBounds(10, 35*(i+1), 30, 30);
            lbLetraDef[i].setText(letra[i]);
            
            lbLetraAt[i] = new JLabel();
            add(lbLetraAt[i]);
            lbLetraAt[i].setBounds(410, 35*(i+1), 30, 30);
            lbLetraAt[i].setText(letra[i]);
            
            lbNumeroDef[i] = new JLabel();
            add(lbNumeroDef[i]);
            lbNumeroDef[i].setBounds(35*(i+1), 10, 30, 30);
            lbNumeroDef[i].setText(numero[i]);
            
            lbNumeroAt[i] = new JLabel();
            add(lbNumeroAt[i]);
            lbNumeroAt[i].setBounds(400+35*(i+1), 10, 30, 30);
            lbNumeroAt[i].setText(numero[i]);
            
            for (int j = 0; j < dimensionTablero; j++) {
                tableroDefensa[i][j] = new JButton();
                add(tableroDefensa[i][j]);
                tableroDefensa[i][j].addActionListener(this);
                tableroDefensa[i][j].setEnabled(true);
                tableroDefensa[i][j].setBounds(35*(j+1), 35*(i+1), 30, 30);
                tableroDefensa[i][j].setText("");
                tableroDefensa[i][j].addMouseWheelListener(this);
                tableroDefensa[i][j].addMouseListener(this);
                
                tableroAtaque[i][j] = new JButton();
                add(tableroAtaque[i][j]);
                tableroAtaque[i][j].addActionListener(this);
                tableroAtaque[i][j].setEnabled(true);
                tableroAtaque[i][j].setBounds(400+35*(j+1), 35*(i+1), 30, 30);
                tableroAtaque[i][j].setText("");
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
        
        add(empiezaJuego);
        empiezaJuego.addActionListener(this);
        empiezaJuego.setBounds(270,425,100,30);
        empiezaJuego.setText("Jugar");
        
        add(limpiaTablero);
        limpiaTablero.addActionListener(this);
        limpiaTablero.setBounds(270,465,100,30);
        limpiaTablero.setText("Limpiar");
        
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
        impactosJugador=30;
        impactosIA=30;
        numColocaBarco = new int[]{4, 3, 2, 1};
        turnoJugador = true; //será false en los chequeos posteriores al turno del jugador
        faseColocarPiezas=true;
        orientacionBarco=true;
        colision=false;
        dimBarco=0;
        
        //Activamos y añadimos texto a los botones de colocación de barcos
        for (int i = 0; i < btColocaBarco.length; i++) {
            btColocaBarco[i].setEnabled(true);
            btColocaBarco[i].setText(numColocaBarco[i]+" x Barco ["+(i+1)+"]");
        } 
        
        //empiezaJuego será true cuando se hayan colocado todos los barcos
        empiezaJuego.setEnabled(false);
        limpiaTablero.setEnabled(true);
        
        //Inicializamos los colores de las casillas de los diferentes tableros
        for (int i = 0; i < dimensionTablero; i++) {
            for (int j = 0; j < dimensionTablero; j++) {
                tableroDefensa[i][j].setBackground(colorAgua);
                tableroAtaque[i][j].setBackground(null);
                barcosIA[i][j]=false;
            }
        }
        
        //Añadimos texto inicial a los marcadores
        lbImpactosIA.setText("Barcos vivos IA: "+impactosIA);
        lbImpactosJugador.setText("Barcos vivos jugador: "+impactosJugador);
        
        // Creamos el tablero de defensa de la IA
        colocaBarcosIA();
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
                for (int k = i; k < Math.min(tableroDefensa.length, i+dimBarco); k++) {
                    tableroDefensa[k][j].setBackground(colorBarco);
                }
            }else{
                for (int k = j; k < Math.min(tableroDefensa.length, j+dimBarco); k++) {
                    tableroDefensa[i][k].setBackground(colorBarco);
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
                empiezaJuego.setEnabled(true);
            }
        }
    }
    
    /**
     * colocaBarcosIA indica qué barcos debe colocar la IA
     * 
     */
    public void colocaBarcosIA(){
        situaBarco(1,4);
        situaBarco(2,3);
        situaBarco(3,2);
        situaBarco(4,1);
    }
    
    /**
     * situaBarco se encarga de situar en el tablero de defensa de la IA cierta cantidad
     * de barcos de la dimensión indicada.
     * 
     * Para ello, el metodo generará 3 variables aleatorias, las cuales determinarán 
     * en qué casilla comenzará el barco y cuál será su orientación
     * 
     * Una vez generados estos valores, el programa comprobará si se generan colisiones 
     * con otros barcos anteriormente colocados, repitiendo en este caso la generación
     * de la totalidad de dichas variables aleatorias
     * 
     * @param numBarcos numero de barcos a colocar
     * @param dimBarcoIA dimension de dichos barcos
     */
    public void situaBarco(int numBarcos, int dimBarcoIA){
        int i;
        int j;
        double posBarco;
        boolean colisionIA;
        while(numBarcos>0){
            i=(int)(Math.random()*10);
            j=(int)(Math.random()*10);
            posBarco=Math.random();
            colisionIA=false;
            if (posBarco<0.5) { //Barco horizontal
                if (j+dimBarcoIA>=dimensionTablero) {
                    colisionIA=true;
                }else{
                    for (int k = Math.max(0,j-1); k < Math.min(j+dimBarcoIA+1,dimensionTablero); k++) {
                        for (int l = Math.max(0,i-1); l < Math.min(i+2,dimensionTablero); l++) {
                            if (barcosIA[l][k]) {
                                colisionIA=true;
                            }
                        }
                    }
                }
                if (!colisionIA) {
                    for (int k = j; k < j+dimBarcoIA; k++) {
                        barcosIA[i][k]=true;
                    }
                    numBarcos--;
                }
            }else{ //Barco vertical
               if (i+dimBarcoIA>=dimensionTablero) {
                    colisionIA=true;
                }else{
                    for (int k = Math.max(0,i-1); k < Math.min(i+dimBarcoIA+1,dimensionTablero); k++) {
                        for (int l = Math.max(0,j-1); l < Math.min(j+2,dimensionTablero); l++) {
                            if (barcosIA[k][l]) {
                                colisionIA=true;
                            }
                        }
                    }
                }
                if (!colisionIA) {
                    for (int k = i; k < i+dimBarcoIA; k++) {
                        barcosIA[k][j]=true;
                    }
                    numBarcos--;
                } 
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
        int i;
        int j;
        do{
            i=(int)(Math.random()*10);
            j=(int)(Math.random()*10);
        }while(tableroDefensa[i][j].getBackground().equals(colorTocado)
            || tableroDefensa[i][j].getBackground().equals(colorAguaTocado));
        
        if (tableroDefensa[i][j].getBackground().equals(colorAgua)) {
            tableroDefensa[i][j].setBackground(colorAguaTocado);
        }else{
            tableroDefensa[i][j].setBackground(colorTocado);;
            impactosJugador--;
            lbImpactosJugador.setText("Barcos vivos Jugador: "+impactosJugador);
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
                    tableroAtaque[i][j].setBackground(colorTocado);;
                }
            }
        }
        faseColocarPiezas=false;
        for (int i = 0; i < btColocaBarco.length; i++) {
            btColocaBarco[i].setEnabled(false);
        }
        empiezaJuego.setEnabled(false);
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
                    if (e.getSource()==tableroDefensa[i][j]){
                        if (orientacionBarco) { // Código apra barcos en vertical
                            if (i+dimBarco>dimensionTablero){
                                colision=true;
                                for (int k = i; k < dimensionTablero; k++) {
                                    if(tableroDefensa[k][j].getBackground()!=colorBarco){
                                        tableroDefensa[k][j].setBackground(Color.RED);
                                    }
                                }
                            }else{
                                colision=false;
                                for (int k = Math.max(0,i-1); k < Math.min(i+dimBarco+1,dimensionTablero); k++) {
                                    for (int l = Math.max(0,j-1); l < Math.min(j+2,dimensionTablero); l++) {
                                        if(tableroDefensa[k][l].getBackground()==colorBarco){
                                            colision=true;
                                        }
                                    }
                                }
                                if(colision){
                                    for (int k = i; k < i+dimBarco; k++) {
                                        if(tableroDefensa[k][j].getBackground()!=colorBarco){
                                            tableroDefensa[k][j].setBackground(Color.RED);
                                        }
                                    }
                                }else{
                                    for (int k = i; k < i+dimBarco; k++) {
                                        if(tableroDefensa[k][j].getBackground()!=colorBarco){
                                            tableroDefensa[k][j].setBackground(Color.GREEN);
                                        }
                                    }
                                }
                            }
                        }else{ //Código apra barcos en horizontal
                            if (j+dimBarco>dimensionTablero){
                                colision=true;
                                for (int k = j; k < dimensionTablero; k++) {
                                    if(tableroDefensa[i][k].getBackground()!=colorBarco){
                                        tableroDefensa[i][k].setBackground(Color.RED);
                                    }
                                }
                            }else{
                                colision=false;
                                for (int k = Math.max(0,j-1); k < Math.min(j+dimBarco+1,dimensionTablero); k++) {
                                    for (int l = Math.max(0,i-1); l < Math.min(i+2,dimensionTablero); l++) {
                                        if(tableroDefensa[l][k].getBackground()==colorBarco){
                                            colision=true;
                                        }
                                    }
                                }
                                if(colision){
                                    for (int k = j; k < j+dimBarco; k++) {
                                        if(tableroDefensa[i][k].getBackground()!=colorBarco){
                                            tableroDefensa[i][k].setBackground(Color.RED);
                                        }
                                    }
                                }else{
                                    for (int k = j; k < j+dimBarco; k++) {
                                        if(tableroDefensa[i][k].getBackground()!=colorBarco){
                                            tableroDefensa[i][k].setBackground(Color.GREEN);
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
                    if (e.getSource()==tableroDefensa[i][j]){
                        for (int k = i; k < Math.min(tableroDefensa.length, i+dimBarco); k++) {
                            if(tableroDefensa[k][j].getBackground()!=colorBarco){
                                tableroDefensa[k][j].setBackground(colorAgua);
                            }
                        }
                        for (int k = j; k < Math.min(tableroDefensa.length, j+dimBarco); k++) {
                            if(tableroDefensa[i][k].getBackground()!=colorBarco){
                                tableroDefensa[i][k].setBackground(colorAgua);
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
     * el boton limpiaTablero --> resetearemos el juego
     * 
     * el boton empiezaJuego --> entraremos en la fase de disparos
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
            if (evt.getSource()==limpiaTablero){
                menuItNuevoActionPerformed(evt); //Reseteamos el tablero
            }else if (evt.getSource()==empiezaJuego){
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
                            if (evt.getSource()==tableroDefensa[i][j]) {
                                colocaBarco(i,j);
                            }
                        }
                    }
                }
            }else{ //fase de disparos
                for (int i = 0; i < dimensionTablero; i++) {
                    for (int j = 0; j < dimensionTablero; j++) {
                        if (evt.getSource()==tableroAtaque[i][j] 
                         && tableroAtaque[i][j].getBackground()!= colorAgua
                         && tableroAtaque[i][j].getBackground()!= colorTocado) {
                            if (barcosIA[i][j]) {
                                tableroAtaque[i][j].setBackground(colorTocado);
                                impactosIA--;
                                lbImpactosIA.setText("Barcos vivos IA: "+impactosIA);
                            }else{
                                tableroAtaque[i][j].setBackground(colorAgua);
                            }
                            checkGame();

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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        menuJuego = new javax.swing.JMenu();
        menuItNuevo = new javax.swing.JMenuItem();
        menuItFinalizar = new javax.swing.JMenuItem();
        menuItSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
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

        menuItFinalizar.setText("Finalizar juego");
        menuItFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItFinalizarActionPerformed(evt);
            }
        });
        menuJuego.add(menuItFinalizar);

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
            .addGap(0, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 429, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    private void menuItFinalizarActionPerformed(java.awt.event.ActionEvent evt) {                                                
        muestraSolucion();
    }                                               

    private void menuItSalirActionPerformed(java.awt.event.ActionEvent evt) {                                            
        System.exit(0);
    }                                           

    private void menuItNuevoActionPerformed(java.awt.event.ActionEvent evt) {                                            
        initMoreComponents();
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
    // Variables declaration - do not modify                     
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuItFinalizar;
    private javax.swing.JMenuItem menuItNuevo;
    private javax.swing.JMenuItem menuItSalir;
    private javax.swing.JMenu menuJuego;
    // End of variables declaration                   
}
