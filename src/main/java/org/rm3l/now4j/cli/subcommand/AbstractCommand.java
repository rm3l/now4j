package org.rm3l.now4j.cli.subcommand;

import org.jetbrains.annotations.NotNull;
import org.rm3l.now4j.contract.Now;

/**
 * Created by rm3l on 7/11/17.
 */
public abstract class AbstractCommand {

    @NotNull
    protected Now nowClient;

    public final void setNowClient(@NotNull final Now nowClient) {
        this.nowClient = nowClient;
    }

    public abstract void work() throws Exception;
}
