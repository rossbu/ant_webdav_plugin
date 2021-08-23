package com.shareforever.webdav.client;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Put extends Command {
    private String url;
    private boolean verbose = true;
    Vector<FileSet> fileSets = new Vector<FileSet>();
    private HttpClient client;
    private File file;
    private String filename;

    Set<String> alreadyCreated = new HashSet<String>();

    public Set<String> getAlreadyCreated() {
        return alreadyCreated;
    }

    public void setAlreadyCreated(Set<String> alreadyCreated) {
        this.alreadyCreated = alreadyCreated;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addFileSet(FileSet fileSet) {
        if (!fileSets.contains(fileSet)) {
            fileSets.add(fileSet);
        }
    }

    public void setFile(File f) {
        this.file = f;
    }

    public void setFilename(String name) {
        this.filename = name;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void execute() {
        DirectoryScanner ds;
        client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(user, password);
        client.getState().setCredentials(AuthScope.ANY, creds);

        if (file != null) {
            uploadFile(file, filename == null ? file.getName() : filename);
        }
        for (FileSet fileset : fileSets) {
            ds = fileset.getDirectoryScanner(getProject());
            File dir = ds.getBasedir();
            String[] filesInSet = ds.getIncludedFiles();

            for (String filename : filesInSet) {
                if (verbose) {
                    log("\n ++++ Processing ++++ : " + filename + " \n");
                }
                File f = new File(dir, filename);
                createDirectory(filename, f.getName());
                uploadFile(f, filename);
            }
        }
    }

    private void createDirectory(String path, String fileName) {
        try {
            //Remove the filename at the end
            log("path : " + path + " -- fileName: " + fileName);
            String directoryPath = path.split(fileName)[0].trim();
            log("directoryPath : " + directoryPath);

            //Build the upload URL
            String uploadUrl = url;
            String[] directories ;
            if ("\\".equals(File.separator)){
                directories = directoryPath.split("\\\\");
            } else {
                directories = directoryPath.split(File.separator);
            }
            log("directories : " + Arrays.toString(directories));


            if (directoryPath.length() > 0) {
                //Recursively create the directory structure
                for (String directoryName : directories) {
                    directoryName = URLEncoder.encode(directoryName, "UTF-8").replaceAll("\\+","%20");;
                    log("encoded  dir is  : " + directoryName);
                    uploadUrl = uploadUrl + "/" + directoryName;

                    if (alreadyCreated.contains(uploadUrl)) {
                        log("skipp existed : " + directoryName);
                        continue;
                    } else {

                    }

                    MkColMethod mkdir = new MkColMethod(uploadUrl);
                    int status = client.executeMethod(mkdir);  // call url

                    if (status == 405) {
                        /*Directory exists. Do Nothing*/
                    } else if (status != 201) {
                        log("ERR " + " " + status + " " + uploadUrl);
                    } else if (status == 201) {
                        log("+++ Directory " + uploadUrl + " created");
                        alreadyCreated.add(uploadUrl);
                    }
                }
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            //Ignore as there is no directory to be created
        } catch (Exception e) {
            log("XXXXXXXXXXXXXXXXXX ERR creating " + path);
            e.printStackTrace();
            throw new RuntimeException("Error creating directory " + path, e);
        } finally {

        }
    }

    /**
     * Uploads the file. The File object (f) is used for creating a FileInputStream for uploading
     * files to webdav
     *
     * @param f        The File object of the file to be uploaded
     * @param filename The relatvie path of the file
     */
    private void uploadFile(File f, String filename) {
        try {

            log("File name is --> " + filename);


            filename = filename.replaceAll("\\\\","/");
            log("File name (after BS convert)  is --> " + filename);

//            filename = URLEncoder.encode(filename, "UTF-8");
//            log("File encoded filename is --> " + filename);

            String uploadUrl = url + "/" + filename;
            log("File uploaded url is ---------> : " + uploadUrl);


            uploadUrl = URIUtil.encodeQuery(uploadUrl).replaceAll("\\+","%20");
            log("File uploaded url ( After URIUtil) is ---------> : " + uploadUrl);


            PutMethod method = new PutMethod(uploadUrl);
            RequestEntity requestEntity = new InputStreamRequestEntity(new FileInputStream(f));
            method.setRequestEntity(requestEntity);
            client.executeMethod(method);  // call url

            //201 Created => No issues
            if (method.getStatusCode() == 204) {
                log(String.format("%s overwritten with %s", uploadUrl, filename));
            } else if (method.getStatusCode() != 201) {
                log("XXXX Upload ERR " + " " + method.getStatusCode() + " " + method.getStatusText() + " " + f.getAbsolutePath());
                throw new RuntimeException(String.format("Could not upload %s to %s", filename, uploadUrl));
            } else {
                log(String.format("Transferred %s to %s", filename, uploadUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("ERR " + f.getAbsolutePath());
            throw new RuntimeException("Error transferring " + filename, e);
        }
    }


    public static void main(String[] arg0) throws URIException {

        String uploadUrl = URIUtil.encodeQuery("Chrome 52.0.2743 (Windows 10 0.0.0)");
        System.out.println(uploadUrl);

    }


}