package yanbinwa.rewrite.utils;

import yanbinwa.rewrite.element.SentenceElement;

public class EditDistance
{
    public static int getEditDistance(String str1, String str2)
    {
        int len1 = str1.length();
        int len2 = str2.length();
        int[][] dif = new int[len1 + 1][len2 + 1];
        for (int a = 0; a <= len1; a++) 
        {  
            dif[a][0] = a;  
        }
        for (int a = 0; a <= len2; a++) 
        {  
            dif[0][a] = a;  
        }
        int temp;  
        for (int i = 1; i <= len1; i++) {  
            for (int j = 1; j <= len2; j++) {  
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) 
                {  
                    temp = 0;  
                } 
                else 
                {  
                    temp = 1;  
                }  
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);  
            }  
        } 
        return dif[len1][len2]; 
    }
    
    public static int getEditDistance(SentenceElement ele1, SentenceElement ele2)
    {
        String sentence1 = ele1.getSentence();
        String[] pinyin1 = ele1.getPinyin();
        String sentence2 = ele2.getSentence();
        String[] pinyin2 = ele2.getPinyin();
        int len1 = sentence1.length();
        int len2 = sentence2.length();
        
        int[][] dif = new int[len1 + 1][len2 + 1];
        for (int a = 0; a <= len1; a++) 
        {  
            dif[a][0] = a;  
        }
        for (int a = 0; a <= len2; a++) 
        {  
            dif[0][a] = a;  
        }
        int temp;  
        for (int i = 1; i <= len1; i++) 
        {  
            for (int j = 1; j <= len2; j++) 
            {  
                if (sentence1.charAt(i - 1) == sentence2.charAt(j - 1)) 
                {  
                    temp = 0;
                } 
                else
                {
                    String pingyinStr1 = pinyin1[i - 1];
                    String pingyinStr2 = pinyin2[j - 1];
                    if (pingyinStr1.equals(pingyinStr2))
                    {
                        temp = 0;
                    }
                    else
                    {
                        if (pingyinStr1.endsWith("g"))
                        {
                            pingyinStr1 = pingyinStr1.substring(0, pingyinStr1.length() - 1);
                        }
                        if (pingyinStr2.endsWith("g"))
                        {
                            pingyinStr2 = pingyinStr2.substring(0, pingyinStr2.length() - 1);
                        }
                        if (pingyinStr1.equals(pingyinStr2))
                        {
                            temp = 0;
                        }
                        else
                        {
                            temp = 1;
                        }
                    }
                }
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
            }  
        } 
        return dif[len1][len2];
    }
    
    private static int min(int a, int b, int c)
    {
        return a < b ? Math.min(a, c) : Math.min(b, c);
    }
}
