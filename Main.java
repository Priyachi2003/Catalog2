import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject inputJson = (JSONObject) parser.parse(new FileReader("input.json"));
            JSONArray testCases = (JSONArray) inputJson.get("testCases");

            
            for (Object obj : testCases) {
                JSONObject testCase = (JSONObject) obj;
                BigInteger secret = calculateSecret(testCase);
                System.out.println("Secret: " + secret);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error reading or parsing input.json: " + e.getMessage());
        }
    }

    private static BigInteger calculateSecret(JSONObject testCase) {
        JSONObject keys = (JSONObject) testCase.get("keys");
        if (keys == null) {
            throw new IllegalArgumentException("Keys object is missing.");
        }

        int n = Integer.parseInt(keys.get("n").toString());
        int k = Integer.parseInt(keys.get("k").toString());

        List<Integer> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

       
        for (int i = 1; i <= n; i++) {
            JSONObject root = (JSONObject) testCase.get(String.valueOf(i));
            if (root != null) {
                int base = Integer.parseInt(root.get("base").toString());
                BigInteger y = new BigInteger(root.get("value").toString(), base);
                xValues.add(i);
                yValues.add(y);
            }
        }

        
        if (xValues.size() < k) {
            throw new IllegalArgumentException("Not enough valid roots to solve the polynomial.");
        }

     
        return findConstantTerm(xValues, yValues, k);
    }

    private static BigInteger findConstantTerm(List<Integer> xValues, List<BigInteger> yValues, int k) {
        BigInteger c = BigInteger.ZERO;

     
        for (int i = 0; i < k; i++) {
            BigInteger term = yValues.get(i);
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    
                    BigInteger numerator = BigInteger.valueOf(0 - xValues.get(j));
                    BigInteger denominator = BigInteger.valueOf(xValues.get(i) - xValues.get(j));
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            c = c.add(term);
        }

        return c;
    }
}
