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
import javax.microedition.lcdui.Gauge;

public class ThreadForm extends Form implements CommandListener
{

   private ContactsBackupMidlet mainMidlet;
   private Gauge gauge;
   private Command cmdBack;
   private ThreadModel model;

   // Constructor
   public ThreadForm(String title, ContactsBackupMidlet midlet)
   {
      super(title);
      this.mainMidlet = midlet;
      this.gauge = new Gauge("", false, 25, 1);
      this.cmdBack = new Command("Back", Command.BACK, 1);
      append(gauge);
      addCommand(cmdBack);
      setCommandListener(this);
   }

   public void setModel(ThreadModel m, String label)
   {
      this.model = m;
      this.gauge.setLabel(label);
   }

   public void commandAction(Command c, Displayable s)
   {
      if (c == cmdBack)
      {
         this.model.stop();
         mainMidlet.displayMain();
      }
   }

   public void error(Exception e)
   {
      this.mainMidlet.error(e, this);
   }

   public void info(String s)
   {
      this.gauge.setLabel(s);
   }

   public void alert(String s)
   {
      this.mainMidlet.alert(s);
   }

   public void moveGauge()
   {
      int v = (this.gauge.getValue() == this.gauge.getMaxValue())
              ? 1 : this.gauge.getValue() + 1;
      this.gauge.setValue(v);
   }
}
