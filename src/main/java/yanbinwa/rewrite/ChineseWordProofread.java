package yanbinwa.rewrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yanbinwa.rewrite.element.SentenceElement;
import yanbinwa.rewrite.utils.EditDistance;

public class ChineseWordProofread
{
    private static int selectNum = 2;
    
    public static int hasError = 0;
    long totalTokensCount;
    Map<String, Integer> wordCountMap = null;
    Map<Integer, List<SentenceElement>> movieLengthToMovieMap = null;
    
    public ChineseWordProofread(String argu1,String argu2)
    {
        this.totalTokensCount = 0L;
        this.wordCountMap = calculateTokenCount(argu2);
        this.movieLengthToMovieMap = cacheMovieName(argu1);
    }
    
    public Map<String,Integer> calculateTokenCount(String afterWordSegFile)
    {
        Map<String,Integer> wordCountMap = new HashMap<String,Integer>();
        File movieInfoFile = new File(afterWordSegFile);
        BufferedReader movieBR = null;
        try 
        {
            movieBR = new BufferedReader(new FileReader(movieInfoFile));
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("result file not found " + movieInfoFile);
            e.printStackTrace();
        }
        
        String wordsline = null;
        try 
        {
            while ((wordsline = movieBR.readLine()) != null)
            {
                String[] words = wordsline.trim().split(" ");
                for (int i = 0; i < words.length; i++)
                {
                    int wordCount = wordCountMap.get(words[i]) == null ? 0 : wordCountMap.get(words[i]);
                    wordCountMap.put(words[i], wordCount + 1);
                    totalTokensCount += 1;
                    //这里将该word与之后的word连起来，作为一个新的word，加入计算
                    if (words.length > 1 && i < words.length - 1)
                    {
                        StringBuffer wordStrBuf = new StringBuffer();
                        wordStrBuf.append(words[i]).append(words[i + 1]);
                        int wordStrCount = wordCountMap.get(wordStrBuf.toString()) == null ? 0 : wordCountMap.get(wordStrBuf.toString());
                        wordCountMap.put(wordStrBuf.toString(), wordStrCount+1);
                        totalTokensCount += 1;
                    }
                }               
            }
        } 
        catch (IOException e) 
        {
            System.out.println("read movie_result.txt file failed");
            e.printStackTrace();
        }
        
        return wordCountMap;
    }
    
    public Map<Integer, List<SentenceElement>> cacheMovieName(String movieTXT)
    {
        Map<Integer, List<SentenceElement>> movieLengthToMovieNameMap = new HashMap<Integer, List<SentenceElement>>();
        File movieNameFile = new File(movieTXT);
        BufferedReader movieBR = null;
        try 
        {
            movieBR = new BufferedReader(new FileReader(movieNameFile));
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("movie.txt file not found");
            e.printStackTrace();
        }
        
        String moviename = null;
        try 
        {
            while ((moviename = movieBR.readLine()) != null)
            {
                int length = moviename.length();
                List<SentenceElement> movieNameList = movieLengthToMovieNameMap.get(length);
                if (movieNameList == null)
                {
                    movieNameList = new ArrayList<SentenceElement>();
                    movieLengthToMovieNameMap.put(length, movieNameList);
                }
                SentenceElement element = new SentenceElement(moviename.trim());
                movieNameList.add(element);
            }
        } 
        catch (IOException e) 
        {
            System.out.println("read movieNameFile file failed: " + movieNameFile);
            e.printStackTrace();
        }
        return movieLengthToMovieNameMap;
    }
    
    public float probBetweenTowTokens(String t1,String t2)
    {
        String t1t2 = t1 + t2;
        int count = wordCountMap.get(t1t2) == null ? 0 : wordCountMap.get(t1t2);
        if (totalTokensCount > 0 )
        {
            return (float)count/totalTokensCount;
        }
        else
        {
            return (float) 0.0;
        }            
    }
    
    /**
     * 
     * 
     * @param token
     * @return
     */
    public float probBetweenTowTokens(String token)
    {
        int count = wordCountMap.get(token) == null ? 0 : wordCountMap.get(token);
        if (totalTokensCount > 0 )
        {
            return (float) count / totalTokensCount;
        }
        else
        {
            return (float) 0.0;
        }        
    }
    
