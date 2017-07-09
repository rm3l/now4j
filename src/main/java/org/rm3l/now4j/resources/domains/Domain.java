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
package org.rm3l.now4j.resources.domains;

import java.time.ZonedDateTime;
import java.util.List;

public final class Domain {

    private String uid;
    private Boolean verified;
    private String verifyToken;
    private ZonedDateTime created;
    private Boolean isExternal;
    private String name;
    private List<String> aliases;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getVerifyToken() {
        return verifyToken;
    }

    public void setVerifyToken(String verifyToken) {
        this.verifyToken = verifyToken;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public Boolean getExternal() {
        return isExternal;
    }

    public void setExternal(Boolean external) {
        isExternal = external;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain = (Domain) o;

        if (uid != null ? !uid.equals(domain.uid) : domain.uid != null) return false;
        if (verified != null ? !verified.equals(domain.verified) : domain.verified != null) return false;
        if (verifyToken != null ? !verifyToken.equals(domain.verifyToken) : domain.verifyToken != null) return false;
        if (created != null ? !created.equals(domain.created) : domain.created != null) return false;
        if (isExternal != null ? !isExternal.equals(domain.isExternal) : domain.isExternal != null) return false;
        if (name != null ? !name.equals(domain.name) : domain.name != null) return false;
        return aliases != null ? aliases.equals(domain.aliases) : domain.aliases == null;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (verified != null ? verified.hashCode() : 0);
        result = 31 * result + (verifyToken != null ? verifyToken.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (isExternal != null ? isExternal.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (aliases != null ? aliases.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "uid='" + uid + '\'' +
                ", verified=" + verified +
                ", verifyToken='" + verifyToken + '\'' +
                ", created=" + created +
                ", isExternal=" + isExternal +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                '}';
    }
}
