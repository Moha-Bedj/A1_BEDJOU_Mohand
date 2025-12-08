import java.util.*;

public class Analyseur_Syntaxique {
    private Analyseur_Lexical lexer;
    private tokens currentToken;

    private List<String> erreurs = new ArrayList<>();

    public Analyseur_Syntaxique(Analyseur_Lexical lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
    }

    public List<String> getErreurs() {
        return erreurs;
    }

    private void erreur(String msg) {
        erreurs.add("Erreur syntaxique ligne " + currentToken.ligne + " : " + msg);
    }

    // Méthode pour ignorer un bloc complet,
    private void Skip() {
        // Ignorer la condition entre parenthèses
        if (currentToken.type == tokentype.PAR_OUV) {
            int parCount = 1;
            currentToken = lexer.getNextToken();
            while (parCount > 0 && currentToken.type != tokentype.EOF) {
                if (currentToken.type == tokentype.PAR_OUV) parCount++;
                else if (currentToken.type == tokentype.PAR_FER) parCount--;
                currentToken = lexer.getNextToken();
            }
        }

        // Ignorer le bloc entre accolades
        if (currentToken.type == tokentype.ACOLAD_OUV) {
            int acCount = 1;
            currentToken = lexer.getNextToken();
            while (acCount > 0 && currentToken.type != tokentype.EOF) {
                if (currentToken.type == tokentype.ACOLAD_OUV) acCount++;
                else if (currentToken.type == tokentype.ACOLAD_FER) acCount--;
                currentToken = lexer.getNextToken();
            }
        } else {
            // Si pas de bloc, ignorer juste l’instruction jusqu’au point-virgule
            while (currentToken.type != tokentype.POINT_VIRGULE && currentToken.type != tokentype.EOF) {
                currentToken = lexer.getNextToken();
            }
            if (currentToken.type == tokentype.POINT_VIRGULE) currentToken = lexer.getNextToken();
        }
    }


    // ========== GRAMMAIRE ==========

    public void CLS() {
        if(currentToken.type == tokentype.PUBLIC) currentToken = lexer.getNextToken();
        else erreur("mot-clé Public inttendu");
            if (currentToken.type == tokentype.CLASS) currentToken = lexer.getNextToken();
            else erreur("mot-clé class inttendu"); 
                if (currentToken.type == tokentype.IDENTIFIANT) currentToken = lexer.getNextToken();
                else erreur("non de class inttendu");
                    if (currentToken.type == tokentype.ACOLAD_OUV) {
                        currentToken = lexer.getNextToken();
                        PROG(); 
                        if (currentToken.type == tokentype.ACOLAD_FER) currentToken = lexer.getNextToken();
                        else erreur("acolade fermante inttendu");
                    } else erreur("acolade ouvrante inttendu");            
    }


    public void PROG() {
        while (currentToken.type != tokentype.EOF && currentToken.type != tokentype.ACOLAD_FER) {
            INSTR();
        }
    }

    private void INSTR() {
        switch (currentToken.type) {
            case INT, FLOAT, DOUBLE,CHAR -> DECL();
            case IDENTIFIANT -> AFFECT();
            case TRY -> TRY_CATCH();
            case IF, ELSE, BEDJOU, MOHAND, DO, WHILE, FOR, SWITCH, CASE, FOREACH -> Skip();
            default -> Skip();
        }
    }

    private void DECL() {
        currentToken = lexer.getNextToken();
        if (currentToken.type == tokentype.IDENTIFIANT) currentToken = lexer.getNextToken();
        else erreur("Identifiant attendu dans la déclaration.");
            DECL_LIST();
            if (currentToken.type == tokentype.POINT_VIRGULE) currentToken = lexer.getNextToken();
            else erreur("Point-virgule attendu" );
    }

    private void DECL_LIST() {
        DECL_ITEM();
        DECL_LISTP();
    }

