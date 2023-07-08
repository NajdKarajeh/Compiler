// Najd Mansour 1182687
package najd;
import javax.swing.*; // Importing the necessary package for GUI components


import java.io.BufferedReader; // Importing the necessary package for reading text from a character-input stream
import java.io.FileReader; // Importing the necessary package for reading files
import java.io.IOException; // Importing the necessary package for handling input/output operations
import java.util.ArrayList; // Importing the necessary package for working with dynamic arrays
import java.util.Arrays; // Importing the necessary package for working with arrays
import java.util.List; // Importing the necessary package for working with lists

public class najd {
	private Tokenizer tokenizer; // Declaration of Tokenizer object
	private Token currentToken; // Declaration of Token object

	public najd(String input) { // Constructor method for initializing Tokenizer and currentToken
		this.tokenizer = new Tokenizer(input); // Initializing Tokenizer with the given input
		this.currentToken = tokenizer.getNextToken(); // Initializing currentToken with the next token from Tokenizer
	}
	class ParsingException extends Exception {
	    public ParsingException(String message) {
	        super(message);
	    }
	}
	private void consume(ArrayList<Token.TokenType> typeList) throws ParsingException {
		if (currentToken != null && typeList.contains(currentToken.getType())) {
			currentToken = tokenizer.getNextToken();
		} else {
			throw new ParsingException("Expected token of type " + typeList + " but found "
					+ (currentToken != null ? currentToken.getType() : "null") + " at line "
					+ (currentToken != null ? currentToken.getLineNumber() : ""));
		}
	}

	public void parse() throws ParsingException {
		projectDeclaration();
	}

