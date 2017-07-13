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
package org.rm3l.now4j.cli.subcommand.aliases;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.rm3l.now4j.cli.subcommand.AbstractCommand;

@Parameters(separators = "=", commandDescription = "Manage aliases")
public class CommandAliases extends AbstractCommand {

    @Parameter(names = {"-list", "-ls"}, description = "List aliases. Optional: --deploymentId")
    private boolean list = false;

    @Parameter(names = "--deploymentId", description = "ID of Deployment")
    private String deploymentId;

    @Parameter(names = "--aliasId", description = "ID of Alias")
    private String aliasId;

    @Parameter(names = {"-add", "-create"},
            description = "Create a new alias. Required: --deploymentId and --alias")
    private boolean add = false;

    @Parameter(names = "--alias", description = "Hostname or custom url for the alias")
    private String alias;

    @Parameter(names = {"-remove", "-rm", "-delete", "-del"},
            description = "Remove an alias. Required: --aliasId")
    private boolean remove = false;

    @Override
    public void work() throws Exception {
        final Object response;
        if (this.list) {
            if (this.deploymentId == null || this.deploymentId.isEmpty()) {
                response = this.nowClient.getAliases();
            } else {
                response = this.nowClient.getDeploymentAliases(this.deploymentId);
            }
        } else if (this.remove) {
            if (this.aliasId == null || this.aliasId.isEmpty()) {
                throw new IllegalArgumentException("Missing --aliasId option");
            }
            this.nowClient.deleteAlias(this.aliasId);
            response = true;
        } else if (this.add) {
            if (this.deploymentId == null || this.deploymentId.isEmpty()) {
                throw new IllegalArgumentException("Missing --deploymentId option");
            }
            if (this.alias == null || this.alias.isEmpty()) {
                throw new IllegalArgumentException("Missing --alias option");
            }
            response = this.nowClient.createDeploymentAlias(this.deploymentId, this.alias);
        } else {
            response = null;
        }
        if (response != null) {
            System.out.println(gson.toJson(response));
        }
    }
}
