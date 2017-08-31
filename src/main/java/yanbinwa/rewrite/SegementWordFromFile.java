package yanbinwa.rewrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;

import java.io.FileInputStream;  
import java.io.InputStreamReader;
import java.util.List;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer; 

public class SegementWordFromFile
{
    private String originFilePath = null;
    private String segementFilePath = null;
    
    private static final String ORIGIN_FILE_PATH = "/Users/emotibot/Documents/workspace/other/hanlpUsage/file/emotibot2.txt";
    private static final String SEGEMENT_FILE_PATH = "/Users/emotibot/Documents/workspace/other/hanlpUsage/file/emotibot_result.txt";

    public SegementWordFromFile(String originFilePath, String segementFilePath)
    {
        this.originFilePath = originFilePath;
        this.segementFilePath = segementFilePath;
    }
    
    public void runSegement() throws Exception
    {
        boolean ret = checkFiles();
        if (!ret)
        {
            throw new Exception();
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(originFilePath)));
        FileWriter fw = new FileWriter(segementFilePath);
        try
        {
            String line = null;
            while((line = br.readLine()) != null)
            {
                List<Term> termList = StandardTokenizer.segment(line);
                String output = "";
                for(Term term : termList)
                {
                    output = output + term.word + " ";
                }
                fw.write(output.trim() + "\r\n");
            }
        }
        finally
        {
            if (br != null)
            {
                br.close();
            }
            if (fw != null)
            {
                fw.close();
            }
        }
    }
    
    private boolean checkFiles()
    {
        File originFile = new File(originFilePath);
        if (!originFile.exists() || !originFile.isFile())
        {
            System.out.println("origin file not exist");
            return false;
        }
        
        File segementFile = new File(segementFilePath);
        if (segementFile.exists())
        {
            if (segementFile.isDirectory())
            {
                System.out.println("segement file is exist and is directory");
                return false;
            }
            else
            {
                segementFile.delete();
            }
        }
        return true;
    }
    
    public static void main(String[] args)
    {
        SegementWordFromFile segementWordFromFile = new SegementWordFromFile(ORIGIN_FILE_PATH, SEGEMENT_FILE_PATH);
        try
        {
            segementWordFromFile.runSegement();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
