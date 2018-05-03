package red.dim.dynamiclog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import red.dim.monitor.core.Monitor;
import red.dim.monitor.core.method.Advice;
import red.dim.monitor.core.method.Aspect;

/**
 * Created by dim on 18/03/12.
 */
public class MainActivity extends Activity {

    private EditText targetET;
    private EditText methodId;
    private CheckBox beforeCB;
    private CheckBox afterCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        targetET = findViewById(R.id.tagertET);
        methodId = findViewById(R.id.methodET);
        beforeCB = findViewById(R.id.beforeCB);
        afterCB = findViewById(R.id.afterCB);
        findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Aspect> aspects = new ArrayList<>();
                Aspect aspect = new Aspect();
                aspect.advices = new ArrayList<>();
                aspect.target = targetET.getText().toString();
                Advice advice = new Advice();
                advice.before = beforeCB.isChecked();
                advice.after = afterCB.isChecked();;
                advice.methodId = Integer.valueOf(methodId.getText().toString());
                aspect.advices.add(advice);
                aspects.add(aspect);
                Monitor.getInstance().execute(aspects, false);
            }
        });
        findViewById(R.id.method1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TestMethod().method1(System.currentTimeMillis());
            }
        });
        findViewById(R.id.method2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TestMethod().method2(v.getId());
            }
        });
        findViewById(R.id.method3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TestMethod().method3(v.getId());
            }
        });
    }

}
