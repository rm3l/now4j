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
package org.rm3l.now4j.cli.subcommand.secrets;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.rm3l.now4j.cli.subcommand.AbstractCommand;

@Parameters(separators = "=", commandDescription = "Manage secrets")
public class CommandSecrets extends AbstractCommand {

    @Parameter(names = {"-list", "-ls"}, description = "List secrets")
    private boolean list = false;

    @Parameter(names = {"--secretName", "--name"}, description = "Secret name")
    private String secretName;

    @Parameter(names = {"--secretValue", "--value"}, description = "Secret value")
    private String secretValue;

    @Parameter(names = {"-add", "-create"},
            description = "Create a new secret. Required: --name and --value")
    private boolean add = false;

    @Parameter(names = "-rename",
            description = "Rename a secret. Required: --uid and --name")
    private boolean rename = false;

    @Parameter(names = {"--secretUid", "--uid"}, description = "UID of Secret")
    private String secretUid;

    @Parameter(names = {"-remove", "-rm", "-delete", "-del"},
            description = "Remove a secret. Required: --name or --uid")
    private boolean remove = false;

    @Override
    public void work() throws Exception {
        final Object response;
        if (this.list) {
            response = this.nowClient.getSecrets();
        } else if (this.add) {
            if (this.secretName == null || this.secretName.isEmpty()) {
                throw new IllegalArgumentException("Missing --name option");
            }
            if (this.secretValue == null || this.secretValue.isEmpty()) {
                throw new IllegalArgumentException("Missing --value option");
            }
            response = this.nowClient.createSecret(this.secretName, this.secretValue);
        } else if (this.rename) {
            if (this.secretUid == null || this.secretUid.isEmpty()) {
                throw new IllegalArgumentException("Missing --uid option");
            }
            if (this.secretName == null || this.secretName.isEmpty()) {
                throw new IllegalArgumentException("Missing --name option");
            }
            response = this.nowClient.renameSecret(this.secretUid, this.secretName);
        } else if (this.remove) {
            String secretUidOrName = this.secretName;
            if (this.secretUid == null || this.secretUid.isEmpty()) {
                //UID takes precedence over the name, if any
                secretUidOrName = this.secretUid;
            }
            if (secretUidOrName == null || secretUidOrName.isEmpty()) {
                throw new IllegalArgumentException(
                        "Missing one of --name or --uid options");
            }
            response = this.nowClient.deleteSecret(secretUidOrName);
        } else {
            response = null;
        }
        if (response != null) {
            System.out.println(gson.toJson(response));
        }
    }
}
