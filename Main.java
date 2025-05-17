import java.util.*;

class RegexParser {

    // 정규 표현식을 파싱하여 List<Object> 형태로 변환하는 메소드
    public List<Object> parseRegex(String regex) {
        if (regex == null || regex.isEmpty()) {
            return null; // 입력이 null이거나 비어 있으면 null 반환
        }
        int[] index = {0}; // 현재 문자열 위치를 추적하는 배열, parseExpression에서 사용
        return parseExpression(regex, index); // 실제 파싱 작업을 수행하는 메소드 호출
    }

    // 정규 표현식에서 '표현식'을 파싱하는 메소드 (최상위 구문 규칙)
    private List<Object> parseExpression(String regex, int[] index) {
        List<Object> term = parseTerm(regex, index); // term 파싱
        while (index[0] < regex.length() && regex.charAt(index[0]) == '+') {
            // '+' 연산자가 있는 동안 반복
            index[0]++; // '+' 연산자 다음 문자로 인덱스 이동
            List<Object> nextTerm = parseTerm(regex, index); // 다음 term 파싱
            List<Object> plusExpr = new ArrayList<>(); // '+' 연산을 나타내는 리스트 생성
            plusExpr.add("+");             // 연산자 추가
            plusExpr.add(term);            // 왼쪽 피연산자 (term) 추가
            plusExpr.add(nextTerm);       // 오른쪽 피연산자 (nextTerm) 추가
            term = plusExpr;             // 현재 term을 '+' 연산 결과로 갱신
        }
        return term; // 최종 term (표현식) 반환
    }

    // 정규 표현식의 · 처리
    private List<Object> parseTerm(String regex, int[] index) {
        List<Object> factor = parseFactor(regex, index); // factor 파싱
        while (index[0] < regex.length() && isConcatenation(regex.charAt(index[0]))) {
            // 연결 연산자가 있는 동안 반복 (연결 연산자는 명시적이지 않음)
            List<Object> nextFactor = parseFactor(regex, index); // 다음 factor 파싱
            List<Object> concatExpr = new ArrayList<>();    // 연결 연산을 나타내는 리스트 생성
            concatExpr.add("·");           // 연결 연산자 '·' 추가
            concatExpr.add(factor);          // 왼쪽 피연산자 (factor) 추가
            concatExpr.add(nextFactor);     // 오른쪽 피연산자 (nextFactor) 추가
            factor = concatExpr;           // 현재 factor를 연결 연산 결과로 갱신
        }
        return factor; // 최종 factor반환
    }

    // 정규 표현식의 * 연산 처리
    private List<Object> parseFactor(String regex, int[] index) {
        List<Object> base = parseBase(regex, index); // base 파싱
        while (index[0] < regex.length() && regex.charAt(index[0]) == '*') {
            // '*' 연산자가 있는 동안 반복
            index[0]++;             // '*' 연산자 다음 문자로 인덱스 이동
            List<Object> starExpr = new ArrayList<>(); // '*' 연산을 나타내는 리스트 생성
            starExpr.add("*");         // 연산자 추가
            starExpr.add(base);        // 피연산자 (base) 추가
            base = starExpr;         // 현재 base를 '*' 연산 결과로 갱신
        }
        return base; // 최종 base (factor) 반환
    }

    // 정규 표현식에서 기본 심볼 및 괄호를 파싱하는 메소드
    private List<Object> parseBase(String regex, int[] index) {
        if (index[0] < regex.length() && regex.charAt(index[0]) == '(') {
            // '('로 시작하면 괄호 안의 표현식 파싱
            index[0]++; // '(' 다음 문자로 인덱스 이동
            List<Object> expression = parseExpression(regex, index); // 괄호 안의 표현식 파싱 (재귀 호출)
            if (index[0] < regex.length() && regex.charAt(index[0]) == ')') {
                // ')'가 있어야 함
                index[0]++; // ')' 다음 문자로 인덱스 이동
                return expression; // 괄호 안의 표현식 반환
            } else {
                throw new RuntimeException("Expected ')'"); // ')'가 없으면 예외 발생
            }
        } else if (index[0] < regex.length() && isAlphaNumeric(regex.charAt(index[0]))) {
            // 알파벳 또는 숫자이면
            List<Object> baseExpr = new ArrayList<>(); // 알파벳/숫자를 담을 리스트 생성
            baseExpr.add(String.valueOf(regex.charAt(index[0]))); // 문자열로 변환하여 추가
            index[0]++; // 다음 문자로 인덱스 이동
            return baseExpr; // 알파벳/숫자 반환
        } else {
            // 그 외의 경우
            if (index[0] >= regex.length()) {
                // 입력 문자열의 끝에 도달했으면 ε 반환
                List<Object> emptyExpr = new ArrayList<>();
                emptyExpr.add("ε"); // ε (empty string) 추가
                return emptyExpr;
            }
            throw new RuntimeException("Unexpected character: " + regex.charAt(index[0])); // 예상치 못한 문자 예외 발생
        }
    }

    // 주어진 문자가 알파벳 또는 숫자인지 확인하는 메소드
    private boolean isAlphaNumeric(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    // 주어진 문자가 연결 연산자의 일부가 될 수 있는지 확인하는 메소드
    private boolean isConcatenation(char c) {
        return isAlphaNumeric(c) || c == '('; // 알파벳/숫자 또는 '('이면 연결 연산 가능
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

        // 1. size 계산 결과 출력
        int size = calculateSize(result);
        System.out.println("Size: " + size);

        // 2. 상태 및 arc 수 계산 및 결과 출력
        calculateStatesAndTransitions(result, size);

        PrettyBinaryTreePrinter.TreeNode root = PrettyBinaryTreePrinter.fromNestedList(result);
        PrettyBinaryTreePrinter.print(root);
    }

    private static int calculateSize(List<Object> expr) {
        if (expr == null || expr.isEmpty()) {
            return 0;
        }
        int size = 1;
        for (Object obj : expr) {
            if (obj instanceof List) {
                size += calculateSize((List<Object>) obj);
            }
        }
        return size;
    }

    public static void calculateStatesAndTransitions(Object regex, int size) {
        class Analyzer {
            int stateCount = 0;
            int transitionCount = 0;

            void analyze(Object node) {
                if (node instanceof String s) {
                    // 알파벳/숫자 심볼이면 상태 2개, 전이 1개
                    stateCount += 2;
                    transitionCount += 1;
                }
                else if (node instanceof List<?> list && !list.isEmpty()) {
                    String operator = list.get(0).toString();

                    switch (operator) {
                        case "+":
                            for (int i = 1; i < list.size(); i++) {
                                analyze(list.get(i));
                            }
                            stateCount += 2;
                            transitionCount += 2 * (list.size() - 1);
                            break;

                        case "·":
                            for (int i = 1; i < list.size(); i++) {
                                analyze(list.get(i));
                            }
                            break;

                        case "*":
                            analyze(list.get(1));
                            stateCount += 2;
                            transitionCount += 4;
                            break;
                    }
                }
            }
        }

        Analyzer analyzer = new Analyzer();
        analyzer.analyze(regex);
        System.out.println("상태 수 : " + analyzer.stateCount + " 상한 : "+ size*2);
        System.out.println("전이 수 : " + analyzer.transitionCount + " 상한 : "+ size*4);
    }
}