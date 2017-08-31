package yanbinwa;

import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

public class HanLPMain
{
    public static void test1()
    {
        System.out.println(HanLP.segment("你好，欢迎使用HanLP汉语处理包！"));
    }
    
    public static void test2()
    {
        List<Term> termList = StandardTokenizer.segment("我吃苹果");
        System.out.println(termList);
    }
    
    public static void test3()
    {
        List<Term> termList = NLPTokenizer.segment("我吃苹果");
        System.out.println(termList);
    }
    
    public static void main(String[] args)
    {
        test2();
        test3();
    }
}
