# WizardGame 2

My game project for my "Designing Object Oriented Applications" (in Romanian "Proiectarea
aplicațiilor orientate obiect") course.

I wrote a report about it (in Romanian), you can see the $\LaTeX$ source code in the
[report](./report/) folder.

You can see the repository as it was on the day I submitted it for grading at commit [TO BE FILLED IN].

## Gameplay

The game is inspired by [Vampire Survivors](https://store.steampowered.com/app/1794680/Vampire_Survivors/)
and [Holocure](https://kay-yu.itch.io/holocure), so playing it should feel similar to playing
those games.

## The WizardVerse

WizardGame 2 is part of the greater WizardVerse, an alternative reality where magic is real and
Michael Jackson died in 1964. (don't ask)

Games in the series include:

- [WizardGame](https://github.com/RealKC/WizardGame) - the game that started it all, you must stop
Adrian, the Shaman of Neamț from destroying the Carpathian Mountains.
- WizardGame 2 - you are here! Cristian, the Necromancer of Neamț wishes to take revenge on you
- [WizardGame: The War](https://github.com/Kime78/WizardGame-The-War) - a prequel/side-story telling
how Mihai lost his arm before the original game.
- [Mesterul Grigore](https://github.com/Eduard975/PAOO_Grigore) - a game set in the future when
magic and technology have been brought together, fight as the son of Eduard as the now sentient
programming languages are rebelling in grief as Eduard has died.

## Building

I used IDEA as a "build system", so that is the easiest way to build the game, however you will
also need an sqlite3 JDBC driver jar (IDEA expects it to be named `sqlite-jdbc-3.7.2.jar`, and in
the root, our professor provided us with [this link](https://jar-download.com/artifacts/org.xerial/sqlite-jdbc/3.8.11.2/source-code))
and [Gson](https://github.com/google/gson) (IDEA expects it to be named `gson-2.10.1.jar`).

## License

The source code of the game is licensed under the [EUPL v1.2](./LICENSE.txt).

For licensing information about sprites, see [res/textures/README.md](./res/textures/README.md).

For licensing information about sounds, see [res/ost/README.md](./res/ost/README.md).
