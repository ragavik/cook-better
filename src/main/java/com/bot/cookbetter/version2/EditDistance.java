package com.bot.cookbetter.version2;

/**
 * @author Shrikanth N C (snaraya7)
 */
public class EditDistance
    {
       public static int computeScore(String string1 , String string2 )
        {
            if (string1.length() > string2.length()){
                return traverse(string1, string2, 0, string2.length() - 1);
            }else{
                return traverse(string2, string1, 0, string1.length() - 1);
            }
        }

        private static int traverse(String longer, String shorter, int start, int end){

           if (longer.contains(shorter)){
               return shorter.length();
           }else{

                System.out.println(longer+" - "+shorter+" "+start+" "+end);
              int case1 =  traverse(longer, shorter.substring(start, end), (start+1), end);
              int case2 =  traverse(longer, shorter.substring(start, end), start, (end+1));


               return Math.max(case1,case2);
           }
        }
    }