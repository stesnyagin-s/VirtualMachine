public class Command {
    private CommandType commandType;
    private String arg1;
    private Integer arg2;
    private String raw;

    public Command(CommandType commandType, String arg1, Integer arg2, String raw) {
        this.commandType = commandType;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.raw = raw;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String getArg1() {
        if(commandType.equals(CommandType.C_RETURN))
            throw new IllegalStateException();
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public int getArg2() {
        if(commandType.equals(CommandType.C_PUSH) || commandType.equals(CommandType.C_POP) ||
                commandType.equals(CommandType.C_FUNCTION) || commandType.equals(CommandType.C_CALL))
            return arg2;
        else
            throw new IllegalStateException();
    }

    public void setArg2(int arg2) {
        this.arg2 = arg2;
    }

    public void setArg2(Integer arg2) {
        this.arg2 = arg2;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