    /**
     * 
     * 
     * @param sInputResult
     * @return
     */
    public List<String> getCorrectTokens(String[] sInputResult)
    {
        List<String> correctTokens = new ArrayList<String>();
        float probOne = 0;
        List<Integer> isCorrect = new ArrayList<Integer>();
        for (int i = 0; i < sInputResult.length; i++)
        {
            //这里获取到该单个字对应的token数量占总token的比例
            probOne = probBetweenTowTokens(sInputResult[i]);
            if (probOne <= 0)
            {
                isCorrect.add(i, 0);
            } 
            else 
            {
                isCorrect.add(i, 1);
            }
        }
     
        //含有两个字符以上的单词
        if (sInputResult.length > 2)
        {
            //这里所有的单个的字都匹配上了
            if (!isCorrect.contains(0))
            {
                //这里是将该sInputResult中所有连在一起的组合的可能性，都查找一遍
                for (int i = 0; i < sInputResult.length - 1; i++)
                {
                    StringBuffer tokenbuf = new StringBuffer();
                    tokenbuf.append(sInputResult[i]);
                    for(int j = i + 1; j < sInputResult.length; j++)
                    {
                        float b = probBetweenTowTokens(tokenbuf.toString() + sInputResult[j]);
                        //这里应该是保证最大匹配吧
                        if (b > 0)
                        {
                            tokenbuf.append(sInputResult[j]);
                        }
                        else
                        {
                            hasError = 1;
                            if (j < sInputResult.length-1 && 
                                    probBetweenTowTokens(tokenbuf.toString() + sInputResult[j] + sInputResult[j + 1]) > 0)
                            {
                                tokenbuf.append(sInputResult[j] + sInputResult[j + 1]);
                            }
                            else
                            {
                                break;
                            }
                        }                       
                    }
                    correctTokens.add(tokenbuf.toString());
                }
                
                if (probBetweenTowTokens(sInputResult[sInputResult.length - 1]) > 0)
                {
                    correctTokens.add(sInputResult[sInputResult.length - 1]);
                }
            }
            else 
            {
                for (int i = 0; i < sInputResult.length - 1; i++)
                {
                    StringBuffer tokenbuf = new StringBuffer();
                    int a = isCorrect.get(i);
                    //单个词匹配上了
                    if (a > 0)
                    {
                        tokenbuf.append(sInputResult[i]);
                        for(int j = i + 1; j < sInputResult.length; j++)
                        {
                            float b = probBetweenTowTokens(tokenbuf.toString() + sInputResult[j]);
                            if (b > 0) 
                            {
                                tokenbuf.append(sInputResult[j]);
                            }
                            else
                            {
                                hasError = 2;
                                break;
                            }
                        }
                        correctTokens.add(tokenbuf.toString());
                    }
                    //虽然这个单词没有匹配成功，但是其与后面的匹配成功了，所以也是可以加入的
                    else if (probBetweenTowTokens(sInputResult[i] + sInputResult[i + 1]) > 0.0)
                    {
                        tokenbuf.append(sInputResult[i]).append(sInputResult[i + 1]);
                        for(int j = i + 2; j < sInputResult.length; j++)
                        {
                            float b = probBetweenTowTokens(tokenbuf.toString() + sInputResult[j]);
                            if (b > 0) 
                            {
                                tokenbuf.append(sInputResult[j]);
                            }
                            else
                            {
                                hasError = 2;
                                break;
                            }
                        }
                        //这里的correctTokens可能为空
                        correctTokens.add(tokenbuf.toString());
                    }
                }
            }
        } 
        else if (sInputResult.length == 2)
        {
            if (probBetweenTowTokens(sInputResult[0] + sInputResult[1]) > 0)
            {
                correctTokens.add(sInputResult[0] + sInputResult[1]);
            }
        }
        return correctTokens ;
    }
    
