
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author c012952a
 */
public class ProcessFilesThread implements Runnable {

    static int totalFileCount = 0;
    JLabel statusLabel;
    static int cannotReadFileCount = 0;
    boolean boolGo;
    Timer updateTimer;
    SimpleGUI mainGUI;
    JTextArea scanDisplayTextArea;
    static ArrayList<File> searchDirectoryList = new ArrayList();
    static ArrayList<File> searchSig = new ArrayList();
    static ArrayList<String> executableCriteriaList = new ArrayList();
    static ArrayList<File> resultFiles = new ArrayList();
    ImageIcon tickFile = new ImageIcon(SimpleGUI.class.getResource("\\Images\\file-complete-icon.png"));
    ImageIcon warningFile = new ImageIcon(SimpleGUI.class.getResource("\\Images\\file-warning-icon.png"));
    ImageIcon errorFile = new ImageIcon(SimpleGUI.class.getResource("\\Images\\help-file-icon.png"));
    static boolean errorScan = false;
    boolean cleanScan = true;
    int cannotReadDirCount;
    int errorReadFileCount;
    static byte[] signatureBytes;

    static byte[] fileBytes;

    public boolean isCleanScan() {
        return cleanScan;
    }

    public void setCleanScan(boolean cleanScan) {
        this.cleanScan = cleanScan;
    }

    public void setErrorScan(boolean errorScan) {
        this.errorScan = errorScan;
    }

    public static void setTotalFileCount(int totalFileCount) {
        ProcessFilesThread.totalFileCount = totalFileCount;
    }

    public static void setCannotReadCount(int cannotReadCount) {
        ProcessFilesThread.cannotReadFileCount = cannotReadCount;
    }

    public void setSearchDirectoryList(ArrayList<File> searchDirectoryList) {
        this.searchDirectoryList = searchDirectoryList;
    }

    public void setSearchSig(ArrayList<File> searchSig) {
        this.searchSig = searchSig;
    }

    public void setExecutableCriteriaList(ArrayList<String> executableCriteriaList) {
        this.executableCriteriaList = executableCriteriaList;
    }

    public void setResultFiles(ArrayList<File> resultFiles) {
        this.resultFiles = resultFiles;
    }

    public void setScanDisplayTextArea(JTextArea scanDisplayTextArea) {
        this.scanDisplayTextArea = scanDisplayTextArea;
    }

    public void setBoolGo(boolean boolGo) {
        this.boolGo = boolGo;
    }

    public void setUpdateTimer(Timer updateTimer) {
        this.updateTimer = updateTimer;
    }

