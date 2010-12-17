/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup.google;

import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.HttpsConnection;

import fi.leif.midlet.contactsbackup.Log;
import fi.leif.midlet.contactsbackup.util.StringUtil;
import fi.leif.midlet.contactsbackup.util.URLUtil;

public class GoogleHTTP
{

   public GoogleHTTP()
   {
   }

   // Login to google account & return authorization key
   public String authorize(String url, String username, String pass)
           throws Exception
   {
      HttpConnection conn = null;
      InputStream is = null;
      OutputStream os = null;
      String auth = null;

      try
      {
         // Open connection
         conn = (HttpsConnection) Connector.open(url);
         conn.setRequestMethod(HttpsConnection.POST);

         // Set parameters
         StringBuffer params = new StringBuffer();
         URLUtil.addParam(params, "accountType", "GOOGLE");
         URLUtil.addParam(params, "Email", username);
         URLUtil.addParam(params, "Passwd", pass);
         URLUtil.addParam(params, "service", "cp");
         URLUtil.addParam(params, "source", GoogleConstants.G_APP_SRC);

         // Set headers
         conn.setRequestProperty("Content-Type",
                 "application/x-www-form-urlencoded");
         conn.setRequestProperty("Content-Length",
                 String.valueOf(params.length()));

         // Send data
         os = conn.openOutputStream();
         os.write(params.toString().getBytes());

         // Retrieve response
         StringBuffer response = new StringBuffer();
         is = conn.openDataInputStream();
         int chr;
         while ((chr = is.read()) != -1)
         {
            response.append((char) chr);
         }

         // Error happened
         if (conn.getResponseCode() >= 400)
         {
            throw new Exception(conn.getResponseCode() + ": "
                    + conn.getResponseMessage() + "\n" + response.toString());
         }

         // Parse auth key
         String resp = response.toString();
         int key = resp.indexOf("Auth=");

         if (key == -1)
         {
            throw new Exception("Unable to retrieve Auth key!");
         }

         auth = resp.substring(key + 5).trim();
      }
      catch (Exception e)
      {
         throw e;
      }
      finally
      {
         if (is != null)
         {
            try
            {
               is.close();
            }
            catch (Exception e)
            {
               Log.error(e);
            }
         }
         if (os != null)
         {
            try
            {
               os.close();
            }
            catch (Exception e)
            {
               Log.error(e);
            }
         }
         if (conn != null)
         {
            try
            {
               conn.close();
            }
            catch (Exception e)
            {
               Log.error(e);
            }
         }
      }
      return auth;
   }

   public String makeRequest(String url, String user, String auth,
           String sendMethod, String contentType)
           throws Exception
   {
      return makeRequest(url,user,auth,sendMethod,contentType,null,null);
   }

   public String makeRequest(String url, String user, String auth,
           String sendMethod, String contentType, String charset, String data)
           throws Exception
   {
      HttpConnection conn = null;
      InputStream is = null;

      try
      {
         conn = sendRequest(url, user, auth, sendMethod, contentType, charset, data);

         // Retrieve data
         StringBuffer response = new StringBuffer();
         is = conn.openDataInputStream();
         int chr;
         while ((chr = is.read()) != -1)
         {
            response.append((char) chr);
         }

         // Error happened
         if (conn.getResponseCode() >= 400)
         {
            throw new Exception(conn.getResponseCode() + ": "
                    + conn.getResponseMessage() + "\n" + response.toString());
         }

         return StringUtil.replaceEntitys(response.toString());
      }
      catch (Exception e)
      {
         throw e;
      }
      finally
      {
         if (is != null)
         {
            try
            {
               is.close();
            }
            catch (Exception e)
            {
               Log.error(e);
            }
         }
         if (conn != null)
         {
            try
            {
               conn.close();
            }
            catch (Exception e)
            {
               Log.error(e);
            }
         }
      }
   }

   public HttpConnection sendRequest(String url, String user, String auth,
           String sendMethod, String contentType)
           throws Exception
   {
      return sendRequest(url,user,auth,sendMethod,contentType,null,null);
   }

   public HttpConnection sendRequest(String url, String user, String auth,
           String sendMethod, String contentType, String charset, String data)
           throws Exception
   {
      OutputStream os = null;

      try
      {
         // Get connection
         HttpConnection conn = (HttpConnection) Connector.open(
                 StringUtil.replaceAll(url, "{EMAIL}", user));
         conn.setRequestMethod(sendMethod);

         // Set necessary headers
         conn.setRequestProperty("Authorization", "GoogleLogin auth=" + auth);
         
         contentType += (charset == null) ? ";charset="
                 + System.getProperty("microedition.encoding") : charset;
         conn.setRequestProperty("Content-Type", contentType);
         if (data != null)
         {
            conn.setRequestProperty("Content-Length",
                    Integer.toString(data.length()));
         }
         conn.setRequestProperty("GData-Version", "3.0");

         // Send data
         if (data != null)
         {
            os = conn.openOutputStream();
            os.write(data.getBytes());
         }

         return conn;
      }
      catch (Exception e)
      {
         throw e;
      }
      finally
      {
         if (os != null)
         {
            try
            {
               os.close();
            }
            catch (Exception e)
            {
               Log.error(e);
            }
         }
      }
   }
}
