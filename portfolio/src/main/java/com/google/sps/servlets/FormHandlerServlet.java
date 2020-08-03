// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.servlets;

import com.google.sps.data.CommentInfo;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;  
import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the user submits the form, Blobstore processes the file upload and then forwards the request
 * to this servlet. This servlet can then process the request using the file URL we get from
 * Blobstore.
 */
@WebServlet("/formhandler")
public class FormHandlerServlet extends HttpServlet {
    //milleseconds since epoc
    //timestamps System.currentTimemillis
    //javasortby timestamp
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
  
    Query query = new Query("CommentInfo").addSort("timestampL", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    
    List<CommentInfo> CommentInfoList = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
        long timestampL = (long) entity.getProperty("timestampL");
        String timestampS = (String) entity.getProperty("timestampS");
        String comment = (String) entity.getProperty("comment");
        String imageurl = (String) entity.getProperty("imgurl");
       
        CommentInfo commentinfo = new CommentInfo(timestampL, timestampS, comment, imageurl);

        CommentInfoList.add(commentinfo);
    }
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(CommentInfoList));//too json list ds containing class
  }

    
    

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the message entered by the user.
    String comment = request.getParameter("comment");
    // Get the URL of the image that the user uploaded to Blobstore.
    String imageurl = getUploadedFileUrl(request, "image");
    //get time
    long timestampL = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");    
    Date resultdate = new Date(timestampL);
    String timestampS = sdf.format(resultdate);

    CommentInfo commentinfo = new CommentInfo(timestampL, timestampS, comment, imageurl);
 
    //datastore
    Entity comEntity = new Entity("CommentInfo");
    comEntity.setProperty("timestampL", commentinfo.timestampL);
    comEntity.setProperty("timestampS", commentinfo.timestampS);
    comEntity.setProperty("comment", commentinfo.comment);
    comEntity.setProperty("imgurl", commentinfo.imageurl);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(comEntity);

    // Output some HTML that shows the data the user entered.
    // A real codebase would probably store these in Datastore.
    // PrintWriter out = response.getWriter();
    // out.println("<p>Here's the image you uploaded:</p>");
    // out.println("<a href=\"" + imageurl + "\">");
    // out.println("<img src=\"" + imageurl + "\" />");
    // out.println("</a>");
    // out.println("<p>Here's the text you entered:</p>");
    // out.println(comment);
    // out.println("<p>Here's the date:</p>");
    // out.println("Converted Date: " + strDate);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.jsp");
     
  }

  /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> img_blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (img_blobKeys == null || img_blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = img_blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    String url = imagesService.getServingUrl(options);

    // GCS's localhost preview is not actually on localhost,
    // so make the URL relative to the current domain.
    if(url.startsWith("http://localhost:8080/")){
      url = url.replace("http://localhost:8080/", "/");
    }
    return url;
  }
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}


