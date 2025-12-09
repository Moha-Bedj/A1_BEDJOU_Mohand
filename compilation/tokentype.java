
public enum tokentype {
        // mots-clés
        TRY, CATCH, INT, FLOAT, DOUBLE, CHAR, WHILE, IF,
        ELSE, DO, PUBLIC, CLASS, BEDJOU, MOHAND,
        FOR, FOREACH, SWITCH, CASE,

        // identifiants et littéraux
        IDENTIFIANT, ENTIER, REEL,

        // délimiteurs
        ACOLAD_OUV, ACOLAD_FER,
        PAR_OUV, PAR_FER,
        POINT_VIRGULE, VIRGULE,

        // opérateurs arithmétiques
        PLUS, MOINS, FOIS, DIVISE,

        // affectation / égalité
        AFFECTATION, EGAL,

        // comparaisons
        INFERIEUR, INFERIEUR_EGAL,
        SUPERIEUR, SUPERIEUR_EGAL,

        // fin / erreur
        EOF, ERREUR, TYPE
    }
