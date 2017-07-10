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
package org.rm3l.now4j.contract;

import org.jetbrains.annotations.NotNull;
import org.rm3l.now4j.exceptions.UnsuccessfulResponseException;
import org.rm3l.now4j.resources.aliases.Alias;
import org.rm3l.now4j.resources.certs.Certificate;
import org.rm3l.now4j.resources.deployments.Deployment;
import org.rm3l.now4j.resources.deployments.DeploymentFileStructure;
import org.rm3l.now4j.resources.domains.Domain;
import org.rm3l.now4j.resources.domains.DomainRecord;
import org.rm3l.now4j.resources.secrets.Secret;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Contract API for Now Clients
 */
@SuppressWarnings("unused")
public interface Now {

    /**
     * Get all deployments
     *
     * @return a list of all deployments
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    @NotNull
    List<Deployment> getDeployments() throws IOException;

    /**
     * Get all deployments, asynchronously.
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getDeployments(@NotNull final ClientCallback<List<Deployment>> callback) throws IOException;

    /**
     * Get a given deployment
     *
     * @param deploymentId ID of deployment
     * @return the deployment
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    Deployment getDeployment(@NotNull String deploymentId) throws IOException;

    /**
     * Returns a given deployment, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param deploymentId ID of deployment
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getDeployment(@NotNull String deploymentId,
                       @NotNull final ClientCallback<Deployment> callback) throws IOException;

    /**
     * Creates a new deployment and returns its data
     *
     * @param body The keys should represent a file path, with their respective values containing the file contents.
     * @return the deployment created
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    Deployment createDeployment(@NotNull Map<String, Object> body) throws IOException;

    /**
     * Creates a new deployment and returns its data, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param body The keys should represent a file path, with their respective values containing the file contents.
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void createDeployment(@NotNull Map<String, Object> body,
                          @NotNull final ClientCallback<Deployment> callback) throws IOException;

    /**
     * Deletes a deployment
     *
     * @param deploymentId ID of deployment
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void deleteDeployment(@NotNull String deploymentId) throws IOException;

    /**
     * Deletes a deployment, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param deploymentId ID of deployment
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void deleteDeployment(@NotNull String deploymentId,
                          @NotNull final ClientCallback<Void> callback) throws IOException;

    /**
     * Returns a list with the deployment file structure
     * @param deploymentId ID of deployment
     * @return a list with the deployment file structure
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    List<DeploymentFileStructure> getFiles(@NotNull String deploymentId) throws IOException;

    /**
     * Returns a list with the deployment file structure, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param deploymentId ID of deployment
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getFiles(@NotNull String deploymentId,
                  @NotNull final ClientCallback<List<DeploymentFileStructure>> callback) throws IOException;

    /**
     * Returns the content of a file as a {@link String}
     *
     * @param deploymentId ID of deployment
     * @param fileId ID of the file
     * @return the content of the file as an {@link String}
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    String getFileAsString(@NotNull String deploymentId,
                           @NotNull String fileId) throws IOException;

    /**
     * Returns the content of a file as a {@link String}, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param deploymentId ID of deployment
     * @param fileId ID of the file
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getFileAsString(@NotNull String deploymentId,
                         @NotNull String fileId,
                         @NotNull final ClientCallback<String> callback) throws IOException;

    /**
     * Returns the content of a file as an {@link InputStream}
     *
     * @param deploymentId ID of deployment
     * @param fileId ID of the file
     * @return the content of the file as an {@link InputStream}
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    InputStream getFileAsInputStream(@NotNull String deploymentId,
                                     @NotNull String fileId) throws IOException;

    /**
     * Returns the content of a file as an {@link InputStream}, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param deploymentId ID of deployment
     * @param fileId ID of the file
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getFileAsInputStream(@NotNull String deploymentId,
                              @NotNull String fileId,
                              @NotNull final ClientCallback<InputStream> callback) throws IOException;

    /**
     * Returns a list with all domain names and related aliases
     *
     * @return a list with all domain names and related aliases
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    @NotNull
    List<Domain> getDomains() throws IOException;

    /**
     * Returns a list with all domain names and related aliases, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getDomains(@NotNull final ClientCallback<List<Domain>> callback) throws IOException;

    /**
     * Adds a new domain and returns its data
     * @param name the domain name
     * @param isExternalDNS whether this is an external DNS or not
     * @return the domain added
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    @NotNull
    Domain addDomain(@NotNull String name, boolean isExternalDNS) throws IOException;

    /**
     * Adds a new domain and returns its data, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param name the domain name
     * @param isExternalDNS whether this is an external DNS or not
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void addDomain(@NotNull String name,
                   boolean isExternalDNS,
                   @NotNull final ClientCallback<Domain> callback) throws IOException;

    /**
     * Deletes a domain name
     *
     * @param name Domain name
     * @return the domain ID
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    String deleteDomain(@NotNull String name) throws IOException;

    /**
     * Deletes a domain name, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param name Domain name
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void deleteDomain(@NotNull String name,
                      @NotNull final ClientCallback<String> callback) throws IOException;

    /**
     * Returns a list with all DNS records configured for a domain name
     *
     * @param name Domain name
     * @return a list with all DNS records configured for a domain name
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    @NotNull
    List<DomainRecord> getDomainRecords(@NotNull String name) throws IOException;

    /**
     * Returns a list with all DNS records configured for a domain name, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param name Domain name
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getDomainRecords(@NotNull String name,
                          @NotNull final ClientCallback<List<DomainRecord>> callback) throws IOException;

    /**
     * Adds a new DNS record for a domain
     *
     * @param name Domain name
     * @param record the record data
     * @return the record created
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    DomainRecord addDomainRecord(@NotNull String name,
                                 @NotNull DomainRecord record) throws IOException;

    /**
     * Adds a new DNS record for a domain, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param name Domain name
     * @param record the record data
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void addDomainRecord(@NotNull String name,
                         @NotNull DomainRecord record,
                         @NotNull final ClientCallback<DomainRecord> callback) throws IOException;

    /**
     * Deletes a DNS record associated with a domain
     *
     * @param domainName Domain name
     * @param recordId Record ID
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void deleteDomainRecord(@NotNull String domainName,
                            @NotNull String recordId) throws IOException;

    /**
     * Deletes a DNS record associated with a domain, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param domainName Domain name
     * @param recordId Record ID
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void deleteDomainRecord(@NotNull String domainName,
                            @NotNull String recordId,
                            @NotNull final ClientCallback<Void> callback) throws IOException;

    /**
     * Returns a list of all certificates
     *
     * @param commonName Common Name
     * @return the list of all certificates
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    List<Certificate> getCertificates(@NotNull String commonName) throws IOException;

    /**
     * Returns a list of all certificates, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param commonName Common Name
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getCertificates(@NotNull String commonName,
                         @NotNull final ClientCallback<List<Certificate>> callback) throws IOException;

    /**
     * Creates a new certificate for the given domains registered to the user
     *
     * @param domains the list of domains
     * @return the certificate ID
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    String createCertificate(@NotNull List<String> domains) throws IOException;

    /**
     * Creates a new certificate for the given domains registered to the user, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param domains the list of domains
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void createCertificate(@NotNull List<String> domains,
                           @NotNull final ClientCallback<String> callback) throws IOException;

    /**
     * Renews an existing certificate
     * @param domains the list of domains
     * @return the certificate ID
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    String renewCertificate(@NotNull List<String> domains) throws IOException;

    /**
     * Renews an existing certificate, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param domains the list of domains
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void renewCertificate(@NotNull List<String> domains,
                          @NotNull final ClientCallback<String> callback) throws IOException;

    /**
     * Replace an existing certificate
     *
     * @param domains the list of domains
     * @param ca X.509 certificate
     * @param cert Private key for the certificate
     * @param key CA certificate chain
     * @return the date at which the certificate has been replaced
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    String replaceCertificate(@NotNull List<String> domains,
                                     @NotNull String ca,
                                     @NotNull String cert,
                                     @NotNull String key) throws IOException;

    /**
     * Replace an existing certificate, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param domains the list of domains
     * @param ca X.509 certificate
     * @param cert Private key for the certificate
     * @param key CA certificate chain
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void replaceCertificate(@NotNull List<String> domains,
                            @NotNull String ca,
                            @NotNull String cert,
                            @NotNull String key,
                            @NotNull final ClientCallback<String> callback) throws IOException;

    /**
     * Deletes a certificate
     * @param commonName Common Name
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void deleteCertificate(@NotNull String commonName) throws IOException;

    /**
     * Deletes a certificate, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param commonName Common Name
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void deleteCertificate(@NotNull String commonName,
                           @NotNull final ClientCallback<Void> callback) throws IOException;

    /**
     * Gets all aliases
     *
     * @return a list of all aliases
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    List<Alias> getAliases() throws IOException;

    /**
     * Gets all aliases, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getAliases(@NotNull final ClientCallback<List<Alias>> callback) throws IOException;

    /**
     * Delete an alias
     *
     * @param aliasId the alias ID
     * @return the alias ID
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    String deleteAlias(@NotNull String aliasId) throws IOException;

    /**
     * Delete an alias, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param aliasId the alias ID
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void deleteAlias(@NotNull String aliasId,
                     @NotNull final ClientCallback<String> callback) throws IOException;

    /**
     * Gets the list of aliases for the given deployment
     *
     * @param deploymentId ID of deployment
     * @return the list of aliases for the given deployment
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    List<Alias> getDeploymentAliases(@NotNull String deploymentId) throws IOException;

    /**
     * Gets the list of aliases for the given deployment, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param deploymentId ID of deployment
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getDeploymentAliases(@NotNull String deploymentId,
                              @NotNull final ClientCallback<List<Alias>> callback) throws IOException;

    /**
     * Creates an alias for the given deployment
     *
     * @param deploymentId ID of deployment
     * @param alias Hostname or custom url for the alias
     * @return the alias created for the given deployment
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    Alias createDeploymentAlias(@NotNull String deploymentId,
                                @NotNull String alias) throws IOException;

    /**
     * Creates an alias for the given deployment, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param deploymentId ID of deployment
     * @param alias Hostname or custom url for the alias
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void createDeploymentAlias(@NotNull String deploymentId,
                               @NotNull String alias,
                               @NotNull final ClientCallback<Alias> callback) throws IOException;

    /**
     * Returns a list with all secrets
     *
     * @return a list of all secrets
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    List<Secret> getSecrets() throws IOException;

    /**
     * Returns a list with all secrets, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void getSecrets(@NotNull final ClientCallback<List<Secret>> callback) throws IOException;

    /**
     * Creates a secret and returns it
     *
     * @param name name for the secret
     * @param value value for the secret
     * @return the secret created
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    Secret createSecret(@NotNull String name,
                        @NotNull String value) throws IOException;

    /**
     * Creates a secret and returns it, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param name name for the secret
     * @param value value for the secret
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void createSecret(@NotNull String name,
                      @NotNull String value,
                      @NotNull final ClientCallback<Secret> callback) throws IOException;

    /**
     * Changes the name of the given secret and returns it
     *
     * @param uidOrName id or name of the secret
     * @param newName new name for the secret
     * @return the secret updated
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    Secret renameSecret(@NotNull String uidOrName,
                        @NotNull String newName) throws IOException;

    /**
     * Changes the name of the given secret and returns it, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param uidOrName id or name of the secret
     * @param newName new name for the secret
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void renameSecret(@NotNull String uidOrName,
                      @NotNull String newName,
                      @NotNull final ClientCallback<Secret> callback) throws IOException;

    /**
     * Deletes a secret and returns it
     *
     * @param uidOrName ID or name of the secret
     * @return the secret, with its ID
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    Secret deleteSecret(@NotNull String uidOrName) throws IOException;

    /**
     * Deletes a secret and returns it, asynchronously
     * <p>
     * You are notified (with either a result or an exception)
     * via the callback provided.
     *
     * @param uidOrName ID or name of the secret
     * @param callback Callback object will be called asynchronously
     * @throws IOException if a problem occurred talking to the server.
     * @throws UnsuccessfulResponseException if response code got from the server was not successful
     */
    void deleteSecret(@NotNull String uidOrName,
                      @NotNull final ClientCallback<Secret> callback) throws IOException;
}