    public void setMainGUI(SimpleGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    public void reset() {
        scanDisplayTextArea.setText("");

        totalFileCount = 0;
        cannotReadFileCount = 0;
        resultFiles.clear();
    }

    @Override
    public void run() {

        reset();

        String errorMessage
                = "\t================================================================================"
                + "\n\t\t\t              SCAN COMPLETE                "
                + "\n\t================================================================================"
                + "\n\t\t  "
                + "\n\t\t  "
                + "\n\t\t  "
                + "\n\t\t  "
                + "\n\t================================================================================"
                + "\n\t\t\t          ERROR WHILE SCANNING "
                + "\n\t================================================================================";

        if (searchDirectoryList.isEmpty()) {
            errorScan = true;
            mainGUI.setBool(false);
            scanDisplayTextArea.setText(errorMessage);
            scanDisplayTextArea.setForeground(new java.awt.Color(187, 109, 62));

            JOptionPane.showMessageDialog(mainGUI,
                    "No Directories Specified",
                    "Scan Error",
                    JOptionPane.INFORMATION_MESSAGE,
                    errorFile);

        } else if (searchSig.isEmpty()) {
            errorScan = true;
            mainGUI.setBool(false);
            scanDisplayTextArea.setText(errorMessage);
            scanDisplayTextArea.setForeground(new java.awt.Color(187, 109, 62));

            JOptionPane.showMessageDialog(mainGUI,
                    "No Signature File Specified",
                    "Scan Error",
                    JOptionPane.INFORMATION_MESSAGE,
                    errorFile);
        } else if (executableCriteriaList.isEmpty()) {
            errorScan = true;
            mainGUI.setBool(false);
            scanDisplayTextArea.setText(errorMessage);
            scanDisplayTextArea.setForeground(new java.awt.Color(187, 109, 62));

            JOptionPane.showMessageDialog(mainGUI,
                    "No Executable Specified",
                    "Scan Error",
                    JOptionPane.INFORMATION_MESSAGE,
                    errorFile);

        } else if (!errorScan) {

            String str = "<html>" + "<font color=\"#008000\">" + "Status: " + "<b>" + "Scanning..." + "</b>" + "</font>" + "</html>";

            statusLabel.setText(str);

            long startTime = System.currentTimeMillis();

            for (int a = 0; a < searchDirectoryList.size(); a++) {

                Search(searchDirectoryList.get(a));

                if (a + 1 == searchDirectoryList.size()) {
                    mainGUI.setBool(false);

                }
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            DateFormat outFormat = new SimpleDateFormat("HH:mm:ss:SS");
            outFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = new Date(totalTime);
            String time = outFormat.format(d);

            if (errorScan) {

                scanDisplayTextArea.setForeground(new java.awt.Color(255, 102, 0));
                scanDisplayTextArea.append("\t================================================================================");
                scanDisplayTextArea.append("\n\t\t\t              SCAN ERROR                ");
                scanDisplayTextArea.append("\n\t================================================================================");
                scanDisplayTextArea.append("\n\t\t  Unreadable Files: " + cannotReadFileCount);
                scanDisplayTextArea.append("\n\t\t  Unreadable Directories: " + cannotReadDirCount);
                scanDisplayTextArea.append("\n");
                scanDisplayTextArea.append("\n\t\t  Clean Files: " + (totalFileCount - resultFiles.size() - cannotReadFileCount));
                scanDisplayTextArea.append("\n\t\t  Infected Files: " + resultFiles.size());
                scanDisplayTextArea.append("\n\t\t  Total Files Scanned: " + totalFileCount);
                scanDisplayTextArea.append("\n\t================================================================================");
                scanDisplayTextArea.append("\n\t\t  VIRUS SIGNATURE FORMAT ERROR");
                scanDisplayTextArea.append("\n\t================================================================================");
                JOptionPane.showMessageDialog(mainGUI,
                        "Virus Signature " + signatureString + "Invalid.",
                        "Scan Error",
                        JOptionPane.INFORMATION_MESSAGE,
                        errorFile);

            } else if (!resultFiles.isEmpty()) {

                // change to in the search 
                for (int i = 0; i < resultFiles.size(); i++) {
                    scanDisplayTextArea.append(resultFiles.get(i) + "\n");
                }

                scanDisplayTextArea.setText(scanDisplayTextArea.getText().substring(0, scanDisplayTextArea.getText().length() - 1));

                cleanScan = false;

                scanDisplayTextArea.setForeground(new java.awt.Color(255, 0, 0));

                scanDisplayTextArea.append("\n\n\t================================================================================");
                scanDisplayTextArea.append("\n\t\t\t              SCAN COMPLETE                ");
                scanDisplayTextArea.append("\n\t================================================================================");
                scanDisplayTextArea.append("\n\t\t  Unreadable Files: " + cannotReadFileCount);
                scanDisplayTextArea.append("\n\t\t  Unreadable Directories: " + cannotReadDirCount);
                scanDisplayTextArea.append("\n");
                scanDisplayTextArea.append("\n\t\t  Clean Files: " + (totalFileCount - resultFiles.size() - cannotReadFileCount));
                scanDisplayTextArea.append("\n\t\t  Infected Files: " + resultFiles.size());
                scanDisplayTextArea.append("\n\t\t  Total Files Scanned: " + totalFileCount);
                scanDisplayTextArea.append("\n\t================================================================================");
                scanDisplayTextArea.append("\n\t\t  Time Taken: " + time + " (Hours:Minutes:Seconds:Milliseconds)");
                scanDisplayTextArea.append("\n\t================================================================================");

                try {
                    scanDisplayTextArea.setCaretPosition(scanDisplayTextArea.getLineStartOffset(scanDisplayTextArea.getLineCount() - 1));
                } catch (BadLocationException ex) {
                    Logger.getLogger(ProcessFilesThread.class.getName()).log(Level.SEVERE, null, ex);
                }

                JOptionPane.showMessageDialog(mainGUI,
                        "Scan Completed: n Viruses Found",
                        "Scan Completed",
                        JOptionPane.INFORMATION_MESSAGE,
                        warningFile);

            } else {

                cleanScan = true;

                scanDisplayTextArea.setForeground(new java.awt.Color(33, 115, 70));

                scanDisplayTextArea.append("\t================================================================================");
                scanDisplayTextArea.append("\n\t\t\t              SCAN COMPLETE                ");
                scanDisplayTextArea.append("\n\t================================================================================");
                scanDisplayTextArea.append("\n\t\t  Unreadable Files: " + cannotReadFileCount);
                scanDisplayTextArea.append("\n\t\t  Unreadable Directories: " + cannotReadDirCount);
                scanDisplayTextArea.append("\n");
                scanDisplayTextArea.append("\n\t\t  Clean Files: " + (totalFileCount - resultFiles.size() - cannotReadFileCount));
                scanDisplayTextArea.append("\n\t\t  Infected Files: " + resultFiles.size());
                scanDisplayTextArea.append("\n\t\t  Total Files Scanned: " + totalFileCount);
                scanDisplayTextArea.append("\n\t================================================================================");
                scanDisplayTextArea.append("\n\t\t  Time Taken: " + time + " (Hours:Minutes:Seconds:Milliseconds)");
                scanDisplayTextArea.append("\n\t================================================================================");

                JOptionPane.showMessageDialog(mainGUI,
                        "Scan Completed: No Viruses Found",
                        "Scan Completed",
                        JOptionPane.INFORMATION_MESSAGE,
                        tickFile);

            }
        }

    }

    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
    }

    public void Search(File file) {

        if (file.exists()) {

            if (file.isDirectory()) {
                if (file.canRead()) {

                    File[] listOfFiles = file.listFiles();
                    if (listOfFiles != null) {
                        for (int i = 0; i < listOfFiles.length; i++) {
                            Search(listOfFiles[i]);
                        }
                    }
                } else {
                    cannotReadDirCount++;
                }
            } else if (file.isFile()) {

                if (file.canRead()) {

                    totalFileCount++;

                    for (int a = 0; a < executableCriteriaList.size(); a++) {

                        if (file.getName().endsWith(executableCriteriaList.get(a).toLowerCase()) || file.getName().endsWith(executableCriteriaList.get(a).toUpperCase())) {

                            
                            
                            if (scanExecutableFile(file)){
                                resultFiles.add(file);
                            }

                        }

                    }

                } else {
                    cannotReadFileCount++;
                }

            }
        } else {
            cannotReadFileCount++;
        }
    }
    String signatureString;
    static ArrayList<String> virusSignatureFileTextList = new ArrayList();
    static ArrayList<String> SignatureStringList = new ArrayList();

    public static boolean scanExecutableFile(File specifiedFile) {
        //check if right format

        fileBytes = readFile(specifiedFile);
        String filesHex = bytesToHex(fileBytes);

        for (int a = 0; a < searchSig.size(); a++) {

            try {
                File file = searchSig.get(a);
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    virusSignatureFileTextList.add(line);
                }
                fileReader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int a = 0; a < virusSignatureFileTextList.size(); a++) {
            SignatureStringList.add(virusSignatureFileTextList.get(a).split("=")[0]);
           
        }

        for (int a = 0; a < SignatureStringList.size(); a++) {;
            checkSignature(SignatureStringList.get(a));

            if (!errorScan) {
                String signatureHex = bytesToHex(SignatureStringList.get(a).getBytes());

                
                System.out.println(signatureHex + " : " + filesHex);
                
                int search = search(filesHex, signatureHex);
                
                if (search < filesHex.length()){
                    return true;
                }
            }
        }
        return false;
    }

    public static void checkSignature(String signatureString) {
        boolean matches = signatureString.matches("[a-fA-F0-9]{32}");

        if (!matches) {
            errorScan = true;
        }
    }

    public static byte[] readFile(File speficFile) {

        FileInputStream fileInputStream = null;

        byte[] fileBytesArray = new byte[(int) speficFile.length()];

        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(speficFile);
            fileInputStream.read(fileBytesArray);
            fileInputStream.close();
        } catch (IOException e) {
            cannotReadFileCount++;
        }
        return fileBytesArray;
    }

    public static String bytesToHex(byte[] bytes) {

        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String hex = formatter.toString();
        return hex;
    }

    public static int search(String text, String pattern) {
        int BASE = 256;
        int[] occurrence;
        int n = text.length();
        int m = pattern.length();
        int skip;

        occurrence = new int[BASE];
        for (int c = 0;
                c < BASE;
                c++) {
            occurrence[c] = -1;
        }
        for (int j = 0;
                j < pattern.length();
                j++) {
            occurrence[pattern.charAt(j)] = j;
        }

        for (int i = 0;
                i <= n - m;
                i += skip) {
            skip = 0;
            for (int j = m - 1; j >= 0; j--) {
                if (pattern.charAt(j) != text.charAt(i + j)) {
                    skip = Math.max(1, j - occurrence[text.charAt(i + j)]);
                    break;
                }
            }
            if (skip == 0) {
                return i;
            }
        }
        return n;
    }
}
