package com.lyf.jason.sslkiller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

public final class TrustAllApacheSSLSocketFactory extends SSLSocketFactory {
	
	private final SSLContext sslContext = SSLContext.getInstance(TLS);

	public TrustAllApacheSSLSocketFactory(final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
		
		super(truststore);
		
		final TrustManager[] tm = { new TrustAllX509TrustManager() };
		
		sslContext.init(null, tm, null);
	}

	@Override
	public Socket createSocket() throws IOException {
		
		return sslContext.getSocketFactory().createSocket();
	}

	@Override
	public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
		
		return sslContext.getSocketFactory().createSocket(socket, host, port,autoClose);
	}

	public static SSLSocketFactory getSocketFactory() {
		try {
			
			final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			
			trustStore.load(null, null);
			
			final SSLSocketFactory factory = new TrustAllApacheSSLSocketFactory(trustStore);
			
			factory.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);
			
			return factory;
			
		} catch (final GeneralSecurityException e) {
			
		} catch (final IOException e) {
			
		}
		return null;
	}
}