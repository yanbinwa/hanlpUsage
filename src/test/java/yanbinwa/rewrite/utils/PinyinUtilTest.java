package yanbinwa.rewrite.utils;

import org.junit.Test;

public class PinyinUtilTest
{

    @Test
    public void test()
    {
        long timestart1 = System.currentTimeMillis();
        String str1 = "我想看周星驰";
        String str2 = "我想看新星驰";
        for (int i = 0; i < str1.length(); i ++)
        {
            boolean ret = PinyinUtil.comparePinyin2(str1.charAt(i), str2.charAt(i));    
            if (!ret)
            {
                System.out.println("index error " + i);
            }
        }
        long timeend1 = System.currentTimeMillis();
        System.out.println("process work elapsed " + (timeend1 - timestart1) + " ms");   
    }

    @Test
    public void test1()
    {
        long timestart1 = System.currentTimeMillis();
        String str1 = "我想看周星";
        String str2 = PinyinUtil.getPinyin(str1);
        System.out.println(str2);
        long timeend1 = System.currentTimeMillis();
        System.out.println("process work elapsed " + (timeend1 - timestart1) + " ms");   
    }
    
}
