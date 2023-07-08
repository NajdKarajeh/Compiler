package najd;

//Najd Mansour 1182687
import javax.swing.*; // Importing the necessary package for GUI components

import najd.Token.TokenType;

import java.io.BufferedReader; // Importing the necessary package for reading text from a character-input stream
import java.io.File;
import java.io.FileReader; // Importing the necessary package for reading files
import java.io.IOException; // Importing the necessary package for handling input/output operations
import java.util.ArrayList; // Importing the necessary package for working with dynamic arrays
import java.util.Arrays; // Importing the necessary package for working with arrays
import java.util.List; // Importing the necessary package for working with lists
import java.util.stream.Collectors;



class ParsingException extends Exception {
    public ParsingException(String message) {
        super(message);
    }
}

class Token {
    public enum TokenType {
        PROJECT, NAME, DECLARATIONS, CONST, VAR, ROUTINE, SUBROUTINE, COMPOUND_STMT, START, STMT_LIST, ASSIGNMENT,
        INOUT_STMT, IF, THEN, ELSE, LOOP, DO, ENDIF, END, LEFT_PAREN, RIGHT_PAREN, EQUALS, NOT_EQUALS, LESS_THAN,
        LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, ADD_SIGN, SUB_SIGN, MULT_SIGN, DIV_SIGN, MOD_SIGN,
        INTEGER_VALUE, COLON, COMMA, DOT, INPUT, OUTPUT, ERROR, EOF, SEMICOLON, INT, UNKNOWN
    }

    private TokenType type;         // Type of the token
    private String value;           // Value of the token
    private int lineNumber;         // Line number where the token was found

    public Token(TokenType type, String value, int lineNumber) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}

class Tokenizer {
    private String input;       // Input string to tokenize
    private int position;       // Current position in the input string
    private int lineNumber;     // Current line number

    public Tokenizer(String input) {
        this.input = input;
        this.position = 0;
        this.lineNumber = 1;     // Start at line number 1
    }

    public Token getNextToken() {
        // Skip whitespaces and keep track of line numbers
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            if (input.charAt(position) == '\n') {
                lineNumber++;     // Increment line number if newline character is encountered
            }
            position++;
        }

        // End of input
        if (position == input.length()) {
            return null;
        }

        char currentChar = input.charAt(position);

        // Identify token types
        if (Character.isLetter(currentChar)) {
            // Process alphanumeric tokens
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(currentChar);
                position++;
                if (position < input.length()) {
                    currentChar = input.charAt(position);
                } else {
                    break;
                }
            } while (Character.isLetterOrDigit(currentChar));

            String result = builder.toString();

