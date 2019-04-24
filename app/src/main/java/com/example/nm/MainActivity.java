package com.example.nm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final double epsilon = Math.pow(10, -6);
    private final double delta = 0.05;
    private final double lowEdge = -2;
    private final double highEdge = 2;

    Button calculate;
    EditText inputA, inputK;
    LinearLayout l1, l2;
    Animation upToDown, downToUp;
    TextView showAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculate = (Button) findViewById(R.id.calculate);

        inputA = (EditText) findViewById(R.id.inputA);
        inputK = (EditText) findViewById(R.id.inputK);

        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);

        showAnswer = (TextView) findViewById(R.id.showAnswer);

        upToDown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downToUp = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        l1.setAnimation(upToDown);
        l2.setAnimation(downToUp);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
    }

    public void calculate() {
        if (inputA.getText().length() > 0 && inputK.getText().length() > 0) {
            if (showAnswer.length() > 0) {
                showAnswer.setText("");
            }
            double aParam = Double.parseDouble(String.valueOf(inputA.getText()));
            double kParam = Double.parseDouble(String.valueOf(inputK.getText()));
            runApprox(aParam, kParam);
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Fields are empty!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public double calcFirstFunc(double x, double y, double kParam) {
        return (Math.tan(x - y + kParam) - x*y);
    }

    public double calcSecondFunc(double x, double y, double aParam) {
        return (aParam*Math.pow(x,2) + 2*Math.pow(y, 2) - 1);
    }

    public double calcFirstDerX(double x, double y, double kParam) {
        return (Math.pow(Math.cos(x - y + kParam), -2) - y);
    }

    public double calcFirstDerY(double x, double y, double kParam) {
        return (-Math.pow(Math.cos(x - y + kParam), -2) - x);
    }

    public double calcSecondDerX(double x, double y, double aParam) {
        return (2*aParam*x);
    }

    public double calcSecondDerY(double x, double y) {
        return (4*y);
    }

    public void runApprox(double aParam, double kParam) {
        double pointX = lowEdge;
        double pointY = lowEdge;
        showAnswer.setText(showAnswer.getText().toString() + "Answer:" + " a = " + aParam + " k = " + kParam);
        while (pointY < highEdge) {
            while (pointX < highEdge) {
                if ((Math.abs(calcFirstFunc(pointX, pointY,kParam)) < 0.1) && (Math.abs(calcSecondFunc(pointX, pointY,aParam)) < 0.1)) {
                    NewtonAlg(pointX, pointY,aParam,kParam);
                }
                pointX = pointX + delta;
            }
            pointX = lowEdge;
            pointY = pointY + delta;
        }
    }

    private void NewtonAlg(double x, double y, double aParam, double kParam) {
        int step = 1;
        System.out.println();
        showAnswer.setText(showAnswer.getText().toString() + "\n" + "\nNew point:\n x = " + x + "\n y = " + y);
        double prevX = x;
        double prevY = y;
        double nextX = prevX + (-calcFirstFunc(x,y,kParam)*calcSecondDerY(x,y) + calcSecondFunc(x,y,aParam)*calcFirstDerY(x,y,kParam))
                / determinant(x,y,aParam,kParam);
        double nextY = prevY + (-calcFirstDerX(x,y,kParam)*calcSecondFunc(x,y,aParam) + calcFirstFunc(x,y,kParam)*calcSecondDerX(x,y,aParam))
                / determinant(x,y,aParam,kParam);
        while ((Math.abs(nextY - prevY) > epsilon)&& (Math.abs(nextX - prevY) > epsilon)) {
            showAnswer.setText(showAnswer.getText().toString() + "\nk: " + step + " \nX: " + nextX + " \nY: " + nextY + " \nf(X,Y): " + calcFirstFunc(nextX, nextY, kParam) + " \ng(X,Y): " + calcSecondFunc(nextX, nextY, aParam));
            prevX = nextX;
            prevY = nextY;
            nextX = prevX + (-calcFirstFunc(prevX,prevY,kParam)*calcSecondDerY(prevX,prevY) + calcSecondFunc(prevX,prevY,aParam)*calcFirstDerY(prevX,prevY,kParam))
                    / determinant(prevX,prevY,aParam,kParam);
            nextY = prevY + (-calcFirstDerX(prevX,prevY,kParam)*calcSecondFunc(prevX,prevY,aParam) + calcFirstFunc(prevX,prevY,kParam)*calcSecondDerX(prevX,prevY,aParam))
                    / determinant(prevX,prevY,aParam,kParam);
            step++;
        }
        System.out.println();
    }

    public double determinant(double x, double y, double aParam, double kParam) {
        double answer = (calcFirstDerX(x,y,kParam)*calcSecondDerY(x,y) - calcFirstDerY(x,y,kParam)*calcSecondDerX(x,y,aParam));
        return answer;
    }
}
