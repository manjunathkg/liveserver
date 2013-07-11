package org.liveSense.service.guacamole;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.guacamole.GuacamoleClientException;
import net.sourceforge.guacamole.GuacamoleException;
import net.sourceforge.guacamole.GuacamoleResourceNotFoundException;
import net.sourceforge.guacamole.GuacamoleSecurityException;
import net.sourceforge.guacamole.GuacamoleServerException;
import net.sourceforge.guacamole.io.GuacamoleReader;
import net.sourceforge.guacamole.io.GuacamoleWriter;
import net.sourceforge.guacamole.net.GuacamoleSocket;
import net.sourceforge.guacamole.net.GuacamoleTunnel;
import net.sourceforge.guacamole.net.InetGuacamoleSocket;
import net.sourceforge.guacamole.protocol.ConfiguredGuacamoleSocket;
import net.sourceforge.guacamole.protocol.GuacamoleConfiguration;
import net.sourceforge.guacamole.servlet.GuacamoleHTTPTunnelServlet;
import net.sourceforge.guacamole.servlet.GuacamoleSession;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.auth.core.AuthenticationSupport;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.liveSense.service.securityManager.SecurityManagerService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GuacamoleTunnelServletParameterProvider {
	public static final String PROP_TUNNEL_URL = "tunnel.url";
	public static final String DEFAULT_TUNNEL_URL = "/session/guacamole-tunnel";

	public static final String PROP_GAUCD_SOCKET_ADDRESS = "guacd.socket.address";
	public static final String DEFAULT_GUACD_SOCKET_ADDRESS = "localhost";

	public static final String PROP_GAUCD_SOCKET_PORT = "guacd.socket.port";
	public static final int DEFAULT_GUACD_SOCKET_PORT = 4822;

	public static String getTunnelUrl(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_TUNNEL_URL):DEFAULT_TUNNEL_URL, DEFAULT_TUNNEL_URL);
	}

	public static String getGuacdSocketAddress(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_GAUCD_SOCKET_ADDRESS):DEFAULT_GUACD_SOCKET_ADDRESS, DEFAULT_GUACD_SOCKET_ADDRESS);
	}

	public static Integer getGuacdSocketPort(ComponentContext context) {
		return PropertiesUtil.toInteger(context!=null?context.getProperties().get(PROP_GAUCD_SOCKET_PORT):DEFAULT_GUACD_SOCKET_PORT, DEFAULT_GUACD_SOCKET_PORT);
	}

}