    /**
     * 这里应该是找到前两个可以匹配到最长的token，同时要选择出现频率较低的，这样可以包含更对的特征信息
     * 同时还要对于其子字符串中提取出出现频率最大的字串，将其从字符串中删除
     * 
     * @param sInputResult
     * @return
     */
    public String[] getMaxAndSecondMaxSequnce(String[] sInputResult)
    {
        //这句话中每个单词开始能够找到的最长的String，并且该String是在tokenMap中可以找的，把这个写入到correctTokens中
        List<String> correctTokens = getCorrectTokens(sInputResult);
        String[] maxAndSecondMaxSeq = new String[2];
        if (correctTokens.size() == 0) 
        {
            return null;
        }
        else if (correctTokens.size() == 1)
        {
            maxAndSecondMaxSeq[0]=correctTokens.get(0);
            maxAndSecondMaxSeq[1]=correctTokens.get(0);
            return maxAndSecondMaxSeq;
        }
        
        String maxSequence = correctTokens.get(0);
        String maxSequence2 = correctTokens.get(correctTokens.size() - 1);
        String littleword = "";
        for (int i = 1; i < correctTokens.size(); i++)
        {
            //优先匹配最长的，所以最长的应该是在maxSequence中的
            if (correctTokens.get(i).length() > maxSequence.length())
            {
                maxSequence = correctTokens.get(i);
            } 
            //如果长度一致，就比较出现的频率
            else if (correctTokens.get(i).length() == maxSequence.length())
            {
                //单个单词
                if (correctTokens.get(i).length() == 1)
                {
                    if (probBetweenTowTokens(correctTokens.get(i)) > probBetweenTowTokens(maxSequence)) 
                    {
                        //为什么是maxSequence2？
                        maxSequence2 = correctTokens.get(i);
                    }
                }
                //select words with smaller probability for multi-word, because the smaller has more self information
                else if (correctTokens.get(i).length() > 1)
                {
                    if (probBetweenTowTokens(correctTokens.get(i)) <= probBetweenTowTokens(maxSequence)) 
                    {
                        //这里为什么选择频率低的呢？
                        maxSequence2 = correctTokens.get(i);
                    }
                }
            } 
            else if (correctTokens.get(i).length() > maxSequence2.length())
            {
                maxSequence2 = correctTokens.get(i);
            } 
            else if (correctTokens.get(i).length() == maxSequence2.length())
            {
                if (probBetweenTowTokens(correctTokens.get(i)) > probBetweenTowTokens(maxSequence2))
                {
                    maxSequence2 = correctTokens.get(i);
                }
            }
        }
        //delete the sub-word from a string
        if (maxSequence2.length() == maxSequence.length())
        {
            int maxseqvaluableTokens = maxSequence.length();
            int maxseq2valuableTokens = maxSequence2.length();
            float min_truncate_prob_a = 0 ;
            float min_truncate_prob_b = 0;
            String aword = "";
            String bword = "";
            //这里在选择出maxSequence和maxSequence2后，会查看其subString出现的最大频次，出现频率越多，说明其带有的特征信息越少，所以要把这部分除去
            for (int i = 0; i < correctTokens.size(); i++)
            {
                float tokenprob = probBetweenTowTokens(correctTokens.get(i));
                if ((!maxSequence.equals(correctTokens.get(i))) && maxSequence.contains(correctTokens.get(i)))
                {
                    if (tokenprob >= min_truncate_prob_a)
                    {
                        min_truncate_prob_a = tokenprob ;
                        aword = correctTokens.get(i);
                    }
                }
                else if ((!maxSequence2.equals(correctTokens.get(i))) && maxSequence2.contains(correctTokens.get(i)))
                {
                    if (tokenprob >= min_truncate_prob_b)
                    {
                        min_truncate_prob_b = tokenprob;
                        bword = correctTokens.get(i);
                    }
                }
            }
            //System.out.println(min_truncate_prob_a + " VS " + min_truncate_prob_b);
            //maxSequence的subString频次较小，说明maxSequence2的substring肯定也有了，就会对token的权重进行修改
            //这里是什么情况？？
            if (aword.length() > 0 && min_truncate_prob_a < min_truncate_prob_b)
            {
                //对长度进行修改
                maxseqvaluableTokens -= 1 ;
                littleword = maxSequence.replace(aword, "");
            }
            else 
            {
                maxseq2valuableTokens -= 1 ;
                String temp = maxSequence2;
                //如果maxSequence也包含maxSequence2除去共性较多的信息后，那么littleword就是maxSequence2
                if (maxSequence.contains(temp.replace(bword, "")))
                {
                    littleword =  maxSequence2;
                }
                else 
                {
                    littleword =  maxSequence2.replace(bword, "");
                }
            }
            
            if (maxseqvaluableTokens < maxseq2valuableTokens)
            {
                maxSequence = maxSequence2;
                maxSequence2 = littleword;
            }
            else 
            {
                maxSequence2 = littleword;
            }
            
        }
        maxAndSecondMaxSeq[0] = maxSequence;
        maxAndSecondMaxSeq[1] = maxSequence2;
        System.out.println("maxAndSecondMaxSeq0 " + maxAndSecondMaxSeq[0]);
        System.out.println("maxAndSecondMaxSeq1 " + maxAndSecondMaxSeq[1]);
        return maxAndSecondMaxSeq;
    }
    
    
    public List<String> proofreadAndSuggest(String sInput)
    {
        List<SentenceElement> correctedList = new ArrayList<SentenceElement>();
        List<SentenceElement> crtTempList = new ArrayList<SentenceElement>();

        Calendar startProcess = Calendar.getInstance();
        char[] str2char = sInput.toCharArray();
        String[] sInputResult = new String[str2char.length];
        for (int t = 0; t < str2char.length; t++)
        {
            sInputResult[t] = String.valueOf(str2char[t]);
        }
        String[] MaxAndSecondMaxSequnce = getMaxAndSecondMaxSequnce(sInputResult);
        
        if (hasError !=0)
        {
            if (MaxAndSecondMaxSequnce.length>1)
            {
                String maxSequence = MaxAndSecondMaxSequnce[0];
                String maxSequence2 = MaxAndSecondMaxSequnce[1];
                //这里遍历movieName，可以做成并行处理，提高效率
                List<SentenceElement> retMoveNameList = getCandidateMoveNameList(sInput);
                
                for (int j = 0; j < retMoveNameList.size(); j++)
                {
                    String movie = retMoveNameList.get(j).getSentence();
                    if (maxSequence2.equals(""))
                    {
                        if (movie.contains(maxSequence)) 
                        {
                            correctedList.add(retMoveNameList.get(j));
                        }
                    }
                    else 
                    {
                        if (movie.contains(maxSequence) && movie.contains(maxSequence2))
                        {
                            crtTempList.add(retMoveNameList.get(j));
                        }
                        else if (movie.contains(maxSequence)) 
                        {
                            correctedList.add(retMoveNameList.get(j));
                        }
                        else if (movie.contains(maxSequence2))
                        {
                            correctedList.add(retMoveNameList.get(j));
                        }
                    }
                }
                
                if (crtTempList.size() > 0)
                {
                    correctedList.clear();
                    correctedList.addAll(crtTempList);
                }
                SentenceElement element = new SentenceElement(sInput);
                correctedList = sortList(element, correctedList);
                if (hasError == 1) 
                {
                    System.out.println("拼写正确，查不到电影: [" + sInput + "] 你是否想看 :" + correctedList.toString() + " ?");
                }
                else 
                {
                    System.out.println("有拼写错误, 查不到电影: [" + sInput + "] 你是否想看 :" + correctedList.toString() + " ?");
                }
            } 
            else 
            {
                System.out.println("查不到电影: [" + sInput + "]，请重试");
            }
        }
        else 
        {
            System.out.println("查到电影: [" + sInput + "]");
        }
                
        Calendar endProcess = Calendar.getInstance();
        long elapsetime = (endProcess.getTimeInMillis() - startProcess.getTimeInMillis());
        System.out.println("用时 [" + elapsetime + " ms]");
        
        return getMoveNameList(correctedList);
    }
    
