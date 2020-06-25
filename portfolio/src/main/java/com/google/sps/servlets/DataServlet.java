
package com.google.sps.servlets;

import java.util.*;  
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  public ArrayList<String> com;

  @Override
  public void init() {
    com = new ArrayList<String>(); 
    com.add(
        "Those who think they can change the world are the ones who do. - ");
    com.add("They told me computers could only do arithmetic. - ");
    com.add("A ship in port is safe, but thats not what ships are built for. - ");
    // console.log("ArrayList : " + com); 
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = new Gson().toJson(com);
     // Convert the server stats to JSON
    System.out.println(json); //cloud shell
    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json); //web page
    // console.log(json);
  }
  /**
   * Converts a ServerStats instance into a JSON string using the Gson library. Note: We first added
   * the Gson library dependency to pom.xml.
   */
//   private String convertToJsonUsingGson(Comments comments) {
//     Gson gson = new Gson();
//     String json = gson.toJson(comments);
//     return json;
//   }
}

