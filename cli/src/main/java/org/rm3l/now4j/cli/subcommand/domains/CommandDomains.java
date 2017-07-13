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
package org.rm3l.now4j.cli.subcommand.domains;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.rm3l.now4j.cli.subcommand.AbstractCommand;
import org.rm3l.now4j.resources.domains.DomainRecord;

@Parameters(separators = "=", commandDescription = "Manage domains")
public class CommandDomains extends AbstractCommand {

    @Parameter(names = {"-list", "-ls"}, description = "List domains")
    private boolean list = false;

    @Parameter(names = "-add", description = "Add a new domain. " +
            "Required: --domainName . Optional: --externalDNS")
    private boolean add = false;

    @Parameter(names = "--domainName", description = "Name of domain")
    private String domainName;

    @Parameter(names = "--externalDNS", description = "Indicates whether the domain is an external DNS or not")
    private boolean externalDNS = false;

    @Parameter(names = {"-remove", "-rm", "-delete", "-del"},
            description = "Remove a domain. Required: --domainName")
    private boolean remove = false;

    @Parameter(names = {"-listRecords", "-lsr"},
            description = "List domain records. Required: --domainName")
    private boolean listRecords = false;

    @Parameter(names = "-addRecord",
            description = "Add a new domain record. Required: --domainName and --recordData")
    private boolean addRecord = false;

    @Parameter(names = "--recordData",
            description = "JSON-serialized description of the domain record to add. ")
    private String recordData;

    @Parameter(names = {"-removeRecord", "-rmr", "-deleteRecord", "-delr"},
            description = "Remove a domain record. Required: --domainName and --recordId")
    private boolean removeRecord = false;

    @Parameter(names = "--recordId", description = "ID of Domain Record")
    private String recordId;

    @Override
    public void work() throws Exception {
        final Object response;
        if (this.list) {
            response = this.nowClient.getDomains();
        } else if (this.add) {
            if (this.domainName == null || this.domainName.isEmpty()) {
                throw new IllegalArgumentException("Missing --domainName option");
            }
            response = this.nowClient.addDomain(this.domainName, this.externalDNS);
        } else if (this.remove) {
            if (this.domainName == null || this.domainName.isEmpty()) {
                throw new IllegalArgumentException("Missing --domainName option");
            }
            response = this.nowClient.deleteDomain(this.domainName);
        } else if (listRecords) {
            if (this.domainName == null || this.domainName.isEmpty()) {
                throw new IllegalArgumentException("Missing --domainName option");
            }
            response = this.nowClient.getDomainRecords(this.domainName);
        } else if (this.addRecord) {
            if (this.domainName == null || this.domainName.isEmpty()) {
                throw new IllegalArgumentException("Missing --domainName option");
            }
            if (this.recordData == null || this.recordData.isEmpty()) {
                throw new IllegalArgumentException("Missing --recordData option");
            }
            response = this.nowClient.addDomainRecord(
                    this.domainName,
                    gson.fromJson(this.recordData, DomainRecord.class));
        } else if (this.removeRecord) {
            if (this.domainName == null || this.domainName.isEmpty()) {
                throw new IllegalArgumentException("Missing --domainName option");
            }
            if (this.recordId == null || this.recordId.isEmpty()) {
                throw new IllegalArgumentException("Missing --recordId option");
            }
            this.nowClient.deleteDomainRecord(this.domainName, this.recordId);
            response = true;
        } else {
            response = null;
        }
        if (response != null) {
            System.out.println(gson.toJson(response));
        }
    }

}
