import java.util.Arrays;

public enum ArithmeticCommand {
    ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT;

    public static boolean contains(String command) {
        String commandUpperCase = command.toUpperCase();
        return Arrays.stream(values())
                .map(ArithmeticCommand::name)
                .anyMatch(arithmeticCommand -> arithmeticCommand.equals(commandUpperCase));
    }

}
