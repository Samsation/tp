package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.EmptyStackException;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;


/**
 * A build-in calculator for bobaBot for cashiers to do basic arithmetic
 * calculation including +, -, /, -, with precedence enabled
 * e.g. calc 3*(1+4)/2
 */
public class CalculateCommand extends Command {

    /**
     * Command word for calculator
     */
    public static final String COMMAND_WORD = "calc";

    /**
     * Message for calculator
     */
    public static final String MESSAGE_USAGE = COMMAND_WORD + "Calculate an arithmetic expression"
            + "Example: " + COMMAND_WORD + "3 * (1 + 2.4)";

    public String expression;

    public CalculateCommand(String expression) {
        this.expression = expression;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {

        requireNonNull(model);
        String calcResult = ExpressionParser.parseCalculation(this.expression);
        String feedback = "Calculator:\n";
        feedback += this.expression + " = " + calcResult;
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CalculateCommand // instanceof handles nulls
                && expression.equals(((CalculateCommand) other).expression)); // state check
    }

    private static class ExpressionParser {
        private static final int HIGH_PRECEDENCE = 2;
        private static final int LOW_PRECEDENCE = 0;

        // All operators set with their precedence
        private static final Map<String, Integer> OPERATORS = new HashMap<>();

        static {
            // Map<"token", precedence>
            OPERATORS.put("+", LOW_PRECEDENCE);
            OPERATORS.put("-", LOW_PRECEDENCE);
            OPERATORS.put("*", HIGH_PRECEDENCE);
            OPERATORS.put("/", HIGH_PRECEDENCE);
        }

        // Test if token is an operator
        private static boolean isOperator(String token) {
            return OPERATORS.containsKey(token);
        }

        // Compare precedence of operators.
        private static int comparePrecedence(String token1, String token2) {
            if (!isOperator(token1) || !isOperator(token2)) {
                throw new IllegalArgumentException("Invalid tokens: " + token1
                        + " " + token2);
            }
            return OPERATORS.get(token1) - OPERATORS.get(token2);
        }

        // Convert infix expression format into reverse Polish notation
        public static String[] expressionToRPN(String[] inputTokens) {
            ArrayList<String> RPN = new ArrayList<>();
            Stack<String> stack = new Stack<>();

            // For each token
            for (String token : inputTokens) {
                if (isOperator(token)) {
                    // If token is an operator
                    
                    // While stack not empty AND stack top element
                    // is an operator and have higher precedence
                    while (!stack.empty() && isOperator(stack.peek()) &&
                            comparePrecedence(token, stack.peek()) <= 0) {
                        RPN.add(stack.pop());
                    }
                    // Push the new operator on the stack
                    stack.push(token);
                } else if (token.equals("(")) {
                    // If token is a left parenthesis
                    stack.push(token);  //
                } else if (token.equals(")")) {
                    // If token is a right parenthesis
                    while (!stack.empty() && !stack.peek().equals("(")) {
                        RPN.add(stack.pop());
                    }
                    stack.pop();
                } else {
                    // If token is a number
                    RPN.add(token);
                }
            }
            while (!stack.empty()) {
                RPN.add(stack.pop());
            }
            String[] RPNStrArr = new String[RPN.size()];
            return RPN.toArray(RPNStrArr);
        }

        public static double RPNtoDouble(String[] tokens) {
            Stack<String> stack = new Stack<>();

            // For each token
            for (String token : tokens) //for each
            {
                // If the token is a value push it onto the stack
                if (!isOperator(token)) {
                    stack.push(token);
                } else {
                    // Token is an operator: pop top two entries
                    Double d2 = Double.valueOf(stack.pop());
                    Double d1 = Double.valueOf(stack.pop());

                    //Get the result
                    Double result = token.compareTo("*") == 0 ? d1 * d2 :
                            token.compareTo("/") == 0 ? d1 / d2 :
                                    token.compareTo("+") == 0 ? d1 + d2 :
                                            d1 - d2;
                    // Push result onto stack
                    stack.push(String.valueOf(result));
                }
            }

            return Double.parseDouble(stack.pop());
        }
        public static String parseCalculation(String userInput) {
            String regex = "((?<=[(|)|\\+|\\*|\\-|/])|(?=[(|)|\\+|\\*|\\-|/]))";
            String resultStr;
            System.out.println(userInput);
            try{
                String[] input =  userInput.split(regex);
                String[] output = expressionToRPN(input);

                System.out.print("Stack: ");
                for (String token : output) {
                    System.out.print("[ ");System.out.print(token + " "); System.out.print("]");
                }
                System.out.println(" ");
                // Feed the RPN string to RPNtoDouble to give result
                Double result = RPNtoDouble( output );
                resultStr = String.format("%.2f", result);
            } catch (NumberFormatException | EmptyStackException nfe){
                resultStr = "INVALID EXPRESSION";
            }
            return resultStr;
        }
    }
}
