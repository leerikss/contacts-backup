/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup;

import java.util.Enumeration;

import javax.microedition.io.HttpConnection;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;

import fi.leif.midlet.contactsbackup.google.GoogleConstants;
import fi.leif.midlet.contactsbackup.google.GoogleHTTP;
import fi.leif.midlet.contactsbackup.google.GoogleXML;

public class ThreadModelPushContacts extends ThreadModel
{

   public ThreadModelPushContacts(Settings settings)
   {
      super(settings);
   }

   public void run()
   {
      if (this.running)
      {
         form.error(new Exception("Previous thread is still running!"));
      }

      this.running = true;
      ContactList contacts = null;
      GoogleHTTP gooHttp = new GoogleHTTP();
      GoogleXML gooXml = new GoogleXML();

      try
      {
         form.info("Accessing Contacts...");

         // Get contact list
         contacts = (ContactList) PIM.getInstance().openPIMList(
                 PIM.CONTACT_LIST, PIM.READ_ONLY);
         Enumeration e = contacts.items();

         form.info("Login to Google...");

         // Login to google
         String auth = gooHttp.authorize(GoogleConstants.G_LOGIN_URL,
                 this.settings.getUsername(), this.settings.getPassword());

         // Iterate contacts & send them to Google
         int i = 1;
         int all = 0;
         while (this.running == true && e.hasMoreElements())
         {
	        Contact contact = (Contact) e.nextElement();

        	// Only push records between set limits
        	if( i < settings.getStartIndex() )
        	{
        		i++;
        		continue;
        	}
        	else if( i >= ( settings.getStartIndex() + settings.getMaxRecords() ) )
        		break;

	        this.form.moveGauge();
        	String fullName = gooXml.getItem(contacts, contact, Contact.FORMATTED_NAME);
        	form.info("Sending #"+i+": "+fullName);
        	
            // Get XML
            String xml = gooXml.getNewContactXML(contacts, contact);

            // Send new contact to google
            gooHttp.makeRequest(GoogleConstants.G_CONTACT_URL,
                    this.settings.getUsername(), auth,
                    HttpConnection.POST, "application/atom+xml", null, xml);
            
            i++;
            all++;
         }
         String s = "Successfully sent "+all+" Contacts!";
         form.info(s);
         form.alert(s);
      }
      catch (Exception e)
      {
         form.error(e);
      }
      finally
      {
         this.running = false;

         try
         {
            contacts.close();
         }
         catch (Exception e)
         {
            form.error(e);
         }
      }
   }
}
