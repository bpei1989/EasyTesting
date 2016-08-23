package com.easytesting.http;

import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;


/**
 * Custom implementation of Trust provider so that we can work with the
 * self signed certificates that vcautomation uses
 */
public final class TrustAllTrustProvider extends Provider {
   private static final String ALGO = "TrustAllTrustAlgorithm";
   private static final String ID = TrustAllTrustProvider.class.getSimpleName();

   /**
    *
    */
   public TrustAllTrustProvider() {
      super(ID, (double) 0.1, "TrustProvider that provides all secure socket " +
         "factories by ignoring issues in the chain of certificate trust");
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object
         run() {
            put("TrustManagerFactory." + TrustAllTrustManagerFactory.getAlgorithm(),
               TrustAllTrustManagerFactory.class.getName());
            return null;
         }
      });
   }

   /**
    * Sets up JVM to always trust the certificates
    */
   public static void
   trustAll() {
      Provider registered = Security.getProvider(ID);
      if (null == registered) {
         Security.insertProviderAt(new TrustAllTrustProvider(), 1);
         Security.setProperty("ssl.TrustManagerFactory.algorithm",
            ALGO);
      }
   }

   /**
    * Factory for {@code TrustAllTrustProvider}
    */
   public final static class TrustAllTrustManagerFactory extends TrustManagerFactorySpi {
      public TrustAllTrustManagerFactory() {
      }

      protected void
      engineInit(ManagerFactoryParameters mgrparams) {
      }

      protected void
      engineInit(KeyStore keystore) {
      }

      protected TrustManager[]
      engineGetTrustManagers() {
         return new TrustManager[]{new X509TrustManager() {
            /**
             * Doesn't throw an exception, thus approving the certificate.
             */
            public void
            checkClientTrusted(X509Certificate[] cert,
                               String authType)
               throws CertificateException {
            }

            /**
             * Doesn't throw an exception, thus approving the certificate.
             */
            public void
            checkServerTrusted(X509Certificate[] cert,
                               String authType)
               throws CertificateException {
            }

            public X509Certificate[]
            getAcceptedIssuers() {
               return null;
            }
         }};
      }

      public static String
      getAlgorithm() {
         return ALGO;
      }
   }
}

