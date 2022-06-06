import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private final BufferedWriter bufferedWriter;
    private Long labelCounter = 0L;
    private String file;
    private String functionName = "";

    public CodeWriter(File outputFile) throws IOException {
        this.bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        this.file = outputFile.getName().split("\\.")[0];
    }

    public void writeArithmetic(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_ARITHMETIC));
        ArithmeticCommand arithmeticCommand = ArithmeticCommand.valueOf(command.getArg1().toUpperCase());
        StringBuilder sb = new StringBuilder("// " + command.getRaw() +  "\n");
        switch (arithmeticCommand) {
            case ADD : {
                sb.append("@SP\n");
                sb.append("A=M-1\n");
                sb.append("D=M\n");
                sb.append("A=A-1\n");
                sb.append("M=D+M\n");
                sb.append("@SP\n");
                sb.append("M=M-1\n");
                break;
            }
            case SUB : {
                sb.append("@SP\n");
                sb.append("A=M-1\n");
                sb.append("D=M\n");
                sb.append("A=A-1\n");
                sb.append("M=M-D\n");
                sb.append("@SP\n");
                sb.append("M=M-1\n");
                break;
            }
            case NEG : {
                sb.append("@SP\n");
                sb.append("A=M-1\n");
                sb.append("M=-M\n");
                break;
            }
            case EQ : {
                String endLabel = getGlobalLabel();
                String notEqualLabel = getGlobalLabel();
                sb.append("@SP\n");
                sb.append("A=M-1\n");
                sb.append("D=M\n");
                sb.append("A=A-1\n");
                sb.append("D=D-M\n");
                sb.append("@SP\n");
                sb.append("M=M-1\n");
                sb.append("M=M-1\n");
                sb.append("@" + notEqualLabel + "\n");
                sb.append("D;JNE\n");
                sb.append("@SP\n" );
                sb.append("A=M\n");
                sb.append("M=-1\n");
                sb.append("@" + endLabel + "\n");
                sb.append("0;JMP\n");
                sb.append("(" + notEqualLabel + ")" + "\n");
                sb.append("@SP\n");
                sb.append("A=M\n");
                sb.append("M=0\n");
                sb.append("(" + endLabel + ")" + "\n");
                sb.append("@SP\n");
                sb.append("M=M+1\n");
                break;
            }
            case GT : {
                String endLabel = getGlobalLabel();
                String greaterLabel = getGlobalLabel();
                sb.append("@SP\n");
                sb.append("A=M-1\n");
                sb.append("D=M\n");
                sb.append("A=A-1\n");
                sb.append("D=M-D\n");
                sb.append("@SP\n");
                sb.append("M=M-1\n");
                sb.append("M=M-1\n");
                sb.append("@" + greaterLabel + "\n");
                sb.append("D;JGT" + "\n");
                sb.append("@SP" + "\n");
                sb.append("A=M\n");
                sb.append("M=0" + "\n");
                sb.append("@" + endLabel + "\n");
                sb.append("0;JMP" + "\n");
                sb.append("(" + greaterLabel + ")" + "\n");
                sb.append("@SP\n");
                sb.append("A=M\n");
                sb.append("M=-1" + "\n");
                sb.append("(" + endLabel + ")" + "\n");
                sb.append("@SP" + "\n");
                sb.append("M=M+1" + "\n");
                break;
            }
            case LT : {
                String endLabel = getGlobalLabel();
                String lessLabel = getGlobalLabel();
                sb.append("@SP\n");
                sb.append("A=M-1\n");
                sb.append("D=M\n");
                sb.append("A=A-1\n");
                sb.append("D=M-D\n");
                sb.append("@SP\n");
                sb.append("M=M-1\n");
                sb.append("M=M-1\n");
                sb.append("@" + lessLabel  + "\n");
                sb.append("D;JLT" + "\n");
                sb.append("@SP" + "\n");
                sb.append("A=M\n");
                sb.append("M=0" + "\n");
                sb.append("@" + endLabel + "\n");
                sb.append("0;JMP" + "\n");
                sb.append("(" + lessLabel + ")" + "\n");
                sb.append("@SP" + "\n");
                sb.append("A=M\n");
                sb.append("M=-1" + "\n");
                sb.append("(" + endLabel + ")" + "\n");
                sb.append("@SP" + "\n");
                sb.append("M=M+1" + "\n");
                break;
            }
            case AND : {
                sb.append("@SP\n");
                sb.append("A=M-1\n");
                sb.append("D=M\n");
                sb.append("A=A-1\n");
                sb.append("M=D&M\n");
                sb.append("@SP\n");
                sb.append("M=M-1\n");
                break;
            }
            case OR : {
                sb.append("@SP\n");
                sb.append("A=M-1\n");
                sb.append("D=M\n");
                sb.append("A=A-1\n");
                sb.append("M=D|M\n");
                sb.append("@SP\n");
                sb.append("M=M-1\n");
                break;
            }
            case NOT : {
                sb.append("@SP\n");
                sb.append("A=M-1\n");
                sb.append("M=!M\n");
                break;
            }
        }
        bufferedWriter.write(sb.toString());
    }

    public void writePushPop(Command command) throws IOException {
        if(!command.getCommandType().equals(CommandType.C_PUSH) && !command.getCommandType().equals(CommandType.C_POP))
            throw new IllegalArgumentException();
       String segment = command.getArg1().toUpperCase();
       StringBuilder sb = new StringBuilder("// " + command.getRaw() + "\n");
       if(command.getCommandType().equals(CommandType.C_PUSH))
           switch (segment) {
               case "CONSTANT" : {
                    sb.append("@" + command.getArg2() + "\n");
                    sb.append("D=A\n");
                    sb.append("@SP\n");
                    sb.append("A=M\n");
                    sb.append("M=D\n");
                    sb.append("@SP\n");
                    sb.append("M=M+1\n");
                    break;
               }
               case "LOCAL":
               case "ARGUMENT":
               case "THIS":
               case "THAT" : {
                    sb.append("@" + command.getArg2() + "\n");
                    sb.append("D=A\n");
                    sb.append("@" + getPointerNameBySegment(command.getArg1()) + "\n");
                    sb.append("A=D+M\n");
                    sb.append("D=M\n");
                    sb.append("@SP\n");
                    sb.append("A=M\n");
                    sb.append("M=D\n");
                    sb.append("@SP\n");
                    sb.append("M=M+1\n");
                    break;
               }
               case "TEMP" : {
                   if(command.getArg2() < 0 || command.getArg2() > 7)
                       throw new IllegalArgumentException();
                   sb.append("@" + (command.getArg2() + 5) + "\n");
                   sb.append("D=M\n");
                   sb.append("@SP\n");
                   sb.append("A=M\n");
                   sb.append("M=D\n");
                   sb.append("@SP\n");
                   sb.append("M=M+1\n");
                   break;
               }
               case "POINTER" : {
                   String label = command.getArg2() == 0 ? "THIS" : "THAT";
                   sb.append("@" + label + "\n");
                   sb.append("D=M\n");
                   sb.append("@SP\n");
                   sb.append("A=M\n");
                   sb.append("M=D\n");
                   sb.append("@SP\n");
                   sb.append("M=M+1\n");
                   break;
               }
               case "STATIC" : {
                   sb.append("@" + file + "." + command.getArg2() + "\n");
                   sb.append("D=M\n");
                   sb.append("@SP\n");
                   sb.append("A=M\n");
                   sb.append("M=D\n");
                   sb.append("@SP\n");
                   sb.append("M=M+1\n");
                   break;
               }
           }
       else // POP
           switch (segment) {
               case "LOCAL":
               case  "ARGUMENT":
               case "THIS":
               case  "THAT" : {
                   sb.append("@" + command.getArg2() + "\n");
                   sb.append("D=A\n");
                   sb.append("@" + getPointerNameBySegment(command.getArg1()) + "\n");
                   sb.append("D=D+M\n");
                   sb.append("@SP\n");
                   sb.append("M=M-1\n");
                   sb.append("A=M+1\n");
                   sb.append("M=D\n");
                   sb.append("@SP\n");
                   sb.append("A=M\n");
                   sb.append("D=M\n");
                   sb.append("A=A+1\n");
                   sb.append("A=M\n");
                   sb.append("M=D\n");
                   break;
               }
               case "TEMP" : {
                   if(command.getArg2() < 0 || command.getArg2() > 7)
                       throw new IllegalArgumentException();
                   sb.append("@SP\n");
                   sb.append("M=M-1\n");
                   sb.append("A=M\n");
                   sb.append("D=M\n");
                   sb.append("@" + (command.getArg2() + 5) + "\n");
                   sb.append("M=D\n");
                   break;
               }
               case "POINTER" : {
                   String label = command.getArg2() == 0 ? "THIS" : "THAT";
                   sb.append("@SP\n");
                   sb.append("M=M-1\n");
                   sb.append("A=M\n");
                   sb.append("D=M\n");
                   sb.append("@" + label + "\n");
                   sb.append("M=D\n");
                   break;
               }
               case "STATIC" : {
                   sb.append("@SP\n");
                   sb.append("M=M-1\n");
                   sb.append("A=M\n");
                   sb.append("D=M\n");
                   sb.append("@" + file + "." + command.getArg2() + "\n");
                   sb.append("M=D\n");
                   break;
               }
           }
       bufferedWriter.write(sb.toString());
    }

    public void close() throws IOException {
        bufferedWriter.close();
    }

    public void setFileName(String fileName) {
        this.file = fileName;
    }

    public void writeInit() {

    }

    public void writeLabel(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_LABEL));
        StringBuilder sb = new StringBuilder("// " + command.getRaw() +  "\n");
        sb.append("(" + getHackLocalLabel(command.getArg1()) + ")\n");
        bufferedWriter.write(sb.toString());
    }

    public void writeGoto(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_GOTO));
        StringBuilder sb = new StringBuilder("// " + command.getRaw() +  "\n");
        sb.append("@" + getHackLocalLabel(command.getArg1()) + "\n");
        sb.append("0;JMP\n");
        bufferedWriter.write(sb.toString());
    }

    public void writeIf(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_IF));
        StringBuilder sb = new StringBuilder("// " + command.getRaw() +  "\n");
        sb.append("@SP\n");
        sb.append("M=M-1\n");
        sb.append("A=M\n");
        sb.append("D=M\n");
        sb.append("@" + getHackLocalLabel(command.getArg1()) + "\n");
        sb.append("D;JNE\n");
        bufferedWriter.write(sb.toString());
    }

    private String getGlobalLabel() {
        return "_label" + labelCounter++;
    }

    private String getHackLocalLabel(String label) {
        return file + "." + functionName + "$" + label;
    }

    private String getPointerNameBySegment(String segment) {
        switch (segment.toUpperCase()) {
            case "LOCAL" : {
                return "LCL";
            }
            case "ARGUMENT" : {
                return "ARG";
            }
            case "THIS" : {
                return "THIS";
            }
            case "THAT" : {
                return "THAT";
            }
            default : {
                throw new IllegalArgumentException();
            }
        }
    }
}
