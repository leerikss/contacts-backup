/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup.util;

public class StringUtil
{

   public static String replaceEntitys(String data)
   {
	   // TODO: Add more
      return replaceAll(data, "&quot;", "\"");
   }

   
   public static String toEntitys(String data)
   {
	   // TODO: Add more
	   data = replaceAll(data, "&", "_#38;");
	   data = replaceAll(data, "<", "&#60;");
	   data = replaceAll(data, ">", "&#62;");
	   data = replaceAll(data, "_#38;", "&#38;");
	   return data;
   }
   
   public static String replaceAll(String data, String replace, String with)
   {
	  if(data == null)
		  return null;
      while (data.indexOf(replace) > -1)
      {
         int from = data.indexOf(replace);
         data = data.substring(0, from) + with + data.substring(from + replace.length());
      }
      return data;
   }

   public static String[] split(String str, String ch)
   {
      java.util.Vector v = new java.util.Vector();
      while (str.indexOf(ch) != -1)
      {
         String tmp = str.substring(0, str.indexOf(ch)).trim();
         if (tmp.length() > 0)
         {
            v.addElement(tmp);
         }
         str = str.substring(str.indexOf(ch) + 1, str.length());
      }
      String[] returned = new String[v.size()];
      for (int i = 0; i < v.size(); i++)
      {
         returned[i] = (String) v.elementAt(i);
      }
      return returned;
   }

   public static String getTagValue(String data, String tagName)
   {
      String start = new StringBuffer("<").append(tagName).append(">").toString();
      String end = new StringBuffer("</").append(tagName).append(">").toString();
      if (data.indexOf(start) == -1 && data.indexOf(end) == -1)
      {
         return data;
      }
      int from = data.indexOf(start) + start.length();
      int to = data.indexOf(end);
      return data.substring(from, to);
   }

   public static boolean isEmpty(String s)
   {
      return (s == null || s.equals(""));
   }

   public static String capitalize(String s)
   {
      return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
   }
}
