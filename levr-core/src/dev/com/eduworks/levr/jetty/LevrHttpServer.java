package com.eduworks.levr.jetty;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import com.eduworks.levr.servlet.LevrServlet;
import com.eduworks.levr.servlet.impl.LevrResolverServlet;
import com.eduworks.levr.websocket.LevrResolverWebSocket;

/**
 * This class is meant for developers to be able to run an instance of the LEVR
 * server without issue. It executes using JETTY, and servlets are available for
 * various tasks.
 * 
 * The root can be found at http://localhost:9722 Servlets are /api/custom (
 * {@link LevrResolverServlet}), /api/help ({@link LevrHelpServlet}), /api/test
 * ({@link LevrTestServlet}), /api/counters ({@link LevrCounterServlet}).
 * 
 * @author Fritz Ray
 * 
 */
public class LevrHttpServer extends Server
{
	/** Thread-safe lazy initialization for the singleton instance */
	private static class LevrHttpServerLoader
	{
		private static LevrHttpServer INSTANCE = new LevrHttpServer();
	}

	/*
	 * This holds the servlets that are available to the JETTY server. Note that
	 * TOMCAT servlets are recorded in the web/WEB-INF/web.xml.
	 */
	private final static LevrServlet[] DEF_SERVLETS = new LevrServlet[] { new LevrResolverServlet() };

	protected final static String SOAP_BIND_ADDR = "http://localhost:9723/web/ws";

	public static final int DEFAULT_PORT = 9722;

	/**
	 * Activate the server on a specified port (9722 by default).
	 * 
	 * @param args
	 *            if present, the first is used as the port
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		int port = (args.length > 0) ? Integer.parseInt(args[0]) : DEFAULT_PORT;
		LevrHttpServer.getInstance().setupMetadataServer(port);
		LevrHttpServer.getInstance().startMetadataServer();
	}

	/**
	 * Supports servlet classes extending {@link LevrServlet} for lazy
	 * initialization
	 */
	public static void attach(ServletContextHandler sh, Class<? extends LevrServlet> servlet)
	{
		try
		{
			LevrHttpServer.attach(sh, servlet.newInstance());
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * Multiple paths may be specified in {@link LevrServlet#getServletPath()}
	 * by separating them with whitespace.
	 */
	public static void attach(ServletContextHandler sh, LevrServlet servlet)
	{
		for (String servletPath : servlet.getServletPath().split("\\s+"))
			if (servletPath != null && servletPath.length() > 0)
				sh.addServlet(servlet.getClass(), servletPath);
	}

	/**
	 * Supports Class&lt;{@link LevrServlet}&gt;[] of servlets for lazy
	 * initialization
	 */
	public static void attachAll(ServletContextHandler sh, Class<? extends LevrServlet>[] levrServlets)
	{
		if (levrServlets != null)
			for (Class<? extends LevrServlet> servlet : levrServlets)
				LevrHttpServer.attach(sh, servlet);
	}

	/**
	 * Supports Class&lt;{@link LevrServlet}&gt;[] of servlets for lazy
	 * initialization
	 */
	public static void attachAll(ServletContextHandler sh, LevrServlet[] array)
	{
		if (array != null)
			for (LevrServlet servlet : array)
				LevrHttpServer.attach(sh, servlet);
	}

	/**
	 * Singleton Instance Retreival
	 */
	public static LevrHttpServer getInstance()
	{
		return LevrHttpServerLoader.INSTANCE;
	}

	public static LevrServlet[] getServlets()
	{
		return LevrHttpServer.DEF_SERVLETS.clone();
	}

	/* INSTANCE MEMBERS */

	/** Protected constructor as part of singleton implementation */
	protected LevrHttpServer()
	{
		// This always fails if called implicitly by child constructor
		if (this.getClass() == LevrHttpServer.class && LevrHttpServerLoader.INSTANCE != null)
			throw new IllegalStateException("Already instantiated");

	}

	/**
	 * Start up the server on the given port.
	 * 
	 * @param port
	 *            port to start server on, typically "9722".
	 * @param servletClasses
	 *            an array of LevrServlet classes to attach to the
	 *            {@link ServletHandler}
	 * @throws Exception
	 */
	public void setupMetadataServer(int port, Class<? extends LevrServlet>... servletClasses)
	{
		setupMetadataServer(port, SOAP_BIND_ADDR, servletClasses);
	}

	/**
	 * Start up the server on the given port, publish address, resource base
	 * folder, and welcome page(s).
	 * 
	 * @param port
	 *            the port to set on the HTTP {@link Connector}
	 * @param soapAddress
	 *            the {@link EndPoint} address to which to publish
	 * @param resourceBase
	 *            the base folder to set on the {@link JettyResourceHandler}
	 * @param welcomePages
	 *            the web pages to set as default for the
	 *            {@link JettyResourceHandler}
	 * @param servletClasses
	 *            an array of LevrServlet classes to attach to the
	 *            {@link ServletHandler}
	 * @throws Exception
	 */
	protected void setupMetadataServer(int port, String soapAddress, Class<? extends LevrServlet>... servletClasses)
	{
		ServerConnector connector = new ServerConnector(this);
		connector.setPort(port);
		setConnectors(new Connector[] { connector });

		ServletContextHandler sh = new ServletContextHandler(ServletContextHandler.SESSIONS);
		try
		{
			sh.setServer(this);
			sh.setStopTimeout(Long.MAX_VALUE);
			ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(sh);
			wsContainer.addEndpoint(LevrResolverWebSocket.class);
		}
		catch (DeploymentException e)
		{
			e.printStackTrace();
		}
		catch (ServletException e)
		{
			e.printStackTrace();
		}

		LevrHttpServer.attachAll(sh, LevrHttpServer.getServlets());
		LevrHttpServer.attachAll(sh, servletClasses);

		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { sh, new ContextHandlerCollection(), new DefaultHandler() });
		setHandler(handlers);
	}

	public void startMetadataServer() throws Exception
	{
		if (this.isStopped())
		{
			this.start();
			join();
			System.out.println("Server started. Thread count: " + getThreadPool().getThreads());
		}
		else
		{
			throw new IllegalStateException("Server already started");
		}
	}
}
