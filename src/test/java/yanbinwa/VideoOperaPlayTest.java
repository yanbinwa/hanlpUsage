package yanbinwa;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.csvreader.CsvReader;

import yanbinwa.utils.DigitUtil;

public class VideoOperaPlayTest
{
    private static List<String> textList = new ArrayList<String>();
    private static List<Integer> resultList = new ArrayList<Integer>();
    
    private static final String PART_NUMBER = "([零,一,二,三,四,五,六,七,八,九,十]+)";
    private static String PATTERN_EXCLUDE = "([开,播,放])?(一下)";
    private static String PATTERN_BACKWORD = "(最后|倒数)";
    private static String PATTERN_BACKWORD_1 = "(%s)(第)?([零,一,二,三,四,五,六,七,八,九,十]+)(个|部)";
    private static String PATTERN_CATCH = "(第)([零,一,二,三,四,五,六,七,八,九,十]+)(个|部)";
    private static String PATTERN_CATCH_1 = "([零,一,二,三,四,五,六,七,八,九,十]+)(个|部)";
    
    @Test
    public void test() throws IOException
    {
        String fileName = "/Users/emotibot/Documents/workspace/other/hanlpUsage/text2.csv";
        loadTestCase(fileName);
        for (int i = 0; i < textList.size(); i ++)
        {
            String text = textList.get(i);
            int result = resultList.get(i);
            int getResult = getPlayIndex(text);
            if (result != getResult)
            {
                System.out.println("Error, text is: " + text + "; result is: " + result + "; getResult is: " + getResult);
            }
        }
    }
     
    private void loadTestCase(String csvfile)
    {
        try 
        {
            CsvReader csvReader = new CsvReader(csvfile,',', Charset.forName("GBK"));

            csvReader.readHeaders();
            while (csvReader.readRecord())
            {
                String line = csvReader.getRawRecord();
                String[] elemets = line.split(",");
                textList.add(elemets[0]);
                resultList.add(Integer.parseInt(elemets[1]));
            }
            System.out.println(textList);
            System.out.println(resultList);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private String matched(String patternStr, String text)
    {
        if (patternStr == null || text == null)
        {
            return null;
        }
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find())
        {
            return null;
        }
        return matcher.group(0);
    }
    
    private int getPlayIndex(String text)
    {
        if (text == null)
        {
            return Integer.MIN_VALUE;
        }
        text = adjustText(text);
        String matchStr = matched(PATTERN_BACKWORD, text);
        if (matchStr != null)
        {
            return getPlayIndexFromBack(matchStr, text);
        }
        else
        {
            return getPlayIndexFromBegin(text);
        }
    }
    
    /**
     * 要排除放一下、播一下这类的数字
     * 
     * @param text
     * @return
     */
    private String adjustText(String text)
    {
        if (text == null)
        {
            return null;
        }
        String match = matched(PATTERN_EXCLUDE, text);
        if (match != null)
        {
            text =  text.replaceAll(match, "");
        }
        text = text.replaceAll("两", "二");
        return text;
    }
    
    private int getPlayIndexFromBack(String backword, String text)
    {
        String patternStr = String.format(PATTERN_BACKWORD_1, backword);
        String match = matched(patternStr, text);
        if (match != null)
        {
            return getNumFromString(match) * -1;
        }

        int playIndex = getPlayIndexGenerate(text);
        if (playIndex != Integer.MAX_VALUE)
        {
            return playIndex * -1;
        }
        else
        {
            return -1;
        }
    }
    
    private int getPlayIndexGenerate(String text)
    {
        String match = matched(PATTERN_CATCH, text);
        if (match == null)
        {
            match = matched(PATTERN_CATCH_1, text);
        }
        if (match != null)
        {
            return getNumFromString(match);
        }
        else
        {
            return getNumFromString(text);
        }
    }
    
    private int getNumFromString(String text)
    {
        Pattern pattern = Pattern.compile(PART_NUMBER);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find())
        {
            String numStr = matcher.group(0);
            return DigitUtil.parseDigits(numStr);
        }
        else
        {
            return Integer.MAX_VALUE;
        }
    }
    
    private int getPlayIndexFromBegin(String text)
    {
        int playIndex = getPlayIndexGenerate(text);
        if (playIndex != Integer.MAX_VALUE)
        {
            return playIndex;
        }
        else
        {
            return 1;
        }
    }

}