            // Map keyword strings to their corresponding token types
            switch (result.toLowerCase()) {
                case "project":
                    return new Token(Token.TokenType.PROJECT, result, lineNumber);
                case "const":
                    return new Token(Token.TokenType.CONST, result, lineNumber);
                case "var":
                    return new Token(Token.TokenType.VAR, result, lineNumber);
                case "routine":
                    return new Token(Token.TokenType.ROUTINE, result, lineNumber);
                case "start":
                    return new Token(Token.TokenType.START, result, lineNumber);
                case "end":
                    return new Token(Token.TokenType.END, result, lineNumber);
                case "input":
                    return new Token(Token.TokenType.INPUT, result, lineNumber);
                case "output":
                    return new Token(Token.TokenType.OUTPUT, result, lineNumber);
                case "if":
                    return new Token(Token.TokenType.IF, result, lineNumber);
                case "then":
                    return new Token(Token.TokenType.THEN, result, lineNumber);
                case "endif":
                    return new Token(Token.TokenType.ENDIF, result, lineNumber);
                case "else":
                    return new Token(Token.TokenType.ELSE, result, lineNumber);
                case "loop":
                    return new Token(Token.TokenType.LOOP, result, lineNumber);
                case "do":
                    return new Token(Token.TokenType.DO, result, lineNumber);
                case "int":
                    return new Token(Token.TokenType.INT, result, lineNumber);
                default:
                    return new Token(Token.TokenType.NAME, result, lineNumber);
            }
        } else if (currentChar == ':') {
            // Process colon token
            position++;
            return new Token(Token.TokenType.COLON, ":", lineNumber);
        } else if (currentChar == ',') {
            // Process comma token
            position++;
            return new Token(Token.TokenType.COMMA, ",", lineNumber);
        } else if (currentChar == ';') {
            // Process semicolon token
            position++;
            return new Token(Token.TokenType.SEMICOLON, ";", lineNumber);
        } else if (currentChar == '.') {
            // Process dot token
            position++;
            return new Token(Token.TokenType.DOT, ".", lineNumber);
        } else if (currentChar == '=') {
            // Process equals token
            position++;
            return new Token(Token.TokenType.EQUALS, "=", lineNumber);
        } else if (Character.isDigit(currentChar)) {
            // Process integer value token
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(currentChar);
                position++;
                if (position < input.length()) {
                    currentChar = input.charAt(position);
                } else {
                    break;
                }
            } while (Character.isDigit(currentChar));

            return new Token(Token.TokenType.INTEGER_VALUE, builder.toString(), lineNumber);
        } else if (currentChar == '(') {
            // Process left parenthesis token
            position++;
            return new Token(Token.TokenType.LEFT_PAREN, "(", lineNumber);
        } else if (currentChar == ')') {
            // Process right parenthesis token
            position++;
            return new Token(Token.TokenType.RIGHT_PAREN, ")", lineNumber);
        } else if (currentChar == '+') {
            // Process addition sign token
            position++;
            return new Token(Token.TokenType.ADD_SIGN, "+", lineNumber);
        } else if (currentChar == '-') {
            // Process subtraction sign token
            position++;
            return new Token(Token.TokenType.SUB_SIGN, "-", lineNumber);
        } else if (currentChar == '%') {
            // Process modulo sign token
            position++;
            return new Token(Token.TokenType.MOD_SIGN, "%", lineNumber);
        } else if (currentChar == '/') {
            // Process division sign token
            position++;
            return new Token(Token.TokenType.DIV_SIGN, "/", lineNumber);
        } else if (currentChar == '*') {
            // Process multiplication sign token
            position++;
            return new Token(Token.TokenType.MULT_SIGN, "*", lineNumber);
        } else if (currentChar == '<') {
            // Process less than token or less than or equal to token or not equals token
            position++;
            if (position < input.length() && input.charAt(position) == '=') {
                // Process less than or equal to token
                position++;
                return new Token(Token.TokenType.LESS_THAN_EQUALS, "<=", lineNumber);
            } else if (position < input.length() && input.charAt(position) == '>') {
                // Process not equals token
                position++;
               
                if (position < input.length() && input.charAt(position) == '=') {
                    // Process not equals token
                    position++;
                    return new Token(Token.TokenType.NOT_EQUALS, "<>", lineNumber);
                }
            }
            return new Token(Token.TokenType.LESS_THAN, "<", lineNumber);
        } else if (currentChar == '>') {
            // Process greater than token or greater than or equal to token
            position++;
            if (position < input.length() && input.charAt(position) == '=') {
                // Process greater than or equal to token
                position++;
                return new Token(Token.TokenType.GREATER_THAN_EQUALS, ">=", lineNumber);
            }
            return new Token(Token.TokenType.GREATER_THAN, ">", lineNumber);
        } else {
            // Process unrecognized token
            position++;
            return new Token(Token.TokenType.UNKNOWN, Character.toString(currentChar), lineNumber);
        }
    }
}

public class najd {
    private Tokenizer tokenizer; // Declaration of Tokenizer object
    private Token currentToken; // Declaration of Token object

    public najd(String input) { // Constructor method for initializing Tokenizer and currentToken
        this.tokenizer = new Tokenizer(input); // Initializing Tokenizer with the given input
        this.currentToken = tokenizer.getNextToken(); // Initializing currentToken with the next token from Tokenizer
    }

    private void consume(Token.TokenType expectedType) throws ParsingException {
        if (currentToken != null && currentToken.getType() == expectedType) {
            currentToken = tokenizer.getNextToken();
        } else {
            throw new ParsingException("Expected token of type " + expectedType +
                    " but found " + (currentToken != null ? currentToken.getType() : "null") +
                    " at line " + (currentToken != null ? currentToken.getLineNumber() : ""));
        }
    }
    public void parse() throws ParsingException {
        projectDeclaration();
    }

    private void projectDeclaration() throws ParsingException {
        projectDef();
        consume(Token.TokenType.DOT);
    }

    private void projectDef() throws ParsingException {
        projectHeading();
        declarations();
        compoundStmt();
    }

    private void projectHeading() throws ParsingException {
        consume(Token.TokenType.PROJECT);
        consume(Token.TokenType.NAME);
        consume(Token.TokenType.SEMICOLON);
    }

