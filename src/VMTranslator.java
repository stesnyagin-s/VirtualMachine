import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VMTranslator {
    public static void main(String[] args) throws IOException {
        List<File> sourceFiles = new ArrayList<>();
        if(args == null || args.length != 1 || !args[0].endsWith(".vm"))
            throw new IllegalArgumentException();
        String inputFileName = args[0];
        String outputFileName = args[0].split("\\.")[0] + ".asm";

        String inputFilePath = System.getProperty("user.dir") + File.separator + inputFileName;
        String outputFilePath = System.getProperty("user.dir") + File.separator + outputFileName;

        File inputFile = new File(inputFileName);
        if(inputFile.isFile()) {
            sourceFiles.add(inputFile);
        } else {
            sourceFiles.addAll(Arrays.stream(inputFile
                    .listFiles((dir, name) -> name.endsWith(".vm")))
                    .collect(Collectors.toList()));
        }
        File outputFile = new File(outputFileName);

        Parser parser = new Parser(inputFile);
        CodeWriter codeWriter = new CodeWriter(outputFile);

        while (parser.hasMoreCommands()) {
            Command command = parser.advance();
            if(command.getCommandType().equals(CommandType.C_ARITHMETIC))
                codeWriter.writeArithmetic(command);
            else if (command.getCommandType().equals(CommandType.C_PUSH) ||
                    command.getCommandType().equals(CommandType.C_POP))
                codeWriter.writePushPop(command);
            else if (command.getCommandType().equals(CommandType.C_LABEL))
                codeWriter.writeLabel(command);
            else if (command.getCommandType().equals(CommandType.C_GOTO))
                codeWriter.writeGoto(command);
            else if (command.getCommandType().equals(CommandType.C_IF))
                codeWriter.writeIf(command);
        }
        codeWriter.close();
    }
}