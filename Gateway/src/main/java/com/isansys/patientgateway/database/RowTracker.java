package com.isansys.patientgateway.database;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class RowTracker
{
    private final SortedSet<Integer> numbers = new TreeSet<>();

    public void addRow(String database_row_as_string)
    {
        int database_row = Integer.parseInt(database_row_as_string);
        numbers.add(database_row);
    }


    public ArrayList<RowRange> getRowRanges()
    {
        final ArrayList<RowRange> row_ranges = new ArrayList<>();

        Integer start = null;
        Integer end = null;

        for(Integer num : numbers)
        {
            if(start == null)
            {
                // Initialize
                start = num;
                end = num;
            }
            else if( end.equals( num - 1 ) )
            {
                // Next number in range
                end = num;
            }
            else
            {
                // There's a gap
                row_ranges.add(new RowRange(start, end));

                start = num;
                end = num;
            }
        }

        // Handle the last range
        row_ranges.add(new RowRange(start, end));

        return row_ranges;
    }

}