    private void declarations() throws ParsingException {
        while (currentToken != null && (currentToken.getType() == Token.TokenType.CONST ||
                currentToken.getType() == Token.TokenType.VAR ||
                currentToken.getType() == Token.TokenType.ROUTINE)) {
            if (currentToken.getType() == Token.TokenType.CONST) {
                constDecl();
            } else if (currentToken.getType() == Token.TokenType.VAR) {
                varDecl();
            } else if (currentToken.getType() == Token.TokenType.ROUTINE) {
                subroutineDecl();
            }
        }
    }

    private void constDecl() throws ParsingException {
        consume(Token.TokenType.CONST);
        while (currentToken.getType() == Token.TokenType.NAME) {
            constItem();
            consume(Token.TokenType.SEMICOLON);
        }
    }

    private void constItem() throws ParsingException {
        consume(Token.TokenType.NAME);
        consume(Token.TokenType.EQUALS);
        consume(Token.TokenType.INTEGER_VALUE);
    }

    private void varDecl() throws ParsingException {
        consume(Token.TokenType.VAR);
        while (currentToken.getType() == Token.TokenType.NAME) {
            varItem();
            consume(Token.TokenType.SEMICOLON);
        }
    }

    private void varItem() throws ParsingException {
        nameList();
        consume(Token.TokenType.COLON);
        consume(Token.TokenType.INT);
    }

    private void nameList() throws ParsingException {
        consume(Token.TokenType.NAME);
        while (currentToken.getType() == Token.TokenType.COMMA) {
            consume(Token.TokenType.COMMA);
            consume(Token.TokenType.NAME);
        }
    }

    private void subroutineDecl() throws ParsingException {
        consume(Token.TokenType.ROUTINE);
        consume(Token.TokenType.NAME);
        consume(Token.TokenType.SEMICOLON);
        declarations();
        compoundStmt();
        consume(Token.TokenType.SEMICOLON);
    }

    private void compoundStmt() throws ParsingException {
        consume(Token.TokenType.START);
        stmtList();
        consume(Token.TokenType.END);
        if (currentToken != null && currentToken.getType() == Token.TokenType.SEMICOLON) {
            currentToken = tokenizer.getNextToken();
        } else {
            throw new ParsingException("Expected token of type " + Token.TokenType.SEMICOLON +
                    " but found " + (currentToken != null ? currentToken.getType() : "null") +
                    " at line " + (currentToken != null ? currentToken.getLineNumber() : ""));
        }
    }
    private void stmtList() throws ParsingException {
        while (currentToken != null && (currentToken.getType() == Token.TokenType.NAME ||
                currentToken.getType() == Token.TokenType.IF ||
                currentToken.getType() == Token.TokenType.INPUT ||
                currentToken.getType() == Token.TokenType.OUTPUT ||
                currentToken.getType() == Token.TokenType.LOOP ||
                currentToken.getType() == Token.TokenType.START)) {
            statement();
        }
    }

    private void statement() throws ParsingException {
        if (currentToken.getType() == Token.TokenType.NAME) {
            assignmentStmt();
        } else if (currentToken.getType() == Token.TokenType.INPUT ||
                currentToken.getType() == Token.TokenType.OUTPUT) {
            inoutStmt();
        } else if (currentToken.getType() == Token.TokenType.IF) {
            ifStmt();
        } else if (currentToken.getType() == Token.TokenType.LOOP) {
            loopStmt();
        } else if (currentToken.getType() == Token.TokenType.START) {
            compoundStmt();
        } else {
            consume(Token.TokenType.SEMICOLON);
        }
    }

    private void assignmentStmt() throws ParsingException {
        consume(Token.TokenType.NAME);
        consume(Token.TokenType.COLON);
        consume(Token.TokenType.EQUALS);
        arithExp();
    }

    private void arithExp() throws ParsingException {
        term();
        while (currentToken != null && (currentToken.getType() == Token.TokenType.ADD_SIGN ||
                currentToken.getType() == Token.TokenType.SUB_SIGN)) {
            addSign();
            term();
        }
    }

    private void term() throws ParsingException {
        factor();
        while (currentToken != null && (currentToken.getType() == Token.TokenType.MULT_SIGN ||
                currentToken.getType() == Token.TokenType.DIV_SIGN ||
                currentToken.getType() == Token.TokenType.MOD_SIGN)) {
            mulSign();
            factor();
        }
    }

