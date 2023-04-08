package WizardGame2.Graphics;

import java.awt.image.BufferedImage;

/*! \class public class Assets
    \brief Clasa incarca fiecare element grafic necesar jocului.

    Game assets include tot ce este folosit intr-un joc: imagini, sunete, harti etc.
 */
public class Assets {
    public static SpriteSheet characters;
    public static BufferedImage tempMap;

    /*! \fn public static void Init()
        \brief Functia initializaza referintele catre elementele grafice utilizate.

        Aceasta functie poate fi rescrisa astfel incat elementele grafice incarcate/utilizate
        sa fie parametrizate. Din acest motiv referintele nu sunt finale.
     */
    public static void init() {
        characters = new SpriteSheet(ImageLoader.loadImage("/textures/characters.png"));
        tempMap = ImageLoader.loadImage("/textures/bigmap.png");
    }
}
