import java.io.*;

public class Parser {
    private final BufferedReader bufferedReader;
    private Command currentCommand;
    public Parser(File file) throws FileNotFoundException {
        bufferedReader = new BufferedReader(new FileReader(file));
    }

    public boolean hasMoreCommands() throws IOException {
        if(currentCommand != null)
            return true;
        tryParseCommand();
        return currentCommand != null;
    }

    public Command advance(){
        Command command = currentCommand;
        currentCommand = null;
        return command;
    }

    private void tryParseCommand() throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.split("//")[0].trim().toLowerCase();
            if(line.equals("")  || line.startsWith("//"))
                continue;
            if(ArithmeticCommand.contains(line))
                currentCommand = new Command(CommandType.C_ARITHMETIC, line, null, line);
            else if(line.startsWith("push")) {
                String[] splittedLine = line.split(" ");
                currentCommand = new Command(CommandType.C_PUSH, splittedLine[1], Integer.parseInt(splittedLine[2]), line);
            }
            else if(line.startsWith("pop")) {
                String[] splittedLine = line.split(" ");
                currentCommand = new Command(CommandType.C_POP, splittedLine[1], Integer.parseInt(splittedLine[2]), line);
            }
            else if(line.startsWith("label")) {
                String[] splittedLine = line.split(" ");
                currentCommand = new Command(CommandType.C_LABEL, splittedLine[1], null, line);
            }
            else if(line.startsWith("goto")) {
                String[] splittedLine = line.split(" ");
                currentCommand = new Command(CommandType.C_GOTO, splittedLine[1], null, line);
            }
            else if(line.startsWith("if-goto")) {
                String[] splittedLine = line.split(" ");
                currentCommand = new Command(CommandType.C_IF, splittedLine[1], null, line);
            }
            else if(line.startsWith("function")) {
                String[] splittedLine = line.split(" ");
                currentCommand = new Command(CommandType.C_FUNCTION, splittedLine[1], Integer.parseInt(splittedLine[2]), line);
            }
            else if(line.startsWith("call")) {
                String[] splittedLine = line.split(" ");
                currentCommand = new Command(CommandType.C_CALL, splittedLine[1], Integer.parseInt(splittedLine[2]), line);
            }
            else if(line.startsWith("return")) {
                String[] splittedLine = line.split(" ");
                currentCommand = new Command(CommandType.C_RETURN, splittedLine[1], null, line);
            }
            break;
        }
    }

}
