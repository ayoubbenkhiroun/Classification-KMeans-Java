import au.com.bytecode.opencsv.CSVReader;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Lecture_Fichier {

    public static void Read(String chemin, char separateur){

       String [][] Tableau = new String[100000][17];
        int i = 0;
        int j = 0;
        int nbr_ligne = 0;

        try {
            CSVReader reader = new CSVReader(new FileReader(chemin), separateur);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                //System.out.println("taille : " + nextLine.length);


           if (nextLine.length == 17)
           {
                    boolean b = true;
                   j = 0;
                   while ((j<17) && (b == true))
               {
                   
                   if (nextLine[j].equals("unknown"))
                       b = false;
                   j += 1;
               }

               
               if (b == true){

                for (j = 0; j<17; j++)
                {
                   Tableau[i][j] = nextLine[j];
                    //System.out.println(Tableau[i][j]);
                }
               }
                i+=1;
                }
           







     CSVWriter writer = new CSVWriter(new FileWriter("D:\\yourfile.csv"), '\t');
     // feed in your array (or convert your data to an array)
     String File_data = "";
     String[] entries = null;
     String ligne = "";
     
     for (int x=0 ; x<i ; x++ )
     {   
         
         ligne = Tableau[x][0];

         for (int y=1 ; y<17 ; y++ )
         
             {
                ligne = ligne+","+Tableau[x][y];
      
             }

        //System.out.println(ligne);
        

        entries = ligne.split(";");
        writer.writeNext(entries);
    
         }
     


     
     
     
     writer.close();

        



                     
                           // System.out.println(nextLine[i]);



                //System.out.println("Les valeur null :"+Val_null);
//                System.out.println(nextLine[0] + nextLine[1] + "etc...");
            }
            // TODO add your handling code here:
            // TODO add your handling code here:
        } catch (IOException ex) {
            Logger.getLogger(Accueil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  public static void main(String[] args) {

       

  }
}