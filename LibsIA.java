/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package HundirLaFlota;

import javax.swing.JButton;

/**
 *
 * @author Juan
 */
public class LibsIA {

    /**
     * colocaBarcosIA indica qué barcos debe colocar la IA
     * 
     */
    public static boolean[][] colocaBarcosIA(boolean[][] barcosIA){
        barcosIA=situaBarco(barcosIA,1,4);
        barcosIA=situaBarco(barcosIA,2,3);
        barcosIA=situaBarco(barcosIA,3,2);
        barcosIA=situaBarco(barcosIA,4,1);
        return barcosIA;
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
     * @param barcosIA tablero de defensa de la IA
     * @param numBarcos numero de barcos a colocar
     * @param dimBarcoIA dimension de dichos barcos
     * @return tablero de defensa de la IA, actualizado
     */
    public static boolean[][] situaBarco(boolean[][] barcosIA, int numBarcos, int dimBarcoIA){
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
                if (j+dimBarcoIA>=barcosIA.length) {
                    colisionIA=true;
                }else{
                    for (int k = Math.max(0,j-1); k < Math.min(j+dimBarcoIA+1,barcosIA.length); k++) {
                        for (int l = Math.max(0,i-1); l < Math.min(i+2,barcosIA.length); l++) {
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
               if (i+dimBarcoIA>=barcosIA.length) {
                    colisionIA=true;
                }else{
                    for (int k = Math.max(0,i-1); k < Math.min(i+dimBarcoIA+1,barcosIA.length); k++) {
                        for (int l = Math.max(0,j-1); l < Math.min(j+2,barcosIA.length); l++) {
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
        return barcosIA;
    }
    
    public static boolean estaHundidoIA(JButton[][] btCasilla, int i,int j){
               
        int[] casillaDer = {i,j+1};
        int[] casillaIzq = {i,j-1};
        int[] casillaArr = {i-1,j};
        int[] casillaAbj = {i+1,j};
        
        while (casillaDer[1]<btCasilla.length
            && btCasilla[casillaDer[0]][casillaDer[1]].getBackground()!=Libs.colorAgua
            && btCasilla[casillaDer[0]][casillaDer[1]].getBackground()!=Libs.colorAguaTocado){
            
            if (btCasilla[casillaDer[0]][casillaDer[1]].getBackground()!=Libs.colorTocado){
                return false;
            }
            casillaDer[1]++;
        }
        
        while (casillaIzq[1]>=0 
            && btCasilla[casillaIzq[0]][casillaIzq[1]].getBackground()!=Libs.colorAgua
            && btCasilla[casillaIzq[0]][casillaIzq[1]].getBackground()!=Libs.colorAguaTocado){
            
            if (btCasilla[casillaIzq[0]][casillaIzq[1]].getBackground()!=Libs.colorTocado){
                return false;
            }
            casillaIzq[1]--;
        }
        
        while (casillaAbj[0]<btCasilla.length
            && btCasilla[casillaAbj[0]][casillaAbj[1]].getBackground()!=Libs.colorAgua
            && btCasilla[casillaAbj[0]][casillaAbj[1]].getBackground()!=Libs.colorAguaTocado){
            
            if (btCasilla[casillaAbj[0]][casillaAbj[1]].getBackground()!=Libs.colorTocado){
                return false;
            }
            casillaAbj[0]++;
        }
        
        while (casillaArr[0]>=0 
            && btCasilla[casillaArr[0]][casillaArr[1]].getBackground()!=Libs.colorAgua
            && btCasilla[casillaArr[0]][casillaArr[1]].getBackground()!=Libs.colorAguaTocado){
            
            if (btCasilla[casillaArr[0]][casillaArr[1]].getBackground()!=Libs.colorTocado){
                return false;
            }
            casillaArr[0]--;
        }
        return true;
    }
}
