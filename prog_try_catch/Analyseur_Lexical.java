import java.util.ArrayList;
import java.util.List;

public class Analyseur_Lexical {
    private String codeSource;
    private int position;
    private int ligne;
    private final List<String> erreurs = new ArrayList<>();

    public Analyseur_Lexical(String codeSource) {
        this.codeSource = codeSource + "#"; // sentinel EOF
        this.position = 0;
        this.ligne = 1;
    }

    private static final int Auto_ident[][] = {
        {1,-1,1,-1},
        {1,1,2,-1},
        {1,1,2,-1}
    };

    public void reset() {
        this.position = 0;
        this.ligne = 1;
        erreurs.clear();
    }


    private char charcurrent() {
        if (position >= codeSource.length()) return '#';
        return codeSource.charAt(position);
    }

    public List<String> getErreurs() {
        return erreurs;
    }

    private void avvance() {
        if (position < codeSource.length()) {
            if (codeSource.charAt(position) == '\n') ligne++;
            position++;
        }
    }

    private void skipSpacesAndComments() {

    boolean continuer = true;

    while (continuer) {
        continuer = false;

        // --- 1) Ignorer les espaces et retours ligne ---
        while (true) {
            char c = charcurrent();
            if (c == ' ' || c == '\t' || c == '\r') {
                avvance();
            } else if (c == '\n') {
                avvance();
            } else break;
        }

        // --- 2) Commentaire ligne // ---
        if (charcurrent() == '/' && position + 1 < codeSource.length()
            && codeSource.charAt(position + 1) == '/') {

            // avancer jusqu’à la fin de la ligne
            while (charcurrent() != '\n' && charcurrent() != '#') {
                avvance();
            }
            continuer = true;
        }

        // --- 3) Commentaire multi-lignes /* ... */ ---
        if (charcurrent() == '/' && position + 1 < codeSource.length()
            && codeSource.charAt(position + 1) == '*') {

            avvance(); // /
            avvance(); // *

            while (true) {
                char c = charcurrent();

                if (c == '#') break; // fin fichier sans fermer : on sort

                if (c == '\n') ligne++;

                // fermeture */
                if (c == '*' && position + 1 < codeSource.length()
                    && codeSource.charAt(position + 1) == '/') {
                    avvance();
                    avvance();
                    break;
                }
                avvance();
            }
            continuer = true;
        }
    }
}


    static int classe(char c){
        if(Character.isLetter(c)) return 0;
        if(Character.isDigit(c)) return 1;
        if (c == '_') return 2;
        return 3;
    }

    private tokens identifierORkeyword() {
        StringBuilder characters = new StringBuilder();
        int state = 0;
        while(true) {
            char currentChar = charcurrent();
            int cl = classe(currentChar);
            int nextState = Auto_ident[state][cl];
            if (nextState == -1) break;
            state = nextState;
            characters.append(currentChar);
            avvance();
        }
        
        String lexeme = characters.toString();
        return switch (lexeme) {
            case "try" -> new tokens(tokentype.TRY, lexeme, ligne);
            case "catch" -> new tokens(tokentype.CATCH, lexeme, ligne);
            case "int" -> new tokens(tokentype.INT, lexeme, ligne);
            case "float" -> new tokens(tokentype.FLOAT, lexeme, ligne);
            case "double" -> new tokens(tokentype.DOUBLE, lexeme, ligne);
            case "char" -> new tokens(tokentype.CHAR, lexeme, ligne);
            case "while" -> new tokens(tokentype.WHILE, lexeme, ligne);
            case "if" -> new tokens(tokentype.IF, lexeme, ligne);
            case "else" -> new tokens(tokentype.ELSE, lexeme, ligne);
            case "do" -> new tokens(tokentype.DO, lexeme, ligne);
            case "public" -> new tokens(tokentype.PUBLIC, lexeme, ligne);
            case "class" -> new tokens(tokentype.CLASS, lexeme, ligne);
            case "bedjou" -> new tokens(tokentype.BEDJOU, lexeme, ligne);
            case "mohand" -> new tokens(tokentype.MOHAND, lexeme, ligne);
            case "switch" -> new tokens(tokentype.SWITCH, lexeme, ligne);
            case "case" -> new tokens(tokentype.CASE, lexeme, ligne);
            case "for" -> new tokens(tokentype.FOR ,lexeme, ligne);
            case "foreach" -> new tokens(tokentype.FOREACH, lexeme, ligne);      
            default -> new tokens(tokentype.IDENTIFIANT, lexeme, ligne);
        };
        
    }

