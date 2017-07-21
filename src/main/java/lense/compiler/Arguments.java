package lense.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lense.compiler.tools.LenseCommand;

public class Arguments {

    private final Map<LenseCommand.Parameter, String> parameters = new HashMap<>();

    private LenseCommand command = LenseCommand.HELP;
    private LenseCommand.Mode mode;

    public LenseCommand getCommand() {
        return command;
    }

    public void setCommand(LenseCommand command) {
        this.command = command;
    }

    public Optional<String> getRepositoryBase() {
        return getParameter(LenseCommand.Parameter.REPOSITORY);
    }

    public void setRepositoryBase(String repositoryBase) {
        setParameter(LenseCommand.Parameter.REPOSITORY,repositoryBase);
    }

    public String getSource() {
        return getParameter(LenseCommand.Parameter.SOURCE).orElse("");
    }

    public void setSource(String source) {
        setParameter(LenseCommand.Parameter.SOURCE, source);
    }

    public String getTarget() {
        return getParameter(LenseCommand.Parameter.TARGET).orElse("");
    }

    public void setTarget(String target) {
        setParameter(LenseCommand.Parameter.TARGET, target);
    }

    public void setMode(LenseCommand.Mode mode) {
        this.mode = mode;
    }

    public Optional<LenseCommand.Mode> getMode() {
        return Optional.ofNullable(mode);
    }

    public void setParameter(LenseCommand.Parameter parameter, String value) {
        this.parameters.put(parameter, value);
    }

    public Optional<String> getParameter(LenseCommand.Parameter parameter){
        return Optional.ofNullable(this.parameters.get(parameter));
    }
}
