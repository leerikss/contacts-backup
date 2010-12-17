/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup;

import java.io.Reader;
import java.io.InputStreamReader;

import fi.leif.midlet.contactsbackup.google.GoogleConstants;
import fi.leif.midlet.contactsbackup.google.GoogleHTTP;

import javax.microedition.io.HttpConnection;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMItem;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

/**
 *
 * @author leerikss
 */
public class ThreadModelPullContacts extends ThreadModel
{

   public ThreadModelPullContacts(Settings settings)
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
      ContactList clist = null;
      GoogleHTTP gooHttp = new GoogleHTTP();
      HttpConnection conn = null;
      KXmlParser p = new KXmlParser();
      Contact c = null;
      int i = 0;
      // int all = 0;
      boolean numberSet = false;
      boolean nameSet = false;
      String[] names = null;
      try
      {
         // Get contact list
         clist = (ContactList) PIM.getInstance().openPIMList(
                 PIM.CONTACT_LIST, PIM.READ_WRITE);

         form.info("Login to Google...");

         // Login to google
         String auth = gooHttp.authorize(GoogleConstants.G_LOGIN_URL,
                 this.settings.getUsername(), this.settings.getPassword());

         form.info("Getting contacts from Google...");

         // Retrieve contacts
         conn = gooHttp.sendRequest(GoogleConstants.G_CONTACT_URL +
                 "?max-results=" + this.settings.getMaxRecords() +
                 "&start-index=" + this.settings.getStartIndex(),
                 this.settings.getUsername(), auth,
                 HttpConnection.GET,
                 "application/x-www-form-urlencoded");


         // DEBUG RESPONSE
         /*
         InputStreamReader isr = new InputStreamReader(conn.openInputStream(), "UTF-8");
         int r = isr.read();
         StringBuffer lb = new StringBuffer();
         while(r != -1)
         {
        	 lb.append((char)r);
        	 r = isr.read();
         }
         Log.debug(lb.toString());
         */
         
         // Init KXML
         Reader reader = new InputStreamReader(conn.openInputStream(), "UTF-8");
         p.setInput(reader);
         String fullName = "";

         while (this.isRunning() && p.next() != XmlPullParser.END_DOCUMENT)
         {
            // Start tags
            if (p.getEventType() == XmlPullParser.START_TAG)
            {
            	/*
               // Fetch total nr of items
               if (isNode(p, "openSearch:totalResults"))
               {
                  p.next();
                  //all = Integer.parseInt(p.getText());
               }
               */

               // Init new contact
               if (isNode(p, "entry"))
               {
                  // Init vals
                  c = clist.createContact();
                  numberSet = false;
                  nameSet = false;
                  names = new String[clist.stringArraySize(Contact.NAME)];
               }

               // Name(s)
               if (isNode(p, "gd:fullName"))
               {
                  setItemByText(p, clist, c, Contact.FORMATTED_NAME);
                  nameSet = true;
                  fullName = p.getText();
               }
               if (isNode(p, "gd:givenName"))
               {
                  setName(p, clist, names, Contact.NAME_GIVEN);
                  nameSet = true;
               }
               if (isNode(p, "gd:familyName"))
               {
                  setName(p, clist, names, Contact.NAME_FAMILY);
                  nameSet = true;
               }
               if (isNode(p, "gd:additionalName"))
               {
                  setName(p, clist, names, Contact.NAME_OTHER);
                  nameSet = true;
               }

               // Phone(s)
               if (isNode(p, "gd:phoneNumber"))
               {
                  if (attrMatch(p, "rel", "mobile"))
                  {
                     setItemByText(p, clist, c, Contact.TEL, Contact.ATTR_MOBILE);
                     numberSet = true;
                  }
                  else if (attrMatch(p, "rel", "home"))
                  {
                     setItemByText(p, clist, c, Contact.TEL, Contact.ATTR_HOME);
                     numberSet = true;
                  }
                  else if (attrMatch(p, "rel", "work"))
                  {
                     setItemByText(p, clist, c, Contact.TEL, Contact.ATTR_WORK);
                     numberSet = true;
                  }
                  else if (attrMatch(p, "rel", "other"))
                  {
                     setItemByText(p, clist, c, Contact.TEL, Contact.ATTR_OTHER);
                     numberSet = true;
                  }
               }

               // Organization
               if (isNode(p, "gd:orgName"))
               {
                  setItemByText(p, clist, c, Contact.ORG);
               }

               // Organization title
               if (isNode(p, "gd:orgTitle"))
               {
                  setItemByText(p, clist, c, Contact.TITLE);
               }

               // Email(s)
               if (isNode(p, "gd:email"))
               {
                  if (attrMatch(p, "rel", "home"))
                  {
                     setItemByAttr(p, clist, c, "address", Contact.EMAIL, Contact.ATTR_HOME);
                  }
                  else if (attrMatch(p, "rel", "work"))
                  {
                     setItemByAttr(p, clist, c, "address", Contact.EMAIL, Contact.ATTR_WORK);
                  }
                  else if (attrMatch(p, "rel", "other"))
                  {
                     setItemByAttr(p, clist, c, "address", Contact.EMAIL, Contact.ATTR_OTHER);
                  }
               }

               // Address(es)
               if (isNode(p, "gd:structuredPostalAddress"))
               {
                  if (attrMatch(p, "rel", "home"))
                  {
                     p.next();
                     setItemByText(p, clist, c, Contact.FORMATTED_ADDR, Contact.ATTR_HOME);
                  }
                  else if (attrMatch(p, "rel", "work"))
                  {
                     p.next();
                     setItemByText(p, clist, c, Contact.FORMATTED_ADDR, Contact.ATTR_WORK);
                  }
                  else if (attrMatch(p, "rel", "other"))
                  {
                     p.next();
                     setItemByText(p, clist, c, Contact.FORMATTED_ADDR, Contact.ATTR_OTHER);
                  }
               }

               // Notes
               if (isNode(p, "content"))
               {
                  setItemByText(p, clist, c, Contact.NOTE, Contact.ATTR_OTHER);
               }

               // URL(s)
               if (isNode(p, "gContact:website"))
               {
                  if (attrMatch(p, "rel", "home"))
                  {
                     setItemByAttr(p, clist, c, "href", Contact.UID, Contact.ATTR_HOME);
                  }
                  else if (attrMatch(p, "rel", "work"))
                  {
                     setItemByAttr(p, clist, c, "href", Contact.UID, Contact.ATTR_WORK);
                  }
               }
            }

            // End tags
            if (p.getEventType() == XmlPullParser.END_TAG)
            {
               // Save contact
               if (isNode(p, "entry") && nameSet && numberSet)
               {
                  // Set additional names
                  if (names != null)
                     c.addStringArray(Contact.NAME, PIMItem.ATTR_NONE, names);

             	 // Info
             	 form.info("Saving #"+( settings.getStartIndex()+i )+": "+fullName);
                 i++;
                  
                  // Commit
                  c.commit();

                  // Clear
                  c = null;
                  names = null;
                  numberSet = false;
                  nameSet = false;
               }
            }
         }
         String s = "Successfully added "+i+" Contacts!";
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
         try
         {
            clist.close();
         }
         catch (Exception e)
         {
            form.error(e);
         }
      }
   }

   private boolean isNode(KXmlParser p, String name)
   {
      if (p == null || p.getName() == null || name == null)
      {
         return false;
      }
      return p.getName().equals(name);
   }

   private boolean attrMatch(KXmlParser p, String attrName, String attrMatch)
   {
      if (p == null || attrName == null || attrMatch == null)
      {
         return false;
      }

      for (int i = 0; i < p.getAttributeCount(); i++)
      {
         if (p.getAttributeName(i).equals(attrName))
         {
            String attr = p.getAttributeValue(i);
            return (attr != null && attr.indexOf(attrMatch) != -1);
         }
      }
      return false;
   }

   private String getAttr(KXmlParser p, String attrName)
   {
      if (p == null || attrName == null)
      {
         return null;
      }

      for (int i = 0; i < p.getAttributeCount(); i++)
      {
         if (p.getAttributeName(i).equals(attrName))
         {
            return p.getAttributeValue(i);
         }
      }
      return null;
   }
   
   private void setItemByText(KXmlParser p, ContactList clist,
           Contact c, int field)
           throws Exception
   {
      setItemByText(p, clist, c, field, -1);
   }

   private void setItemByText(KXmlParser p, ContactList clist,
           Contact c, int field, int index)
           throws Exception
   {
      if (clist.isSupportedField(field) )
      {
         p.next();
         index = (index <= 0) ? Contact.ATTR_NONE : index;
         if( p.getText() != null )
        	 c.addString(field, index, p.getText());
      }
   }

   private void setItemByAttr(KXmlParser p, ContactList clist,
           Contact c, String attrName, int field, int index)
           throws Exception
   {
      if (clist.isSupportedField(field) &&
              clist.isSupportedAttribute(field, index))
      {
         String attr = getAttr(p, attrName);
         if(attr != null)
            c.addString(field, index, attr);
      }
   }

   private void setName(KXmlParser p, ContactList clist,
           String[] names, int field)
           throws Exception
   {
      if (clist.isSupportedArrayElement(Contact.NAME, field))
      {
         p.next();
         names[field] = p.getText();
      }
   }
}
