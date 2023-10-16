package beermaster.io.console;

public class UserChoiceProvider {
    private static final String NON_INT_INPUT = "Error occurred! You should enter a number for a menu item entry but input was: %s%n";
    private static final String INPUT_LESSER_THAN_ZERO = "Your choice can not be lesser than 0. The choice was: %d%n";
    private final StringInput input;

    public UserChoiceProvider(StringInput input) {
        this.input = input;
    }

    public int get() {
        String userInput = input.read();
        int userNumber;
        try {
            userNumber = Integer.parseInt(userInput);
        } catch (Exception ex) {
            throw new IllegalArgumentException(NON_INT_INPUT.formatted(userInput));
        }
        if (userNumber < 0) {
            throw new IllegalArgumentException(INPUT_LESSER_THAN_ZERO.formatted(userNumber));
        }
        return userNumber;
    }
}
