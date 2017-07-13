/*
 * Copyright (c) 2017 Armel Soro
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.rm3l.now4j.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.rm3l.now4j.NowClient;
import org.rm3l.now4j.cli.subcommand.AbstractCommand;
import org.rm3l.now4j.cli.subcommand.aliases.CommandAliases;
import org.rm3l.now4j.cli.subcommand.certs.CommandCertificates;
import org.rm3l.now4j.cli.subcommand.deployments.CommandDeployments;
import org.rm3l.now4j.cli.subcommand.domains.CommandDomains;
import org.rm3l.now4j.cli.subcommand.secrets.CommandSecrets;

import java.util.HashMap;
import java.util.Map;

public class Now {

    private static final Map<String, AbstractCommand> commandMap = new HashMap<>();

    static {
        //Register available commands
        commandMap.put("aliases", new CommandAliases());
        commandMap.put("certs", new CommandCertificates());
        commandMap.put("deployments", new CommandDeployments());
        commandMap.put("domains", new CommandDomains());
        commandMap.put("secrets", new CommandSecrets());
    }

    @Parameter(names = {"--verbose", "--log", "-v", "-l"})
    private int verboseLevel = 0;

    public static void main(String... argv) throws Exception {

        //Attempt to determine verbose level first
        final Now now = new Now();
        JCommander.newBuilder().addObject(now)
                .acceptUnknownOptions(true)
                .build().parse(argv);

        final CommandMain commandMain = new CommandMain();

        final JCommander.Builder commanderBuilder = JCommander.newBuilder()
                .programName("java -jar now4j-uberjar.jar")
                .verbose(now.verboseLevel)
                .addObject(commandMain);
        for (final Map.Entry<String, AbstractCommand> commandEntry : commandMap.entrySet()) {
            commanderBuilder.addCommand(commandEntry.getKey(), commandEntry.getValue());
        }
        final JCommander commander = commanderBuilder.build();
        commander.parse(argv);

        if (commandMain.help) {
            commander.usage();
            return;
        }

        final NowClient nowClient;
        final String token = commandMain.token;
        if (token == null || token.isEmpty()) {
            //Default Now Client, with no option => read from  /.now.json file
            nowClient = NowClient.create();
        } else {
            //There is a token option
            final String team = commandMain.team;
            if (team == null || team.isEmpty()) {
                //Read team from  /.now.json, if any
                nowClient = NowClient.create(token);
            } else {
                nowClient = NowClient.create(token, team);
            }
        }

        final String parsedCommand = commander.getParsedCommand();
        if (!commandMap.containsKey(parsedCommand)) {
            throw new IllegalArgumentException("Unsupported command: " +
                    parsedCommand + ". Possible commands: " + commandMap.keySet());
        }
        final AbstractCommand abstractCommand = commandMap.get(parsedCommand);
        abstractCommand.setNowClient(nowClient);
        abstractCommand.work();
    }

    private static class CommandMain {

        @Parameter(
                names = {"--help", "-h"},
                description = "Show this help",
                help = true,
                order = 3)
        boolean help;

        @Parameter(names = {"--debug", "-d"},
                description = "Debug mode",
                order = 2)
        boolean debug = false;

        @Parameter(names = {"--token", "--T"},
                description = "Now API Token. Read from ~/.now.json if not specified here.",
                order = 0)
        String token;

        @Parameter(names = {"--team", "--t"},
                description = "Now API Team. Read from ~/.now.json if not specified here.",
                order = 1)
        String team;
    }
}
