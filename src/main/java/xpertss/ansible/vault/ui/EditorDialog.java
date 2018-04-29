/*
 * Copyright 2018 XpertSoftware
 *
 * Created By: cfloersch
 * Date: 4/28/2018
 */
package xpertss.ansible.vault.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class EditorDialog extends JDialog {

   private JTextArea textArea = new JTextArea();
   private int disposedBy;

   public EditorDialog(String text)
   {
      super((JFrame)null, "Plaintext Content", true);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setFocusTraversalPolicy(new AppFocusTraversalPolicy());

      Container contentPane = getContentPane();

      contentPane.setLayout(new BorderLayout());
      JScrollPane scroll = new JScrollPane(textArea);
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      contentPane.add(scroll, BorderLayout.CENTER);

      JPanel commands = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      commands.add(createActionComponent(new CancelAction()));
      commands.add(createActionComponent(new OkAction()));
      contentPane.add(commands, BorderLayout.SOUTH);

      textArea.setBorder(BorderFactory.createEmptyBorder(10,10,0,0));
      if(text != null) textArea.setText(text);
   }







   public int display()
   {
      Rectangle screen = getDefaultBounds();
      Dimension frame = computeSize(screen);

      int x = (int) ((screen.getWidth() - frame.getWidth()) / 2);
      int y = (int) ((screen.getHeight() - frame.getHeight()) / 2);

      setLocation(x,y);
      setSize(frame);
      setVisible(true);
      return disposedBy;
   }


   public void setText(String text)
   {
      textArea.setText(text);
   }

   public String getText()
   {
      return textArea.getText();
   }



   protected void processWindowEvent(WindowEvent e)
   {
      if(e.getID() == WindowEvent.WINDOW_CLOSING) {
         disposedBy = JOptionPane.CLOSED_OPTION;
         dispose();
      }
   }




   protected Dimension computeSize(Rectangle bounds)
   {
      return new Dimension((int)(bounds.width * .7), (int) (bounds.height * .7));
   }

   protected Rectangle getDefaultBounds()
   {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice def = ge.getDefaultScreenDevice();
      GraphicsConfiguration conf = def.getDefaultConfiguration();
      return conf.getBounds();
   }

   protected JButton createActionComponent(Action a)
   {
      JButton b = new JButton(a);
      KeyStroke ks = (a==null) ? null : (KeyStroke)a.getValue(Action.ACCELERATOR_KEY);
      if(ks != null) {
         String name = String.format("%s-%s", a.getClass().getName(), ks.getKeyChar());
         b.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, name);
         b.getActionMap().put(name, a);
      }
      return b;
   }






   private class AppFocusTraversalPolicy extends LayoutFocusTraversalPolicy {

      public Component getDefaultComponent(Container focusCycleRoot)
      {
         return textArea;
      }
   }




   private class OkAction extends AbstractAction {

      public OkAction()
      {
         super("Ok");
         putValue(Action.SHORT_DESCRIPTION, "Complete Editing");
         putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         disposedBy = JOptionPane.OK_OPTION;
         dispose();
      }

   }

   private class CancelAction extends AbstractAction {

      public CancelAction()
      {
         super("Cancel");
         putValue(Action.SHORT_DESCRIPTION, "Cancel Editing");
         putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
      }

      @Override
      public void actionPerformed(ActionEvent e)
      {
         disposedBy = JOptionPane.CANCEL_OPTION;
         dispose();
      }
   }
}
