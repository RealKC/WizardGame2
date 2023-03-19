package PaooGame.GameWindow;

import javax.swing.*;
import java.awt.*;

/*! \class GameWindow
    \brief Implementeaza notiunea de fereastra a jocului.

    Membrul wndFrame este un obiect de tip JFrame care va avea utilitatea unei
    ferestre grafice si totodata si cea a unui container (toate elementele
    grafice vor fi continute de fereastra).
 */
public class GameWindow {
    private JFrame windowFrame;       /*!< fereastra principala a jocului*/
    private final String windowTitle;       /*!< titlul ferestrei*/
    private final int windowWidth;       /*!< latimea ferestrei in pixeli*/
    private final int windowHeight;      /*!< inaltimea ferestrei in pixeli*/

    private Canvas canvas;         /*!< "panza/tablou" in care se poate desena*/

    /*! \fn GameWindow(String title, int width, int height)
            \brief Constructorul cu parametri al clasei GameWindow

            Retine proprietatile ferestrei proprietatile (titlu, latime, inaltime)
            in variabilele membre deoarece vor fi necesare pe parcursul jocului.
            Crearea obiectului va trebui urmata de crearea ferestrei propriuzise
            prin apelul metodei BuildGameWindow()

            \param title Titlul ferestrei.
            \param width Latimea ferestrei in pixeli.
            \param height Inaltimea ferestrei in pixeli.
         */
    public GameWindow(String title, int width, int height) {
        windowTitle = title;    /*!< Retine titlul ferestrei.*/
        windowWidth = width;    /*!< Retine latimea ferestrei.*/
        windowHeight = height;   /*!< Retine inaltimea ferestrei.*/
        windowFrame = null;     /*!< Fereastra nu este construita.*/
    }

    /*! \fn private void BuildGameWindow()
        \brief Construieste/creaza fereastra si seteaza toate proprietatile
        necesare: dimensiuni, pozitionare in centrul ecranului, operatia de
        inchidere, invalideaza redimensionarea ferestrei, afiseaza fereastra.

     */
    public void buildGameWindow() {
        /// Daca fereastra a mai fost construita intr-un apel anterior
        /// se renunta la apel
        if (windowFrame != null) {
            return;
        }
        /// Aloca memorie pentru obiectul de tip fereastra si seteaza denumirea
        /// ce apare in bara de titlu
        windowFrame = new JFrame(windowTitle);
        /// Seteaza dimensiunile ferestrei in pixeli
        windowFrame.setSize(windowWidth, windowHeight);
        /// Operatia de inchidere (fereastra sa poata fi inchisa atunci cand
        /// este apasat butonul x din dreapta sus al ferestrei). Totodata acest
        /// lucru garanteaza ca nu doar fereastra va fi inchisa ci intregul
        /// program
        windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /// Avand in vedere ca dimensiunea ferestrei poate fi modificata
        /// si corespunzator continutul actualizat (aici ma refer la dalele
        /// randate) va recomand sa constrangeti deocamdata jucatorul
        /// sa se joace in fereastra stabilitata de voi. Puteti reveni asupra
        /// urmatorului apel ulterior.
        windowFrame.setResizable(false);
        /// Recomand ca fereastra sa apara in centrul ecranului. Pentru orice
        /// alte pozitie se va apela "wndFrame.setLocation(x, y)" etc.
        windowFrame.setLocationRelativeTo(null);
        /// Implicit o fereastra cand este creata nu este vizibila motiv pentru
        /// care trebuie setata aceasta proprietate
        windowFrame.setVisible(true);

        /// Creaza obiectul de tip canvas (panza) pe care se poate desena.
        canvas = new Canvas();
        /// In aceeasi maniera trebuiesc setate proprietatile pentru acest obiect
        /// canvas (panza): dimensiuni preferabile, minime, maxime etc.
        /// Urmotorul apel de functie seteaza dimensiunea "preferata"/implicita
        /// a obiectului de tip canvas.
        /// Functia primeste ca parametru un obiect de tip Dimension ca incapsuleaza
        /// doua proprietati: latime si inaltime. Cum acest obiect nu exista
        /// a fost creat unul si dat ca parametru.
        canvas.setPreferredSize(new Dimension(windowWidth, windowHeight));
        /// Avand in vedere ca elementele unei ferestre pot fi scalate atunci cand
        /// fereastra este redimensionata
        canvas.setMaximumSize(new Dimension(windowWidth, windowHeight));
        canvas.setMinimumSize(new Dimension(windowWidth, windowHeight));
        /// Avand in vedere ca obiectul de tip canvas, proaspat creat, nu este automat
        /// adaugat in fereastra trebuie apelata metoda add a obiectul wndFrame
        windowFrame.add(canvas);
        /// Urmatorul apel de functie are ca scop eventuala redimensionare a ferestrei
        /// ca tot ce contine sa poate fi afisat complet
        windowFrame.pack();
    }

    /*! \fn public int getWindowWidth()
        \brief Returneaza latimea ferestrei.
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /*! \fn public int getWindowHeight()
        \brief Returneaza inaltimea ferestrei.
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    /*! \fn public Canvas getCanvas()
        \brief Returneaza referinta catre canvas-ul din fereastra pe care se poate desena.
     */
    public Canvas getCanvas() {
        return canvas;
    }
}