@Component(label="%guacamole.servlet.name", description="%guacamole.servlet.description", immediate=true, metatype=true)
@Service
@Properties(value={
		@Property(label="%guacamole.servlet.tunnel.url", 
				description="%guacamole.servlet.tunnel.url.description", 
				name=GuacamoleTunnelServletParameterProvider.PROP_TUNNEL_URL,
				value=GuacamoleTunnelServletParameterProvider.DEFAULT_TUNNEL_URL),
		@Property(label="%guacamole.servlet.guacd.socket.address", 
				description="%guacamole.servlet.guacd.socket.address.description", 
				name=GuacamoleTunnelServletParameterProvider.PROP_GAUCD_SOCKET_ADDRESS,
				value=GuacamoleTunnelServletParameterProvider.DEFAULT_GUACD_SOCKET_ADDRESS),
		@Property(label="%guacamole.servlet.guacd.socket.port", 
				description="%guacamole.servlet.guacd.socket.port.description", 
				name=GuacamoleTunnelServletParameterProvider.PROP_GAUCD_SOCKET_PORT,
				intValue={GuacamoleTunnelServletParameterProvider.DEFAULT_GUACD_SOCKET_PORT})
})
public class GuacamoleTunnelServlet extends HttpServlet {
	Logger log = LoggerFactory.getLogger(GuacamoleHTTPTunnelServlet.class);

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	private HttpService osgiHttpService;

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	private AuthenticationSupport slingAuthenticator;

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private MimeTypeService mimeTypeService;

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY_UNARY)
	private SecurityManagerService securityService;

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY_UNARY)
	private SlingRepository repository;
	
	private ComponentContext context;
	
	@Activate
	protected void activate(ComponentContext context) {
		
		this.context = context;	
		HttpContext httpContext = new HttpContext() {

			@Override
			public boolean handleSecurity(HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				return true;
			}

			// this context provides no resources, always call the servlet
			@Override
			public URL getResource(String name) {
				return null;
			}

			@Override
			public String getMimeType(String name) {
				MimeTypeService mts = GuacamoleTunnelServlet.this.mimeTypeService;
				return (mts != null) ? mts.getMimeType(name) : null;
			}
		};


		log.info("Registering Guacamole tunnel servlet: "+GuacamoleTunnelServletParameterProvider.getTunnelUrl(context));
		try {
			osgiHttpService.registerServlet(GuacamoleTunnelServletParameterProvider.getTunnelUrl(context), this, new java.util.Properties(), httpContext);
		} catch (ServletException e) {
			log.error("Activate: ",e);
		} catch (NamespaceException e) {
			log.error("Activate: ",e);
		}

	}

	@Deactivate
	protected void deactivate() {
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		handleTunnelRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		handleTunnelRequest(request, response);
	}


	protected GuacamoleTunnel doConnect(HttpServletRequest request, HttpServletResponse response)
			throws GuacamoleException {

		if (slingAuthenticator.handleSecurity(request, response)) {
			Session adminSession = null;
			try {
				// Create our configuration
				GuacamoleConfiguration config = new GuacamoleConfiguration();
				String userName = (String) request.getAttribute("org.osgi.service.http.authentication.remote.user");				
				// Anonynous user is not allowed
				if (userName.equals("anonymous")) {
					return null;
				}
				
				adminSession = repository.loginAdministrative(null);
				User user = securityService.getUserByName(adminSession, userName);
				
				config.setProtocol(user.getProperty("rdpProtocol") != null ? user.getProperty("rdpProtocol")[0].getString() : null);
				config.setParameter("hostname", user.getProperty("rdpHost") != null ? user.getProperty("rdpHost")[0].getString() : null);
				config.setParameter("port", user.getProperty("rdpPort") != null ? user.getProperty("rdpPort")[0].getString() : null);
				config.setParameter("username", user.getProperty("rdpUserName") != null ? user.getProperty("rdpUserName")[0].getString() : null);
				config.setParameter("password", user.getProperty("rdpPassword") != null ? user.getProperty("rdpPassword")[0].getString() : null);
	
				if (StringUtils.isEmpty(config.getParameter("username"))) {
					return null;
				}

				log.info("Connecting to GUACD daemon at "+GuacamoleTunnelServletParameterProvider.getGuacdSocketAddress(context)+":"+GuacamoleTunnelServletParameterProvider.getGuacdSocketPort(context));
				log.info("RDP host: "+config.getParameter("hostname")+":"+config.getParameter("port")+" with user "+config.getParameter("username"));
				// Connect to guacd - everything is hard-coded here.
				GuacamoleSocket socket = new ConfiguredGuacamoleSocket(
						new InetGuacamoleSocket(GuacamoleTunnelServletParameterProvider.getGuacdSocketAddress(context), GuacamoleTunnelServletParameterProvider.getGuacdSocketPort(context)),
						config
						);
	
				// Establish the tunnel using the connected socket
				GuacamoleTunnel tunnel = new GuacamoleTunnel(socket);
	
				// Attach tunnel to session
				HttpSession httpSession = request.getSession(true);
				GuacamoleSession session = new GuacamoleSession(httpSession);
				session.attachTunnel(tunnel);
	
				// Return pre-attached tunnel
				return tunnel;
			} catch (Throwable th) {
				log.error("Could not get tunnel", th);
			} finally {
				if (adminSession != null && adminSession.isLive()) {
					adminSession.logout();
				}
			}
		} else {
			return null;
		}
		return null;
	}

	/**
	 * The prefix of the query string which denotes a tunnel read operation.
	 */
	private static final String READ_PREFIX  = "read:";

	/**
	 * The prefix of the query string which denotes a tunnel write operation.
	 */
	private static final String WRITE_PREFIX = "write:";

	/**
	 * The length of the read prefix, in characters.
	 */
	private static final int READ_PREFIX_LENGTH = READ_PREFIX.length();

	/**
	 * The length of the write prefix, in characters.
	 */
	private static final int WRITE_PREFIX_LENGTH = WRITE_PREFIX.length();

	/**
	 * The length of every tunnel UUID, in characters.
	 */
	private static final int UUID_LENGTH = 36;

	/**
	 * Sends an error on the given HTTP response with the given integer error
	 * code.
	 *
	 * @param response The HTTP response to use to send the error.
	 * @param code The HTTP status code of the error.
	 * @throws ServletException If an error prevents sending of the error
	 *                          code.
	 */
	private void sendError(HttpServletResponse response, int code) throws ServletException {

		try {

			// If response not committed, send error code
			if (!response.isCommitted())
				response.sendError(code);

		}
		catch (IOException ioe) {

			// If unable to send error at all due to I/O problems,
			// rethrow as servlet exception
			throw new ServletException(ioe);

		}

	}



	/**
	 * Dispatches every HTTP GET and POST request to the appropriate handler
	 * function based on the query string.
	 *
	 * @param request The HttpServletRequest associated with the GET or POST
	 *                request received.
	 * @param response The HttpServletResponse associated with the GET or POST
	 *                 request received.
	 * @throws ServletException If an error occurs while servicing the request.
	 */
	protected void handleTunnelRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		try {

			String query = request.getQueryString();
			if (query == null)
				throw new GuacamoleClientException("No query string provided.");

			// If connect operation, call doConnect() and return tunnel UUID
			// in response.
			if (query.equals("connect")) {

				GuacamoleTunnel tunnel = doConnect(request, response);
				if (tunnel != null) {

					// Get session
					HttpSession httpSession = request.getSession(true);
					GuacamoleSession session = new GuacamoleSession(httpSession);

					// Attach tunnel to session
					session.attachTunnel(tunnel);

					log.info("Connection from {} succeeded.", request.getRemoteAddr());

					try {
						// Ensure buggy browsers do not cache response
						response.setHeader("Cache-Control", "no-cache");

						// Send UUID to client
						response.getWriter().print(tunnel.getUUID().toString());
					}
					catch (IOException e) {
						throw new GuacamoleServerException(e);
					}

				}
/*
				// Failed to connect
				else {
					log.info("Connection from {} failed.", request.getRemoteAddr());
					throw new GuacamoleResourceNotFoundException("No tunnel created.");
				}
*/
			}

			// If read operation, call doRead() with tunnel UUID, ignoring any
			// characters following the tunnel UUID.
			else if(query.startsWith(READ_PREFIX))
				doRead(request, response, query.substring(
						READ_PREFIX_LENGTH,
						READ_PREFIX_LENGTH + UUID_LENGTH));

			// If write operation, call doWrite() with tunnel UUID, ignoring any
			// characters following the tunnel UUID.
			else if(query.startsWith(WRITE_PREFIX))
				doWrite(request, response, query.substring(
						WRITE_PREFIX_LENGTH,
						WRITE_PREFIX_LENGTH + UUID_LENGTH));

			// Otherwise, invalid operation
			else
				throw new GuacamoleClientException("Invalid tunnel operation: " + query);
		}

		// Catch any thrown guacamole exception and attempt to pass within the
		// HTTP response, logging each error appropriately.
		catch (GuacamoleSecurityException e) {
			log.warn("Authorization failed.", e);
			sendError(response, HttpServletResponse.SC_FORBIDDEN);
		}
		catch (GuacamoleResourceNotFoundException e) {
			log.debug("Resource not found.", e);
			sendError(response, HttpServletResponse.SC_NOT_FOUND);
		}
		catch (GuacamoleClientException e) {
			log.warn("Error in client request.", e);
			sendError(response, HttpServletResponse.SC_BAD_REQUEST);
		}
		catch (GuacamoleException e) {
			log.error("Server error in tunnel", e);
			sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}


	/**
	 * Called whenever the JavaScript Guacamole client makes a read request.
	 * This function should in general not be overridden, as it already
	 * contains a proper implementation of the read operation.
	 *
	 * @param request The HttpServletRequest associated with the read request
	 *                received.
	 * @param response The HttpServletResponse associated with the write request
	 *                 received. Any data to be sent to the client in response
	 *                 to the write request should be written to the response
	 *                 body of this HttpServletResponse.
	 * @param tunnelUUID The UUID of the tunnel to read from, as specified in
	 *                   the write request. This tunnel must be attached to
	 *                   the Guacamole session.
	 * @throws GuacamoleException If an error occurs while handling the read
	 *                            request.
	 */
	protected void doRead(HttpServletRequest request, HttpServletResponse response, String tunnelUUID) throws GuacamoleException {

		HttpSession httpSession = request.getSession(false);
		GuacamoleSession session = new GuacamoleSession(httpSession);

		// Get tunnel, ensure tunnel exists
		GuacamoleTunnel tunnel = session.getTunnel(tunnelUUID);
		if (tunnel == null)
			throw new GuacamoleResourceNotFoundException("No such tunnel.");

		// Ensure tunnel is open
		if (!tunnel.isOpen())
			throw new GuacamoleResourceNotFoundException("Tunnel is closed.");

		// Obtain exclusive read access
		GuacamoleReader reader = tunnel.acquireReader();

		try {

			// Note that although we are sending text, Webkit browsers will
			// buffer 1024 bytes before starting a normal stream if we use
			// anything but application/octet-stream.
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control", "no-cache");

			Writer out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

			// Detach tunnel and throw error if EOF (and we haven't sent any
			// data yet.
			char[] message = reader.read();
			if (message == null)
				throw new GuacamoleResourceNotFoundException("Tunnel reached end of stream.");

			// For all messages, until another stream is ready (we send at least one message)
			do {

				// Get message output bytes
				out.write(message, 0, message.length);

				// Flush if we expect to wait
				if (!reader.available()) {
					out.flush();
					response.flushBuffer();
				}

				// No more messages another stream can take over
				if (tunnel.hasQueuedReaderThreads())
					break;

			} while (tunnel.isOpen() && (message = reader.read()) != null);

			// Close tunnel immediately upon EOF
			if (message == null)
				tunnel.close();

			// End-of-instructions marker
			out.write("0.;");
			out.flush();
			response.flushBuffer();

		}
		catch (GuacamoleException e) {

			// Detach and close
			session.detachTunnel(tunnel);
			tunnel.close();

			throw e;
		}
		catch (IOException e) {

			// Log typically frequent I/O error if desired
			log.debug("Error writing to servlet output stream", e);

			// Detach and close
			session.detachTunnel(tunnel);
			tunnel.close();

		}
		finally {
			tunnel.releaseReader();
		}

	}

	/**
	 * Called whenever the JavaScript Guacamole client makes a write request.
	 * This function should in general not be overridden, as it already
	 * contains a proper implementation of the write operation.
	 *
	 * @param request The HttpServletRequest associated with the write request
	 *                received. Any data to be written will be specified within
	 *                the body of this request.
	 * @param response The HttpServletResponse associated with the write request
	 *                 received.
	 * @param tunnelUUID The UUID of the tunnel to write to, as specified in
	 *                   the write request. This tunnel must be attached to
	 *                   the Guacamole session.
	 * @throws GuacamoleException If an error occurs while handling the write
	 *                            request.
	 */
	protected void doWrite(HttpServletRequest request, HttpServletResponse response, String tunnelUUID) throws GuacamoleException {

		HttpSession httpSession = request.getSession(false);
		GuacamoleSession session = new GuacamoleSession(httpSession);

		GuacamoleTunnel tunnel = session.getTunnel(tunnelUUID);
		if (tunnel == null)
			throw new GuacamoleResourceNotFoundException("No such tunnel.");

		// We still need to set the content type to avoid the default of
		// text/html, as such a content type would cause some browsers to
		// attempt to parse the result, even though the JavaScript client
		// does not explicitly request such parsing.
		response.setContentType("application/octet-stream");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentLength(0);

		// Send data
		try {

			GuacamoleWriter writer = tunnel.acquireWriter();

			Reader input = new InputStreamReader(request.getInputStream(), "UTF-8");
			char[] buffer = new char[8192];

			int length;
			while (tunnel.isOpen() &&
					(length = input.read(buffer, 0, buffer.length)) != -1)
				writer.write(buffer, 0, length);

		}
		catch (IOException e) {

			// Detach and close
			session.detachTunnel(tunnel);
			tunnel.close();

			throw new GuacamoleServerException("I/O Error sending data to server: " + e.getMessage(), e);
		}
		finally {
			tunnel.releaseWriter();
		}

	}

}

