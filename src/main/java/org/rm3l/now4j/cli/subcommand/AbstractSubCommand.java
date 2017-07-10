package org.rm3l.now4j.cli.subcommand;

import com.beust.jcommander.JCommander;
import org.jetbrains.annotations.NotNull;
import org.rm3l.now4j.cli.Args;
import org.rm3l.now4j.contract.Now;

/**
 * Created by rm3l on 7/11/17.
 */
public abstract class AbstractSubCommand<T extends SubCommandArgs> {

    @NotNull
    protected final Now nowClient;

    @NotNull
    private final Class<T> argsType;

    protected AbstractSubCommand(@NotNull Class<T> argsType, @NotNull Now nowClient) {
        this.argsType = argsType;
        this.nowClient = nowClient;
    }

    public final Object handle(String... argv) {
        final T args;
        try {
            args = this.argsType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        JCommander.newBuilder().addObject(args).build().parse(argv);
        return this.handle(args);
    }

    protected abstract Object handle(T args);
}