    private List<SentenceElement> getCandidateMoveNameList(String sInput)
    {
        List<SentenceElement> retMoveNameList = new ArrayList<SentenceElement>();
        int moveLength = sInput.length();
        for (int i = moveLength - 1; i <= moveLength + 2; i ++)
        {
            List<SentenceElement> moveNameList = movieLengthToMovieMap.get(i);
            if (moveNameList != null)
            {
                retMoveNameList.addAll(moveNameList);
            }
        }
        return retMoveNameList;
    }
    
    @SuppressWarnings("unused")
    private List<String> sortList(final String originName, List<String> originList)
    {
        Collections.sort(originList, new Comparator<String>() 
        {
            public int compare(String o1, String o2)
            {
                int distance1 = EditDistance.getEditDistance(o1, originName);
                int distance2 = EditDistance.getEditDistance(o2, originName);
                if (distance1 > distance2)
                {
                    return 1;
                }
                else if (distance1 < distance2)
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
        });
        if (originList.size() > selectNum)
        {
            return originList.subList(0, selectNum);
        }
        else
        {
            return originList;
        }
    }
    
    private List<SentenceElement> sortList(final SentenceElement originName, List<SentenceElement> originList)
    {
        Collections.sort(originList, new Comparator<SentenceElement>() 
        {
            public int compare(SentenceElement o1, SentenceElement o2)
            {
                int distance1 = EditDistance.getEditDistance(o1, originName);
                int distance2 = EditDistance.getEditDistance(o2, originName);
                if (distance1 > distance2)
                {
                    return 1;
                }
                else if (distance1 < distance2)
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
        });
        if (originList.size() > selectNum)
        {
            return originList.subList(0, selectNum);
        }
        else
        {
            return originList;
        }
    }
    
    private List<String> getMoveNameList(List<SentenceElement> elementList)
    {
        List<String> moveNameList = new ArrayList<String>();
        for (SentenceElement element : elementList)
        {
            moveNameList.add(element.getSentence());
        }
        return moveNameList;
    }
}
