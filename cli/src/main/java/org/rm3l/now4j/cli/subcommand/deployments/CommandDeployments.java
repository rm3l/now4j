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
package org.rm3l.now4j.cli.subcommand.deployments;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.rm3l.now4j.cli.subcommand.AbstractCommand;

import java.util.Map;

@Parameters(separators = "=", commandDescription = "Manage deployments")
public class CommandDeployments extends AbstractCommand {

    @Parameter(names = {"-list", "-ls"},
            description = "List deployments. Optional: --deploymentId")
    private boolean list = false;

    @Parameter(names = "--deploymentId", description = "ID of deployment")
    private String deploymentId;

    @Parameter(names = "-add",
            description = "Perform a deployment. Required: --deploymentData")
    private boolean add = false;

    @Parameter(names = {"-remove", "-rm", "-delete", "-del"},
            description = "Remove a deployment. Required: --deploymentId")
    private boolean remove = false;

    @Parameter(names = {"-listFiles", "-lsFiles"},
            description = "List file structure of deployment. Required: --deploymentId")
    private boolean listFiles = false;

    @Parameter(names = {"-getFile", "-dl"},
            description = "Get file content. Required: --deploymentId and --fileId")
    private boolean dlFile = false;

    @Parameter(names = "--fileId", description = "ID of file")
    private String fileId;

    @Parameter(names = "--deploymentData",
            description = "JSON-serialized description of the deployment to add. " +
                    "The keys should represent a file path, with their respective " +
                    "values containing the file contents.")
    private String deploymentData;

    @Override
    public void work() throws Exception {
        final Object response;
        if (this.list) {
            if (this.deploymentId == null || this.deploymentId.isEmpty()) {
                response = this.nowClient.getDeployments();
            } else {
                response = this.nowClient.getDeployment(this.deploymentId);
            }
        } else if (this.add) {
            if (this.deploymentData == null || this.deploymentData.isEmpty()) {
                throw new IllegalArgumentException("Missing --deploymentData option");
            }
            @SuppressWarnings("unchecked") final Map<String, Object> data = gson
                    .fromJson(this.deploymentData, Map.class);
            response = this.nowClient.createDeployment(data);
        } else if (this.remove) {
            if (this.deploymentId == null || this.deploymentId.isEmpty()) {
                throw new IllegalArgumentException("Missing --deploymentId option");
            }
            this.nowClient.deleteDeployment(this.deploymentId);
            response = true;
        } else if (listFiles) {
            if (this.deploymentId == null || this.deploymentId.isEmpty()) {
                throw new IllegalArgumentException("Missing --deploymentId option");
            }
            response = this.nowClient.getFiles(this.deploymentId);
        } else if (this.dlFile) {
            if (this.deploymentId == null || this.deploymentId.isEmpty()) {
                throw new IllegalArgumentException("Missing --deploymentId option");
            }
            if (this.fileId == null || this.fileId.isEmpty()) {
                throw new IllegalArgumentException("Missing --fileId option");
            }
            response = this.nowClient.getFileAsString(this.deploymentId, this.fileId);
        } else {
            response = null;
        }
        if (response != null) {
            System.out.println(gson.toJson(response));
        }
    }

}
