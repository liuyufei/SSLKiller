package com.lyf.jason.sslkiller;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by liuyufei on 15/12/1.
 */
public class SSLKiller  implements IXposedHookLoadPackage{

    public static final String PACKAGENAME = "com.chinatelecom.bestpayclient";

    public TrustManager[] trustManagers = new TrustManager[] {new TrustAllX509TrustManager()};

    public XC_MethodHook getSocketFactoryHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            param.setResult(TrustAllApacheSSLSocketFactory.getSocketFactory());
            XposedBridge.log("Executed");
            return;
        }
    };

    public XC_MethodHook sslContextInitHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            param.args[1] = trustManagers;
            XposedBridge.log("Executed");
        }
    };

    public XC_MethodHook hostNameVerifierHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            param.args[0] = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            XposedBridge.log("Executed");
        }
    };

    public XC_MethodHook setSSLSocketFactoryHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            param.args[0] = new TrustAllSSLSocketFactory();
            XposedBridge.log("Executed");
        }
    };

    public XC_MethodHook urlConnectionHook = new XC_MethodHook() {

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            param.setResult(new TrustAllSSLSocketFactory());
            XposedBridge.log("Executed");
        }

    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if(PACKAGENAME.equals(loadPackageParam.packageName)) {

            XposedHelpers.findAndHookMethod(SSLSocketFactory.class, "getSocketFactory", getSocketFactoryHook);
            XposedHelpers.findAndHookMethod(SSLContext.class, "init", "javax.net.ssl.KeyManager[]", "javax.net.ssl.TrustManager[]", SecureRandom.class, sslContextInitHook);
            XposedHelpers.findAndHookMethod(SSLSocketFactory.class, "setHostnameVerifier", X509HostnameVerifier.class, hostNameVerifierHook);

            XposedHelpers.findAndHookMethod(HttpsURLConnection.class, "setHostnameVerifier", HostnameVerifier.class, hostNameVerifierHook);
            XposedHelpers.findAndHookMethod(HttpsURLConnection.class, "setDefaultHostnameVerifier", HostnameVerifier.class, hostNameVerifierHook);
            XposedHelpers.findAndHookMethod(HttpsURLConnection.class, "setSSLSocketFactory", javax.net.ssl.SSLSocketFactory.class, setSSLSocketFactoryHook);
            XposedHelpers.findAndHookMethod(HttpsURLConnection.class, "setDefaultSSLSocketFactory", javax.net.ssl.SSLSocketFactory.class, setSSLSocketFactoryHook);
            XposedHelpers.findAndHookMethod(HttpsURLConnection.class, "getDefaultSSLSocketFactory", urlConnectionHook);

        }
    }
}
