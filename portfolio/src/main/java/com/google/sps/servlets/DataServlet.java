
package com.google.sps.servlets;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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
  public ArrayList<String> com = new ArrayList<String>();

//   @Override
//   public void init() {
//     com = new ArrayList<String>(); 
//     com.add(
//         "Those who think they can change the world are the ones who do. - ");
//     com.add("They told me computers could only do arithmetic. - ");
//     com.add("A ship in port is safe, but thats not what ships are built for. - ");
//     console.log("ArrayList : " + com); 
//   }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // String json = new Gson().toJson(com);
    //  // Convert the server stats to JSON
    // System.out.println(json); //cloud shell
    // // Send the JSON as the response
    // response.setContentType("application/json;");
    // response.getWriter().println(json); //web page
    // // console.log(json);
    Query query = new Query("Comment");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
    //   long id = entity.getKey().getId();
        String comment = (String) entity.getProperty("comment");
        comments.add(comment);
    }
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
   
    String comment = getParameter(request, "comment", "");
    com.add(comment);
    
    //datastore
    Entity comEntity = new Entity("Comment");
    comEntity.setProperty("comment", comment);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(comEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }
    /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
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

