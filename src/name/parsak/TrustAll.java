package name.parsak;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustAll implements TrustManager, X509TrustManager {
    public TrustAll() { super(); }
    public X509Certificate[] getAcceptedIssuers() {return null;}
    public boolean isServerTrusted(final X509Certificate[] certs) {return true;}
    public boolean isClientTrusted(final X509Certificate[] certs) {return true;}
    public void checkServerTrusted(
    		final X509Certificate[] certs, 
    		final String authType) throws CertificateException
    { return; }
    public void checkClientTrusted(
    		final X509Certificate[] certs,
    		final String authType) throws CertificateException
    { return; }
}
