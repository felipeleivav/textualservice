package org.selknam.textualservice.main;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.selknam.textualservice.arq.exception.ConnectionException;
import org.selknam.textualservice.dao.InitDAO;
import org.selknam.textualservice.dao.TestDAO;
import org.selknam.textualservice.filter.Authenticator;
import org.selknam.textualservice.filter.CrossOrigin;
import org.selknam.textualservice.utils.Constants;
import org.selknam.textualservice.utils.PropLoader;

public class App {
	private static Logger logger = Logger.getLogger(App.class);
	
    public static void main(String[] args) throws Exception {
    	logger.warn("STARTING SERVER");
    	PropLoader.initialize();
    	Integer httpPort = Integer.valueOf(PropLoader.get(Constants.SERVER_PORT_HTTP));
    	Integer httpsPort = Integer.valueOf(PropLoader.get(Constants.SERVER_PORT_HTTPS));
    	String keystoreFile = PropLoader.get(Constants.SERVER_KEYSTORE_FILE);
    	String keystorePass = PropLoader.get(Constants.SERVER_KEYSTORE_PASS);
    	
    	TestDAO tester = new TestDAO();
    	if (!tester.testConnection()) {
    		logger.fatal("Error connecting to database");
    		throw new ConnectionException("Error connecting to database");
    	}
    	tester.close();
    	
    	InitDAO initer = new InitDAO();
    	if (!initer.isDbInstalled()) {
    		initer.installDb();
    	}
    	initer.close();
    	
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(httpPort);
        jettyServer.setHandler(context);
        
        ServerConnector sslConnector = null;
        try {
			HttpConfiguration https = new HttpConfiguration();
			https.addCustomizer(new SecureRequestCustomizer());
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStorePath(App.class.getResource("/"+keystoreFile).toExternalForm());
			sslContextFactory.setKeyStorePassword(keystorePass);
			sslContextFactory.setKeyManagerPassword(keystorePass);
			sslConnector = new ServerConnector(jettyServer, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
			sslConnector.setPort(httpsPort);
			// If no error setting https up, then disable plain http
			jettyServer.setConnectors(new Connector[]{sslConnector});
			logger.warn("Running on: "+httpsPort);
        } catch (Exception e) {
        	logger.warn("Can't enable SSL ", e);
        	logger.warn("Running on: "+httpPort);
        }
		
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("org.glassfish.jersey.spi.container.ContainerRequestFilters", Authenticator.class.getCanonicalName());
        jerseyServlet.setInitParameter("org.glassfish.jersey.spi.container.ContainerResponeFilters", CrossOrigin.class.getCanonicalName());
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "org.selknam.textualservice.rest;org.selknam.textualservice.filter");
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", "org.glassfish.jersey.moxy.json.MoxyJsonFeature");
        
        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
        	logger.fatal("Error starting server ", e);
        	if (sslConnector!=null) {
	        	sslConnector.close();
	        	sslConnector.destroy();
        	}
           if (jettyServer!=null) {
        	   jettyServer.stop();
        	   jettyServer.destroy();
           }
        }
    }
    
}
