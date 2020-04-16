package addbook;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class CopyFile {

    public static void copyFile(File f,String BookName)
    {
        String s1 = f.getName();
        String dir ="F:\\My Academic (Level 1, Term 2)\\Java Programming\\LibraryServer\\Books\\";
        String fileName = dir.concat(s1);
       String s2 = BookName.concat(".pdf");
       String bookName = dir.concat(s2);
        
        File destination = new File("F:\\My Academic (Level 1, Term 2)\\Java Programming\\LibraryServer\\Books");
        try {
            FileUtils.copyFileToDirectory(f, destination);
        } catch (IOException ex) {
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
