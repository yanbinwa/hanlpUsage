package yanbinwa.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import yanbinwa.rewrite.utils.EditDistance;

public class EditDistanceTest
{

    @Test
    public void test()
    {
        List<String> arrays = new ArrayList<String>();
        arrays.add("西游新传");
        arrays.add("西游记后传");
        arrays.add("浙版新《西游记》片花");
        arrays.add("大话西游");
        arrays.add("小话西游");
        String name = "大话西游";
        for (String newName : arrays)
        {
            System.out.println("name " + name + "; new name " + newName + "; diction " + EditDistance.getEditDistance(name, newName));
        }
        arrays = sortList(name, arrays);
        System.out.println(arrays);
    }
    
    private static List<String> sortList(final String originName, List<String> originList)
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
        return originList;
    }

}
