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
package org.rm3l.now4j.cli.subcommand.certs;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.rm3l.now4j.cli.subcommand.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

@Parameters(separators = "=", commandDescription = "Manage certificates")
public class CommandCertificates extends AbstractCommand {

    @Parameter(names = {"-list", "-ls"},
            description = "List certificates. Required: --cn")
    private boolean list = false;

    @Parameter(names = {"--commonName", "--cn"}, description = "Common Name (CN)")
    private String commonName;

    @Parameter(names = {"-add", "-create"}, description = "Create a new certificate")
    private boolean add = false;

    @Parameter(names = "--domain", description = "The domain.")
    private List<String> domains = new ArrayList<>();

    @Parameter(names = "-renew", description = "Renew certificate for domains")
    private boolean renew = false;

    @Parameter(names = "-replace", description = "Replace certificate for domains")
    private boolean replace = false;

    @Parameter(names = {"--x509", "--ca"}, description = "X.509 certificate")
    private String ca;

    @Parameter(names = {"--certificate", "--cert"}, description = "CA certificate chain")
    private String cert;

    @Parameter(names = "--key", description = "Private key for the certificate")
    private String key;

    @Parameter(names = {"-remove", "-rm", "-delete", "-del"},
            description = "Remove a certificate. Required: --cn")
    private boolean remove = false;

    @Override
    public void work() throws Exception {
        final Object response;
        if (this.list) {
            if (this.commonName == null || this.commonName.isEmpty()) {
                throw new IllegalArgumentException("Missing --cn option");
            }
            response = this.nowClient.getCertificates(this.commonName);
        } else if (this.add) {
            response = this.nowClient.createCertificate(this.domains);
        } else if (this.renew) {
            response = this.nowClient.renewCertificate(this.domains);
        } else if (this.replace) {
            response = this.nowClient.replaceCertificate(this.domains, this.ca, this.cert, this.key);
        } else if (this.remove) {
            if (this.commonName == null || this.commonName.isEmpty()) {
                throw new IllegalArgumentException("Missing --cn option");
            }
            this.nowClient.deleteCertificate(this.commonName);
            response = true;
        } else {
            response = null;
        }
        if (response != null) {
            System.out.println(gson.toJson(response));
        }
    }
}
