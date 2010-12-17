/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class SettingsForm extends Form
        implements CommandListener
{

   private ContactsBackupMidlet mainMidlet;
   private Settings settings;
   private TextField userName;
   private TextField password;
   private TextField startIndex;
   private TextField maxRecords;
   private Command cmdBack;
   private Command cmdSave;

   // Constructor
   public SettingsForm(String title, ContactsBackupMidlet midlet, Settings settings)
   {
      super(title);

      this.mainMidlet = midlet;
      this.settings = settings;
      userName = new TextField("Google Username:", "", 30, TextField.ANY);
      password = new TextField("Google Password:", "", 30, TextField.PASSWORD);
      startIndex = new TextField("Start from Contact:", "", 4, TextField.NUMERIC);
      maxRecords = new TextField("Max Contacts:", "", 4, TextField.NUMERIC);
      cmdBack = new Command("Back", Command.BACK, 1);
      cmdSave = new Command("Save", Command.OK, 2);
      append(userName);
      append(password);
      append(startIndex);
      append(maxRecords);
      addCommand(cmdBack);
      addCommand(cmdSave);
      setCommandListener(this);
   }

   public void init()
   {
      this.userName.setString(settings.getUsername());
      this.password.setString(settings.getPassword());
      this.startIndex.setString( Integer.toString( settings.getStartIndex()) );
      this.maxRecords.setString( Integer.toString( settings.getMaxRecords()) );
   }

   public void commandAction(Command cmd, Displayable s)
   {
      if (cmd == cmdBack)
      {
         mainMidlet.displayMain();
      }
      else if (cmd == cmdSave)
      {
         try
         {
        	// Validate
            String user = this.userName.getString();
            if (user.indexOf("@gmail") == -1)
               user += "@gmail.com";
            String si = getMinVal( this.startIndex, 1 );
            String mr = getMinVal( this.maxRecords, 1 );

            // Save
            settings.save(user,this.password.getString(),si,mr);
            mainMidlet.alert("Settings saved.");
         }
         catch (Exception e)
         {
            error(e);
         }
      }
   }

   private String getMinVal(TextField tf, int min)
   {
       int i = min;
       try 
       { 
    	   i = Integer.parseInt( tf.getString() ); 
           i = (i<min) ? min : i; 
       }
       catch(Exception e) {}
       return Integer.toString(i);
   }
   
   private void error(Exception e)
   {
      this.mainMidlet.error(e, this);
   }
}
