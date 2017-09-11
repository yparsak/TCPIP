package name.parsak;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

public class TCPIP {
	public static final String NL = System.getProperty("line.separator");
	public static final String XML = "text/xml";
	private static final String POST = "POST";
	private static final String GET = "GET";
	private static final String SSL = "SSL";
	
	private static URL url = null;
	private static HttpURLConnection conn = null;
	private static OutputStreamWriter writer = null;
	
    static {
        try {
            final TrustManager[] trustAllCerts = { new TrustAll() };
            final SSLContext ssl;
            ssl = SSLContext.getInstance(SSL);
            ssl.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(ssl
                .getSocketFactory());
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final KeyManagementException e) {
            e.printStackTrace();
        }
        final HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(final String host, final SSLSession session) {
                if (!host.equals(session.getPeerHost())) {
                    System.err.println("Warning: SSL Host '" + host + "' vs. '"+ session.getPeerHost() + "'");
                }
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }	
    //
	private static void setURL(final String url_str) throws MalformedURLException {
		url = new URL(url_str);
	}
	//
	private static void connect(final String method) throws IOException {
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		if (method.equals(POST)) {
			conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.addRequestProperty("accept", XML);
            conn.addRequestProperty("content-type", XML);
		}
		conn.connect();	
	}
    private static void disconnect() {
    	conn.disconnect();
    }
	//
	private static void setWriter() throws IOException {
		writer = new OutputStreamWriter(conn.getOutputStream());
	}
	private static String readResponse() throws IOException {
		InputStream inputstream = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
		StringBuilder response = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) { response.append(line + NL); }
		return response.toString();
	}
	//   
    public String post(String url_str, String request) {
    	try {
			setURL(url_str);
			try {
				connect(POST);
				setWriter();
				writer.write(request);
				writer.flush();
				String response = readResponse();
				disconnect();
				return response;
			} catch (IOException e) {
				e.printStackTrace();
				return e.getMessage();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
    }
    //
    public String get(String url_str) {
    	try {
			setURL(url_str);
			try {
				connect(GET);
				String response = readResponse();
				disconnect();
				return response;
			} catch (IOException e) {
				e.printStackTrace();
				return e.getMessage();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
    }
    //
    public void download(String url_str, String file_name)
    {
    	download(url_str,file_name, "");
    }
    public void download(String url_str, String file_name, String path)  {
    	try {
    		File f = new File(".");
    		if (path.equals("")) {
    			path = f.getAbsolutePath().replace(".", "");
    		}
    		BufferedInputStream in = null;
    	    FileOutputStream fout = null;
    	    in = new BufferedInputStream(new URL(url_str+"/"+file_name).openStream());
	        fout = new FileOutputStream(path+file_name);
	        final byte data[] = new byte[1024];
	        int count;
	        while ((count = in.read(data, 0, 1024)) != -1) {
	            fout.write(data, 0, count);
	        }	        
	        in.close();
	        fout.close();
    	}
    	catch (IOException e) {
			e.printStackTrace();
		}
    }   
}