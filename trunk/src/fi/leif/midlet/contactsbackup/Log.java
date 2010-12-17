/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup;

public class Log
{

   public static void debug(String s)
   {
      System.out.println("DEBUG: " + s);
   }

   public static void error(Exception e)
   {
      System.out.println("ERROR: " + e.getMessage());
      e.printStackTrace();
   }
}