    private void factor() throws ParsingException {
        if (currentToken.getType() == Token.TokenType.LEFT_PAREN) {
            consume(Token.TokenType.LEFT_PAREN);
            arithExp();
            consume(Token.TokenType.RIGHT_PAREN);
        } else if (currentToken.getType() == Token.TokenType.NAME ||
                currentToken.getType() == Token.TokenType.INTEGER_VALUE) {
            nameValue();
        } else {
            throw new ParsingException("Invalid factor at line " +
                    (currentToken != null ? currentToken.getLineNumber() : ""));
        }
    }

    private void nameValue() throws ParsingException {
        if (currentToken.getType() == Token.TokenType.NAME ||
                currentToken.getType() == Token.TokenType.INTEGER_VALUE) {
            consume(currentToken.getType());
        } else {
            throw new ParsingException("Invalid name or value at line " +
                    (currentToken != null ? currentToken.getLineNumber() : ""));
        }
    }

    private void addSign() throws ParsingException {
        if (currentToken.getType() == Token.TokenType.ADD_SIGN ||
                currentToken.getType() == Token.TokenType.SUB_SIGN) {
            consume(currentToken.getType());
        } else {
            throw new ParsingException("Invalid addition sign at line " +
                    (currentToken != null ? currentToken.getLineNumber() : ""));
        }
    }

    private void mulSign() throws ParsingException {
        if (currentToken.getType() == Token.TokenType.MULT_SIGN ||
                currentToken.getType() == Token.TokenType.DIV_SIGN ||
                currentToken.getType() == Token.TokenType.MOD_SIGN) {
            consume(currentToken.getType());
        } else {
            throw new ParsingException("Invalid multiplication sign at line " +
                    (currentToken != null ? currentToken.getLineNumber() : ""));
        }
    }

    private void inoutStmt() throws ParsingException {
        if (currentToken.getType() == Token.TokenType.INPUT) {
            consume(Token.TokenType.INPUT);
            consume(Token.TokenType.LEFT_PAREN);
            nameList();
            consume(Token.TokenType.RIGHT_PAREN);
        } else if (currentToken.getType() == Token.TokenType.OUTPUT) {
            consume(Token.TokenType.OUTPUT);
            consume(Token.TokenType.LEFT_PAREN);
            nameValue();
            consume(Token.TokenType.RIGHT_PAREN);
        } else {
            throw new ParsingException("Expected INPUT or OUTPUT statement at line " +
                    (currentToken != null ? currentToken.getLineNumber() : ""));
        }
    }

    private void ifStmt() throws ParsingException {
        consume(Token.TokenType.IF);
        consume(Token.TokenType.LEFT_PAREN);
        boolExp();
        consume(Token.TokenType.RIGHT_PAREN);
        consume(Token.TokenType.THEN);
        statement();
        elsePart();
        consume(Token.TokenType.ENDIF);
    }

    private void elsePart() throws ParsingException {
        if (currentToken.getType() == Token.TokenType.ELSE) {
            consume(Token.TokenType.ELSE);
            statement();
        }
    }

    private void loopStmt() throws ParsingException {
        consume(Token.TokenType.LOOP);
        consume(Token.TokenType.LEFT_PAREN);
        boolExp();
        consume(Token.TokenType.RIGHT_PAREN);
        consume(Token.TokenType.DO);
        statement();
    }

    private void boolExp() throws ParsingException {
        nameValue();
        relationalOper();
        nameValue();
    }

    private void relationalOper() throws ParsingException {
        switch (currentToken.getType()) {
            case EQUALS:
            case NOT_EQUALS:
            case LESS_THAN:
            case LESS_THAN_EQUALS:
            case GREATER_THAN:
            case GREATER_THAN_EQUALS:
                consume(currentToken.getType());
                break;
            default:
                throw new ParsingException("Invalid relational operator at line " +
                        (currentToken != null ? currentToken.getLineNumber() : ""));
        }
    }

    public static void main(String[] args) {
        // Select input file using file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Input File");
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileChooser.getSelectedFile();
            try {
                FileReader fileReader = new FileReader(inputFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuilder inputBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    inputBuilder.append(line).append("\n");
                }
                bufferedReader.close();

                String input = inputBuilder.toString();
                najd parser = new najd(input);
                parser.parse();
                System.out.println("Parsing successful.");
            } catch (IOException e) {
                System.err.println("Error reading the input file: " + e.getMessage());
            } catch (ParsingException e) {
                System.err.println("Parsing error: " + e.getMessage());
            }
        } else {
            System.out.println("No input file selected.");
        }
}
}
