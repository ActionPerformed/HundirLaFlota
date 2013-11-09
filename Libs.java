/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package HundirLaFlota;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Juan
 */
public class Libs {
    
    //Paleta de colores
    public final static Color colorAguaTocado = new Color(255, 255, 255);
    public final static Color colorBarco = new Color(34, 34, 34);
    public final static Color colorAgua = new Color(111, 142, 194);
    public final static Color colorTocado = new Color(204, 102, 0);
    public final static Color colorHundido = new Color(204, 0, 0);

    /**
     * Busca un valor en una lista de arrays
     * 
     * @param lista
     * @param valor
     * @return true si el valor está en la lista
     */
    public static boolean contieneValor(List<int[]> lista, int[] valor){
        Iterator <int[]> it;
        it= lista.iterator();
        while(it.hasNext()){
            if (Arrays.equals(it.next(),valor)){
                return true;
            }
        }
        return false;
    }

    /**
     * Borra un valor de una lista de elementos
     *
     * @param lista
     * @param valor
     * @return true si ha encontrado y borrado el elemento
     */
    public static boolean borraValor(List<int[]> lista, int[] valor){
        Iterator <int[]> it;
        it= lista.iterator();
        while(it.hasNext()){
            if (Arrays.equals(it.next(),valor)){
                it.remove();
                return true;
            }
        }
        return false;
    }
    
    /**
     * cargaRegistro usa randomAccessFile para leer el los valores enteros de
     * un archivo y devuelve un array con todos los datos leidos
     * 
     * 
     * @param fichero fichero a leer
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static int[] cargaRegistro(File fichero) throws FileNotFoundException, IOException{
        RandomAccessFile rndFile = new RandomAccessFile(fichero,"r"); //Random de solo lectura
        int[] cargaRegistro = new int[(int)(rndFile.length()/4)];
        for (int i = 0; i < cargaRegistro.length; i++) {
            cargaRegistro[i]=rndFile.readInt();
        }
        rndFile.close(); //Cerrar, esto siempre
        return cargaRegistro;
    }
    
     /**
     * Añade un valor entero al final del fichero indicado,
     * mediante el uso de RandomAccessFile
     *
     * @param fichero fichero donde se añadirá el valor entero
     * @param entero valor entero que se añadirá al fichero
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void grabarDatos(File fichero, int entero) throws FileNotFoundException, IOException{
        RandomAccessFile rndFile = new RandomAccessFile(fichero,"rw"); //creo un RandomAccessFile que permite lectura y escritura (RW)
        rndFile.seek(rndFile.length()); //Me posiciono, mediante el seek, al final del fichero
        rndFile.writeInt(entero); // grabo el dato
        rndFile.close();
    }
}