    private void DECL_LISTP() {
        while (currentToken.type == tokentype.VIRGULE) {
            currentToken = lexer.getNextToken();
            DECL_ITEM();
        }
    }

    private void DECL_ITEM() {
        if (currentToken.type == tokentype.IDENTIFIANT) currentToken = lexer.getNextToken();
        DECL_SUITE();
    }

    private void DECL_SUITE() {
        if (currentToken.type == tokentype.AFFECTATION) {
            currentToken = lexer.getNextToken();
            EXPR();    
        }
    }

    private void AFFECT() {
        if (currentToken.type == tokentype.IDENTIFIANT) currentToken = lexer.getNextToken();
        else erreur("Identifiant attendu dans l'affectation.");
            if (currentToken.type == tokentype.AFFECTATION) currentToken = lexer.getNextToken();
            else erreur("affectation attendu dans la ligne :" +currentToken.ligne);
                EXPR();
                if (currentToken.type == tokentype.POINT_VIRGULE) currentToken = lexer.getNextToken();
                else erreur("Point-virgule attendu");
    }

    private void TRY_CATCH() {
        currentToken = lexer.getNextToken();
        if (currentToken.type == tokentype.ACOLAD_OUV) currentToken = lexer.getNextToken();
        else erreur("acolade ouvrante attendu");
        while (currentToken.type != tokentype.ACOLAD_FER && currentToken.type != tokentype.EOF) {
            INSTR();
        }
        if (currentToken.type == tokentype.ACOLAD_FER) currentToken = lexer.getNextToken();
        else erreur("Accolade fermante attendue après le bloc try.");
            if (currentToken.type == tokentype.CATCH) currentToken = lexer.getNextToken();
            else erreur("Mot-clé 'catch' attendu après le bloc try.");
                if (currentToken.type == tokentype.ACOLAD_OUV) currentToken = lexer.getNextToken();
                else erreur("Accolade ouvrante attendue après 'catch'.");
                    while (currentToken.type != tokentype.ACOLAD_FER && currentToken.type != tokentype.EOF) {
                        INSTR();
                    }
                        if (currentToken.type == tokentype.ACOLAD_FER) currentToken = lexer.getNextToken();
                        else erreur("Accolade fermante attendue après le bloc catch.");
    }

    private void EXPR() {
        TERME();
        EXPR_SUITE();
    }

    private void EXPR_SUITE() {
        if (currentToken.type == tokentype.PLUS || currentToken.type == tokentype.MOINS) {
            currentToken = lexer.getNextToken();
            TERME();
            EXPR_SUITE();
        }
    }

    private void TERME() {
        FACTEUR();
        TERME_SUITE();
    }

    private void TERME_SUITE() {
        if (currentToken.type == tokentype.FOIS || currentToken.type == tokentype.DIVISE) {
            currentToken = lexer.getNextToken();
            FACTEUR();
            TERME_SUITE();
        }
    }

    private void FACTEUR() {
        switch (currentToken.type) {
            case IDENTIFIANT:
                currentToken = lexer.getNextToken();
                break;
            case ENTIER:
                currentToken = lexer.getNextToken();
                break;
            case REEL:
                currentToken = lexer.getNextToken();
                break;
            case PAR_OUV:
                currentToken = lexer.getNextToken();
                EXPR();
                if (currentToken.type == tokentype.PAR_FER) currentToken = lexer.getNextToken();
                else erreur("parentese fermante attendu");
                break;
            default:
                erreur("Facteur inattendu");
        }
        
    }

    public void BLOC() {
        if (currentToken.type == tokentype.ACOLAD_OUV) currentToken = lexer.getNextToken();
        else erreur("acolade ouvrante attendu");
        while (currentToken.type != tokentype.ACOLAD_FER && currentToken.type != tokentype.EOF) {
            INSTR();
        }
        if (currentToken.type == tokentype.ACOLAD_FER) currentToken = lexer.getNextToken();
        else erreur("acolade fermante attendu");
    }
}
