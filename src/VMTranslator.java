import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VMTranslator {
    public static void main(String[] args) throws IOException {
        List<File> sourceFiles = new ArrayList<>();
        if(args == null || args.length != 1)
            throw new IllegalArgumentException();
        String inputFileName = args[0];
        String outputFileName = args[0].split("\\.")[0] + ".asm";

        String inputFilePath = System.getProperty("user.dir") + File.separator + inputFileName;
        String outputFilePath;

        File inputFile = new File(inputFilePath);
        if(inputFile.isFile()) {
            sourceFiles.add(inputFile);
            outputFilePath = System.getProperty("user.dir") + File.separator + outputFileName;
        } else {
            sourceFiles.addAll(Arrays.stream(inputFile
                    .listFiles((dir, name) -> name.endsWith(".vm")))
                    .collect(Collectors.toList()));
            outputFilePath = inputFile.getPath() + File.separator + outputFileName;
        }
        File outputFile = new File(outputFilePath);

        Parser parser = new Parser();
        CodeWriter codeWriter = new CodeWriter(outputFile);
        codeWriter.setFunctionName("Bootstrap");
        if(inputFile.isDirectory()) {
            codeWriter.writeInit();
        }
        for (File file : sourceFiles) {
            codeWriter.setFileName(file.getName().split("\\.")[0]);
            parser.setFile(file);
            while (parser.hasMoreCommands()) {
                Command command = parser.advance();
                codeWriter.writeRawCommand(command);
                if (command.getCommandType().equals(CommandType.C_ARITHMETIC))
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
                else if (command.getCommandType().equals(CommandType.C_FUNCTION)) {
                    codeWriter.setFunctionName(command.getArg1());
                    codeWriter.writeFunction(command);
                } else if (command.getCommandType().equals(CommandType.C_RETURN)) {
                    codeWriter.writeReturn(command);
                } else if (command.getCommandType().equals(CommandType.C_CALL)) {
                    codeWriter.writeCall(command);
                }
            }
        }
        codeWriter.close();
    }
}