    private tokens numbers() {
        StringBuilder characters = new StringBuilder();
        while (Character.isDigit(charcurrent())) {
            characters.append(charcurrent());
            avvance();
        }
        if (charcurrent() == '#' || !Character.isDigit(charcurrent()) && charcurrent() != '.') {
            return new tokens(tokentype.ENTIER, characters.toString(), ligne);
        }
        if (charcurrent() == '.') {
            if (position + 1 < codeSource.length() && Character.isDigit(codeSource.charAt(position + 1))) {
                characters.append('.');
                avvance();
                while (Character.isDigit(charcurrent())) {
                    characters.append(charcurrent());
                    avvance();
                }
                return new tokens(tokentype.REEL, characters.toString(), ligne);
            }
        }
        return new tokens(tokentype.ENTIER, characters.toString(), ligne);
    }

    public tokens getNextToken() {
        skipSpacesAndComments();
        char currentChar = charcurrent();
        if (currentChar == '#') return new tokens(tokentype.EOF, "#", ligne);
        if (Character.isLetter(currentChar)) return identifierORkeyword();
        if (Character.isDigit(currentChar)) return numbers();

        switch (currentChar) {
            case '{' -> { avvance(); return new tokens(tokentype.ACOLAD_OUV, "{", ligne); }
            case '}' -> { avvance(); return new tokens(tokentype.ACOLAD_FER, "}", ligne); }
            case '(' -> { avvance(); return new tokens(tokentype.PAR_OUV, "(", ligne); }
            case ')' -> { avvance(); return new tokens(tokentype.PAR_FER, ")", ligne); }
            case ';' -> { avvance(); return new tokens(tokentype.POINT_VIRGULE, ";", ligne); }
            case ',' -> { avvance(); return new tokens(tokentype.VIRGULE, ",", ligne); }
            case '+' -> { avvance(); return new tokens(tokentype.PLUS, "+", ligne); }
            case '-' -> { avvance(); return new tokens(tokentype.MOINS, "-", ligne); }
            case '*' -> { avvance(); return new tokens(tokentype.FOIS, "*", ligne); }
            case '/' -> { avvance(); return new tokens(tokentype.DIVISE, "/", ligne); }
            case '=' -> {
                avvance();
                if (charcurrent() == '=') { avvance(); return new tokens(tokentype.EGAL, "==", ligne); }
                else return new tokens(tokentype.AFFECTATION, "=", ligne);
            }
            case '<' -> {
                avvance();
                if (charcurrent() == '=') { avvance(); return new tokens(tokentype.INFERIEUR_EGAL, "<=", ligne); }
                else return new tokens(tokentype.INFERIEUR, "<", ligne);
            }
            case '>' -> {
                avvance();
                if (charcurrent() == '=') { avvance(); return new tokens(tokentype.SUPERIEUR_EGAL, ">=", ligne); }
                else return new tokens(tokentype.SUPERIEUR, ">", ligne);
            }
            default -> {
                String msg = "Erreur lexicale à la ligne " + ligne + ": caractère inattendu '" + currentChar + "'";
                erreurs.add(msg);
                avvance();
                return new tokens(tokentype.ERREUR, msg, ligne);
            }
        }
    }

    
}
