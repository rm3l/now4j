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
package org.rm3l.now4j;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rm3l.now4j.api.NowService;
import org.rm3l.now4j.contract.ClientCallback;
import org.rm3l.now4j.contract.Now;
import org.rm3l.now4j.exceptions.UnsuccessfulResponseException;
import org.rm3l.now4j.interceptors.Now4jInterceptors;
import org.rm3l.now4j.resources.aliases.Alias;
import org.rm3l.now4j.resources.aliases.Aliases;
import org.rm3l.now4j.resources.aliases.DeleteAliasResponse;
import org.rm3l.now4j.resources.certs.Certificate;
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
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.rm3l.now4j.interceptors.Now4jInterceptors.TeamInterceptor.NO_TEAM;

public final class NowClient implements Now {

    private static final String NOW_TOKEN = "NOW_TOKEN";
    private static final String NOW_TEAM = "NOW_TEAM";
    private static final String TOKEN = "token";
    private static final String TEAM = "team";
    private static final String HOME_DIR = System.getProperty("user.home");
    private static final String NOW_JSON = ".now.json";
    private static final String BASE_API_URL = "https://api.zeit.co/";

    @NotNull
    private final String token;
    @Nullable
    private final String team;
    private NowService nowService;

    private NowClient() {
        //Read from ~/.now.json or NOW_TOKEN variable
        Object tokenFound;
        Object teamFound;
        final File nowJsonFile = new File(HOME_DIR, NOW_JSON);
        if (nowJsonFile.exists()) {
            final Gson gson = new Gson();
            final Map fromJson;
            try {
                fromJson = gson.fromJson(new FileReader(nowJsonFile), Map.class);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
            tokenFound = fromJson.get(TOKEN);
            teamFound = fromJson.get(TEAM);
        } else {
            tokenFound = System.getProperty(NOW_TOKEN);
            if (tokenFound == null || tokenFound.toString().trim().isEmpty()) {
                tokenFound = System.getenv(NOW_TOKEN);
            }
            teamFound = System.getProperty(NOW_TEAM);
            if (teamFound == null || teamFound.toString().trim().isEmpty()) {
                teamFound = System.getenv(NOW_TEAM);
            }
        }
        if (tokenFound != null && tokenFound.toString().trim().isEmpty()) {
            tokenFound = null;
        }
        if (teamFound != null && teamFound.toString().trim().isEmpty()) {
            teamFound = null;
        }

        if (tokenFound == null) {
            throw new IllegalStateException("Token not found");
        }
        this.token = tokenFound.toString();
        this.team = teamFound != null ? tokenFound.toString() : null;
        this.buildNowService();
    }

    private NowClient(@NotNull final String token) {
        this(token, null);
    }

    private NowClient(@NotNull final String token, @Nullable final String team) {
        if (token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be NULL or blank");
        }
        this.token = token;
        this.team = team;
        this.buildNowService();
    }

    private NowClient(@NotNull final OkHttpClient httpClient) {
        this.token = "";
        this.team = null;
        this.buildNowService(httpClient);
    }

    public static NowClient create() {
        return new NowClient();
    }

    public static NowClient create(@NotNull final String token) {
        return new NowClient(token);
    }

    public static NowClient create(@NotNull final String token,
                                   @Nullable final String team) {
        return new NowClient(token, team);
    }

    public static NowClient create(@NotNull final OkHttpClient httpClient) {
        return new NowClient(httpClient);
    }

    private void buildNowService() {
        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(Now4jInterceptors.HEADERS_INTERCEPTOR)
                .addInterceptor(new Now4jInterceptors.AuthenticationInterceptor(
                        this.token))
                .addInterceptor(new Now4jInterceptors.TeamInterceptor(
                        this.team != null ? this.team : NO_TEAM))
                .build();
        this.buildNowService(httpClient);
    }

    private void buildNowService(@NotNull final OkHttpClient httpClient) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        this.nowService = retrofit.create(NowService.class);
    }


    @Override
    public @NotNull List<Deployment> getDeployments() throws IOException {
        final Response<GetDeploymentsResponse> response =
                this.nowService.getDeployments().execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final GetDeploymentsResponse body = response.body();
        return body != null ? body.getDeployments() : Collections.<Deployment>emptyList();
    }

