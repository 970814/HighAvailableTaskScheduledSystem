package dtss.simpleui.test;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class Test {
    public static MouseInputListener getMouseInputListener(final JTable jTable) {
        return new MouseInputListener() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("?");
                processEvent(e);
            }

            /***
             * //in order to trigger Left-click the event
             */
            public void mousePressed(MouseEvent e) {
                System.out.println("?");
                processEvent(e);// is necessary!!!
            }

            public void mouseReleased(MouseEvent e) {
                // processEvent(e);
                System.out.println(e);
                System.out.println("?");

                if (e.getButton() == MouseEvent.BUTTON3) {// right click

                    JPopupMenu popupmenu = new JPopupMenu();
                    JMenuItem runM = new JMenuItem("ACTION_COMMAND_RUN");
                    JMenuItem copyParameterM = new JMenuItem("ACTION_COMMAND_COPY_REQUEST_PARAMETER");
                    JMenuItem copyResponseM = new JMenuItem("ACTION_COMMAND_COPY_RESPONSE");
//					JMenuItem encodingM = new JMenuItem(ACTION_COMMAND_ENCODING);
                    // JMenuItem editM=new JMenuItem("edit");
                    MyMenuActionListener yMenuActionListener = new MyMenuActionListener();
                    runM.addActionListener(yMenuActionListener);
                    copyParameterM.addActionListener(yMenuActionListener);
                    copyResponseM.addActionListener(yMenuActionListener);
//					encodingM.addActionListener(yMenuActionListener);
                    popupmenu.add(runM);
                    popupmenu.add(copyParameterM);
                    popupmenu.add(copyResponseM);
//					popupmenu.add(encodingM);
                    popupmenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            public void mouseEntered(MouseEvent e) {
                processEvent(e);
            }

            public void mouseExited(MouseEvent e) {
                processEvent(e);
            }

            public void mouseDragged(MouseEvent e) {
                processEvent(e);
            }

            public void mouseMoved(MouseEvent e) {
                processEvent(e);
            }

            private void processEvent(MouseEvent e) {
                // Right-click on
                if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                    // System.out.println(e.getModifiers());
                    // System.out.println("Right-click on");
                    int modifiers = e.getModifiers();
                    modifiers -= MouseEvent.BUTTON3_MASK;
                    modifiers |= MouseEvent.BUTTON1_MASK;
                    MouseEvent ne = new MouseEvent(e.getComponent(), e.getID(),
                            e.getWhen(), modifiers, e.getX(), e.getY(),
                            e.getClickCount(), false);
                    jTable.dispatchEvent(ne);// in order to trigger Left-click
                    // the event
                }
            }
        };
    }

    static class MyMenuActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
			System.out.println(command);



        }

    }
}
