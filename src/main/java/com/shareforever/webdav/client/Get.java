package com.shareforever.webdav.client;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.*;

public class Get extends Command {
    private String url;
    private String file;
    private String outFile;
    private boolean verbose = false;
    private boolean overwrite = false;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public void execute() throws Exception {
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(user, password);
        client.getState().setCredentials(AuthScope.ANY, creds);
        File f = new File(outFile);
        long startTime = System.currentTimeMillis();

        if (this.overwrite || !f.exists()) {
            if (verbose) {
                if (f.exists()) log("Overwriting " + f.getAbsolutePath());
                log("Downloading " + url + "/" + file + " to " + f.getAbsolutePath());
            }

            //Download the file
            GetMethod method = new GetMethod(url + "/" + file);
            client.executeMethod(method);

            //200 OK => No issues
            if (method.getStatusCode() != 200)
                throw new Exception(method.getStatusCode() + " " + method.getStatusText());

            InputStream is = method.getResponseBodyAsStream();
            OutputStream out = new FileOutputStream(f);
            byte buf[] = new byte[1024];
            int len;

            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
                out.flush();
            }
            out.close();

            if (verbose) {
                long endTime = System.currentTimeMillis();
                long elapsed = ((endTime - startTime) / 1000);
                log(file + " took " + elapsed + " seconds to complete");
            }
        } else
            log("Skipping " + file);


    }

}