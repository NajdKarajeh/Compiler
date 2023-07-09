package najd;

// Najd Mansour 1182687

import javax.swing.*;

//Importing java.io.BufferedReader for reading text from a character-input stream
import java.io.BufferedReader;

//Importing java.io.FileReader for reading character files
import java.io.FileReader;

//Importing java.io.IOException for exception handling related to Input/Output operations
import java.io.IOException;

//Importing java.util.ArrayList for working with dynamic (resizeable) arrays
import java.util.ArrayList;

//Importing java.util.Arrays for working with arrays and performing operations on them like sorting and searching
import java.util.Arrays;
import java.util.LinkedList;
//Importing java.util.List to work with collection of elements (Lists)
import java.util.List;
import java.util.Queue;
import java.util.Stack;


public class najd {

// Declaring a private instance of Tokenizer named "tokenizer"
	private Tokenizer tokenizer;

// Declaring a private instance of Token named "currentToken"
	private Token currentToken;

// Declaring a private List of Strings named "errors" and initializing it as a new ArrayList
	private List<String> errors = new ArrayList<>();

// Defining an inner class named "ParsingException" which extends the built-in Exception class
	class ParsingException extends Exception {

 // Defining a private string "tokenValue"
		private String tokenValue;

 // Constructor for the ParsingException class
		public ParsingException(String message, String tokenValue) {
			// Calling the constructor of the parent class (Exception) with the argument "message"
			super(message);

   // Assigning the input "tokenValue" to the class's field "tokenValue"
			this.tokenValue = tokenValue;
		}

 // Getter method for "tokenValue"
		public String getTokenValue() {
			return tokenValue;
		}
	}

// Constructor for the "najd" class, taking a string "input" as an argument
	public najd(String input) {
		// Initializing the "tokenizer" object with the input string
		this.tokenizer = new Tokenizer(input);

 // Setting "currentToken" to the next token from the tokenizer
		this.currentToken = tokenizer.getNextToken();
	}

// Method "consume" for consuming tokens of specific types from the tokenizer
	private void consume(List<Token.TokenType> typeList) {
 // If the currentToken is not null and its type is in the provided list, get the next token
		if (currentToken != null && typeList.contains(currentToken.getType())) {
			currentToken = tokenizer.getNextToken();
 // If the currentToken's type is not in the list, add an error and get the next token
		} else if (currentToken != null) {
			errors.add("Unexpected token: " + currentToken.getValue() +
					" at line " + currentToken.getLineNumber());
			currentToken = tokenizer.getNextToken();
 // If there are no more tokens but parsing isn't complete, add an error
		} else {
			errors.add("No more tokens to consume but parsing is not yet complete.");
		}
	}

// Method "parse" for starting the parsing process
	public void parse() {
 // The parsing process is started inside a try-catch block to handle potential exceptions
		try {
			projectDeclaration();
		} catch (Exception e) {
   // If an exception is caught, add its message to the errors list
			errors.add(e.getMessage());
		}
 // If there are no errors, print a success message
		if (errors.isEmpty()) {
			System.out.println("Parsing completed successfully.");
 // If there are errors, print each unique error message once per line
		} else {
			System.out.println("Parsing finished with errors:");
			int currentLine = -1;
			for (String error : errors) {
				int line = getErrorLineNumber(error);
				if (line != currentLine) {
					System.out.println(error);
					currentLine = line;
				}
			}
			System.out.println("Parsing failed due to errors.");
		}
	}

