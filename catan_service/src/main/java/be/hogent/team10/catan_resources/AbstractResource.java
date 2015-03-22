/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan_resources;

import be.hogent.team10.exceptions.GeneralException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author Joachim
 */
public abstract class AbstractResource {
    
    /**
     * This method is used to handle specific error messages for the website when a propper message is needed.
     * @param ex
     * @param request
     * @param response
     * @return A string containing the error message.
     * @throws IOException
     */
    @ExceptionHandler(GeneralException.class)
    public @ResponseBody
    String handleUncaughtException(GeneralException ex, WebRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        response.setHeader("ERROR_MESSAGE", ex.getMessage());
        return ex.getMessage();
    }
}
