/*
 *Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *WSO2 Inc. licenses this file to you under the Apache License,
 *Version 2.0 (the "License"); you may not use this file except
 *in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing,
 *software distributed under the License is distributed on an
 *"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *KIND, either express or implied.  See the License for the
 *specific language governing permissions and limitations
 *under the License.
 */

package server;

import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/memory",
        initParams = { @WebInitParam(name = "heartBeatPeriod",
                value = "5") },
        asyncSupported = true)
public class SSEServlet extends EventSourceServlet {

    // private final Log log = LogFactory.getLog(SSEServlet.class);

    private AtomicInteger eventsSent = new AtomicInteger(0);

    private long interval = Long.parseLong(System.getenv("emission_interval"));

    private int count = Integer.parseInt(System.getenv("msg_count"));

    public void resetEventsSentCount() {
        this.eventsSent.set(0);
    }

    public int getEventsSent() {
        return eventsSent.get();
    }

    int connectionCount = 1;

    @Override
    protected EventSource newEventSource(HttpServletRequest httpServletRequest) {
        return new EventSource() {

            @Override
            public void onOpen(final Emitter emitter) throws IOException {
                // log.info("SSE Servlet opened");
                int msgCount = count;
                System.out.println("Connections ::" + connectionCount);
                connectionCount++;

                while (msgCount > 0) {
                    //   log.info("Propagating event...");
                    try {
                        Thread.sleep(interval);
                        emitter.data("new server event " + new Date().toString());
                        eventsSent.incrementAndGet();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    msgCount--;
                    //System.out.println("msg count : " + msgCount);
                }
                emitter.close();
            }

            @Override
            public void onClose() {
                //log.info("SSE Servlet closed");

            }
        };
    }
}