    @Override
    public void getDeployments(@NotNull final ClientCallback<List<Deployment>> callback) throws IOException {
        this.nowService.getDeployments()
                .enqueue(new Callback<GetDeploymentsResponse>() {
                    @Override
                    public void onResponse(Call<GetDeploymentsResponse> call, Response<GetDeploymentsResponse> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        final GetDeploymentsResponse body = response.body();
                        callback.onSuccess(body != null ? body.getDeployments() : Collections.<Deployment>emptyList());
                    }

                    @Override
                    public void onFailure(Call<GetDeploymentsResponse> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public Deployment getDeployment(@NotNull String deploymentId) throws IOException {
        final Response<Deployment> response =
                this.nowService.getDeployment(deploymentId).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return response.body();
    }

    @Override
    public void getDeployment(@NotNull String deploymentId, @NotNull final ClientCallback<Deployment> callback) throws IOException {
        this.nowService.getDeployment(deploymentId)
                .enqueue(new Callback<Deployment>() {
                    @Override
                    public void onResponse(Call<Deployment> call, Response<Deployment> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<Deployment> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public Deployment createDeployment(@NotNull Map<String, Object> body) throws IOException {
        final Response<Deployment> response =
                this.nowService.createDeployment(body).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return response.body();
    }

    @Override
    public void createDeployment(@NotNull Map<String, Object> body, @NotNull final ClientCallback<Deployment> callback) throws IOException {
        this.nowService.createDeployment(body)
                .enqueue(new Callback<Deployment>() {
                    @Override
                    public void onResponse(Call<Deployment> call, Response<Deployment> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<Deployment> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public void deleteDeployment(@NotNull String deploymentId) throws IOException {
        final Response<ResponseBody> response =
                this.nowService.deleteDeployment(deploymentId).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
    }

    @Override
    public void deleteDeployment(@NotNull String deploymentId, @NotNull final ClientCallback<Void> callback) throws IOException {
        this.nowService.deleteDeployment(deploymentId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public List<DeploymentFileStructure> getFiles(@NotNull String deploymentId) throws IOException {
        final Response<List<DeploymentFileStructure>> response =
                this.nowService.getFiles(deploymentId).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return response.body();
    }

    @Override
    public void getFiles(@NotNull String deploymentId, @NotNull final ClientCallback<List<DeploymentFileStructure>> callback) throws IOException {
        this.nowService.getFiles(deploymentId)
                .enqueue(new Callback<List<DeploymentFileStructure>>() {
                    @Override
                    public void onResponse(Call<List<DeploymentFileStructure>> call, Response<List<DeploymentFileStructure>> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<DeploymentFileStructure>> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public String getFileAsString(@NotNull String deploymentId, @NotNull String fileId) throws IOException {
        final Response<ResponseBody> response =
                this.nowService.getFile(deploymentId, fileId).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final ResponseBody responseBody = response.body();
        return responseBody != null ? responseBody.string() : null;
    }

    @Override
    public void getFileAsString(@NotNull String deploymentId,
                                @NotNull String fileId,
                                @NotNull final ClientCallback<String> callback) throws IOException {
        this.nowService.getFile(deploymentId, fileId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        final ResponseBody responseBody = response.body();
                        try {
                            callback.onSuccess(responseBody != null ? responseBody.string() : null);
                        } catch (IOException e) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public InputStream getFileAsInputStream(@NotNull String deploymentId,
                                            @NotNull String fileId) throws IOException {
        final Response<ResponseBody> response =
                this.nowService.getFile(deploymentId, fileId).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final ResponseBody responseBody = response.body();
        return responseBody != null ? responseBody.byteStream() : null;
    }

    @Override
    public void getFileAsInputStream(@NotNull String deploymentId,
                                     @NotNull String fileId,
                                     @NotNull final ClientCallback<InputStream> callback) throws IOException {
        this.nowService.getFile(deploymentId, fileId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        final ResponseBody responseBody = response.body();
                        callback.onSuccess(responseBody != null ? responseBody.byteStream() : null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public @NotNull List<Domain> getDomains() throws IOException {
        final Response<Domains> response = this.nowService.getDomains().execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final Domains responseBody = response.body();
        return responseBody != null ? responseBody.getDomains() : Collections.<Domain>emptyList();
    }

    @Override
    public void getDomains(@NotNull final ClientCallback<List<Domain>> callback) throws IOException {
        this.nowService.getDomains()
                .enqueue(new Callback<Domains>() {
                    @Override
                    public void onResponse(Call<Domains> call, Response<Domains> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        final Domains body = response.body();
                        callback.onSuccess(body != null ? body.getDomains() : Collections.<Domain>emptyList());
                    }

                    @Override
                    public void onFailure(Call<Domains> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public @NotNull Domain addDomain(@NotNull String name, boolean isExternalDNS) throws IOException {
        final Domain newDomain = new Domain();
        newDomain.setName(name);
        newDomain.setExternal(isExternalDNS);
        final Response<Domain> response = this.nowService.createDomain(newDomain).execute();
        final Domain body;
        if (!response.isSuccessful() || (body = response.body()) == null) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return body;
    }

    @Override
    public void addDomain(@NotNull String name, boolean isExternalDNS, @NotNull final ClientCallback<Domain> callback) throws IOException {
        final Domain newDomain = new Domain();
        newDomain.setName(name);
        newDomain.setExternal(isExternalDNS);
        this.nowService.createDomain(newDomain)
                .enqueue(new Callback<Domain>() {
                    @Override
                    public void onResponse(Call<Domain> call, Response<Domain> response) {
                        final Domain body;
                        if (!response.isSuccessful() || (body = response.body()) == null) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(body);
                    }

                    @Override
                    public void onFailure(Call<Domain> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public String deleteDomain(@NotNull String name) throws IOException {
        final Response<Domain> response = this.nowService.deleteDomain(name).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final Domain body = response.body();
        return body != null ? body.getUid() : null;
    }

    @Override
    public void deleteDomain(@NotNull String name, @NotNull final ClientCallback<String> callback) throws IOException {
        this.nowService.deleteDomain(name)
                .enqueue(new Callback<Domain>() {
                    @Override
                    public void onResponse(Call<Domain> call, Response<Domain> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        final Domain body = response.body();
                        callback.onSuccess(body != null ? body.getUid() : null);
                    }

                    @Override
                    public void onFailure(Call<Domain> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public @NotNull List<DomainRecord> getDomainRecords(@NotNull String name) throws IOException {
        final Response<DomainRecords> response = this.nowService.getDomainRecords(name).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final DomainRecords body = response.body();
        return body != null ? body.getRecords() : Collections.<DomainRecord>emptyList();
    }

    @Override
    public void getDomainRecords(@NotNull String name, @NotNull final ClientCallback<List<DomainRecord>> callback) throws IOException {
        this.nowService.getDomainRecords(name)
                .enqueue(new Callback<DomainRecords>() {
                    @Override
                    public void onResponse(Call<DomainRecords> call, Response<DomainRecords> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        final DomainRecords body = response.body();
                        callback.onSuccess(body != null ? body.getRecords() : Collections.<DomainRecord>emptyList());
                    }

                    @Override
                    public void onFailure(Call<DomainRecords> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public DomainRecord addDomainRecord(@NotNull String name, @NotNull DomainRecord record) throws IOException {
        final DomainRecordCreationRequest domainRecordCreationRequest = new DomainRecordCreationRequest();
        domainRecordCreationRequest.setData(record);
        final Response<DomainRecord> response = this.nowService
                .createDomainRecord(name, domainRecordCreationRequest).execute();
        final DomainRecord body;
        if (!response.isSuccessful() || (body = response.body()) == null) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return body;
    }

    @Override
    public void addDomainRecord(@NotNull String name, @NotNull DomainRecord record, @NotNull final ClientCallback<DomainRecord> callback) throws IOException {
        final DomainRecordCreationRequest domainRecordCreationRequest = new DomainRecordCreationRequest();
        domainRecordCreationRequest.setData(record);
        this.nowService.createDomainRecord(name, domainRecordCreationRequest)
                .enqueue(new Callback<DomainRecord>() {
                    @Override
                    public void onResponse(Call<DomainRecord> call, Response<DomainRecord> response) {
                        final DomainRecord body;
                        if (!response.isSuccessful() || (body = response.body()) == null) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(body);
                    }

                    @Override
                    public void onFailure(Call<DomainRecord> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public void deleteDomainRecord(@NotNull String domainName, @NotNull String recordId) throws IOException {
        final Response<ResponseBody> response = this.nowService.deleteDomainRecord(domainName, recordId).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
    }

    @Override
    public void deleteDomainRecord(@NotNull String domainName, @NotNull String recordId, @NotNull final ClientCallback<Void> callback) throws IOException {
        this.nowService.deleteDomainRecord(domainName, recordId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public List<Certificate> getCertificates(@NotNull String commonName) throws IOException {
        final Response<Certificates> response = this.nowService.getCertificates(commonName).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final Certificates certificates = response.body();
        return certificates != null ? certificates.getCerts() : Collections.<Certificate>emptyList();
    }

    @Override
    public void getCertificates(@NotNull String commonName, @NotNull final ClientCallback<List<Certificate>> callback) throws IOException {
        this.nowService.getCertificates(commonName)
                .enqueue(new Callback<Certificates>() {
                    @Override
                    public void onResponse(Call<Certificates> call, Response<Certificates> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        final Certificates certificates = response.body();
                        callback.onSuccess(certificates != null ? certificates.getCerts() : Collections.<Certificate>emptyList());
                    }

                    @Override
                    public void onFailure(Call<Certificates> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public String createCertificate(@NotNull List<String> domains) throws IOException {
        final CertificateCreationOrUpdateRequest request = new CertificateCreationOrUpdateRequest();
        request.setDomains(domains);
        final Response<CertificateCreationOrUpdateResponse> response = this.nowService.issueCertificate(request).execute();
        final CertificateCreationOrUpdateResponse body;
        if (!response.isSuccessful() || (body  = response.body()) == null) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return body.getUid();
    }

    @Override
    public void createCertificate(@NotNull List<String> domains, @NotNull final ClientCallback<String> callback) throws IOException {
        final CertificateCreationOrUpdateRequest request = new CertificateCreationOrUpdateRequest();
        request.setDomains(domains);
        this.nowService.issueCertificate(request)
                .enqueue(new Callback<CertificateCreationOrUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CertificateCreationOrUpdateResponse> call, Response<CertificateCreationOrUpdateResponse> response) {
                        final CertificateCreationOrUpdateResponse body;
                        if (!response.isSuccessful() || (body  = response.body()) == null) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(body.getUid());
                    }

                    @Override
                    public void onFailure(Call<CertificateCreationOrUpdateResponse> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public String renewCertificate(@NotNull List<String> domains) throws IOException {
        final CertificateCreationOrUpdateRequest request = new CertificateCreationOrUpdateRequest();
        request.setDomains(domains);
        request.setRenew(true);
        final Response<CertificateCreationOrUpdateResponse> response = this.nowService.createOrReplaceCertificate(request).execute();
        final CertificateCreationOrUpdateResponse body;
        if (!response.isSuccessful() || (body  = response.body()) == null) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return body.getUid();
    }

    @Override
    public void renewCertificate(@NotNull List<String> domains, @NotNull final ClientCallback<String> callback) throws IOException {
        final CertificateCreationOrUpdateRequest request = new CertificateCreationOrUpdateRequest();
        request.setDomains(domains);
        request.setRenew(true);
        this.nowService.createOrReplaceCertificate(request)
                .enqueue(new Callback<CertificateCreationOrUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CertificateCreationOrUpdateResponse> call, Response<CertificateCreationOrUpdateResponse> response) {
                        final CertificateCreationOrUpdateResponse body;
                        if (!response.isSuccessful() || (body  = response.body()) == null) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(body.getUid());
                    }

                    @Override
                    public void onFailure(Call<CertificateCreationOrUpdateResponse> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public String replaceCertificate(@NotNull List<String> domains, @NotNull String ca, @NotNull String cert, @NotNull String key) throws IOException {
        final CertificateCreationOrUpdateRequest request = new CertificateCreationOrUpdateRequest();
        request.setDomains(domains);
        request.setCa(ca);
        request.setCert(cert);
        request.setKey(key);
        final Response<CertificateCreationOrUpdateResponse> response = this.nowService.createOrReplaceCertificate(request).execute();
        final CertificateCreationOrUpdateResponse body;
        if (!response.isSuccessful() || (body  = response.body()) == null) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return body.getCreated_at();
    }

    @Override
    public void replaceCertificate(@NotNull List<String> domains, @NotNull String ca, @NotNull String cert, @NotNull String key, @NotNull final ClientCallback<String> callback) throws IOException {
        final CertificateCreationOrUpdateRequest request = new CertificateCreationOrUpdateRequest();
        request.setDomains(domains);
        request.setCa(ca);
        request.setCert(cert);
        request.setKey(key);
        this.nowService.createOrReplaceCertificate(request)
                .enqueue(new Callback<CertificateCreationOrUpdateResponse>() {
                    @Override
                    public void onResponse(Call<CertificateCreationOrUpdateResponse> call, Response<CertificateCreationOrUpdateResponse> response) {
                        final CertificateCreationOrUpdateResponse body;
                        if (!response.isSuccessful() || (body  = response.body()) == null) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        callback.onSuccess(body.getCreated_at());
                    }

                    @Override
                    public void onFailure(Call<CertificateCreationOrUpdateResponse> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public void deleteCertificate(@NotNull String commonName) throws IOException {
        final Response<ResponseBody> response = this.nowService.deleteCertificate(commonName).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
    }

    @Override
    public void deleteCertificate(@NotNull String commonName, @NotNull final ClientCallback<Void> callback) throws IOException {
        this.nowService.deleteCertificate(commonName)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                        } else {
                            callback.onSuccess(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public List<Alias> getAliases() throws IOException {
        final Response<Aliases> response = this.nowService.getAliases().execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final Aliases body = response.body();
        return body != null ? body.getAliases() : Collections.<Alias>emptyList();
    }

    @Override
    public void getAliases(@NotNull final ClientCallback<List<Alias>> callback) throws IOException {
        this.nowService.getAliases()
                .enqueue(new Callback<Aliases>() {
                    @Override
                    public void onResponse(Call<Aliases> call, Response<Aliases> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                        } else {
                            final Aliases body = response.body();
                            callback.onSuccess(body != null ? body.getAliases() : Collections.<Alias>emptyList());
                        }
                    }

                    @Override
                    public void onFailure(Call<Aliases> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public String deleteAlias(@NotNull String aliasId) throws IOException {
        final Response<DeleteAliasResponse> response = this.nowService.deleteAlias(aliasId).execute();
        final DeleteAliasResponse body;
        if (!response.isSuccessful() || (body = response.body()) == null) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return  body.getStatus();
    }

    @Override
    public void deleteAlias(@NotNull String aliasId, @NotNull final ClientCallback<String> callback) throws IOException {
        this.nowService.deleteAlias(aliasId)
                .enqueue(new Callback<DeleteAliasResponse>() {
                    @Override
                    public void onResponse(Call<DeleteAliasResponse> call, Response<DeleteAliasResponse> response) {
                        final DeleteAliasResponse body;
                        if (!response.isSuccessful() || (body = response.body()) == null) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                        } else {
                            callback.onSuccess(body.getStatus());
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteAliasResponse> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public List<Alias> getDeploymentAliases(@NotNull String deploymentId) throws IOException {
        final Response<Aliases> response = this.nowService.getDeploymentAliases(deploymentId).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final Aliases body = response.body();
        return body != null ? body.getAliases() : Collections.<Alias> emptyList();
    }

    @Override
    public void getDeploymentAliases(@NotNull String deploymentId, @NotNull final ClientCallback<List<Alias>> callback) throws IOException {
        this.nowService.getDeploymentAliases(deploymentId)
                .enqueue(new Callback<Aliases>() {
                    @Override
                    public void onResponse(Call<Aliases> call, Response<Aliases> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        final Aliases body = response.body();
                        callback.onSuccess(body != null ? body.getAliases() : Collections.<Alias>emptyList());
                    }

                    @Override
                    public void onFailure(Call<Aliases> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public Alias createDeploymentAlias(@NotNull String deploymentId, @NotNull String alias) throws IOException {
        final Alias aliasToCreate = new Alias();
        aliasToCreate.setAlias(alias);
        final Response<Alias> response = this.nowService.createDeploymentAliases(deploymentId, aliasToCreate).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return response.body();
    }

    @Override
    public void createDeploymentAlias(@NotNull String deploymentId, @NotNull String alias, @NotNull final ClientCallback<Alias> callback) throws IOException {
        final Alias aliasToCreate = new Alias();
        aliasToCreate.setAlias(alias);
        this.nowService.createDeploymentAliases(deploymentId, aliasToCreate)
                .enqueue(new Callback<Alias>() {
                    @Override
                    public void onResponse(Call<Alias> call, Response<Alias> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                        } else {
                            callback.onSuccess(response.body());
                        }

                    }

                    @Override
                    public void onFailure(Call<Alias> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public List<Secret> getSecrets() throws IOException {
        final Response<GetSecretsResponse> response = this.nowService.getSecrets().execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        final GetSecretsResponse body = response.body();
        return body != null ? body.getSecrets() : Collections.<Secret>emptyList();
    }

    @Override
    public void getSecrets(@NotNull final ClientCallback<List<Secret>> callback) throws IOException {
        this.nowService.getSecrets()
                .enqueue(new Callback<GetSecretsResponse>() {
                    @Override
                    public void onResponse(Call<GetSecretsResponse> call, Response<GetSecretsResponse> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                            return;
                        }
                        final GetSecretsResponse body = response.body();
                        callback.onSuccess(body != null ? body.getSecrets() : Collections.<Secret>emptyList());
                    }

                    @Override
                    public void onFailure(Call<GetSecretsResponse> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public Secret createSecret(@NotNull String name, @NotNull String value) throws IOException {
        final CreateOrUpdateSecretRequest request = new CreateOrUpdateSecretRequest();
        request.setName(name);
        request.setValue(value);
        final Response<Secret> response = this.nowService.createSecret(request).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return response.body();
    }

    @Override
    public void createSecret(@NotNull String name, @NotNull String value, @NotNull final ClientCallback<Secret> callback) throws IOException {
        final CreateOrUpdateSecretRequest request = new CreateOrUpdateSecretRequest();
        request.setName(name);
        request.setValue(value);
        this.nowService.createSecret(request)
                .enqueue(new Callback<Secret>() {
                    @Override
                    public void onResponse(Call<Secret> call, Response<Secret> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                        } else {
                            callback.onSuccess(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Secret> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public Secret renameSecret(@NotNull String uidOrName, @NotNull String newName) throws IOException {
        final CreateOrUpdateSecretRequest request = new CreateOrUpdateSecretRequest();
        request.setName(newName);
        final Response<Secret> response = this.nowService.editSecret(uidOrName, request).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return response.body();
    }

    @Override
    public void renameSecret(@NotNull String uidOrName, @NotNull String newName, @NotNull final ClientCallback<Secret> callback) throws IOException {
        final CreateOrUpdateSecretRequest request = new CreateOrUpdateSecretRequest();
        request.setName(newName);
        this.nowService.editSecret(uidOrName, request)
                .enqueue(new Callback<Secret>() {
                    @Override
                    public void onResponse(Call<Secret> call, Response<Secret> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                        } else {
                            callback.onSuccess(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Secret> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    public Secret deleteSecret(@NotNull String uidOrName) throws IOException {
        final Response<Secret> response = this.nowService.deleteSecret(uidOrName).execute();
        if (!response.isSuccessful()) {
            throw new UnsuccessfulResponseException(response.code(), response.message());
        }
        return response.body();
    }

    @Override
    public void deleteSecret(@NotNull String uidOrName, @NotNull final ClientCallback<Secret> callback) throws IOException {
        this.nowService.deleteSecret(uidOrName)
                .enqueue(new Callback<Secret>() {
                    @Override
                    public void onResponse(Call<Secret> call, Response<Secret> response) {
                        if (!response.isSuccessful()) {
                            this.onFailure(call, new UnsuccessfulResponseException(response.code(), response.message()));
                        } else {
                            callback.onSuccess(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Secret> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }
}
