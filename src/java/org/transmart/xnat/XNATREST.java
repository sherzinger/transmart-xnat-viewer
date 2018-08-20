package org.transmart.xnat;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author Sijin He
 */
public class XNATREST {

    static String jsessionid = "";
    private String user;
    private String pass;

    public XNATREST(String domain, String username, String password){
        this.user = username;
        this.pass = password;
        login(domain, username, password);
    }

    public void login(String domain, String username, String password) {
        try {
            RESTRequest rest = new RESTRequest();
            List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("login_method", "Database"));
            nameValuePairs.add(new BasicNameValuePair("j_username", username));
            nameValuePairs.add(new BasicNameValuePair("j_password", password));
            nameValuePairs.add(new BasicNameValuePair("login", "Login"));
            nameValuePairs.add(new BasicNameValuePair("XNAT_CSRF", ""));
            HttpResponse response = rest.doPost("https://" + domain + "/j_spring_security_check", nameValuePairs, this.user, this.pass);
            //Get Headers
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase("Set-Cookie")) {

                    String[] sessionInfo = header.getValue().split(";", -1);

                    for (String a : sessionInfo) {

                        if (a.startsWith("JSESSIONID=")) {
                            jsessionid = a;
                        }
                    }

                }
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(XNATREST.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HttpResponse fetchData(String url) {


        HttpResponse response = null;

        RESTRequest rest = new RESTRequest();
        response = rest.doGet(url, jsessionid, this.user, this.pass);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }

        return response;
    }

}
