package yanbinwa.rewrite.element;

import yanbinwa.rewrite.utils.PinyinUtil;

public class SentenceElement
{
    private String sentence = null;
    private String[] pinyin = null;
    
    public SentenceElement()
    {
        
    }
    
    public SentenceElement(String sentence, String[] pinyin)
    {
        this.sentence = sentence;
        this.pinyin = pinyin;
    }
    
    public SentenceElement(String sentence)
    {
        this.sentence = sentence;
        String pinyinStr = PinyinUtil.getPinyin(sentence);
        pinyin = pinyinStr.trim().split("&");
    }
    
    public String getSentence()
    {
        return this.sentence;
    }
    
    public void setSentence(String sentence)
    {
        this.sentence = sentence;
    }
    
    public String[] getPinyin()
    {
        return this.pinyin;
    }
    
    public void setPinyin(String[] pinyin)
    {
        this.pinyin = pinyin;
    }
    
    @Override
    public String toString()
    {
//        StringBuilder builder = new StringBuilder();
//        builder.append("sentence: " + sentence).append("; ")
//               .append("pinyin: " + pinyin);
//        return builder.toString();
        return this.sentence;
    }
}
