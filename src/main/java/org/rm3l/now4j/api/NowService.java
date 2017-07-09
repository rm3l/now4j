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
package org.rm3l.now4j.api;

import okhttp3.ResponseBody;
import org.rm3l.now4j.resources.aliases.Alias;
import org.rm3l.now4j.resources.aliases.Aliases;
import org.rm3l.now4j.resources.aliases.DeleteAliasResponse;
import org.rm3l.now4j.resources.certs.CertificateCreationOrUpdateRequest;
import org.rm3l.now4j.resources.certs.CertificateCreationOrUpdateResponse;
import org.rm3l.now4j.resources.certs.Certificates;
import org.rm3l.now4j.resources.deployments.Deployment;
import org.rm3l.now4j.resources.deployments.DeploymentFileStructure;
import org.rm3l.now4j.resources.deployments.GetDeploymentsResponse;
import org.rm3l.now4j.resources.domains.*;
import org.rm3l.now4j.resources.secrets.CreateOrUpdateSecretRequest;
import org.rm3l.now4j.resources.secrets.GetSecretsResponse;
import org.rm3l.now4j.resources.secrets.Secret;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * Connector to Now's REST API
 */
public interface NowService {

    @GET("now/deployments")
    Call<GetDeploymentsResponse> getDeployments();

    @GET("now/deployments/{deployment_id}")
    Call<Deployment> getDeployment(@Path("deployment_id") String deploymentId);

    @DELETE("now/deployments/{deployment_id}")
    Call<ResponseBody> deleteDeployment(@Path("deployment_id") String deploymentId);

    @POST("now/deployments")
    Call<Deployment> createDeployment(@Body Map<String, Object> body);

    @GET("now/deployments/{deployment_id}/files")
    Call<List<DeploymentFileStructure>> getFiles(@Path("deployment_id") String deploymentId);

    @GET("now/deployments/{deployment_id}/files/{file_id}")
    Call<ResponseBody> getFile(
            @Path("deployment_id") String deploymentId,
            @Path("file_id") String fileId);

    @GET("domains")
    Call<Domains> getDomains();

    @POST("domains")
    Call<Domain> createDomain(@Body Domain domain);

    @DELETE("domains/{domain_name}")
    Call<Domain> deleteDomain(@Path("domain_name") String domainName);

    @GET("domains/{domain_name}/records")
    Call<DomainRecords> getDomainRecords(@Path("domain_name") String domainName);

    @POST("domains/{domain_name}/records")
    Call<DomainRecord> createDomainRecord(@Path("domain_name") String domainName,
                                          @Body DomainRecordCreationRequest body);

    @DELETE("domains/{domain_name}/records/{record_id}")
    Call<ResponseBody> deleteDomainRecord(@Path("domain_name") String domainName,
                                          @Path("record_id") String recordId);

    @GET("now/certs/{common_name}")
    Call<Certificates> getCertificates(@Path("common_name") String commonName);

    @POST("now/certs")
    Call<CertificateCreationOrUpdateResponse> issueCertificate(
            @Body CertificateCreationOrUpdateRequest request);

    @PUT("now/certs")
    Call<CertificateCreationOrUpdateResponse> createOrReplaceCertificate(
            @Body CertificateCreationOrUpdateRequest request);

    @DELETE("now/certs/{common_name}")
    Call<ResponseBody> deleteCertificate(@Path("common_name") String commonName);

    @GET("now/aliases")
    Call<Aliases> getAliases();

    @DELETE("now/aliases/{alias_id}")
    Call<DeleteAliasResponse> deleteAlias(@Path("alias_id") String aliasId);


    @GET("deployments/{deployment_id}/aliases")
    Call<Aliases> getDeploymentAliases(@Path("deployment_id") String deploymentId);

    @POST("deployments/{deployment_id}/aliases")
    Call<Alias> createDeploymentAliases(@Path("deployment_id") String deploymentId,
                                        @Body Alias alias);

    @GET("now/secrets")
    Call<GetSecretsResponse> getSecrets();

    @POST("now/secrets")
    Call<Secret> createSecret(@Body CreateOrUpdateSecretRequest request);

    @PATCH("now/secrets/{secret_uid_or_name}")
    Call<Secret> editSecret(@Path("secret_uid_or_name") String secretUidOrName,
                            @Body CreateOrUpdateSecretRequest request);

    @DELETE("now/secrets/{secret_uid_or_name}")
    Call<Secret> deleteSecret(@Path("secret_uid_or_name") String secretUidOrName);
}
