package com.rieke.jettylauncher;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.swing.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public abstract class JettyRunner {

    public interface StateListener {
        void onStateChange(State state);
    }

    protected static int DEFAULT_PORT = 8080;
    protected static String CONTEXT_PATH = "/";
    private Server server;
    private State state = State.STOPPED;
    private JTextField statusField=null;
    private String host;
    private int port;

    private static List<StateListener> stateListeners = new ArrayList<StateListener>();

    protected abstract String getWebappPath();
    protected abstract String getWebdefaultPath();

    public void startJetty(int port) throws Exception {
        this.port = port;
        setState(State.STARTING);
        server = new Server(DEFAULT_PORT);
        String rootPath = getWebappPath();
        WebAppContext webapp = new WebAppContext(rootPath, CONTEXT_PATH);
        String descriptorPath = getWebdefaultPath();
        webapp.setDefaultsDescriptor(descriptorPath);

        System.out.println("Deploying to: "+InetAddress.getLocalHost().getHostAddress());
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);

        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(http_config));
        http.setPort(port);
        http.setIdleTimeout(360000);
        host = InetAddress.getLocalHost().getHostAddress();
        http.setHost(host);

        server.setConnectors(new Connector[] { http });

        server.setHandler(webapp);
        server.start();
        server.join();
    }


    public void stop() {
        stop(null);
    }
    public void stop(String errorMessage) {
        if(server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {

            }
            server.destroy();
        }
        setState(State.STOPPED,errorMessage);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        setState(state,null);
    }

    public void setState(State state,String message) {
        this.state = state;
        if(statusField!=null) {
            message = (message==null?"":" - "+message);
            statusField.setText(state.toString()+message);
        }
        for (StateListener listener:stateListeners) {
            listener.onStateChange(state);
        }
    }

    public void setStatusField(JTextField statusField) {
        this.statusField = statusField;
    }
    public void addStateListener(StateListener stateListener) {
        stateListeners.add(stateListener);
    }
    public void afterDeployed() {
        setState(State.RUNNING, "Available at: http://"+host+(port==80?"":":"+port)+CONTEXT_PATH);
    }
}