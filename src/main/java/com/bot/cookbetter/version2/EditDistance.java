package com.bot.cookbetter.version2;

/**
 * @author Shrikanth N C (snaraya7)
 */
public class EditDistance
    {

        public static void main(String[] arg){

            System.out.println(computeScore("egg", "egg"));
            System.out.println(computeScore("e", "egg"));
            System.out.println(computeScore("eggs", "e"));

        }

        private static int score = 0;

        public static int computeScore(String string1 , String string2 )
        {

            score = 0;

            if (string1.length() > string2.length()){
                 traverse(string1, string2, 0, string2.length() - 1);
            }else{
                traverse(string2, string1, 0, string1.length() - 1);
            }

            return  score;
        }

        private static void traverse(String longer, String shorter, int start, int end){

           if (longer.contains(shorter)){
               score = Math.max(shorter.length(), score);
           }else{


               if (start < end && end < shorter.length())
               {
                   traverse(longer, shorter.substring(start, end), start, (end+1));
                   traverse(longer, shorter.substring(start, end), (start+1), end);
               }


           }
        }
    }