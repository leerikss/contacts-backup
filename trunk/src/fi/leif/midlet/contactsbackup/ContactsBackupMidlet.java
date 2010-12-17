/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

public class ContactsBackupMidlet extends MIDlet implements CommandListener
{

   private Display display;
   private List frmMain;
   private ThreadForm frmThread;
   private SettingsForm frmSettings;
   private Settings settings;
   private ThreadModelPushContacts modelPushCon;
   private ThreadModelPullContacts modelPullCon;
   private Command cmdExit;
   private Command cmdSelect;
   private int ITM_PUSH_CONTACTS = 0;
   private int ITM_PULL_CONTACTS = 1;
   private int ITM_SETTINGS = 2;

   // Constructor
   public ContactsBackupMidlet()
   {
      // Init settings
      settings = new Settings();

      // Forms/Views
      frmMain = new List("Main Menu", List.IMPLICIT);
      frmThread = new ThreadForm("Contacts Form", this);
      frmSettings = new SettingsForm("Settings Form", this, settings);

      // Models
      modelPushCon = new ThreadModelPushContacts(settings);
      modelPushCon.setForm(frmThread);
      modelPullCon = new ThreadModelPullContacts(settings);
      modelPullCon.setForm(frmThread);

      // Get reference to display object
      display = Display.getDisplay(this);

      // Commands
      cmdExit = new Command("Exit", Command.EXIT, 1);
      cmdSelect = new Command("Select", Command.OK, 2);
      frmMain.addCommand(cmdExit);
      frmMain.addCommand(cmdSelect);
      frmMain.setCommandListener(this);
   }

   // Implementations
   public void startApp()
   {
      frmMain.append("Send Contacts", null);
      frmMain.append("Get Contacts", null);
      frmMain.append("Settings", null);

      // Load settings
      try
      {
         settings.load();
      }
      catch (Exception e)
      {
         error(e);
      }

      // Show settings windows or main menu
      if (settings.getUsername().equals("") || settings.getPassword().equals(""))
      {
         frmSettings.init();
         display.setCurrent(frmSettings);
      }
      else
      {
         displayMain();
      }
   }

   public void pauseApp()
   {
   }

   public void destroyApp(boolean unconditional)
   {
   }

   /*
    * COMMAND EVENTS
    */
   public void commandAction(Command cmd, Displayable disp)
   {
      if (cmd == cmdExit)
      {
         destroyApp(false);
         notifyDestroyed();
      }
      else if (cmd == cmdSelect)
      {
         if (frmMain.getSelectedIndex() == ITM_PUSH_CONTACTS)
         {
            frmThread.setModel(modelPushCon, "Send Contacts");
            display.setCurrent(frmThread);
            if (modelPushCon.isRunning())
               alert("Previous thread still running. Try again later");
            else
            {
               new Thread(modelPushCon).start();
            }
         }
         else if(frmMain.getSelectedIndex() == ITM_PULL_CONTACTS)
         {
            frmThread.setModel(modelPullCon, "Get Contacts");
            display.setCurrent(frmThread);
            if (modelPullCon.isRunning())
               alert("Previous thread still running. Try again later");
            else
            {
               new Thread(modelPullCon).start();
            }
         }
         else if(frmMain.getSelectedIndex() == ITM_SETTINGS)
         {
            frmSettings.init();
            display.setCurrent(frmSettings);
         }

      }
   }

   /*
    * VIEW RELATED METHODS
    */
   public void displayMain()
   {
      display.setCurrent(frmMain);
   }

   public void error(Exception e, Form frm)
   {
      Alert alert = new Alert("Error", e.toString(), null, AlertType.ERROR);
      alert.setTimeout(3000);
      display.setCurrent(alert, frm);
      Log.error(e);
   }

   public void error(Exception e)
   {
      Alert alert = new Alert("Error", e.toString(), null, AlertType.ERROR);
      alert.setTimeout(3000);
      display.setCurrent(alert, frmMain);
      Log.error(e);
   }

   public void alert(String s)
   {
      Alert alert = new Alert("Info", s, null, AlertType.INFO);
      alert.setTimeout(3000);
      display.setCurrent(alert, frmMain);
   }
}
