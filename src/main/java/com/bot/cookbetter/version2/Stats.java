package com.bot.cookbetter.version2;

import java.sql.ResultSet;
import java.util.Set;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.util.HashSet;

public class Stats {
    public static void main(String[] args) {
        int[][] feedback_db = new int[][]; // This is an array of 10 arrays
        try {
            i = 0, j = 0;
            Class.forName("com.mysql.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://http://cookbetter.ci2drxnp952j.us-east-1.rds.amazonaws.com:3306/cookbetter?useUnicode=true&characterEncoding=UTF-8&user=cookbetter&password=cookbetter";
            Connection conn = DriverManager.getConnection(connectionUrl);
            String query = "SELECT recipeid, views FROM feedback";
            ResultSet rs = conn.prepareStatement(query).executeQuery();
            while (rs.next()) {
                feedback_db[i][j] = rs.getInt("recipeid");
                feedback_db[i][j] = rs.getInt("views");
                i++
                j++;
            }
            sortbyColumn(feedback_db, col - 1);
        }


    }
    public static void sortbyColumn(int arr[][], int col)
    {
        // Using built-in sort function Arrays.sort
        Arrays.sort(arr, new Comparator<int[]>() {
            @Override
            // Compare values according to columns
            public int compare(final int[] entry1,
                               final int[] entry2) {

                // To sort in descending order revert
                // the '>' Operator
                if (entry1[col] > entry2[col])
                    return 1;
                else
                    return -1;
            }
        });
    }


}