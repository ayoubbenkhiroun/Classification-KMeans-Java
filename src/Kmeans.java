
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.applet.Applet;
import java.util.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/**
 * Cette applet implémente la méthode k-means. Il s'agit d'une méthode de classification non supervisée 
 * (également appelée méthode de clustering), peut être utilisé pour diviser des vecteurs de
 * caractéristiques dans l'espace caractéristique dans les centres urbains ou des clusters
 *
 * @author Jens Spehr
 *
 */
public class Kmeans extends Applet implements Runnable {

    Vector CrossList;
    /** Enthält alle Merkmalsvektoren */
    Vector Centroids;
    /** Enthält die Schwerpunkte der Cluster */
    Choice SubsetChoice;
    /** Steuerelement */
    Button StartButton, RestartButton, ResetButton, RunButton, DrawGButton;
    /** Buttons*/
    Checkbox history;
    /** Checkbox */
    Thread Go;
    /** Thread für den Run-Modus. */
    int step;
    /** Aktueller Schritt, in dem sich der Algorithmus befindet. */
    int subset;
    /** Anzahl der Cluster */
    Random rand;
    /** Zufallsvariable*/
    boolean abort;
    
    private Button TestButon;
    private TextField textField1;
    private TextField textField2;
    private Button ClassButton;
    private TextField textAge;
    private TextField textBalance;
    private Label labeltitre;

    /** Abbruchkriterium */
    /** Erstellt das Graphic User Interface (GUI). */
    public void init() {
        rand = new Random();
        Centroids = new Vector();


        labeltitre = new java.awt.Label("Classification de clients de banque");
        add(labeltitre);

        StartButton = new Button("Start");
        add(StartButton);
        StartButton.setEnabled(false);

        RestartButton = new Button("New Start");
        add(RestartButton);
        RestartButton.setEnabled(false);

        ResetButton = new Button("Reset");
        add(ResetButton);
        ResetButton.setEnabled(false);




        RunButton = new Button("Run");
        add(RunButton);
        RunButton.setEnabled(false);

        DrawGButton = new Button("Draw Cluster");
        add(DrawGButton);

        CrossList = new Vector();

        SubsetChoice = new Choice();
        SubsetChoice.addItem("2");
        SubsetChoice.addItem("3");
        SubsetChoice.addItem("4");
        SubsetChoice.addItem("5");
        SubsetChoice.addItem("6");
        SubsetChoice.addItem("7");
        SubsetChoice.addItem("8");
        add(SubsetChoice);

        history = new Checkbox("Show History");
        add(history);

        subset = 2;
        step = -1;

        textAge = new java.awt.TextField("Age");
        add(textAge);

        textBalance = new java.awt.TextField("Balance");
        add(textBalance);

    }

    /** Dessine le texte, les vecteurs de caractéristiques et les priorités du Cluster. */
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(0, 50, 900, 600);