	public void testConsume(List<Token.TokenType> typeList) {
	    // Store the initial value of the current token for comparison
	    Token initialToken = currentToken;

	    // Call the consume method
	    consume(typeList);

	    // Check if the current token has been updated correctly
	    if (initialToken != null && currentToken != null && initialToken != currentToken) {
	        System.out.println("Consume test passed!");
	    } else if (initialToken == null && currentToken == null) {
	        System.out.println("Consume test passed!");
	    } else {
	        System.out.println("Consume test failed.");
	        System.out.println("Expected current token: " + initialToken);
	        System.out.println("Actual current token: " + currentToken);
	    }

	    // Check if any errors were added during the consume operation
	    if (errors.isEmpty()) {
	        System.out.println("No errors were added during the consume operation.");
	    } else {
	        System.out.println("Errors were added during the consume operation:");
	        for (String error : errors) {
	            System.out.println(error);
	        }
	    }
	}
// Method "getErrorLineNumber" for extracting the line number from an error message
	private int getErrorLineNumber(String error) {
		int line = -1;
 // Splitting the error message into individual words
		String[] parts = error.split(" ");
		for (int i = 0; i < parts.length; i++) {
   // If the current word is "line", try to parse the next word as a number (the line number)
			if (parts[i].equals("line")) {
				if (i + 1 < parts.length) {
					try {
						line = Integer.parseInt(parts[i + 1]);
						break;
					} catch (NumberFormatException e) {
         // If the next word can't be parsed as a number, ignore it and continue with the loop
					}
				}
			}
		}
 // Return the found line number, or -1 if no valid line number was found
		return line;
	}

	
	
	// Method to initiate the project declaration process
	private void projectDeclaration() throws ParsingException {
		// Calls the project definition method
		projectDef();
		// Consumes a DOT token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.DOT)));
	}

	// Method to define the project
	private void projectDef() throws ParsingException {
		// Calls the project heading method
		projectHeading();
		// Calls the declarations method to handle various declarations
		declarations();
		// Calls the compound statement method
		compoundStmt();
	}

	// Method to define the project heading
	private void projectHeading() throws ParsingException {
		// Consumes a PROJECT token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.PROJECT)));
		// Consumes a NAME token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
		// Consumes a SEMICOLON token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
	}

	// Method to handle various declarations
	private void declarations() throws ParsingException {
		// Loop until the current token is null
		while (currentToken != null) {
			// Checks if the current token type is CONST
			if (currentToken.getType() == Token.TokenType.CONST) {
				// Calls the constant declaration method
				constDecl();
			} else if (currentToken.getType() == Token.TokenType.VAR) {
				// Calls the variable declaration method
				varDecl();
			} else if (currentToken.getType() == Token.TokenType.ROUTINE) {
				// Calls the subroutine declaration method
				subroutineDecl();
			} else {
				// Breaks the loop if the current token is not CONST, VAR, or ROUTINE
				break;
			}
		}
	}

