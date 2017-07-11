package org.rm3l.now4j.cli.subcommand.deployments;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.rm3l.now4j.cli.subcommand.AbstractCommand;
import org.rm3l.now4j.contract.Now;

/**
 * Created by rm3l on 7/11/17.
 */
@Parameters(separators = "=", commandDescription = "Manage deployments")
public class CommandDeployments extends AbstractCommand {

    @Parameter(names = "-list", description = "List deployments")
    private boolean list = false;

    private final Gson gson = new Gson();

    @Override
    public void work() throws Exception {
        if (this.list) {
            System.out.println(gson.toJson(this.nowClient.getDeployments()));
        }
        //TODO
    }

    //TODO Add parameters
}
