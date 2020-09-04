
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ayoub
 */
public class Ecrire_Fichier {
    public static void Write(String chemin, char separateur) throws IOException{
         CSVWriter writer = new CSVWriter(new FileWriter(chemin), '\t');
     // feed in your array (or convert your data to an array)
     String[] entries = "first#second#\nthird".split(";");
     writer.writeNext(entries);
	writer.close();
    }
    public static void main(String[] args) {



  }
}
