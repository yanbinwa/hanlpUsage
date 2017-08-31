package yanbinwa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import junit.framework.TestCase;
import yanbinwa.utils.DigitUtil;

public class HandleTest extends TestCase
{
    private static final String TEST1 = "播放第一部";
    private static final String TEST2 = "播放倒数第十五部";
    private static final String PATTERN1 = "(播放第)((\\\\d+)|([零,一,二,三,四,五,六,七,八,九,十,百,千,万,亿])+)(部)";
    private static final String PATTERN2 = "(播放倒数第)((\\\\d+)|([零,一,二,三,四,五,六,七,八,九,十,百,千,万,亿])+)(部)";
    private static final String PART_NUMBER = "((\\\\d+)|[零,一,二,三,四,五,六,七,八,九,十,百,千,万,亿]+)";
    
    private int getIndex(String str)
    {
        Pattern pattern = Pattern.compile(PATTERN1);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find())
        {
            System.out.println(matcher.group(0));
            Pattern pattern1 = Pattern.compile(PART_NUMBER);
            matcher = pattern1.matcher(str);
            if (matcher.find())
            {
                String numStr = matcher.group(0);
                int num = DigitUtil.parseDigits(numStr);
                System.out.println("step1");
                return num;
            }
            else
            {
                return 0;
            }
        }
        
        pattern = Pattern.compile(PATTERN2);
        matcher = pattern.matcher(str);
        if (matcher.find())
        {
            System.out.println(matcher.group(0));
            Pattern pattern1 = Pattern.compile(PART_NUMBER);
            matcher = pattern1.matcher(str);
            if (matcher.find())
            {
                String numStr = matcher.group(0);
                int num = DigitUtil.parseDigits(numStr);
                System.out.println("step2");
                return num * -1;
            }
        }
        else
        {
            return 0;
        }
        
        return 0;
    }
    
    @Test
    public void test()
    {
        System.out.println(getIndex(TEST1));
        System.out.println(getIndex(TEST2));
    }
}
