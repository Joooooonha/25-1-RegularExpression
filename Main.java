import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class RegexParser {
    public List<Object> parseRegex(String regex) {//regex는 입력되는 정규 표현식
        if (regex == null || regex.isEmpty()) {//빈 문자열이 입력되는 경우 null 값 반환
            return null;
        }
        int[] index = {0}; // 정규 표현식 인덱스 추적
        return parseExpr(regex, index); // List<Object> 형식으로 반환
    }

    // 표현식 파싱 및 + 연산자 처리
    private List<Object> parseExpr(String regex, int[] index) {
        Object term = parseTerm(regex, index); // 항 파싱
        while (index[0] < regex.length() && regex.charAt(index[0]) == '+') {
            index[0]++; // '+' 연산자 다음 위치로 이동
            Object nextTerm = parseTerm(regex, index); // 다음 항 파싱
            List<Object> exprList = new ArrayList<>(); // '+' 표현식 리스트 생성
            exprList.add("+");          // 연산자 추가
            exprList.add(term);         // 왼쪽 피연산자 추가
            exprList.add(nextTerm);    // 오른쪽 피연산자 추가
            term = exprList;           // 현재 표현식을 '+' 표현식으로 갱신
        }
        return (List<Object>) term; // 최종 표현식 반환
    }

    // 표현식 파싱 및 · 연산자를 처리
    private Object parseTerm(String regex, int[] index) {
        Object factor = parseFactor(regex, index); // 인수 파싱
        while (index[0] < regex.length() && isConcat(regex.charAt(index[0]))) {
            Object nextFactor = parseFactor(regex, index); // 다음 인수 파싱
            List<Object> termList = new ArrayList<>(); // '·' 표현식 리스트 생성
            termList.add("·");         // 연산자 추가
            termList.add(factor);        // 왼쪽 피연산자 추가
            termList.add(nextFactor);   // 오른쪽 피연산자 추가
            factor = termList;          // 현재 항을 '·' 표현식으로 갱신
        }
        return factor; // 최종 항 반환
    }

    // 표현식 파싱 및 * 연산자 처리
    private List<Object> parseFactor(String regex, int[] index) {
        Object base = parseBase(regex, index); // 기본 요소 파싱
        while (index[0] < regex.length() && regex.charAt(index[0]) == '*') {
            index[0]++;             // '*' 연산자 다음 위치로 이동
            List<Object> factorList = new ArrayList<>(); // '*' 표현식 리스트 생성
            factorList.add("*");         // 연산자 추가
            factorList.add(base);        // 피연산자 추가
            base = factorList;          // 현재 인수를 '*' 표현식으로 갱신
        }
        return (List<Object>) base; // 최종 인수 반환
    }

    //기본 심볼 처리
    private Object parseBase(String regex, int[] index) {
        if (index[0] < regex.length() && regex.charAt(index[0]) == '(') { // '(' 처리
            index[0]++; // '(' 다음 위치로 이동
            Object expression = parseExpr(regex, index); // 괄호 안의 표현식 파싱
            if (index[0] < regex.length() && regex.charAt(index[0]) == ')') { // ')' 처리
                index[0]++; // ')' 다음 위치로 이동
                return expression; // 괄호 안의 표현식 반환
            } else {
                throw new RuntimeException("Expected ')'"); // 닫는 괄호 없음
            }
        } else if (index[0] < regex.length() && isAlphaNum(regex.charAt(index[0]))) { // 알파벳 또는 숫자 처리
            return String.valueOf(regex.charAt(index[0])); // 문자열 형태로 반환
        } else {
            if (index[0] >= regex.length()) { // 입력 끝 도달
                return "ε"; // 빈 문자열 나타내는 'ε' 반환
            }
            throw new RuntimeException("Unexpected character: " + regex.charAt(index[0])); // 예외 발생
        }
    }

    //입력 심볼 영어, 숫자인지 확인
    private boolean isAlphaNum(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    //입력 문자 c가 연결 연산자로 처리되는지 확인
    private boolean isConcat(char c) {
        return isAlphaNum(c) || c == '(';
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("정규 표현식을 입력하세요: ");
        String regex = scanner.nextLine(); // 사용자로부터 정규 표현식 입력
        scanner.close();

        RegexParser parser = new RegexParser();      // RegexParser 객체 생성
        List<Object> result = parser.parseRegex(regex); // 정규 표현식 파싱하여 결과 얻음

        System.out.println("변환된 표현식: " + result); // 결과 출력
    }
}

