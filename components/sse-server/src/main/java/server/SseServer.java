package server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SseServer {

    private static Server sseServer;
    private static Executor executorService = Executors.newSingleThreadExecutor();
    private static long testTime = Long.parseLong(System.getenv("test_time"));

    public static void main(String[] args) {

        int port = 8080;
        Server server = new Server(port);
        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        SSEServlet sseServlet = new SSEServlet();
        ServletHolder servletHolder = new ServletHolder(sseServlet);
        servletHandler.addServletWithMapping(servletHolder, "/memory");

        sseServer = server;
        startAndStopSseServer(testTime);
    }

    private static void startAndStopSseServer(long stopAfterMillis) {
        executorService.execute(() -> {
            try {
                sseServer.start();
                Thread.sleep(stopAfterMillis);
                sseServer.stop();
            } catch (Exception ignored) {
                //
            }
        });
    }
}