	private void projectDeclaration() throws ParsingException {
		projectDef();
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.DOT)));
	}

	private void projectDef() throws ParsingException {
		projectHeading();
		declarations();
		compoundStmt();
	}

	private void projectHeading() throws ParsingException {
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.PROJECT)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
	}


	private void declarations() throws ParsingException {
		while (true) {
			if (currentToken.getType() == Token.TokenType.CONST) {
				constDecl();
			} else if (currentToken.getType() == Token.TokenType.VAR) {
				varDecl();
			} else if (currentToken.getType() == Token.TokenType.ROUTINE) {
				subroutineDecl();
			} else {
				break;
			}
		}
	}

	private void constDecl() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.CONST) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.CONST)));
			while (currentToken.getType() == Token.TokenType.NAME) {
				constItem();
				consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
			}
		} else {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.VAR, Token.TokenType.ROUTINE)));
		}
	}

	private void constItem() throws ParsingException {
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.EQUALS)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.INTEGER_VALUE)));
	}

    private void varDecl() throws ParsingException {
        if (currentToken.getType() == Token.TokenType.VAR) {
            consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.VAR)));
            while (currentToken.getType() == Token.TokenType.NAME) {
                varItem();
                consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
            }
        } else {
        	consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
        }
    }

	private void varItem() throws ParsingException {
		nameList();
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.COLON)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.INT)));
	}

	private void nameList() throws ParsingException {
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
		while (currentToken.getType() == Token.TokenType.COMMA) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.COMMA)));
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
		}
	}

	private void subroutineDecl() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.ROUTINE) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.ROUTINE)));
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
			declarations();
			compoundStmt();
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
		} else {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.ROUTINE, Token.TokenType.START,
					Token.TokenType.SEMICOLON, Token.TokenType.END)));
		}
	}

	private void stmtList() throws ParsingException {
		while (currentToken != null && (currentToken.getType() == Token.TokenType.NAME
				|| currentToken.getType() == Token.TokenType.IF || currentToken.getType() == Token.TokenType.INPUT
				|| currentToken.getType() == Token.TokenType.OUTPUT || currentToken.getType() == Token.TokenType.LOOP
				|| currentToken.getType() == Token.TokenType.START)) {
			statement();
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
		}
	}

	private void statement() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.NAME) {
			assignmentStmt();
		} else if (currentToken.getType() == Token.TokenType.INPUT
				|| currentToken.getType() == Token.TokenType.OUTPUT) {
			inoutStmt();
		} else if (currentToken.getType() == Token.TokenType.IF) {
			ifStmt();
		} else if (currentToken.getType() == Token.TokenType.LOOP) {
			loopStmt();
		} else if (currentToken.getType() == Token.TokenType.START) {
			compoundStmt();
		} else {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
		}
	}

	private void assignmentStmt() throws ParsingException {
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.COLON)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.EQUALS)));
		arithExp();
	}

	private void arithExp() throws ParsingException {
		term();
		while (currentToken != null && (currentToken.getType() == Token.TokenType.ADD_SIGN
				|| currentToken.getType() == Token.TokenType.SUB_SIGN)) {
			addSign();
			term();
			}
	}

	private void term() throws ParsingException {
		factor();
		while (currentToken != null && (currentToken.getType() == Token.TokenType.MULT_SIGN
				|| currentToken.getType() == Token.TokenType.DIV_SIGN
				|| currentToken.getType() == Token.TokenType.MOD_SIGN)) {
			mulSign();
			factor();
		}
	}

	private void factor() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.LEFT_PAREN) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LEFT_PAREN)));
			arithExp();
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.RIGHT_PAREN)));
		} else if (currentToken.getType() == Token.TokenType.NAME
				|| currentToken.getType() == Token.TokenType.INTEGER_VALUE) {
			nameValue();
		} else {
			error("Invalid factor");
		}
	}

	private void nameValue() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.NAME || currentToken.getType() == Token.TokenType.INTEGER_VALUE) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(currentToken.getType())));
		} else {
			error("Invalid name or value");
		}
	}

	private void addSign() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.ADD_SIGN || currentToken.getType() == Token.TokenType.SUB_SIGN) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(currentToken.getType())));
		} else {
			error("Invalid addition sign");
		}
	}

	private void mulSign() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.MULT_SIGN || currentToken.getType() == Token.TokenType.DIV_SIGN
				|| currentToken.getType() == Token.TokenType.MOD_SIGN) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(currentToken.getType())));
		} else {
			error("Invalid multiplication sign");
		}
	}

	private void inoutStmt() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.INPUT) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.INPUT)));
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LEFT_PAREN)));
			nameList();
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.RIGHT_PAREN)));
		} else if (currentToken.getType() == Token.TokenType.OUTPUT) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.OUTPUT)));
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LEFT_PAREN)));
			nameValue();
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.RIGHT_PAREN)));
		} else {
			error("Expected INPUT or OUTPUT statement");
		}
	}

	private void ifStmt() throws ParsingException {
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.IF)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LEFT_PAREN)));
		boolExp();
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.RIGHT_PAREN)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.THEN)));
		statement();
		elsePart();
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.ENDIF)));
	}

	private void elsePart() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.ELSE) {
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.ELSE)));
			statement();
		}
	}

	private void loopStmt() throws ParsingException {
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LOOP)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LEFT_PAREN)));
		boolExp();
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.RIGHT_PAREN)));
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.DO)));
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
			consume(new ArrayList<Token.TokenType>(Arrays.asList(currentToken.getType())));
			break;
		default:
			error("Invalid relational operator");
			break;
		}
	}

	private void compoundStmt() throws ParsingException {
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.START)));
		stmtList();
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.END)));
	}

	private void error(String errorMessage) throws ParsingException {
		throw new ParsingException(
			errorMessage + " at line " + (currentToken != null ? currentToken.getLineNumber() : ""));
	}

	public static void main(String[] args) throws ParsingException {
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			String fileName = fileChooser.getSelectedFile().getAbsolutePath();
			try {
				String input = readFile(fileName);
				najd parser = new najd (input);
				parser.parse();
				System.out.println("Parsing completed successfully.");
			} catch (IOException e) {
				System.err.println("Failed to read the input file: " + e.getMessage());
			}
		} else {
			System.out.println("No file selected.");
		}
	}

	private static String readFile(String fileName) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line).append("\n");
		}
		bufferedReader.close();
		return stringBuilder.toString();
	}

}

class Token {
	public enum TokenType {
		PROJECT, NAME, DECLARATIONS, CONST, VAR, ROUTINE, SUBROUTINE, COMPOUND_STMT, START, STMT_LIST, ASSIGNMENT,
		INOUT_STMT, IF, THEN, ELSE, LOOP, DO, ENDIF, END, LEFT_PAREN, RIGHT_PAREN, EQUALS, NOT_EQUALS, LESS_THAN,
		LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, ADD_SIGN, SUB_SIGN, MULT_SIGN, DIV_SIGN, MOD_SIGN,
		INTEGER_VALUE, COLON, COMMA, DOT, INPUT, OUTPUT, ERROR, EOF, SEMICOLON, INT
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
				return new Token(Token.TokenType.NOT_EQUALS, "<>", lineNumber);
			}

			// Process less than token
			return new Token(Token.TokenType.LESS_THAN, "<", lineNumber);
		} else if (currentChar == '>') {
			// Process greater than token or greater than or equal to token
			position++;
			if (position < input.length() && input.charAt(position) == '=') {
				// Process greater than or equal to token
				position++;
				return new Token(Token.TokenType.GREATER_THAN_EQUALS, ">=", lineNumber);
			}

			// Process greater than token
			return new Token(Token.TokenType.GREATER_THAN, ">", lineNumber);
		}

		// Unknown token
		throw new RuntimeException("Unexpected character: " + currentChar);
	}
}
