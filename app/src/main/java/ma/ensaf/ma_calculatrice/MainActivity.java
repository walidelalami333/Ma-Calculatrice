package ma.ensaf.ma_calculatrice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.MathContext;

public class MainActivity extends AppCompatActivity {


    private TextView tvExpression;
    private TextView tvResult;


    private String currentInput = "";
    private double firstNumber = 0;
    private boolean hasFirst = false;
    private String operatorCode = "";
    private String operatorSymbol = "";
    private boolean justCalculated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        updateDisplay();
    }


    // Bouton chiffre (0 a 9)

    public void onDigit(View view) {
        String digit = ((Button) view).getText().toString();

        // Apres un "=", on recommence un nouveau calcul
        if (justCalculated) {
            currentInput = "";
            justCalculated = false;
        }

        if (currentInput.equals("0")) {
            currentInput = digit;
        } else if (currentInput.length() < 15) {
            currentInput = currentInput + digit;
        }
        updateDisplay();
    }


    // Bouton point decimal

    public void onDot(View view) {
        if (justCalculated) {
            currentInput = "";
            justCalculated = false;
        }

        if (currentInput.isEmpty()) {
            currentInput = "0.";
        } else if (!currentInput.contains(".")) {
            currentInput = currentInput + ".";
        }
        updateDisplay();
    }


    // Bouton operateur (+ - x /)

    public void onOperator(View view) {
        Button button = (Button) view;
        String code = button.getTag().toString();
        String symbol = button.getText().toString();

        if (!currentInput.isEmpty()) {
            double value = Double.parseDouble(currentInput);

            if (hasFirst && !operatorCode.isEmpty()) {

                double result = calculate(firstNumber, value, operatorCode);
                if (Double.isNaN(result)) {
                    showDivisionError();
                    return;
                }
                firstNumber = result;
            } else {
                firstNumber = value;
            }
            hasFirst = true;
            currentInput = "";
        } else if (!hasFirst) {

            firstNumber = 0;
            hasFirst = true;
        }

        operatorCode = code;
        operatorSymbol = symbol;
        justCalculated = false;
        updateDisplay();
    }


    // Bouton egal (=)

    public void onEqual(View view) {
        if (!hasFirst || operatorCode.isEmpty() || currentInput.isEmpty()) {
            return;
        }

        double secondNumber = Double.parseDouble(currentInput);
        double result = calculate(firstNumber, secondNumber, operatorCode);

        if (Double.isNaN(result)) {
            showDivisionError();
            return;
        }


        tvExpression.setText(format(firstNumber) + " " + operatorSymbol + " "
                + format(secondNumber) + " =");
        currentInput = format(result);
        tvResult.setText(currentInput);

        hasFirst = false;
        operatorCode = "";
        operatorSymbol = "";
        justCalculated = true;
    }


    // Bouton C : tout effacer

    public void onClear(View view) {
        resetAll();
        updateDisplay();
    }


    // Bouton fleche : effacer le dernier chiffre

    public void onDelete(View view) {
        if (justCalculated || currentInput.isEmpty()) {
            return;
        }
        currentInput = currentInput.substring(0, currentInput.length() - 1);
        if (currentInput.equals("-")) {
            currentInput = "";
        }
        updateDisplay();
    }


    // Bouton +/- : changer le signe

    public void onNegate(View view) {
        if (currentInput.isEmpty() || currentInput.equals("0")) {
            return;
        }
        if (currentInput.startsWith("-")) {
            currentInput = currentInput.substring(1);
        } else {
            currentInput = "-" + currentInput;
        }
        updateDisplay();
    }


    // Bouton % : diviser le nombre par 100

    public void onPercent(View view) {
        if (currentInput.isEmpty()) {
            return;
        }
        double value = Double.parseDouble(currentInput) / 100;
        currentInput = format(value);
        updateDisplay();
    }


    // Le calcul : retourne NaN si on divise par zero

    private double calculate(double a, double b, String code) {
        switch (code) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    return Double.NaN;
                }
                return a / b;
            default:
                return b;
        }
    }

    // Erreur : division par zero
    private void showDivisionError() {
        Toast.makeText(this, R.string.erreur_division, Toast.LENGTH_SHORT).show();
        resetAll();
        updateDisplay();
    }

    // Remet la calculatrice a zero
    private void resetAll() {
        currentInput = "";
        firstNumber = 0;
        hasFirst = false;
        operatorCode = "";
        operatorSymbol = "";
        justCalculated = false;
    }


    private void updateDisplay() {

        if (!currentInput.isEmpty()) {
            tvResult.setText(currentInput);
        } else if (hasFirst) {
            tvResult.setText(format(firstNumber));
        } else {
            tvResult.setText("0");
        }


        if (hasFirst && !operatorCode.isEmpty()) {
            tvExpression.setText(format(firstNumber) + " " + operatorSymbol);
        } else {
            tvExpression.setText("");
        }
    }


    private String format(double value) {
        BigDecimal number = new BigDecimal(value).round(new MathContext(12));
        if (number.signum() == 0) {
            return "0";
        }
        return number.stripTrailingZeros().toPlainString();
    }
}
