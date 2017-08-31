package yanbinwa.rewrite;

import org.junit.Test;

public class SegementTest
{

    @Test
    public void test()
    {
        String text = "我要看周星驰的电影";
        char[] str2char = text.toCharArray();
        String[] sInputResult = new String[str2char.length];
        for (int t = 0; t < str2char.length; t ++)
        {
            sInputResult[t] = String.valueOf(str2char[t]);
            System.out.println(String.valueOf(str2char[t]));
        }
    }

}
