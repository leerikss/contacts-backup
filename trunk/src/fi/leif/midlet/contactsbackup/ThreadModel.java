/*
 * ContactsBackup
 * Author: Leif Eriksson <leif@leif.fi>
 * Copyright (c) 2010, Leif Eriksson
 * Code licensed under the GPLv3 license (http://www.gnu.org/licenses/gpl.html)
 */

package fi.leif.midlet.contactsbackup;

/**
 *
 * @author leerikss
 */
public class ThreadModel
        implements Runnable
{

   protected Settings settings;
   protected boolean running = false;
   protected ThreadForm form;

   public ThreadModel(Settings settings)
   {
      this.settings = settings;
   }

   public void setForm(ThreadForm v)
   {
      this.form = v;
   }

   public void run()
   {
   }

   public void stop()
   {
      this.running = false;
   }

   public boolean isRunning()
   {
      return this.running;
   }
}
