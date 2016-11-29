package edu.oswego.permaculturemonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Vi on 11/28/16.
 */

public class SoilActivity extends AppCompatActivity {
    private LineGraphSeries<DataPoint> moistSeries;
    private Graph moistgraph;
    private int lastX = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soil_layout);

        //create dataquery
        DataQuery dq = new DataQuery();
        //create reading
        Reading read = new Reading();
        //create readingsanalizers: one for each type of reading
        ReadingsAnalizer moistra = new ReadingsAnalizer(dq.getLatestMoists());

        //create new graph objects from the readinganalizers and dataquery
        moistgraph = new Graph(((int) dq.getLowValue()), ((int) dq.getHighValue()), dq.getFrom().getNanos(),
                dq.getTo().getNanos(), ((int) (dq.getHighValue() - dq.getLowValue())),
                ((int) (dq.getFrom().getNanos() - dq.getTo().getNanos())), moistra.getList());
        GraphView moistGraph = (GraphView) findViewById(R.id.moistgraph);
        //phSeries = new LineGraphSeries<>(hmgraph.getSeries());
        moistSeries = new LineGraphSeries<>();
        //humGraph.addSeries(phSeries);

        moistGraph.addSeries(moistSeries);


    }

    //method that updates the series and graphs in realtime
    public void addEntry() {
        moistSeries.appendData((moistgraph.getSeries()[lastX++]), true, moistgraph.getSeries().length);
    }

    public void onResume() {
        //simulate real time with thread that appends data to the graph
        final DataQuery dq = new DataQuery();
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < dq.getLatestMoists().size(); i++) {
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
