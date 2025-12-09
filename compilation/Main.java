import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        while (true) {
            System.out.println("veullier entrer le chemin complet du code java");
            String fichPth = new Scanner(System.in).nextLine().trim();

            File file = new File(fichPth);
            String code = "";
            
            try {
                List<String> allLines = Files.readAllLines(Paths.get(fichPth));
                code = String.join("\n", allLines);
                // --- Analyse lexicale ---
                System.out.println("=== Analyse lexicale ===");
                Analyseur_Lexical lexer = new Analyseur_Lexical(code);

                tokens t;
                do {
                    t = lexer.getNextToken();
                    System.out.println(t);
                } while (t.type != tokentype.EOF);

                // Afficher les erreurs lexicales
                if (!lexer.getErreurs().isEmpty()) {
                    System.out.println("\nErreurs lexicales détectées :");
                    for (String err : lexer.getErreurs()) {
                        System.out.println(err);
                    }
                } else {
                    System.out.println("\nAucune erreur lexicale.");
                }

                // --- Analyse syntaxique ---
                System.out.println("\n=== Analyse syntaxique ===");
                // On reset le lexer pour repartir du début
                lexer.reset();
                Analyseu_Syuntaxique lex = new Analyseu_Syuntaxique(lexer);

                lex.CLS(); // lancer l'analyse du programme

                // Afficher les erreurs syntaxiques
                if (!lex.getErreurs().isEmpty()) {
                    System.out.println("\nErreurs syntaxiques détectées :");
                    for (String err : lex.getErreurs()) {
                        System.out.println(err);
                    }
                } else {
                    System.out.println("\nAucune erreur syntaxique.");
                }

            } catch(IOException e) {
                System.out.println("Erreur lecture fichier : " + e.getMessage());
            }
        }
    }
}
