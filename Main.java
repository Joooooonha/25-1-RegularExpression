import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class RegexParser {

    // 정규 표현식을 파싱하여 List<Object> 형태로 변환하는 메소드
    public List<Object> parseRegex(String regex) {
        if (regex == null || regex.isEmpty()) {
            return null;
        }
        int[] index = {0};
        return parseExpression(regex, index);
    }

    private List<Object> parseExpression(String regex, int[] index) {
        List<Object> term = parseTerm(regex, index);
        while (index[0] < regex.length() && regex.charAt(index[0]) == '+') {
            index[0]++;
            List<Object> nextTerm = parseTerm(regex, index);
            List<Object> plusExpr = new ArrayList<>();
            plusExpr.add("+");
            plusExpr.add(term);
            plusExpr.add(nextTerm);
            term = plusExpr;
        }
        return term;
    }

    private List<Object> parseTerm(String regex, int[] index) {
        List<Object> factor = parseFactor(regex, index);
        while (index[0] < regex.length() && isConcatenation(regex.charAt(index[0]))) {
            List<Object> nextFactor = parseFactor(regex, index);
            List<Object> concatExpr = new ArrayList<>();
            concatExpr.add("·");
            concatExpr.add(factor);
            concatExpr.add(nextFactor);
            factor = concatExpr;
        }
        return factor;
    }

    private List<Object> parseFactor(String regex, int[] index) {
        List<Object> base = parseBase(regex, index);
        while (index[0] < regex.length() && regex.charAt(index[0]) == '*') {
            index[0]++;
            List<Object> starExpr = new ArrayList<>();
            starExpr.add("*");
            starExpr.add(base);
            base = starExpr;
        }
        return base;
    }

    private List<Object> parseBase(String regex, int[] index) {
        if (index[0] < regex.length() && regex.charAt(index[0]) == '(') {
            index[0]++;
            List<Object> expression = parseExpression(regex, index);
            if (index[0] < regex.length() && regex.charAt(index[0]) == ')') {
                index[0]++;
                return expression;
            } else {
                throw new RuntimeException("Expected ')'");
            }
        } else if (index[0] < regex.length() && isAlphaNumeric(regex.charAt(index[0]))) {
            List<Object> baseExpr = new ArrayList<>();
            baseExpr.add(String.valueOf(regex.charAt(index[0])));
            index[0]++;
            return baseExpr;
        } else {
             if (index[0] >= regex.length()) {
                List<Object> emptyExpr = new ArrayList<>();
                emptyExpr.add("ε");
                return emptyExpr;
            }
            throw new RuntimeException("Unexpected character: " + regex.charAt(index[0]));
        }
    }

    private boolean isAlphaNumeric(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    private boolean isConcatenation(char c) {
        return isAlphaNumeric(c) || c == '(';
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("정규 표현식을 입력하세요: ");
        String regex = scanner.nextLine();
        scanner.close();

        RegexParser parser = new RegexParser();
        List<Object> result = parser.parseRegex(regex);

        System.out.println("변환된 표현식: " + result);
    }
}


