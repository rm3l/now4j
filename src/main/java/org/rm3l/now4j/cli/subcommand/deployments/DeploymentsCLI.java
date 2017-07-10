package org.rm3l.now4j.cli.subcommand.deployments;

import org.jetbrains.annotations.NotNull;
import org.rm3l.now4j.cli.subcommand.AbstractSubCommand;
import org.rm3l.now4j.contract.Now;

/**
 * Created by rm3l on 7/11/17.
 */
public class DeploymentsCLI extends AbstractSubCommand<DeploymentsCLIArgs> {

    protected DeploymentsCLI(@NotNull Class<DeploymentsCLIArgs> argsType,
                             @NotNull Now nowClient) {
        super(argsType, nowClient);
    }

    @Override
    protected Object handle(DeploymentsCLIArgs args) {
        //TODO
        return null;
    }
}