        StringBuffer buffer;
        if (step == 1) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.black);
        }
        buffer = new StringBuffer("Step 1: Lire les données à partir de fichier et placer le groupe initiales dans l'espace 2d.");
        g.drawString(buffer.toString(), 910, 570);

        if (step == 2) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.black);
        }
        buffer = new StringBuffer("Step 2: Attribuer chaque objet vers le groupe qui a le plus proche centre.");
        g.drawString(buffer.toString(), 910, 585);

        if (step == 3) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.black);
        }
        buffer = new StringBuffer("Step 3: Recalculer les positions des centres. ");
        g.drawString(buffer.toString(), 910, 600);

        if (step == 4) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.black);
        }
        buffer = new StringBuffer("Step 4: Si les positions des centres n'ont pas changé passer à l'étape suivante, sinon, passer à l'étape 2.");
        g.drawString(buffer.toString(), 910, 615);

        if (step == 5) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.black);
        }
        buffer = new StringBuffer("Step 5: End.");
        g.drawString(buffer.toString(), 910, 630);

        // Enregistre les vecteurs caractéristiques
        Cross s;
        int numShapes = CrossList.size();
        for (int i = 0; i < numShapes; i++) {
            s = (Cross) CrossList.elementAt(i);
            s.draw(g);
        }
        // Enregistre les zones de regroupement
        if (step != -1) {
            Quad t = new Quad();
            int numCent = Centroids.size();
            for (int i = 0; i < numCent; i++) {
                t = (Quad) Centroids.elementAt(i);
                t.hist = history.getState();

                t.draw(g);
            }
        }

    }

    /** Crée un vecteur nouvelle fonctionnalité avec un clic de souris. */
    public boolean mouseUp(Event e, int x, int y) {

        if ((step == -1) && (allowedMousePosition(x, y) == true)) {
            ResetButton.setEnabled(true);
            StartButton.setEnabled(true);
            RunButton.setEnabled(true);

            Cross s = new Cross();

            s.color = Color.black;
            s.x = x;
            s.y = y;
            CrossList.addElement(s);

            repaint();
        }

        return true;
    }

    /** Vérifier si la position courante de la souris est autorisé. */
    public boolean allowedMousePosition(int x, int y) {
        if ((x >= 5) && (y >= 55) && (x < 595) && (y < 345)) {
            return true;
        } else {
            return false;
        }
    }

    /** Passage automatique par la méthode k-means. Dans une étape s'attarde pendant environ 100ms. */
    public void run() {
        while (true) {
            if (step == -1) {
                this.step1();
            } else if (step == 1) {
                this.step2();
            } else if (step == 2) {
                this.step3();
            } else if (step == 3) {
                step = 4;
            } else if ((step == 4) && (abort == true)) {
                RestartButton.setEnabled(true);
                ResetButton.setEnabled(true);
                step = 5;
                repaint();
                Go.stop();
            } else if ((step == 4) && (abort == false)) {
                this.step2();
            }
            repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    /** Gérer le bouton des événements. */
    public boolean action(Event event, Object eventobject) {
        if ((event.target == StartButton)) {
            StartButton.setLabel("Step");
            RestartButton.setEnabled(true);
            if (step == -1) {
                this.step1();
            } else if (step == 1) {
                this.step2();
            } else if (step == 2) {
                this.step3();
            } else if (step == 3) {
                step = 4;
            } else if ((step == 4) && (abort == true)) {
                StartButton.setEnabled(false);
                RunButton.setEnabled(false);
                step = 5;
            } else if ((step == 4) && (abort == false)) {
                this.step2();
            }
            repaint();
            return true;
        }
        if ((event.target == RunButton)) {

            Go = new Thread(this);
            Go.start();
            StartButton.setEnabled(false);
            RestartButton.setEnabled(false);
            ResetButton.setEnabled(false);
            RunButton.setEnabled(false);

            return true;
        }


        if ((event.target == DrawGButton)) {
            if (CrossList.size() > 0) {
                Reset();
            }

            String SubsetString = SubsetChoice.getSelectedItem();
            if (SubsetString.equals("2")) {
                subset = 2;
            }
            if (SubsetString.equals("3")) {
                subset = 3;
            }
            if (SubsetString.equals("4")) {
                subset = 4;
            }
            if (SubsetString.equals("5")) {
                subset = 5;
            }
            if (SubsetString.equals("6")) {
                subset = 6;
            }
            if (SubsetString.equals("7")) {
                subset = 7;
            }
            if (SubsetString.equals("8")) {
                subset = 8;
            }

            ResetButton.setEnabled(true);
            StartButton.setEnabled(true);
            RunButton.setEnabled(true);








            String[][] Tableau = new String[100000][7];
            int i = 0;
            int j = 0;
            int nbr_ligne = 0;

            try {
                CSVReader reader = new CSVReader(new FileReader("c:\\mon_fichier.csv"), ';');
                String[] nextLine;

                while ((nextLine = reader.readNext()) != null) {

                    if (nextLine.length == 7) {
                        for (j = 0; j < 7; j++) {
                            Tableau[i][j] = nextLine[j];
                        }

                        i += 1;
                    }
                }


                /**
                 * Calculer le valeur min et le valeur max
                 */
//     int min1 = Integer.parseInt(Tableau[1][1]);
//     int max1 = Integer.parseInt(Tableau[1][1]);
//
//     int min2 = Integer.parseInt(Tableau[1][5]);
//     int max2 = Integer.parseInt(Tableau[1][5]);
//
//     for (int x=2 ; x<i ; x++ )
//        {
//            if (Integer.parseInt(Tableau[x][1]) < min1)
//            {
//                min1 = Integer.parseInt(Tableau[x][1]);
//            }
//            if (Integer.parseInt(Tableau[x][1]) > max1)
//            {
//                max1 = Integer.parseInt(Tableau[x][1]);
//            }
//
//
//             if (Integer.parseInt(Tableau[x][5]) < min2)
//            {
//                min2 = Integer.parseInt(Tableau[x][5]);
//            }
//            if (Integer.parseInt(Tableau[x][5]) > max2)
//            {
//                max2 = Integer.parseInt(Tableau[x][5]);
//            }
//
//
//         }
                for (int compt = 1; compt < i; compt++) {

                    int x = 100;
                    int y = 100;

                    if (allowedMousePosition(x, y) == true) {
                        Cross s = new Cross();
                        s.color = Color.black;
                        s.x = 100 + Integer.parseInt(Tableau[compt][0]) * 8;//700;
                        s.y = 100 + Integer.parseInt(Tableau[compt][4]) / 150;//1100;

                        CrossList.addElement(s);
                    }

                }


                repaint();
                return true;



            } catch (IOException ex) {
                Logger.getLogger(Accueil.class.getName()).log(Level.SEVERE, null, ex);
            }

        }



        if ((event.target == RestartButton) && (step != -1)) {
            step = -1;
            abort = false;
            Centroids.removeAllElements();
            int numShapes = CrossList.size();
            Cross s;
            for (int i = 0; i < numShapes; i++) {
                s = (Cross) CrossList.elementAt(i);
                s.color = Color.black;
            }
            StartButton.setLabel("Start");
            StartButton.setEnabled(true);
            ResetButton.setEnabled(true);
            RunButton.setEnabled(true);

            this.repaint();
            return true;
        }
        if ((event.target == ResetButton)) {
            Reset();
            return true;
        }
        return true;
    }

    /** Réinitialiser l'applet par la suppression de tous les vecteurs de caractéristiques et les priorités du cluster. */
    public void Reset() {
        step = -1;
        abort = false;
        Centroids.removeAllElements();
        int numShapes = CrossList.size();
        Cross s;
        for (int i = 0; i < numShapes; i++) {
            s = (Cross) CrossList.elementAt(i);
            s.color = Color.white;
        }
        StartButton.setLabel("Start");
        StartButton.setEnabled(false);
        RestartButton.setEnabled(false);
        ResetButton.setEnabled(false);
        RunButton.setEnabled(false);
        CrossList.removeAllElements();

        this.repaint();
    }

    /** Répartis au hasard dans la grappe se concentre l'espace caractéristique 2d. */
    public void step1() {
        abort = false;
        String SubsetString = SubsetChoice.getSelectedItem();
        if (SubsetString.equals("2")) {
            subset = 2;
        }
        if (SubsetString.equals("3")) {
            subset = 3;
        }
        if (SubsetString.equals("4")) {
            subset = 4;
        }
        if (SubsetString.equals("5")) {
            subset = 5;
        }
        if (SubsetString.equals("6")) {
            subset = 6;
        }
        if (SubsetString.equals("7")) {
            subset = 7;
        }
        if (SubsetString.equals("8")) {
            subset = 8;
        }
        int numShapes = CrossList.size();
        boolean ch[] = new boolean[numShapes];
        for (int i = 0; i < numShapes; i++) {
            ch[i] = false;
        }
        for (int i = 0; i < subset;) {
            Cross s;
            Quad p = new Quad();
            int r = Math.abs(rand.nextInt() % numShapes);
            if (ch[r] == false) {
                s = (Cross) CrossList.elementAt(r);
                p.x = s.x;
                p.y = s.y;
                if (i == 0) {
                    p.color = Color.green;
                } else if (i == 1) {
                    p.color = Color.red;
                } else if (i == 2) {
                    p.color = Color.blue;
                } else if (i == 3) {
                    p.color = Color.yellow;
                } else if (i == 4) {
                    p.color = Color.orange;
                } else if (i == 5) {
                    p.color = Color.magenta;
                } else if (i == 6) {
                    p.color = Color.cyan;
                } else if (i == 7) {
                    p.color = Color.lightGray;
                } else if (i == 8) {
                    p.color = Color.darkGray;
                }
                p.History = new Vector();

                Centroids.addElement(p);

                ch[r] = true;
                i++;
            }
        }
        step = 1;
    }

    /** Affectation de chaque vecteurs de caractéristiques au centre de gravité du cluster prochaine */
    public void step2() {
        Cross s;
        Quad p;
        int numShapes = CrossList.size();
        for (int i = 0; i < numShapes; i++) {
            s = (Cross) CrossList.elementAt(i);

            int numCent = Centroids.size();
            int min = 0;
            double dist_min = 99999999.9;
            for (int j = 0; j < numCent; j++) {
                p = (Quad) Centroids.elementAt(j);

                double dist = Point.distance(s.x, s.y, p.x, p.y);
                if (dist < dist_min) {
                    dist_min = dist;
                    min = j;
                }
            }
            p = (Quad) Centroids.elementAt(min);
            s.color = p.color;
        }
        step = 2;
    }

    public void step3() {
        Quad p;
        Cross s;
        Point m = new Point();
        double changes = 0.0;
        int numCent = Centroids.size();
        for (int j = 0; j < numCent; j++) {
            p = (Quad) Centroids.elementAt(j);
            m.x = 0;
            m.y = 0;
            int Count = 0;
            int numShapes = CrossList.size();
            for (int i = 0; i < numShapes; i++) {
                s = (Cross) CrossList.elementAt(i);
                if (s.color == p.color) {
                    m.x += s.x;
                    m.y += s.y;
                    Count++;
                }
            }
            if (Count > 0) {
                changes += Point.distance(p.x, p.y, m.x / Count, m.y / Count);
                Point pt = new Point();
                pt.x = p.x;
                pt.y = p.y;
                p.History.addElement(pt);
                p.x = m.x / Count;
                p.y = m.y / Count;
            }
        }
        if (changes < 0.1) {
            abort = true;
            double dist_min = 99999999.9;
            Color Couleur_min = Color.WHITE;
            for (Object cent : Centroids) {
                Quad Q = (Quad) cent;
                //System.out.println(((Quad) cent).x + " | " + ((Quad) cent).y);


                double dist = Point.distance(Integer.parseInt(textAge.getText())*8,Integer.parseInt(textBalance.getText())/150, Q.x, Q.y);
                if (dist < dist_min) {
                    dist_min = dist;
                    Couleur_min = Q.color;
                }
                
            }
            
            String Col = "";
                   if (Couleur_min == Color.green) {
                    Col = "Vert";
                } else if (Couleur_min == Color.red) {
                   Col = "Rouge";
                } else if (Couleur_min == Color.blue) {
                    Col = "Blue foncé";
                } else if (Couleur_min == Color.yellow) {
                    Col = "Jaune";
                } else if (Couleur_min == Color.orange) {
                    Col = "Orangé";
                } else if (Couleur_min == Color.magenta) {
                    Col = "Rose";
                } else if (Couleur_min == Color.cyan) {
                    Col = "Bleu clair";
                } else if (Couleur_min == Color.lightGray) {
                    Col = "Gris clair";
                } else if (Couleur_min == Color.darkGray) {
                    Col = "Gris foncé";
                }
            System.out.println("Distance : "+dist_min+" *** Couleur : "+Col);
            JOptionPane.showMessageDialog(null,"Cette enregistrement appartien à la classe : "+Col,"Résultat",JOptionPane.INFORMATION_MESSAGE);

        }
        step = 3;
    }
}

class Quad {

    static public final int shapeRadius = 12;
    Color color;
    Vector History;
    int x;
    int y;
    boolean hist;

    void draw(Graphics g) {
        if ((hist == true) && (History.size() > 0)) {
            Point p1, p2;
            g.setColor(Color.black);
            for (int i = 0; i < History.size(); i++) {
                p1 = (Point) History.elementAt(i);
                if (i + 1 != History.size()) {
                    p2 = (Point) History.elementAt(i + 1);
                } else {
                    p2 = new Point();
                    p2.x = this.x;
                    p2.y = this.y;
                }
                g.drawLine(p1.x + 6, p1.y + 6, p2.x + 6, p2.y + 6);
            }
        }
        g.setColor(this.color);
        g.fillOval(this.x, this.y, shapeRadius, shapeRadius);
        g.setColor(Color.black);
        g.drawOval(this.x, this.y, shapeRadius, shapeRadius);

    }
}

class Cross {

    static public final int shapeRadius = 2;
    Color color;
    int x;
    int y;

    void draw(Graphics g) {
        g.setColor(this.color);
        g.drawLine(this.x - shapeRadius, this.y, this.x + shapeRadius, this.y);
        g.drawLine(this.x, this.y - shapeRadius, this.x, this.y + shapeRadius);
    }
}
