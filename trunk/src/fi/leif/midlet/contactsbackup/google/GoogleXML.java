/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup.google;

import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;

import fi.leif.midlet.contactsbackup.util.StringUtil;

public class GoogleXML
{

   public GoogleXML()
   {
   }

   public String getNewContactXML(ContactList contacts, Contact contact)
   {
      StringBuffer sb = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?>");

      sb.append("<atom:entry xmlns:atom='http://www.w3.org/2005/Atom' xmlns:gd='http://schemas.google.com/g/2005'>");
      sb.append("<atom:category scheme='http://schemas.google.com/g/2005#kind' term='http://schemas.google.com/contact/2008#contact' />");

      // Name
      sb.append("<gd:name>");
      String givenName = getItem(contacts, contact, Contact.NAME_GIVEN, 0, true);
      String familyName = getItem(contacts, contact, Contact.NAME_FAMILY, 0, true);
      String fullName = getItem(contacts, contact, Contact.FORMATTED_NAME);
      if (!StringUtil.isEmpty(givenName))
      {
         sb.append("<gd:givenName>").append(givenName).append("</gd:givenName>");
      }
      if (!StringUtil.isEmpty(familyName))
      {
         sb.append("<gd:familyName>").append(familyName).append("</gd:familyName>");
      }
      if (!StringUtil.isEmpty(fullName))
      {
         sb.append("<gd:fullName>").append(fullName).append("</gd:fullName>");
      }
      sb.append("</gd:name>");

      // Phone(s)
      int phones = contact.countValues(Contact.TEL);
      for (int i = 0; i < phones; i++)
      {
         String phone = getItem(contacts, contact, Contact.TEL, i, false);
         if (!StringUtil.isEmpty(phone))
         {
            String where = getWhere( contact.getAttributes(Contact.TEL, i) );
            sb.append("<gd:phoneNumber rel='http://schemas.google.com/g/2005#").append(where).
                    append("'>").append(phone).append("</gd:phoneNumber>");
         }
      }

      // Organization & title
      String org = getItem(contacts, contact, Contact.ORG);
      String title = getItem(contacts, contact, Contact.TITLE);
      if (!StringUtil.isEmpty(org) && !StringUtil.isEmpty(title))
      {
         sb.append("<gd:organization rel='http://schemas.google.com/g/2005#work'>");
         sb.append("<gd:orgName>").append(org).append("</gd:orgName>");
         sb.append("<gd:orgTitle>").append(title).append("</gd:orgTitle>");
         sb.append("</gd:organization>");
      }

      // Email(s)
      int emails = contact.countValues(Contact.EMAIL);
      for (int i = 0; i < emails; i++)
      {
         String email = getItem(contacts, contact, Contact.EMAIL, i, false);
         if (!StringUtil.isEmpty(email))
         {
            String where = getWhere( contact.getAttributes(Contact.EMAIL, i) );
            sb.append("<gd:email rel='http://schemas.google.com/g/2005#").
                    append(where).append("' address='").append(email).append("'");
            if (!StringUtil.isEmpty(fullName))
            {
               sb.append(" displayName='").append(fullName).append("'");
            }
            sb.append(" />");
         }
      }

      // Address(es)
      int addresses = contact.countValues(Contact.ADDR);
      for (int i = 0; i < addresses; i++)
      {
         sb.append(getAddress(contacts, contact, i));
      }

      // Note
      String note = getItem(contacts, contact, Contact.NOTE);
      if (!StringUtil.isEmpty(note))
      {
         sb.append("<atom:content type='text'>").append(note).append("</atom:content>");
      }

      // URL(s)
      int urls = contact.countValues(Contact.URL);
      for (int i = 0; i < urls; i++)
      {
         String url = getItem(contacts, contact, Contact.URL, i, false);
         if (!StringUtil.isEmpty(url))
         {
            String where = getWhere( contact.getAttributes(Contact.URL, i) );
            sb.append("<gContact:website href='").append(url).
                    append("' rel='").append(where).append("'/>");
         }
      }

      sb.append("</atom:entry>");

      return sb.toString();
   }

   public String getAddress(ContactList contacts, Contact contact, int index)
   {
      StringBuffer sb = new StringBuffer();
      String where = getWhere( contact.getAttributes(Contact.ADDR, index) );
      sb.append("<gd:structuredPostalAddress rel='http://schemas.google.com/g/2005#").
              append(where).append("'>");

      String country = getItem(contacts, contact, Contact.ADDR_COUNTRY, index, true);
      if (!StringUtil.isEmpty(country))
      {
         sb.append("<gd:country>").append(country).append("</gd:country>");
      }
      String region = getItem(contacts, contact, Contact.ADDR_REGION, index, true);
      if (!StringUtil.isEmpty(region))
      {
         sb.append("<gd:region>").append(region).append("</gd:region>");
      }
      String street = getItem(contacts, contact, Contact.ADDR_STREET, index, false);
      if (!StringUtil.isEmpty(street))
      {
         sb.append("<gd:street>").append(street).append("</gd:street>");
      }
      String pCode = getItem(contacts, contact, Contact.ADDR_POSTALCODE, index, false);
      if (!StringUtil.isEmpty(pCode))
      {
         sb.append("<gd:postcode>").append(pCode).append("</gd:postcode>");
      }
      /* TODO:
      <gd:city>...</gd:city>
       */

      String fAddress = getItem(contacts, contact, Contact.FORMATTED_ADDR, index, false);
      if (StringUtil.isEmpty(fAddress))
      {
         fAddress = getItem(contacts, contact, Contact.ADDR, index, false);
      }
      if (!StringUtil.isEmpty(fAddress))
      {
         sb.append("<gd:formattedAddress>").append(fAddress).append("</gd:formattedAddress>");
      }

      sb.append("</gd:structuredPostalAddress>");

      return sb.toString();
   }

   public String getItem(ContactList contacts, Contact contact, int field)
   {
      return getItem(contacts, contact, field, Contact.ATTR_NONE, false);
   }

   public String getItem(ContactList contacts, Contact contact, int field,
           int index, boolean capFirstLet)
   {
      String item = null;
      if (contacts.isSupportedField(field) )
      {
         try
         {
            item = contact.getString(field, index);
            if (item.equals(""))
            {
               return null;
            }
            if (capFirstLet)
            {
               item = StringUtil.capitalize(item);
            }
         }
         catch (Exception e)
         {
            return null;
         }
      }
      return item;
   }

   private String getWhere(int attr)
   {
      return ((attr & Contact.ATTR_WORK) != 0) ? "work"
              : ((attr & Contact.ATTR_OTHER) != 0) ? "other"
              : ((attr & Contact.ATTR_MOBILE) != 0) ? "mobile"
              : "home";
   }
}
