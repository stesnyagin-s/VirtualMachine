import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CodeWriter {
    private final BufferedWriter bw;
    private Long labelCounter = 0L;
    private String file;
    private String functionName = "";
    private Map<String, Integer> returnLabelCounter = new HashMap<>();

    public CodeWriter(File outputFile) throws IOException {
        this.bw = new BufferedWriter(new FileWriter(outputFile));
    }

    public void writeArithmetic(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_ARITHMETIC));
        ArithmeticCommand arithmeticCommand = ArithmeticCommand.valueOf(command.getArg1().toUpperCase());
        switch (arithmeticCommand) {
            case ADD : {
                bw.write("@SP\n");
                bw.write("A=M-1\n");
                bw.write("D=M\n");
                bw.write("A=A-1\n");
                bw.write("M=D+M\n");
                bw.write("@SP\n");
                bw.write("M=M-1\n");
                break;
            }
            case SUB : {
                bw.write("@SP\n");
                bw.write("A=M-1\n");
                bw.write("D=M\n");
                bw.write("A=A-1\n");
                bw.write("M=M-D\n");
                bw.write("@SP\n");
                bw.write("M=M-1\n");
                break;
            }
            case NEG : {
                bw.write("@SP\n");
                bw.write("A=M-1\n");
                bw.write("M=-M\n");
                break;
            }
            case EQ : {
                String endLabel = getGlobalLabel();
                String notEqualLabel = getGlobalLabel();
                bw.write("@SP\n");
                bw.write("A=M-1\n");
                bw.write("D=M\n");
                bw.write("A=A-1\n");
                bw.write("D=D-M\n");
                bw.write("@SP\n");
                bw.write("M=M-1\n");
                bw.write("M=M-1\n");
                bw.write("@" + notEqualLabel + "\n");
                bw.write("D;JNE\n");
                bw.write("@SP\n" );
                bw.write("A=M\n");
                bw.write("M=-1\n");
                bw.write("@" + endLabel + "\n");
                bw.write("0;JMP\n");
                bw.write("(" + notEqualLabel + ")" + "\n");
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=0\n");
                bw.write("(" + endLabel + ")" + "\n");
                bw.write("@SP\n");
                bw.write("M=M+1\n");
                break;
            }
            case GT : {
                String endLabel = getGlobalLabel();
                String greaterLabel = getGlobalLabel();
                bw.write("@SP\n");
                bw.write("A=M-1\n");
                bw.write("D=M\n");
                bw.write("A=A-1\n");
                bw.write("D=M-D\n");
                bw.write("@SP\n");
                bw.write("M=M-1\n");
                bw.write("M=M-1\n");
                bw.write("@" + greaterLabel + "\n");
                bw.write("D;JGT" + "\n");
                bw.write("@SP" + "\n");
                bw.write("A=M\n");
                bw.write("M=0" + "\n");
                bw.write("@" + endLabel + "\n");
                bw.write("0;JMP" + "\n");
                bw.write("(" + greaterLabel + ")" + "\n");
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=-1" + "\n");
                bw.write("(" + endLabel + ")" + "\n");
                bw.write("@SP" + "\n");
                bw.write("M=M+1" + "\n");
                break;
            }
            case LT : {
                String endLabel = getGlobalLabel();
                String lessLabel = getGlobalLabel();
                bw.write("@SP\n");
                bw.write("A=M-1\n");
                bw.write("D=M\n");
                bw.write("A=A-1\n");
                bw.write("D=M-D\n");
                bw.write("@SP\n");
                bw.write("M=M-1\n");
                bw.write("M=M-1\n");
                bw.write("@" + lessLabel  + "\n");
                bw.write("D;JLT" + "\n");
                bw.write("@SP" + "\n");
                bw.write("A=M\n");
                bw.write("M=0" + "\n");
                bw.write("@" + endLabel + "\n");
                bw.write("0;JMP" + "\n");
                bw.write("(" + lessLabel + ")" + "\n");
                bw.write("@SP" + "\n");
                bw.write("A=M\n");
                bw.write("M=-1" + "\n");
                bw.write("(" + endLabel + ")" + "\n");
                bw.write("@SP" + "\n");
                bw.write("M=M+1" + "\n");
                break;
            }
            case AND : {
                bw.write("@SP\n");
                bw.write("A=M-1\n");
                bw.write("D=M\n");
                bw.write("A=A-1\n");
                bw.write("M=D&M\n");
                bw.write("@SP\n");
                bw.write("M=M-1\n");
                break;
            }
            case OR : {
                bw.write("@SP\n");
                bw.write("A=M-1\n");
                bw.write("D=M\n");
                bw.write("A=A-1\n");
                bw.write("M=D|M\n");
                bw.write("@SP\n");
                bw.write("M=M-1\n");
                break;
            }
            case NOT : {
                bw.write("@SP\n");
                bw.write("A=M-1\n");
                bw.write("M=!M\n");
                break;
            }
        }
    }

    public void writePushPop(Command command) throws IOException {
        if(!command.getCommandType().equals(CommandType.C_PUSH) && !command.getCommandType().equals(CommandType.C_POP))
            throw new IllegalArgumentException();
       String segment = command.getArg1().toUpperCase();
       if(command.getCommandType().equals(CommandType.C_PUSH))
           switch (segment) {
               case "CONSTANT" : {
                    bw.write("@" + command.getArg2() + "\n");
                    bw.write("D=A\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    break;
               }
               case "LOCAL":
               case "ARGUMENT":
               case "THIS":
               case "THAT" : {
                    bw.write("@" + command.getArg2() + "\n");
                    bw.write("D=A\n");
                    bw.write("@" + getPointerNameBySegment(command.getArg1()) + "\n");
                    bw.write("A=D+M\n");
                    bw.write("D=M\n");
                    bw.write("@SP\n");
                    bw.write("A=M\n");
                    bw.write("M=D\n");
                    bw.write("@SP\n");
                    bw.write("M=M+1\n");
                    break;
               }
               case "TEMP" : {
                   if(command.getArg2() < 0 || command.getArg2() > 7)
                       throw new IllegalArgumentException();
                   bw.write("@" + (command.getArg2() + 5) + "\n");
                   bw.write("D=M\n");
                   bw.write("@SP\n");
                   bw.write("A=M\n");
                   bw.write("M=D\n");
                   bw.write("@SP\n");
                   bw.write("M=M+1\n");
                   break;
               }
               case "POINTER" : {
                   String label = command.getArg2() == 0 ? "THIS" : "THAT";
                   bw.write("@" + label + "\n");
                   bw.write("D=M\n");
                   bw.write("@SP\n");
                   bw.write("A=M\n");
                   bw.write("M=D\n");
                   bw.write("@SP\n");
                   bw.write("M=M+1\n");
                   break;
               }
               case "STATIC" : {
                   bw.write("@" + file + "." + command.getArg2() + "\n");
                   bw.write("D=M\n");
                   bw.write("@SP\n");
                   bw.write("A=M\n");
                   bw.write("M=D\n");
                   bw.write("@SP\n");
                   bw.write("M=M+1\n");
                   break;
               }
           }
       else // POP
           switch (segment) {
               case "LOCAL":
               case  "ARGUMENT":
               case "THIS":
               case  "THAT" : {
                   bw.write("@" + command.getArg2() + "\n");
                   bw.write("D=A\n");
                   bw.write("@" + getPointerNameBySegment(command.getArg1()) + "\n");
                   bw.write("D=D+M\n");
                   bw.write("@SP\n");
                   bw.write("M=M-1\n");
                   bw.write("A=M+1\n");
                   bw.write("M=D\n");
                   bw.write("@SP\n");
                   bw.write("A=M\n");
                   bw.write("D=M\n");
                   bw.write("A=A+1\n");
                   bw.write("A=M\n");
                   bw.write("M=D\n");
                   break;
               }
               case "TEMP" : {
                   if(command.getArg2() < 0 || command.getArg2() > 7)
                       throw new IllegalArgumentException();
                   bw.write("@SP\n");
                   bw.write("M=M-1\n");
                   bw.write("A=M\n");
                   bw.write("D=M\n");
                   bw.write("@" + (command.getArg2() + 5) + "\n");
                   bw.write("M=D\n");
                   break;
               }
               case "POINTER" : {
                   String label = command.getArg2() == 0 ? "THIS" : "THAT";
                   bw.write("@SP\n");
                   bw.write("M=M-1\n");
                   bw.write("A=M\n");
                   bw.write("D=M\n");
                   bw.write("@" + label + "\n");
                   bw.write("M=D\n");
                   break;
               }
               case "STATIC" : {
                   bw.write("@SP\n");
                   bw.write("M=M-1\n");
                   bw.write("A=M\n");
                   bw.write("D=M\n");
                   bw.write("@" + file + "." + command.getArg2() + "\n");
                   bw.write("M=D\n");
                   break;
               }
           }
    }

    public void close() throws IOException {
        bw.close();
    }

    public void setFileName(String fileName) {
        this.file = fileName;
    }

    public void writeInit() throws IOException {
        bw.write("@" + 256 + "\n");
        bw.write("D=A\n");
        bw.write("@SP\n");
        bw.write("M=D\n");
        writeCall(new Command(CommandType.C_CALL, "Sys.init", 0, null));
    }

    public void writeLabel(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_LABEL));
        bw.write("(" + getHackLocalLabel(command.getArg1()) + ")\n");
    }

    public void writeGoto(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_GOTO));
        bw.write("@" + getHackLocalLabel(command.getArg1()) + "\n");
        bw.write("0;JMP\n");
        
    }

    public void writeIf(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_IF));
        bw.write("@SP\n");
        bw.write("M=M-1\n");
        bw.write("A=M\n");
        bw.write("D=M\n");
        bw.write("@" + getHackLocalLabel(command.getArg1()) + "\n");
        bw.write("D;JNE\n");
    }

    public void writeFunction(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_FUNCTION));
        bw.write("(" + command.getArg1() + ")\n");
        for(int i = 0; i < command.getArg2(); i++) {
            writePushPop(new Command(CommandType.C_PUSH, "CONSTANT", 0, ""));
        };
    }

    public void writeReturn(Command command) throws IOException {
        assert (command.getCommandType().equals(CommandType.C_RETURN));
        //FRAME = LCL
        bw.write("@LCL\n");
        bw.write("D=M\n");
        bw.write("@R13\n");
        bw.write("M=D\n");

        //RET = *(FRAME-5)
        bw.write("@R13\n");
        bw.write("D=M\n");
        bw.write("@5\n");
        bw.write("D=D-A\n");
        bw.write("A=D\n");
        bw.write("D=M\n");
        bw.write("@R14\n");
        bw.write("M=D\n");

        //*ARG = pop()
        writePushPop(new Command(CommandType.C_POP, "ARGUMENT", 0, null));
//        bw.write("@SP\n");
//        bw.write("A=M\n");
//        bw.write("D=M\n");
//        bw.write("@ARG\n");
//        bw.write("A=M\n");
//        bw.write("M=D\n");

        //SP = ARG+1
        bw.write("@ARG\n");
        bw.write("D=M+1\n");
        bw.write("@SP\n");
        bw.write("M=D\n");

//        THAT = *(FRAME-1)
        bw.write("@R13\n");
        bw.write("M=M-1\n");
        bw.write("A=M\n");
        bw.write("D=M\n");
        bw.write("@THAT\n");
        bw.write("M=D\n");

        //THIS = *(FRAME-2)
        bw.write("@R13\n");
        bw.write("M=M-1\n");
        bw.write("A=M\n");
        bw.write("D=M\n");
        bw.write("@THIS\n");
        bw.write("M=D\n");

        //ARG = *(FRAME-3)
        bw.write("@R13\n");
        bw.write("M=M-1\n");
        bw.write("A=M\n");
        bw.write("D=M\n");
        bw.write("@ARG\n");
        bw.write("M=D\n");

        //LCL = *(FRAME-4)
        bw.write("@R13\n");
        bw.write("M=M-1\n");
        bw.write("A=M\n");
        bw.write("D=M\n");
        bw.write("@LCL\n");
        bw.write("M=D\n");

        //goto RET
        bw.write("@R14\n");
        bw.write("A=M\n");
        bw.write("0;JMP\n");
    }

    public void writeCall(Command command) throws IOException {
        String returnLabel =  functionName + "." + command.getArg1() + "$return-address";
        returnLabel = returnLabel + returnLabelCounter.merge(returnLabel, 1, Integer::sum);

        //push return-address
        bw.write("@" + returnLabel + "\n");
        bw.write("D=A\n");
        bw.write("@SP\n");
        bw.write("A=M\n");
        bw.write("M=D\n");
        bw.write("@SP\n");
        bw.write("M=M+1\n");

        //push LCL
        bw.write("@" + "LCL" + "\n");
        bw.write("D=M\n");
        bw.write("@SP\n");
        bw.write("A=M\n");
        bw.write("M=D\n");
        bw.write("@SP\n");
        bw.write("M=M+1\n");

        //push ARG
        bw.write("@" + "ARG" + "\n");
        bw.write("D=M\n");
        bw.write("@SP\n");
        bw.write("A=M\n");
        bw.write("M=D\n");
        bw.write("@SP\n");
        bw.write("M=M+1\n");

        //push THIS
        bw.write("@" + "THIS" + "\n");
        bw.write("D=M\n");
        bw.write("@SP\n");
        bw.write("A=M\n");
        bw.write("M=D\n");
        bw.write("@SP\n");
        bw.write("M=M+1\n");

        //push THAT
        bw.write("@" + "THAT" + "\n");
        bw.write("D=M\n");
        bw.write("@SP\n");
        bw.write("A=M\n");
        bw.write("M=D\n");
        bw.write("@SP\n");
        bw.write("M=M+1\n");

        //ARG = SP-n-5
        bw.write("@SP\n");
        bw.write("D=M\n");
        bw.write("@" + (command.getArg2() + 5) + "\n");
        bw.write("D=D-A\n");
        bw.write("@ARG\n");
        bw.write("M=D\n");

        //LCL = SP
        bw.write("@SP\n");
        bw.write("D=M\n");
        bw.write("@LCL\n");
        bw.write("M=D\n");

        //goto f
        bw.write("@" + command.getArg1() + "\n");
        bw.write("0;JMP\n");

        //(return-address)
        bw.write("(" + returnLabel + ")\n");

    }

    public void writeRawCommand(Command command) throws IOException {
        bw.write("// " + command.getRaw() + "\n");
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    private String getGlobalLabel() {
        return "_label" + labelCounter++;
    }

    private String getHackLocalLabel(String label) {
        return functionName + "$" + label;
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
