package uk.co.compendiumdev.thingifier.application.internalhttpconversion;

import java.util.HashMap;
import java.util.Map;

/*
    The HttpApiResponse is too complicated to re-use and is tied to the ApiResponse

    This is just a cleaner bridge for pure Http access.

 */
public class InternalHttpResponse {
    private int status;
    private String contentType;
    private String body;
    private Map<String, String> headers;

    public InternalHttpResponse(){

        headers = new HashMap<>();
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public void setType(final String contentType) {
        this.contentType = contentType;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public void setHeader(final String headerName, final String header) {

    }

    public int getStatusCode() {
        return status;
    }

    public boolean hasType() {
        return contentType!=null;
    }

    public String getType() {
        return contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
