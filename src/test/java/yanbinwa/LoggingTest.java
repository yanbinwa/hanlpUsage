package yanbinwa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class LoggingTest
{
    @Test
    public void test() throws IOException
    {        
        
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        FileReader reader = new FileReader("controller.log.yanbinwa");
        BufferedReader br = new BufferedReader(reader);
       
        String str = null;
       
        while((str = br.readLine()) != null) {
            if (str.contains("- Time Check "))
            {
                String str1 = str.split("- Time Check ")[1].trim();
                String module = str1.split(":")[0].trim();
                String delayStr = str1.split(":")[1].trim();
                List<Integer> delayList = map.get(module);
                if (delayList == null)
                {
                    delayList = new ArrayList<Integer>();
                    map.put(module, delayList);
                }
                delayList.add(Integer.parseInt(delayStr));
                System.out.println(str);
            }            
        }
        
        for(String key : map.keySet())
        {
            System.out.println("-------" + key + "--------");
            List<Integer> delay = map.get(key);
            System.out.println(delay);
            System.out.println();
        }
        
       
        br.close();
        reader.close();
    }

}
