package com.isansys.patientgateway.serverlink;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.*;

import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.openssl.PEMParser;
import org.spongycastle.openssl.PasswordFinder;


public class SslUtil
{
    private static byte[] readFile(String file) throws IOException
    {
        return readFile(new File(file));
    }

    private static byte[] readFile(File file) throws IOException
    {
        // Open file
        try (RandomAccessFile f = new RandomAccessFile(file, "r"))
        {
            // Get and check length
            long long_length = f.length();
            int length = (int) long_length;

            if (length != long_length)
            {
                throw new IOException("File size >= 2 GB");
            }

            // Read file and return data
            byte[] data = new byte[length];

            f.readFully(data);

            return data;
        }
    }

    // Just authenticating the server
    public static SSLSocketFactory getSocketFactory(final String caCrtFile) throws Exception
    {
        // load CA certificate
        byte[] caFileAsBytes = readFile(caCrtFile);

        PEMParser reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(caFileAsBytes)));
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509CertificateHolder caCertHolder = (X509CertificateHolder)reader.readObject();

        InputStream in = new ByteArrayInputStream(caCertHolder.getEncoded());
        X509Certificate caCert = (X509Certificate) certFactory.generateCertificate(in);

        reader.close();

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1");
        context.init(null, tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }

    // Authenticating the server and the client
    public static SSLSocketFactory getSocketFactory(final String caCrtFile, final String clientCrtFile, final String clientKeyFile, final String password) throws Exception
    {
        // load CA certificate
        byte[] caFileAsBytes = readFile(caCrtFile);

        PEMParser reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(caFileAsBytes)));
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509CertificateHolder caCertHolder = (X509CertificateHolder)reader.readObject();

        InputStream in = new ByteArrayInputStream(caCertHolder.getEncoded());
        X509Certificate caCert = (X509Certificate) certFactory.generateCertificate(in);

        reader.close();

        // load client certificate
        byte[] clientFileAsBytes = readFile(clientCrtFile);
        reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(clientFileAsBytes)));
        X509CertificateHolder clientCertHolder = (X509CertificateHolder)reader.readObject();
        reader.close();

        in = new ByteArrayInputStream(clientCertHolder.getEncoded());
        X509Certificate clientCert = (X509Certificate) certFactory.generateCertificate(in);

        // load client private key
        byte[] clientKeyAsBytes = readFile(clientKeyFile);
        reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(clientKeyAsBytes)));
        Object keyInfo = reader.readObject();
        reader.close();

        KeyFactory keyFactory = KeyFactory.getInstance(((PrivateKeyInfo)keyInfo).getPrivateKeyAlgorithm().getAlgorithm().toString());
        PrivateKey     key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(((PrivateKeyInfo)keyInfo).getEncoded()));

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        // client key and certificates are sent to server so it can authenticate us
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", clientCert);
        ks.setKeyEntry("private-key", key, password.toCharArray(), new java.security.cert.Certificate[]{clientCert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }
}