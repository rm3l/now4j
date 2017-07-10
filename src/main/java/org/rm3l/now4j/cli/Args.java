package org.rm3l.now4j.cli;

import com.beust.jcommander.Parameter;

/**
 * Created by rm3l on 7/11/17.
 */
public class Args {

    @Parameter(names = {"--debug", "-d"}, description = "Debug mode")
    private boolean debug = false;

//    @Parameter(names = "--prompt-token",
//            description = "Prompt for Now API Token",
//            password = true)
//    private String tokenFromPrompt;

    @Parameter(names = {"--token", "--T"}, description = "Now API Token")
    private String tokenFromCLI;

    @Parameter(names = {"--team", "--t"}, description = "Now API Team")
    private String team;

    @Parameter(names = {"--command", "-c"}, description = "Command to execute")
    private String command;

    public boolean isDebug() {
        return debug;
    }

//    public String getTokenFromPrompt() {
//        return tokenFromPrompt;
//    }

    public String getTokenFromCLI() {
        return tokenFromCLI;
    }

    public String getTeam() {
        return team;
    }

    public String getCommand() {
        return command;
    }
}
