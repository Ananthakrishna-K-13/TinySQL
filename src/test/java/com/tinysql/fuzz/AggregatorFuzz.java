package com.tinysql.fuzz;

import com.tinysql.engine.Aggregator;
import com.tinysql.model.Row;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(JQF.class)
public class AggregatorFuzz {
    @Fuzz
    public void fuzzAggregator(int seed) 
    {
        Random random = new Random(seed);

        int rowCount = random.nextInt(100);
        List<Row> rows = new ArrayList<>();
        
        for (int i = 0; i < rowCount; i++) 
        {
            Row r = new Row(i);
            int type = random.nextInt(3);
            if (type == 0) r.set("val", random.nextInt());
            else if (type == 1) r.set("val", random.nextDouble());
            else r.set("val", "NotANumber");
            
            rows.add(r);
        }

        String[] functions = {"COUNT", "SUM", "AVG", "MIN", "MAX", "INVALID_FUNC"};
        String func = functions[random.nextInt(functions.length)];

        Aggregator.calculate(rows, "val", func);
    }
}