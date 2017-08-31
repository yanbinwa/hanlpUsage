package yanbinwa.rewrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

public class ChineseWordProofreadTest
{

    private static final String ORIGIN_FILE_PATH = "/Users/emotibot/Documents/workspace/other/hanlpUsage/file/emotibot2.txt";
    private static final String SEGEMENT_FILE_PATH = "/Users/emotibot/Documents/workspace/other/hanlpUsage/file/emotibot_result.txt";
    private static final String UNIT_TEST_FILE_PATH = "/Users/emotibot/Documents/workspace/other/hanlpUsage/file/unitTestMovieList.txt";
    
    @Test
    public void test()
    {   
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        String startInitTime = sdf.format(new java.util.Date()); 
        System.out.println(startInitTime+" ---start initializing work---");
        ChineseWordProofread cwp = new ChineseWordProofread(ORIGIN_FILE_PATH, SEGEMENT_FILE_PATH);
        String endInitTime = sdf.format(new java.util.Date());
        System.out.println(endInitTime+" ---end initializing work---");
        
        BufferedReader br = null;
        try 
        {
            br = new BufferedReader(new FileReader(new File(UNIT_TEST_FILE_PATH)));
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
        
        int totalLineNum = 0 ;
        int positiveNum = 0 ;
        String line = null;
        List<String> result = null;
        Calendar startProcess = Calendar.getInstance();
        try 
        {
            while ((line=br.readLine()) != null)
            {
                totalLineNum += 1 ;
                String[] line_gbk = line.trim().split("\t");
                String errorName = line_gbk[1];
                String normalName = line_gbk[0];
                result = cwp.proofreadAndSuggest(errorName);
                if (result.contains(normalName)) 
                {
                    positiveNum ++ ;
                }
                else
                {
                    System.out.println(">>>>>>>>>>>>希望的电影是: [" + normalName + "]，但是推荐的电影是: " + result.toString());
                }
                System.out.println("");
            }
            br.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        Calendar endProcess = Calendar.getInstance();
        long elapsetime = (endProcess.getTimeInMillis()-startProcess.getTimeInMillis()) ;
        System.out.println("process work elapsed " + elapsetime + " ms");
        
        System.out.println("-------total forcast result-------");
        float ratio = (float) ((1.0*positiveNum/totalLineNum)*100) ;
        System.out.println("the ratio of shot is : " + positiveNum + "/" + totalLineNum + " = " + ratio + "%");
    }
}
