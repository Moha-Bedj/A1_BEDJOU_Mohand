public class tokens {
        public final tokentype type;
        public final String lexeme;
        public final int ligne;

        public tokens(tokentype type, String lexeme, int ligne) {
            this.type = type;
            this.lexeme = lexeme;
            this.ligne = ligne;
        }

        @Override
        public String toString() {
            return "Token{type=" + type + ", lexeme='" + lexeme + "', ligne=" + ligne + "}";
        }
    }