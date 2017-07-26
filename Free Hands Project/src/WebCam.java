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

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author Mohamed H. Guelleh (Astrophysics-Guy)
 *
 * adapted code from
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
    
    private int intMaxPics = 50;

    private final String nomApp = "Free Hands"; // Name of the App
    private final Path path = Paths.get(System.getProperty("user.home") + System.getProperty("file.separator") + nomApp); // Main working dir

    @Override
    public void run() {
        /*switch (System.getProperty("file.separator")) { // Check file system, doesn't count for mac (idk honestly), use the one below
            case "/":
                System.out.println("File system is Linux");
                break;
            case "\\":
                System.out.println("File system is Windows");
                break;
            default:
                System.out.println("Don't know file system");
                break;
        }*/

        String os = System.getProperty("os.name"); // Check file system
        if(os.startsWith("Windows")) {
            // make the command for windows
            System.out.println("File system is Windows");
        }
        else {
            if (os.startsWith("Linux")) {
                // make the command for linux
                System.out.println("File system is Linux");
            }
            else {
                // make the command for mac
                System.out.println("File system is Mac");
            }
        }

        if (!(Files.isDirectory(path))) { // Create main working dir
            try {
                Files.createDirectory(path);
            } catch (IOException e) { // Crash
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

        if (webcam == null) { // Affiche un message d'erreur s'il ne trouve pas de WebCam et quitte l'App
            System.out.println("No WebCams found...");
            JOptionPane.showMessageDialog(null, "No WebCams found...", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.addWebcamListener(WebCam.this);

        webcamPanel = new WebcamPanel(webcam, false);
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setFitArea(true); // MEH ...

        JButton btnTakePics = new JButton("Take pics & train CNN");
        btnTakePics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listenerBtnTakePics();
            }
        });
        /*btnTakePics.getModel().addChangeListener(new ChangeListener() { // Mouse hover
            @Override
            public void stateChanged(ChangeEvent e) {
                ButtonModel btnM = (ButtonModel) e.getSource();
                if (btnM.isRollover()) JOptionPane.showMessageDialog(null,"500 by default");
            }
        });*/

        JButton btnErase = new JButton("Delete folder & train CNN");
        btnErase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listenerBtnErase();
            }
        });

        jLabelMaxPics = new JLabel();
        jLabelMaxPics.setText("<html><b><u><i>MAX NUMBER</i></u></b> for training pics (" + intMaxPics + "): </html>");

        jFormattedTextField = new JFormattedTextField(NumberFormat.getIntegerInstance()); // Throws error which may Crash when paste number in it
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

        JButton btnSubmit = new JButton("Submit max number");
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listenerBtnSubmit();
            }
        });

        JButton btnRetrainCNN = new JButton("Train CNN");
        btnRetrainCNN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listenerBtnTrainCNN();
            }
        });

        add(webcamPicker, BorderLayout.NORTH);
        add(webcamPanel, BorderLayout.CENTER);
        add(jPanelBorderSouth, BorderLayout.SOUTH);

        jPanelBorderSouth.add(jPanelMaxPics, BorderLayout.WEST);
        jPanelBorderSouth.add(jPanelTakePics, BorderLayout.EAST);

        jPanelTakePics.add(btnErase);
        jPanelTakePics.add(btnRetrainCNN);
        jPanelTakePics.add(btnTakePics);
        jPanelTakePics.setLayout(new FlowLayout(FlowLayout.CENTER));

        jPanelMaxPics.add(jLabelMaxPics);
        jPanelMaxPics.add(jFormattedTextField);
        /*jPanelMaxPics.add(jComboBox1);
        jPanelMaxPics.add(jComboBox2);
        jPanelMaxPics.add(jComboBox3);*/
        jPanelMaxPics.add(btnSubmit);
        jPanelMaxPics.setLayout(new FlowLayout(FlowLayout.LEFT));

        //pack(); // Use setSize() instead, because it crops out the image; took me a fuck ton of time and found it the hard way
        setSize(new Dimension(1010, 674)); // Height is ~ 2/3 of width
        setLocationRelativeTo(null); // Center the whole thing
        setVisible(true); // Self explanatory

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

                pack(); // You need this when changing WebCams, but it will crop out the image. So use it along setSize() but place the latter after pack()
                setSize(new Dimension(1010, 674)); // Height is ~ 2/3 of width

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
    @SuppressWarnings("unchecked") // Suppressed warning for when compiling to jar
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
                    } catch (InterruptedException e) { // Crash
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
                        } catch (IOException e1) { // Crash
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
        int i = Integer.parseInt(((!strJLabelResponse.equals("")) ? strJLabelResponse : "0")), maxPic = 134217726;

        if (i < 50) JOptionPane.showMessageDialog(null, "<html>Number of pics must be <b><u><i>GREATER THAN</i> 49</u></b> or may cause <b><u><i>ISSUES</i></u></b> with the CNN</html>", "May cause issues with the CNN", JOptionPane.ERROR_MESSAGE); // It's croping out the number when it's in italic, so change it to NOT ITALIC
        else if (i > maxPic) JOptionPane.showMessageDialog(null, "<html>Number of pics must be <b><u><i>EQUAL OR LESS THAN</i> " + maxPic + "</u></b> <html>", "TOO...MUCH...DATA...", JOptionPane.ERROR_MESSAGE); // It's croping out the number when it's in italic, so change it to NOT ITALIC
        else {
            intMaxPics = i;
            jLabelMaxPics.setText("<html><b><u><i>MAX NUMBER</i></u></b> for training pics (" + intMaxPics + "): </html>");
        }
    }
    
    private void listenerBtnTakePics() {
        boolean blnStayLoop = true;
        String strMsg = "Please, enter a name you wish to assign to it", strTitle = "Enter name";
        int m = JOptionPane.QUESTION_MESSAGE;

        while (blnStayLoop) {
            boolean blnEmptyName = false;
            String strAddFolder = JOptionPane.showInputDialog(null, strMsg, strTitle, m);

            try {
                if (strAddFolder.equals("") || strAddFolder == null || strAddFolder.length() < 0) {
                    strMsg = "<html>You didn't <b><u><i>ENTER ANYTHING</i></u></b></html>\n<html>Please, enter a <b><u><i>NAME</i></u></b> you wish to assign to it:</html>";
                    strTitle = "Error";
                    blnEmptyName = true;
                }
                else {
                    if (Files.isDirectory(path)) {
                        Path p = Paths.get(path.toString() + System.getProperty("file.separator") + strAddFolder);
                        strMsg = "Please, enter a name you wish to assign to it";
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
                                    int i = JOptionPane.showOptionDialog(null,"<html> Directory \"" + p.toString() + "\" is not <b><i><u>EMPTY</u></i></b></html>\n<html>Do you wish to <b><i><u>ERASE ALL FILES</u></i></b> in it ?</html>", "Directory " + p.getFileName().toString() + " not empty", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, o, o[1]);

                                    if (i == 0) { // Else, retourne au showInputDialog() plus haut juste apres la loop
                                        if (Files.isDirectory(path)) {
                                            if (Files.isDirectory(p)) {
                                                blnStayLoop = false;
                                                delDir(new File(p.toString()), false); // Efface seulement le contenu du Dir
                                                takePics(p, strAddFolder);
                                            }
                                            /*else {
                                                JOptionPane.showMessageDialog(null, "The folder \"" + p.toString() + "\" has already been erased", "Folder \"" + p.getFileName().toString() + "\" already deleted", JOptionPane.ERROR_MESSAGE);
                                            } */
                                        }
                                        else {
                                            JOptionPane.showMessageDialog(null, "<html>The main folder \"" + path.toString() + "\" has been <b><u><i>ERASED</i></u></b></html>\n<html>Please <b><u><i>RELAUNCH</i></u></b> the program to assure it's working correctly</html>\n<html><b><u><i>EXITING</i></u></b> the software <b><u><i>NOW</i></u></b> ...</html>", "Main folder \"" + path.getFileName().toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
                                            System.exit(1);
                                        }
                                    }
                                } else { // Dir ne contient pas des files
                                    blnStayLoop = false;
                                    takePics(p, strAddFolder);
                                }
                            } catch (IOException e) { // Crash
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "<html>The main folder \"" + path.toString() + "\" has been <b><u><i>ERASED</i></u></b></html>\n<html>Please <b><u><i>RELAUNCH</i></u></b> the program to assure it's working correctly</html>\n<html><b><u><i>EXITING</i></u></b> the software <b><u><i>NOW</i></u></b> ...</html>", "Main folder \"" + path.getFileName().toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }

                if (blnEmptyName) m = JOptionPane.ERROR_MESSAGE;
                else m = JOptionPane.QUESTION_MESSAGE; // Kinda fast to see, but meh
            } catch (NullPointerException e) {
                break;
            }
        }

        //btnTakePics.setEnabled(false); FIND ALTERNATIVE TO setENABLED(), because it can't disable fast enough the buttons !!!!!!!!!!!

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
            for(File file: (new File(path.toString())).listFiles()) if (file.isDirectory()) arrList.add(file.getName().toString());

            if (arrList.size() != 0) {
                Collections.sort(arrList);
                //Collections.reverse(arrList); // Not necessary
                Object[] objList = arrList.toArray(new Object[arrList.size()]);
                String strRemoveFolder = (String)JOptionPane.showInputDialog(null, "<html>Please, choose a folder name to <b><i><u>REMOVE</u></i></b></html>", "Remove folder", JOptionPane.INFORMATION_MESSAGE, null, objList, objList[0]);
                if (strRemoveFolder != null) {
                    if (Files.isDirectory(path)) {
                        Path p = Paths.get(path.toString() + System.getProperty("file.separator") + strRemoveFolder);
                        if (Files.isDirectory(p)) { // Train CNN with retrain.py file !!!
                            delDir(new File(p.toString()), true);
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "<html>The folder \"" + p.toString() + "\" has <b><u><i>ALREADY</i></u></b> been <b><u><i>ERASED</i></u></b></html>", "Folder \"" + strRemoveFolder + "\" already deleted", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "<html>The main folder \"" + path.toString() + "\" has been <b><u><i>ERASED</i></u></b></html>\n<html>Please <b><u><i>RELAUNCH</i></u></b> the program to assure it's working correctly</html>\n<html><b><u><i>EXITING</i></u></b> the software <b><u><i>NOW</i></u></b> ...</html>", "Main folder \"" + path.getFileName().toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }
            }
            else JOptionPane.showMessageDialog(null, "<html><b><u><i>NO FOLDERS</i></u></b> found</html>", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(null, "<html>The main folder \"" + path.toString() + "\" has been <b><u><i>ERASED</i></u></b></html>\n<html>Please <b><u><i>RELAUNCH</i></u></b> the program to assure it's working correctly</html>\n<html><b><u><i>EXITING</i></u></b> the software <b><u><i>NOW</i></u></b> ...</html>", "Main folder \"" + path.getFileName().toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void listenerBtnTrainCNN() {
        if (Files.isDirectory(path)) {
            ArrayList<String> arrList = new ArrayList<String>();
            for (File file : (new File(path.toString())).listFiles()) if (file.isDirectory()) arrList.add(file.getName());

            if (arrList.size() != 0) {
                Collections.sort(arrList);
                //Collections.reverse(arrList);
                Object[] objList = arrList.toArray(new Object[arrList.size()]);
                String strTrainFolder = (String) JOptionPane.showInputDialog(null, "<html>Please, choose a folder name to <b><i><u>TRAIN THE CNN</u></i></b> on</html>", "Train folder", JOptionPane.INFORMATION_MESSAGE, null, objList, objList[0]);
                if (strTrainFolder != null) {
                    if (Files.isDirectory(path)) {
                        Path p = Paths.get(path.toString() + System.getProperty("file.separator") + strTrainFolder);
                        if (Files.isDirectory(p)) { // Train the CNN with retrain.py file !!!
                            System.out.println("Training...");
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "<html>The training folder \"" + p.toString() + "\" has been <b><u><i>ERASED</i></u></b></html>", "Folder \"" + strTrainFolder + "\" already deleted", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "<html>The main folder \"" + path.toString() + "\" has been <b><u><i>ERASED</i></u></b></html>\n<html>Please <b><u><i>RELAUNCH</i></u></b> the program to assure it's working correctly</html>\n<html><b><u><i>EXITING</i></u></b> the software <b><u><i>NOW</i></u></b> ...</html>", "Main folder \"" + path.getFileName().toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }
            } else JOptionPane.showMessageDialog(null, "<html><b><u><i>NO FOLDERS</i></u></b> found</html>", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "<html>The main folder \"" + path.toString() + "\" has been <b><u><i>ERASED</i></u></b></html>\n<html>Please <b><u><i>RELAUNCH</i></u></b> the program to assure it's working correctly</html>\n<html><b><u><i>EXITING</i></u></b> the software <b><u><i>NOW</i></u></b> ...</html>", "Main folder \"" + path.getFileName().toString() + "\" deleted", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}