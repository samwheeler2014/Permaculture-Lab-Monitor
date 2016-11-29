package edu.oswego.permaculturemonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Vi on 11/28/16.
 */

public class HumidityActivity extends AppCompatActivity {
    private LineGraphSeries<DataPoint> humSeries;
    private Graph hmgraph;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.humidity_layout);

        //create dataquery
        DataQuery dq = new DataQuery();
        //create reading
        Reading read = new Reading();
        //create readingsanalizers: one for each type of reading
        ReadingsAnalizer hmra = new ReadingsAnalizer(dq.getLatestHumids());

        //create new graph objects from the readinganalizers and dataquery
        hmgraph = new Graph(((int) dq.getLowValue()), ((int) dq.getHighValue()), dq.getFrom().getNanos(), dq.getTo().getNanos(), ((int) (dq.getHighValue() - dq.getLowValue())), ((int) (dq.getFrom().getNanos() - dq.getTo().getNanos())), hmra.getList());
        GraphView humGraph = (GraphView) findViewById(R.id.humgraph);
        //humSeries = new LineGraphSeries<>(hmgraph.getSeries());
        humSeries = new LineGraphSeries<>();
        //humGraph.addSeries(humSeries);

        humGraph.addSeries(humSeries);


    }

    //method that updates the series and graphs in realtime
    public void addEntry() {
        humSeries.appendData((hmgraph.getSeries()[lastX++]), true, hmgraph.getSeries().length);
    }

    public void onResume() {
        //simulate real time with thread that appends data to the graph
        final DataQuery dq = new DataQuery();
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < dq.getLatestHumids().size(); i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                    //sleep to slow down the add of entries
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

