/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup;

import fi.leif.midlet.contactsbackup.util.StringUtil;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class Settings
{

   private String username = "";
   private String password = "";
   private int start_index = 1;
   private int max_records = 9999; // Integer.MAX_VALUE;

   public Settings()
   {
   }

   public void load() throws RecordStoreException
   {
      RecordStore rs = null;
      RecordEnumeration re = null;

      try
      {
         rs = RecordStore.openRecordStore(Constants.RS_NAME, true);
         re = rs.enumerateRecords(null, null, false);
         while (re.hasNextElement())
         {
            byte[] raw = re.nextRecord();
            String[] set = StringUtil.split( new String(raw),"|" );
            if(set.length>=1)
               this.username = set[0];
            if(set.length>=2)
               this.password = set[1];
            if(set.length>=3)
               this.start_index = Integer.parseInt( set[2] );
            if(set.length>=4)
               this.max_records = Integer.parseInt( set[3] );
         }
      }
      finally
      {
         if (re != null)
         {
            re.destroy();
         }
         if (rs != null)
         {
            rs.closeRecordStore();
         }
      }
   }

   public void save(String user, String pass,
           String startIndex, String maxResults) throws RecordStoreException
   {
      RecordStore rs = null;
      RecordEnumeration re = null;
      try
      {
         // Delete old recs
         rs = RecordStore.openRecordStore(Constants.RS_NAME, true);
         re = rs.enumerateRecords(null, null, false);
         while (re.hasNextElement())
         {
            int id = re.nextRecordId();
            rs.deleteRecord(id);
         }

         // Save new recs
         String settings =
                 user + "|" +
                 pass + "|" +
                 startIndex + "|" +
                 maxResults + "|";
         byte[] raw = settings.getBytes();
         rs.addRecord(raw, 0, raw.length);

         // Cache values
         this.username = user;
         this.password = pass;
         this.start_index = Integer.parseInt( startIndex );
         this.max_records = Integer.parseInt( maxResults );
      }
      finally
      {
         if (re != null)
         {
            re.destroy();
         }
         if (rs != null)
         {
            rs.closeRecordStore();
         }
      }
   }

   public String getUsername()
   {
      return this.username;
   }

   public String getPassword()
   {
      return this.password;
   }

   public int getStartIndex()
   {
      return this.start_index;
   }

   public int getMaxRecords()
   {
      return this.max_records;
   }

}
