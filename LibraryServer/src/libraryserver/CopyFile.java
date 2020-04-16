package libraryserver;


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class CopyFile {

    public static void copyFile(File f,String BookName)
    {
        String s1 = f.getName();
        //String dir = "F:\\course materials\\level 1 term 2\\CSE\\java\\project\\librarymanagement3\\Books\\";
        String dir ="F:\\LibraryServer\\Books\\";
        String fileName = dir.concat(s1);
       String s2 = BookName.concat(".pdf");
       String bookName = dir.concat(s2);
        
        //File destination = new File("F:\\course materials\\level 1 term 2\\CSE\\java\\project\\librarymanagement3\\Books");
        File destination = new File("F:\\LibraryServer\\Books");
        try {
            FileUtils.copyFileToDirectory(f, destination);
            //JOptionPane.showMessageDialog(null, "copy successfull");
        } catch (IOException ex) {
           // JOptionPane.showMessageDialog(null, "copy unsuccessfull");
            Logger.getLogger(CopyFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(fileName+ " " + bookName);
        File OldFile = new File(fileName);
        File renameFile = new File(bookName);
        if(OldFile.exists()){
            System.out.println("oldFile Found");
        }
        if(OldFile.renameTo(renameFile)){
            System.out.println("oldFile renamed");
        }
    }
    
}
