//package src;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.*;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;
/*import jdk.nashorn.internal.scripts.JO;
import org.omg.CORBA.BAD_INV_ORDER;*/

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Proof of concept of how to handle WebCam video stream from Java
 *
 * @author Mohamed H. Guelleh (Astrophysics-Guy)
 * @author Bartosz Firyn (SarXos)
 */
public class WebCam extends JFrame implements Runnable, WebcamListener, WindowListener, UncaughtExceptionHandler, ItemListener, WebcamDiscoveryListener {
    private static final long serialVersionUID = 1L;

    private Webcam webcam = null;
    private WebcamPanel webcamPanel = null;
    private WebcamPicker webcamPicker = null;

    private JLabel jLabelMaxPics = null;

    private JFormattedTextField jFormattedTextField = null;
    //private JComboBox jComboBox1 = null, jComboBox2 = null, jComboBox3 = null;
    
    private int intMaxPics = 10;

    private final String nomApp = "Free Hands"; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private final Path path = Paths.get(System.getProperty("user.home") + System.getProperty("file.separator") + nomApp);

    @Override
    public void run() {
        switch (System.getProperty("file.separator")) { // Check file system
            case "/":
                System.out.println("File system is Linux");
                break;
            case "\\":
                System.out.println("File system is Windows");
                break;
            default:
                System.out.println("Don't know file system");
                break;
        }

        if (!(Files.isDirectory(path))) { // Create main dir
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*long start = System.nanoTime();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // Sanity check - Check if path is a folder
                    if (!(Files.isDirectory(path))) {

                    }
                }
            }
        });
        thread.setName("Watching directory Free Hands");
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(this);
        thread.start();
        long end = System.nanoTime();
        System.out.println((end-start)/1000000);*/

        Webcam.addDiscoveryListener(this);

        //setTitle("Java WebCam Capture POC");
        setTitle("Free Hands");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        addWindowListener(this);

        JPanel jPanelBorderSouth = new JPanel();
        jPanelBorderSouth.setLayout(new BorderLayout());

        JPanel jPanelMaxPics = new JPanel();
        jPanelMaxPics.setLayout(new BorderLayout());

        JPanel jPanelTakePics = new JPanel();

        webcamPicker = new WebcamPicker();
        webcamPicker.addItemListener(this);

        webcam = webcamPicker.getSelectedWebcam();

        if (webcam == null) { // Affiche un message d'erreur s'il ne trouve pas des WebCams
            System.out.println("No WebCams found...");
            JOptionPane.showMessageDialog(null, "No folders found", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.addWebcamListener(WebCam.this);

        webcamPanel = new WebcamPanel(webcam, false);
        webcamPanel.setFPSDisplayed(true);

        JButton btnTakePics = new JButton("Take pics");
        btnTakePics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listenerBtnTakePics();
            }
        });
        /*btnTakePics.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ButtonModel btnM = (ButtonModel) e.getSource();
                if (btnM.isRollover()) JOptionPane.showMessageDialog(null,"500 by default");
            }
        });*/

        JButton btnErase = new JButton("Erase Folders");
        btnErase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listenerBtnErase();
            }
        });
        
        jLabelMaxPics = new JLabel("Max number for training pics (" + intMaxPics + "): ");

        jFormattedTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        jFormattedTextField.setPreferredSize(new Dimension(45, 20));

        /*String[] i = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        jComboBox1 = new JComboBox(i);
        jComboBox1.setSelectedIndex(0);
        //jComboBox1.setEditable(true);
        /*jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String s = (String) ((JComboBox)actionEvent.getSource()).getSelectedItem();
                updateLabel(s);
            }
        });

        jComboBox2 = new JComboBox(i);


        jComboBox3 = new JComboBox(i);*/


        JButton btnSubmit = new JButton("Submit number");
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listenerBtnSubmit();
            }
        });

        add(webcamPicker, BorderLayout.NORTH);
        add(webcamPanel, BorderLayout.CENTER);
        add(jPanelBorderSouth, BorderLayout.SOUTH);

        jPanelBorderSouth.add(jPanelMaxPics, BorderLayout.WEST);
        jPanelBorderSouth.add(jPanelTakePics, BorderLayout.EAST);

        jPanelTakePics.add(btnErase);
        jPanelTakePics.add(btnTakePics);
        jPanelTakePics.setLayout(new FlowLayout(FlowLayout.RIGHT));

        jPanelMaxPics.add(jLabelMaxPics);
        jPanelMaxPics.add(jFormattedTextField);
        /*jPanelMaxPics.add(jComboBox1);
        jPanelMaxPics.add(jComboBox2);
        jPanelMaxPics.add(jComboBox3);*/
        jPanelMaxPics.add(btnSubmit);
        jPanelMaxPics.setLayout(new FlowLayout(FlowLayout.LEFT));

        pack();
        setVisible(true);

        Thread t = new Thread() {

            @Override
            public void run() {
                webcamPanel.start();
            }
        };
        t.setName("example-starter");
        t.setDaemon(true);
        t.setUncaughtExceptionHandler(this);
        t.start();
    }

    public static void main(String[] args) {
        //System.getProperties().list(System.out);
        SwingUtilities.invokeLater(new WebCam());
    }

    @Override
    public void webcamOpen(WebcamEvent we) {
        System.out.println("WebCam open");
    }

    @Override
    public void webcamClosed(WebcamEvent we) {
        System.out.println("WebCam closed");
    }

    @Override
    public void webcamDisposed(WebcamEvent we) {
        System.out.println("WebCam disposed");
    }

    @Override
    public void webcamImageObtained(WebcamEvent we) {
        // do nothing
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        webcam.close();
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("WebCam viewer resumed");
        webcamPanel.resume();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("WebCam viewer paused");
        webcamPanel.pause();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.err.println(String.format("Exception in thread %s", t.getName()));
        e.printStackTrace();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() != webcam) {
            if (webcam != null) {

                webcamPanel.stop();

                remove(webcamPanel);

                webcam.removeWebcamListener(this);
                webcam.close();

                webcam = (Webcam) e.getItem();
                webcam.setViewSize(WebcamResolution.VGA.getSize());
                webcam.addWebcamListener(this);

                System.out.println("Selected " + webcam.getName());

                webcamPanel = new WebcamPanel(webcam, false);
                webcamPanel.setFPSDisplayed(true);

                add(webcamPanel, BorderLayout.CENTER);
                pack();

                Thread t = new Thread() {

                    @Override
                    public void run() {
                        webcamPanel.start();
                    }
                };
                t.setName("example-stopper");
                t.setDaemon(true);
                t.setUncaughtExceptionHandler(this);
                t.start();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked") // For when compiling to jar 
    public void webcamFound(WebcamDiscoveryEvent event) {
        if (webcamPicker != null) {
            webcamPicker.addItem(event.getWebcam());
        }
    }

    @Override
    public void webcamGone(WebcamDiscoveryEvent event) {
        if (webcamPicker != null) {
            webcamPicker.removeItem(event.getWebcam());
        }
    }

    private void takePics(Path p, String strNameFolder) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean blnCanceled = false;

                ProgressMonitor pm = new ProgressMonitor(getParent(), "Initializing...", "", 1, 5);
                pm.setMillisToPopup(1);
                pm.setMillisToDecideToPopup(1);

                for (int intSec = 1; intSec <= 5; intSec++) { // Countdown for the user to adjust
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pm.setProgress(intSec);
                    pm.setNote("Taking pics in " + (5 - intSec) + "s");

                    if (pm.isCanceled()) {
                        blnCanceled = true;
                        pm.setProgress(5);
                        System.out.println("Canceled at " + (5 - intSec));
                        break;
                    }
                }
                pm.close();

                if (!blnCanceled) { // Taking pics
                    pm = new ProgressMonitor(getParent(), "Taking picture", "", 1, intMaxPics);
                    pm.setMillisToPopup(1);
                    pm.setMillisToDecideToPopup(1);

                    for (int intNumPic = 1; intNumPic <= intMaxPics; intNumPic++) { // Will crash if the path doesn't exist while taking the pics !!!
                        try {
                            ImageIO.write(webcam.getImage(), "JPEG", new File(p.toString() + System.getProperty("file.separator") + strNameFolder + intNumPic + ".jpg"));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        pm.setProgress(intNumPic);
                        pm.setNote(intNumPic + " of " + intMaxPics + " (" + (intNumPic * 100 / intMaxPics) + " %)");

                        if (pm.isCanceled()) {
                            pm.setProgress(intMaxPics);
                            System.out.println("Taking pics canceled at " + intNumPic);
                            break;
                        }
                    }
                    pm.close();
                }
            }
        });
        thread.setName("Taking pics");
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(this);
        thread.start();
    }

    private boolean isDirEmpty(final Path directory) throws IOException {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }

    /*private boolean dirExist(Path path) {
        return (Files.isDirectory(path));
    }*/
    
    private void listenerBtnSubmit() {
        final String strJLabelResponse = jFormattedTextField.getText().replace(",", "").trim();
        int i = Integer.parseInt(((!strJLabelResponse.equals("")) ? strJLabelResponse : "0")), maxPic = 2000;

        if (i < 1) JOptionPane.showMessageDialog(null, "Number of pics must be greater than 0", "Can't take 0 pics...common sense bro", JOptionPane.ERROR_MESSAGE);
        else if (i > maxPic) JOptionPane.showMessageDialog(null, "Number of pics must be equal or less than " + maxPic, "TOO...MUCH...DATA...", JOptionPane.ERROR_MESSAGE);
        else {
            intMaxPics = i;
            jLabelMaxPics.setText("Max number for training pics (" + intMaxPics + "): ");
        }
    }
    
    private void listenerBtnTakePics() {
        boolean blnStayLoop = true;
        String strMsg = "Please, enter the name you wish to assign to it", strTitle = "Enter name";
        int m = JOptionPane.QUESTION_MESSAGE;

        while (blnStayLoop) {
            boolean blnEmptyName = false;
            String strAddFolder = JOptionPane.showInputDialog(null, strMsg, strTitle, m);

            try {
                if (strAddFolder.equals("") || strAddFolder == null || strAddFolder.length() < 0) {
                    strMsg = "You didn't enter anything\n" + "Please, enter the name you wish to assign to it:";
                    strTitle = "Error";
                    blnEmptyName = true;
                }
                else {
                    if (Files.isDirectory(path)) {
                        Path p = Paths.get(path.toString() + System.getProperty("file.separator") + strAddFolder);
                        strMsg = "Please, enter the name you wish to assign to it";
                        strTitle = "Enter name";

                        if (!(Files.isDirectory(p))) { // Creates Dir and takePics()
                            try {
                                Files.createDirectory(p);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            blnStayLoop = false;
                            takePics(p, strAddFolder);
                        }
                        else { // Dir exist, delete files if necessary then takePics()
                            try {
                                if (!isDirEmpty(p)) { // Verifie si le Dir contient des files
                                    Object[] o = {"yes, burn them to the ground", "OH HELL NO, GET ME OUTTA HERE"};
                                    int i = JOptionPane.showOptionDialog(null,"Directory \"" + p.toString() + "\" is not EMPTY\nDo you wish to ERASE ALL FILES in it ?", "Directory not empty", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, o, o[1]);

                                    if (i == 0) { // Else, retourne au showInputDialog() plus haut
                                        if (Files.isDirectory(path)) {
                                            if (Files.isDirectory(p)) {
                                                blnStayLoop = false;
                                                delDir(new File(p.toString()), false); // Efface seulement le contenu du Dir
                                                takePics(p, strAddFolder);
                                            }
                                            /*else {
                                                JOptionPane.showMessageDialog(null, "The folder \"" + p.toString() + "\" has already been erased", "Folder \"" + p.toString() + "\" already deleted", JOptionPane.ERROR_MESSAGE);
                                            } */
                                        }
                                        else {
                                            JOptionPane.showMessageDialog(null, "The main folder \"" + path.toString() + "\" has been erased\nPlease relaunch the program to assure it's working correctly\nExiting the software now...", "Main folder \"" + path.toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
                                            System.exit(1);
                                        }
                                    }
                                } else { // Dir ne contient pas des files
                                    blnStayLoop = false;
                                    takePics(p, strAddFolder);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "The main folder \"" + path.toString() + "\" has been erased\nPlease relaunch the program to assure it's working correctly\nExiting the software now...", "Main folder \"" + path.toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }

                if (blnEmptyName) m = JOptionPane.ERROR_MESSAGE;
                else m = JOptionPane.QUESTION_MESSAGE; // Kinda fast to see, but meh
            } catch (NullPointerException e) {
                break;
            }
        }

        //btnTakePics.setEnabled(false); FIND ALTERNATIVE TO setENABLED() !!!!!!!!!!!

        //btnTakePics.setEnabled(true);
    }

    private void delDir(File dir, boolean blnDelDir) {
        for(File file: dir.listFiles()) {
            if (file.isDirectory()) delDir(file, true);
            file.delete();
        }

        if (blnDelDir) dir.delete(); // Efface le Dir aussi
    }

    private void listenerBtnErase() {
        if (Files.isDirectory(path)) {
            ArrayList<String> arrList = new ArrayList<String>();

            for(File file: (new File(path.toString())).listFiles()) if (file.isDirectory()) arrList.add(file.getName());

            if (arrList.size() != 0) {
                Collections.sort(arrList);
                //Collections.reverse(arrList);
                Object[] objList = arrList.toArray(new Object[arrList.size()]);
                String strRemoveFolder = (String)JOptionPane.showInputDialog(null, "Please, choose a folder name to remove", "Remove folder", JOptionPane.INFORMATION_MESSAGE, null, objList, objList[0]);
                if (strRemoveFolder != null) {
                    if (Files.isDirectory(path)) {
                        Path p = Paths.get(path.toString() + System.getProperty("file.separator") + strRemoveFolder);
                        if (Files.isDirectory(p)) delDir(new File(p.toString()), true);
                        else {
                            JOptionPane.showMessageDialog(null, "The folder \"" + p.toString() + "\" has already been erased", "Folder \"" + p.toString() + "\" already deleted", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "The main folder \"" + path.toString() + "\" has been erased\nPlease relaunch the program to assure it's working correctly\nExiting the software now...", "Main folder \"" + path.toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }
            }
            else JOptionPane.showMessageDialog(null, "No folders found", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(null, "The main folder \"" + path.toString() + "\" has been erased\nPlease relaunch the program to assure it's working correctly\nExiting the software now...", "Main folder \"" + path.toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}