package org.apache.struts2.json;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.result.Redirectable;
import org.apache.struts2.result.StrutsResultSupport;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Simpler form of {@link JSONActionRedirectResult} which takes care of
 * passing JSON redirect to a client. So this result produces a JSON response
 * as below and JS code must handle it in proper way.
 *
 * <p>Response JSON looks like this:
 *     <pre>{"location": "%{location}"}</pre>
 * </p>
 *
 * @since Struts 2.5.9
 */
public class JSONRedirectResult extends StrutsResultSupport implements Redirectable {

    private static final Logger LOG = LogManager.getLogger(JSONRedirectResult.class);

    private String statusExpression;
    private int status = HttpServletResponse.SC_OK;

    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();

        if (statusExpression != null) {
            String parsedStatus = conditionalParse(statusExpression, invocation);
            try {
                status = Integer.valueOf(parsedStatus);
            } catch(NumberFormatException e) {
                LOG.warn(new ParameterizedMessage("Wrong status code expression was passed [{}]", statusExpression), e);
            }
        }

        response.setStatus(status);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write("{\"location\": \"");
        writer.write(finalLocation);
        writer.write("\"}");
        writer.close();
    }

    public void setStatusExpression(String statusExpression) {
        this.statusExpression = statusExpression;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
