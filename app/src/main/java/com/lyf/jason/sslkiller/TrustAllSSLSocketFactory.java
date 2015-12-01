package com.lyf.jason.sslkiller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class TrustAllSSLSocketFactory extends SSLSocketFactory {

	private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[] { new TrustAllX509TrustManager() };

	private final SSLSocketFactory delegate = getSocketFactory();

	@Override
	public Socket createSocket() throws IOException {
		return delegate.createSocket();
	}

	@Override
	public Socket createSocket(final InetAddress host, final int port)
			throws IOException {
		return delegate.createSocket(host, port);
	}

	@Override
	public Socket createSocket(final InetAddress address, final int port,
			final InetAddress localAddress, final int localPort)
			throws IOException {
		return delegate.createSocket(address, port, localAddress, localPort);
	}

	@Override
	public Socket createSocket(final Socket s, final String host,
			final int port, final boolean autoClose) throws IOException {
		return delegate.createSocket(s, host, port, autoClose);
	}

	@Override
	public Socket createSocket(final String host, final int port)
			throws IOException, UnknownHostException {
		return delegate.createSocket(host, port);
	}

	@Override
	public Socket createSocket(final String host, final int port,
			final InetAddress localHost, final int localPort)
			throws IOException, UnknownHostException {
		return delegate.createSocket(host, port, localHost, localPort);
	}

	@Override
	public boolean equals(final Object o) {
		return delegate.equals(o);
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return delegate.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return delegate.getSupportedCipherSuites();
	}

	public static SSLSocketFactory getSocketFactory() {
		System.setProperty("http.keepAlive", "false");
		try {
			final SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, TRUST_ALL_CERTS, new SecureRandom());
			return sc.getSocketFactory();
		} catch (final KeyManagementException e) {
		} catch (final NoSuchAlgorithmException e) {
		}
		return null;
	}
}