	public void testGetErrorLineNumber(String error, int expectedLineNumber) {
	    // Call the getErrorLineNumber method
	    int lineNumber = getErrorLineNumber(error);

	    // Check if the returned line number matches the expected line number
	    if (lineNumber == expectedLineNumber) {
	        System.out.println("GetErrorLineNumber test passed!");
	    } else {
	        System.out.println("GetErrorLineNumber test failed.");
	        System.out.println("Expected line number: " + expectedLineNumber);
	        System.out.println("Actual line number: " + lineNumber);
	    }
	}
	// Method to declare a constant
	private void constDecl() throws ParsingException {
		if (currentToken.getType() == Token.TokenType.CONST) {
			// Consumes a CONST token
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.CONST)));
			while (currentToken.getType() == Token.TokenType.NAME) {
				// Calls the constant item method
				constItem();
				// Consumes a SEMICOLON token
				consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
			}
		} else {
			// Consumes a VAR or ROUTINE token
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.VAR, Token.TokenType.ROUTINE)));
		}
	}

	// Method to define a constant item
	private void constItem() throws ParsingException {
		// Consumes a NAME token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
		// Consumes an EQUALS token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.EQUALS)));
		// Consumes an INTEGER_VALUE token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.INTEGER_VALUE)));
	}
	
	// The purpose of this method is to check if a given string, named 'token', is one of the specified delimiters.
	private static boolean checkDelimiters(String token)
	{
	    // 'token' is compared to a list of delimiters like ";" ",", ":" and so on. 
	    // If 'token' equals any of these delimiters, the method will return true.
	    if((token.equals(";"))||(token.equals(","))||(token.equals(":"))||(token.equals("."))||(token.equals(":="))
	            ||(token.equals("+"))||(token.equals("-"))||(token.equals("*"))||(token.equals("/"))||(token.equals("%"))
	            ||(token.equals("="))||(token.equals("<>"))||(token.equals("<="))||(token.equals("<"))||(token.equals(">"))
	            ||(token.equals(">="))||(token.equals("("))||(token.equals(")")))
	        return true;
	    // If 'token' does not equal any of the specified delimiters, the method will return false.
	    return false;
	}
	
	
	
	// The purpose of this method is to check if a given string, named 'token', contains only numeric digits.
	private static boolean checkNumeric(String token)
	{
		checkReserved(token);
	    // The method goes through each character of 'token'.
	    for(int index=0;index<token.length();index++)
	        // ASCII values for 0 to 9 are 48 to 57. So, if the character is not a digit (less than '0' or more than '9'), 
	        // the method will return false.
	        if((token.charAt(index)<48)||(token.charAt(index)>57))
	            return false;
	    checkAlphabetic(token);
	    // If all characters in 'token' are digits, the method will return true.
	    return true;
	}
	
	


	// Method to declare a variable
	private void varDecl() throws ParsingException {
	    if (currentToken.getType() == Token.TokenType.VAR) {
	        // Consumes a VAR token
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.VAR)));
	        while (currentToken.getType() == Token.TokenType.NAME) {
	            // Calls the variable item method
	            varItem();
	            // Consumes a SEMICOLON token
	            consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
	        }
	    } else {
	        // Consumes a SEMICOLON token
	    	consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
	    }
	}
	// The purpose of this method is to check if the first character of a given string, named 'token', is an alphabetic character.
		private static boolean checkAlphabetic(String token)
		{
			checkNumeric(token);
		    // 'temp' holds the first character of 'token'.
		    char temp=token.charAt(0);
		    // ASCII values for A to Z are 65 to 90 and for a to z are 97 to 122. 
		    // So, if 'temp' is not an alphabetic character (not in these ranges), the method will return false.
		    if(((temp>=65)&&(temp<=90))||((temp>=97)&&(temp<=122)))
		        return false;
		    // If the first character in 'token' is an alphabetic character, the method will return true.
		    return true;
		}

	// Method to define a variable item
	private void varItem() throws ParsingException {
		// Calls the name list method
		nameList();
		// Consumes a COLON token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.COLON)));
		// Consumes an INT token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.INT)));
	}

	// Method to process a list of names
	private void nameList() throws ParsingException {
		// Consumes a NAME token
		consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
		while (currentToken.getType() == Token.TokenType.COMMA) {
			// Consumes a COMMA token
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.COMMA)));
			// Consumes a NAME token
			consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
		}
	}
	// The purpose of this method is to check if a given string, named 'token', is one of the specified reserved words.
		private static boolean checkReserved(String token)
		{
			checkDelimiters(token);
		    // 'token' is compared to a list of reserved words like "project", "const", "var", etc. 
		    // If 'token' equals any of these reserved words, the method will return true.
		    if((token.equals("project"))||(token.equals("const"))||(token.equals("var"))||(token.equals("int"))
		            ||(token.equals("routine"))||(token.equals("start"))||(token.equals("end"))||(token.equals("input"))
		            ||(token.equals("output"))||(token.equals("if"))||(token.equals("then"))||(token.equals("endif"))
		            ||(token.equals("else"))||(token.equals("loop"))||(token.equals("do"))) {
			} 
		        return true;
		    // If 'token' does not equal any of the specified reserved words, the method will return false.
		}
	

	// Method to handle subroutine declarations
	private void subroutineDecl() throws ParsingException {
	    // If the current token is of type ROUTINE
	    if (currentToken.getType() == Token.TokenType.ROUTINE) {
	        // Then consume the ROUTINE token
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.ROUTINE)));
	        // Then consume the NAME token, which represents the name of the subroutine
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.NAME)));
	        // Then consume the SEMICOLON token, which represents the end of the declaration
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
	        // Then parse all declarations within the subroutine
	        declarations();
	        // Then parse all statements within the subroutine
	        compoundStmt();
	        // Finally, consume the SEMICOLON token, which represents the end of the subroutine
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
	    } else {
	        // If the current token is not a ROUTINE token, consume it
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.ROUTINE, Token.TokenType.START,
	                Token.TokenType.SEMICOLON, Token.TokenType.END)));
	    }
	}

	// Method to handle statement lists
	private void stmtList() throws ParsingException {
	    while (currentToken != null && (currentToken.getType() == Token.TokenType.NAME
	            || currentToken.getType() == Token.TokenType.IF || currentToken.getType() == Token.TokenType.INPUT
	            || currentToken.getType() == Token.TokenType.OUTPUT || currentToken.getType() == Token.TokenType.LOOP
	            || currentToken.getType() == Token.TokenType.START)) {
	        
	        // Process an if statement if the current token is of type IF
	        if (currentToken.getType() == Token.TokenType.IF) {
	            ifStmt(); // Call the ifStmt() method to handle the if statement
	        }
	        
	        // Process a loop statement if the current token is of type LOOP
	        else if (currentToken.getType() == Token.TokenType.LOOP) {
	            loopStmt(); // Call the loopStmt() method to handle the loop statement
	        }
	        
	        // Process other types of statements (assignments, input/output, compound)
	        else {
	            statement(); // Call the statement() method to handle other statement types
	        }

	        // Check if the current token is a semicolon, report an error if missing
	        if (currentToken.getType() != Token.TokenType.SEMICOLON) {
	            error("Missing semicolon at line " + currentToken.getLineNumber());
	            break;
	        }

	        // Consume the semicolon token to ensure proper termination of the statement
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.SEMICOLON)));
	    }
	}

	// Method to handle a single statement
	private void statement() throws ParsingException {
	    if (currentToken != null) {
	        if (currentToken.getType() == Token.TokenType.NAME) {
	            if (currentToken.getValue().equals("if") && currentToken.getLineNumber() == currentToken.getLineNumber()) {
	                error("Unexpected token: " + currentToken.getValue());
	                return; // Stop parsing further statements after encountering an error
	            } else {
	                assignmentStmt();
	            }
	        } else if (currentToken.getType() == Token.TokenType.INPUT
	                || currentToken.getType() == Token.TokenType.OUTPUT) {
	            inoutStmt();
	            if (currentToken != null && currentToken.getType() != Token.TokenType.SEMICOLON
	                    && currentToken.getType() != Token.TokenType.INPUT
	                    && currentToken.getType() != Token.TokenType.OUTPUT) {
	                errors.add("Missing semicolon at line " + (currentToken.getLineNumber() - 1));
	                while(currentToken != null && currentToken.getType() != Token.TokenType.SEMICOLON) {
	                    currentToken = tokenizer.getNextToken();
	                }
	            }
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
	}

	// Method to handle assignment statements
	private void assignmentStmt() {
	    if (currentToken.getType() == Token.TokenType.NAME) {
	        String nameValue = currentToken.getValue();
	        int startLine = currentToken.getLineNumber();  // Save the line number at the start
	        
	        consume(new ArrayList<>(Arrays.asList(Token.TokenType.NAME)));
	        
	        if (currentToken.getType() == Token.TokenType.LEFT_PAREN) {
	            // Handle unrecognized keyword followed by a left parenthesis
	            errors.add("Unrecognized keyword: " + nameValue + " at line " + startLine);
	        } else {
	            consume(new ArrayList<>(Arrays.asList(Token.TokenType.COLON)));
	            consume(new ArrayList<>(Arrays.asList(Token.TokenType.EQUALS)));
	            
	            // Check if there is a token that could start an arithmetic expression
	            if (currentToken == null || currentToken.getType() != Token.TokenType.NAME 
	                    && currentToken.getType() != Token.TokenType.INTEGER_VALUE 
	                    && currentToken.getType() != Token.TokenType.LEFT_PAREN) {
	                // Report missing arithmetic expression
	                errors.add("Expected an arithmetic expression at line " + startLine);
	            } else {
	                try {
	                    arithExp();  // Parse the arithmetic expression
	                } catch (ParsingException e) {
	                    errors.add(e.getMessage());
	                }
	            }
	        }
	    } else {
	        // Handle unrecognized word
	        errors.add("Unrecognized word: " + currentToken.getValue() + " at line " + currentToken.getLineNumber());
	    }
	}


	// Method to handle arithmetic expressions
	private void arithExp() throws ParsingException {
	    // Parse the term
	    term();
	    // While there are more tokens and the current token is an ADD_SIGN or SUB_SIGN token
	    while (currentToken != null && (currentToken.getType() == Token.TokenType.ADD_SIGN
	            || currentToken.getType() == Token.TokenType.SUB_SIGN)) {
	        // Parse the addition sign
	        addSign();
	        // And parse the term
	        term();
	    }
	}
	// This method processes "terms" in your grammar. A term is a multiplication, division, or modulo operation of two factors.
	private void term() throws ParsingException {
	    // We first process a factor
	    factor();
	    // Then, while there are more tokens and the current token signifies a multiplication, division, or modulo operation
	    while (currentToken != null && (currentToken.getType() == Token.TokenType.MULT_SIGN
	            || currentToken.getType() == Token.TokenType.DIV_SIGN
	            || currentToken.getType() == Token.TokenType.MOD_SIGN)) {
	        // We process the multiplication sign (which could be *, /, or %)
	        mulSign();
	        // Then we process the next factor
	        factor();
	    }
	}

	// This method processes "factors" in your grammar. A factor is a simple value, or a parenthesized arithmetic expression.
	private void factor() throws ParsingException {
	    // If the current token is a left parenthesis, then we have a parenthesized arithmetic expression.
	    if (currentToken.getType() == Token.TokenType.LEFT_PAREN) {
	        // We consume the left parenthesis
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LEFT_PAREN)));
	        // Then we process the arithmetic expression
	        arithExp();
	        // Then we consume the right parenthesis
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.RIGHT_PAREN)));
	    } else if (currentToken.getType() == Token.TokenType.NAME
	            || currentToken.getType() == Token.TokenType.INTEGER_VALUE) {
	        // If the current token is a name or an integer value, we process that
	        nameValue();
	    } else {
	        // Otherwise, this is an invalid factor, and we report an error
	        error("Invalid factor");
	    }
	}

	// This method processes a name or integer value
	private void nameValue() throws ParsingException {
	    // If the current token is a name or an integer value
	    if (currentToken.getType() == Token.TokenType.NAME || currentToken.getType() == Token.TokenType.INTEGER_VALUE) {
	        // We consume it
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(currentToken.getType())));
	    } else {
	        // Otherwise, this is an invalid name or value, and we report an error
	        error("Invalid name or value");
	    }
	}

	// This method processes an addition sign, which could be + or -
	private void addSign() throws ParsingException {
	    // If the current token is a + or -
	    if (currentToken.getType() == Token.TokenType.ADD_SIGN || currentToken.getType() == Token.TokenType.SUB_SIGN) {
	        // We consume it
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(currentToken.getType())));
	    } else {
	        // Otherwise, this is an invalid addition sign, and we report an error
	        error("Invalid addition sign");
	    }
	}

	// This method processes a multiplication sign, which could be *, /, or %
	private void mulSign() throws ParsingException {
	    // If the current token is a *, /, or %
	    if (currentToken.getType() == Token.TokenType.MULT_SIGN || currentToken.getType() == Token.TokenType.DIV_SIGN
	            || currentToken.getType() == Token.TokenType.MOD_SIGN) {
	        // We consume it
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(currentToken.getType())));
	    } else {
	        // Otherwise, this is an invalid multiplication sign, and we report an error
	        error("Invalid multiplication sign");
	    }
	}
	public static void parsetest(List<String> tokens) {
	    if (tokens.size() != 5) {
	        throw new IllegalArgumentException("Invalid expression");
	    }
	    
	    String variable = tokens.get(0);
	    String equals = tokens.get(1);
	    int num1 = Integer.parseInt(tokens.get(2));
	    String plus = tokens.get(3);
	    int num2 = Integer.parseInt(tokens.get(4));

	    if (!equals.equals("=") || !plus.equals("+")) {
	        throw new IllegalArgumentException("Invalid expression");
	    }

	    int result = num1 + num2;
	    System.out.println("The variable " + variable + " is assigned the value " + result);
	}

	// This method processes an input or output statement.
	private void inoutStmt() throws ParsingException {
	    // If the current token is INPUT
	    if (currentToken.getType() == Token.TokenType.INPUT) {
	        // We consume the INPUT keyword
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.INPUT)));
	        // Then we expect a left parenthesis. If it's not there, we report an error and skip to the next statement.
	        if (currentToken.getType() != Token.TokenType.LEFT_PAREN) {
	            errors.add("Missing left parenthesis at line " + currentToken.getLineNumber());
	            while(currentToken != null && currentToken.getType() != Token.TokenType.SEMICOLON) {
	                currentToken = tokenizer.getNextToken();
	            }
	        } else {
	            // If the left parenthesis is there, we consume it
	            consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LEFT_PAREN)));
	            // Then we process a list of names (the variables to be input)
	            nameList();
	            // Then we expect a right parenthesis. If it's not there, we report an error and skip to the next statement.
	            if (currentToken.getType() != Token.TokenType.RIGHT_PAREN) {
	                errors.add("Missing right parenthesis at line " + currentToken.getLineNumber());
	                while(currentToken != null && currentToken.getType() != Token.TokenType.SEMICOLON) {
	                    currentToken = tokenizer.getNextToken();
	                }
	            } else {
	                // If the right parenthesis is there, we consume it
	                consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.RIGHT_PAREN)));
	            }
	        }
	    } else if (currentToken.getType() == Token.TokenType.OUTPUT) {
	        // The process for an OUTPUT statement is similar to that for an INPUT statement.
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.OUTPUT)));
	        if (currentToken.getType() != Token.TokenType.LEFT_PAREN) {
	            errors.add("Missing left parenthesis at line " + currentToken.getLineNumber());
	            while(currentToken != null && currentToken.getType() != Token.TokenType.SEMICOLON) {
	                currentToken = tokenizer.getNextToken();
	            }
	        } else {
	            consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LEFT_PAREN)));
	            // But for OUTPUT, we only need one name or value, not a list.
	            nameValue();
	            if (currentToken.getType() != Token.TokenType.RIGHT_PAREN) {
	                errors.add("Missing right parenthesis at line " + currentToken.getLineNumber());
	                while(currentToken != null && currentToken.getType() != Token.TokenType.SEMICOLON) {
	                    currentToken = tokenizer.getNextToken();
	                }
	            } else {
	                consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.RIGHT_PAREN)));
	            }
	        }
	    } else {
	        // If the current token is neither INPUT nor OUTPUT, this is an error.
	        error("Expected INPUT or OUTPUT statement");
	    }
	}

	//
	 // Parses an IF statement.
	 // Example: IF (bool-exp) THEN statement else-part ENDIF
	 // @throws ParsingException if there is a syntax error or unexpected token in the IF statement
	private void ifStmt() throws ParsingException {
	    consume(new ArrayList<>(Arrays.asList(Token.TokenType.IF))); // Consume the "IF" token

	    // Check for the left parenthesis token
	    if (currentToken.getType() == Token.TokenType.LEFT_PAREN) {
	        consume(new ArrayList<>(Arrays.asList(Token.TokenType.LEFT_PAREN))); // Consume the "(" token
	        boolExp(); // Parse the boolean expression
	        consume(new ArrayList<>(Arrays.asList(Token.TokenType.RIGHT_PAREN))); // Consume the ")" token

	        if (currentToken.getType() == Token.TokenType.THEN) {
	            consume(new ArrayList<>(Arrays.asList(Token.TokenType.THEN))); // Consume the "THEN" token
	            statement(); // Parse the statement
	            elsePart(); // Parse the else-part

	            if (currentToken.getType() == Token.TokenType.ENDIF) {
	                consume(new ArrayList<>(Arrays.asList(Token.TokenType.ENDIF))); // Consume the "ENDIF" token
	            } else {
	                error("Expected ENDIF statement");
	            }
	        } else {
	            error("Expected THEN statement");
	        }
	    } else {
	        error("Expected boolean expression in IF statement");
	    }
	}
	// This method handles the optional "else" part of an "if" statement
	private void elsePart() throws ParsingException {
	    // If the current token is "else"
	    if (currentToken.getType() == Token.TokenType.ELSE) {
	        // We consume the "else" keyword
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.ELSE)));
	        // Then we process the statement that is the body of the "else"
	        statement();
	    }
	}

	public static double evaluateExpression(List<String> tokens) {
        // In a real compiler, this would be more complex and would need to handle operator precedence,
        // different data types, function calls, etc.
        // For now, let's just assume all operations are + and the operands are integers.
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();
        for (String token : tokens) {
            switch (token) {
                case "+":
                case "-":
                case "*":
                case "/":
                case "(":
                    operators.push(token);
                    break;
                case ")":
                    // Evaluate the expression inside the parentheses
                    String operator = operators.pop(); // Remove the "("
                    while (!operator.equals("(")) {
                        double right = values.pop();
                        double left = values.pop();
                        switch (operator) {
                            case "+":
                                values.push(left + right);
                                break;
                            case "-":
                                values.push(left - right);
                                break;
                            case "*":
                                values.push(left * right);
                                break;
                            case "/":
                                values.push(left / right);
                                break;
                        }
                        operator = operators.pop();
                    }
                    break;
                default:
                    // The token is a number
                    values.push(Double.parseDouble(token));
                    break;
            }
        }
        // The remaining value on the stack is the result
        return values.pop();
    }

	
	
	// This method handles "loop" statements in the language's grammar
	private void loopStmt() throws ParsingException {
	    consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LOOP)));
	    
	    if (currentToken.getType() == Token.TokenType.LEFT_PAREN) {
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.LEFT_PAREN)));
	        
	        boolExp();  // Parse the loop condition (boolean expression)
	        
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.RIGHT_PAREN)));
	        
	        if (currentToken.getType() == Token.TokenType.DO) {
	            consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.DO)));
	            
	            stmtList();  // Parse the statements inside the loop
	            
	            if (currentToken.getType() != Token.TokenType.END) {
	                // Report missing END keyword
	                error("Missing END keyword at line ");
	            }
	            
	            consume(new ArrayList<Token.TokenType>(Arrays.asList(Token.TokenType.END)));
	        } else {
	            // Report missing DO keyword
	            error("Missing DO keyword");
	        }
	    } else {
	        // Report missing loop condition
	        error("Missing loop condition");
	    }
	}

	// This method handles boolean expressions, which are comparisons between two values
	private void boolExp() throws ParsingException {
	    nameValue(); // Process the first operand in the boolean expression

	    // Continue processing as long as there are more tokens and the current token is a relational operator
	    while (currentToken != null && (currentToken.getType() == Token.TokenType.EQUALS
	            || currentToken.getType() == Token.TokenType.NOT_EQUALS
	            || currentToken.getType() == Token.TokenType.LESS_THAN
	            || currentToken.getType() == Token.TokenType.LESS_THAN_EQUALS
	            || currentToken.getType() == Token.TokenType.GREATER_THAN
	            || currentToken.getType() == Token.TokenType.GREATER_THAN_EQUALS)) {

	        relationalOper(); // Process the relational operator

	        nameValue(); // Process the next operand in the boolean expression
	    }
	}

	// This method handles relational operators, which are used in comparisons
	private void relationalOper() throws ParsingException {
	    // Depending on the type of the current token
	    switch (currentToken.getType()) {
	    // If it is any kind of relational operator
	    case EQUALS:
	    case NOT_EQUALS:
	    case LESS_THAN:
	    case LESS_THAN_EQUALS:
	    case GREATER_THAN:
	    case GREATER_THAN_EQUALS:
	        // We consume it
	        consume(new ArrayList<Token.TokenType>(Arrays.asList(currentToken.getType())));
	        break;
	    default:
	        // Otherwise, this is an invalid operator, and we report an error
	        error("Invalid relational operator");
	        break;
	    }
	}

	// This method handles compound statements, which are sequences of statements enclosed in "start" and "end" keywords
	 // Parses a compound statement.
	 // Example: START stmt-list END
	 // @throws ParsingException if there is a syntax error or unexpected token in the compound statement
	
	private void compoundStmt() throws ParsingException {
	    consume(new ArrayList<>(Arrays.asList(Token.TokenType.START))); // Consume the "START" token

	    // Check for the "if" keyword
	    if (currentToken != null && currentToken.getType() == Token.TokenType.IF) {
	        consume(new ArrayList<>(Arrays.asList(Token.TokenType.IF))); // Consume the "IF" token

	        // Check for unexpected token after "START"
	        if (currentToken.getType() != Token.TokenType.LEFT_PAREN) {
	            error("Unexpected token after 'START'");
	        }
	    }

	    stmtList(); // Parse the statement list

	    if (currentToken.getType() == Token.TokenType.END) {
	        consume(new ArrayList<>(Arrays.asList(Token.TokenType.END))); // Consume the "END" token
	    } else {
	        error("Expected 'END' statement");
	    }
	}

	// This method reports a syntax error in the program
	private void error(String errorMessage) {
	    // We add the error message to the list of errors, including the line number where the error occurred
	    errors.add(errorMessage + " at line " + (currentToken != null ? currentToken.getLineNumber() : ""));
	}


	// This is the starting point for all Java applications. This method is automatically called when the program is run.
	public static void main(String[] args) {

	    // JFileChooser is a component of Java's Swing library used for providing a simple mechanism to select a file.
	    JFileChooser fileChooser = new JFileChooser();

	    // showOpenDialog(null) displays a new dialog for choosing a file to open.
	    // It returns an int representing the option chosen by the user. 
	    int option = fileChooser.showOpenDialog(null);

	    // If the user clicks the 'Open' button (which is the 'APPROVE_OPTION') 
	    if (option == JFileChooser.APPROVE_OPTION) {

	        // We get the selected file's path.
	        String fileName = fileChooser.getSelectedFile().getAbsolutePath();

	        try {
	            // readFile is a custom method defined below which reads a file and returns its contents as a string.
	            String input = readFile(fileName);

	            // Create a new instance of the najd class, passing in the input string
	            najd parser = new najd(input);

	            // Call the parse method on the parser object
	            parser.parse();

	        } catch (IOException e) { // This block will execute if an IOException is thrown in the try block

	            // Print an error message to the console
	            System.err.println("Failed to read the input file: " + e.getMessage());
	        }
	    } else { // This block will execute if the user didn't choose 'APPROVE_OPTION'

	        // Print a message to the console
	        System.out.println("No file selected.");
	    }
	}

	// This method reads a file and returns its contents as a string.
	private static String readFile(String fileName) throws IOException {

	    // StringBuilder is used to build a mutable sequence of characters. It's more efficient than using '+' operator to concatenate strings.
	    StringBuilder stringBuilder = new StringBuilder();

	    // BufferedReader is used to read text from a character-input stream, buffering characters so as to provide efficient reading of characters, arrays, and lines.
	    BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

	    // A variable to hold each line read from the file
	    String line;

	    // Read the file line by line until there are no more lines (readLine() returns null)
	    while ((line = bufferedReader.readLine()) != null) {

	        // Append each line and a newline to the stringBuilder
	        stringBuilder.append(line).append("\n");
	    }

	    // Always close the resources after use, to prevent memory leaks and other problems.
	    bufferedReader.close();

	    // Convert the StringBuilder to a string and return it
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

	// The `type` field stores the type of the token.
	private TokenType type;

	// The `value` field stores the value of the token.
	private String value;

	// The `lineNumber` field stores the line number where the token was found.
	private int lineNumber;

	// The `Token` constructor initializes the `type`, `value`, and `lineNumber` fields.
	public Token(TokenType type, String value, int lineNumber) {
	    this.type = type;
	    this.value = value;
	    this.lineNumber = lineNumber;
	}

	// The `getType()` method returns the type of the token.
	public TokenType getType() {
	    return type;
	}

	// The `getValue()` method returns the value of the token.
	public String getValue() {
	    return value;
	}

	// The `getLineNumber()` method returns the line number where the token was found.
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

    // `public Token getNextToken()` returns the next token in the input string.

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
            return new Token(Token.TokenType.EOF, "", lineNumber);  // Return EOF token instead of null
        }

        // Identify token types
        char currentChar = input.charAt(position);

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
