package com.guier.tfidf;

import com.guier.pojo.Question;
import com.guier.vo.QVo;
import lombok.Getter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.util.*;

@Getter
public class TFIDF {
    private PriorityQueue<QVo> queue = new PriorityQueue<>(Comparator.comparing(QVo::getTfidf).reversed());

    public void call(String in, List<Question> list) throws IOException {
        HashMap<String, Integer> map1 = analyzer_IK(in);
        HashMap<String, Integer> map2 = null;
        ArrayList<int[]> matrix = null;
        System.out.println(map1);
        for (Question question : list) {
            map2 = analyzer_IK(question.getQuestion());
            matrix = vectorMatrix(map1, map2);
            double tfidf1 = calIFIDF(matrix);
            queue.add(new QVo(question, tfidf1));
            System.out.println(map2);
            System.out.println(question + "相似性：" + tfidf1);
        }
    }

    private HashMap<String, Integer> analyzer_IK(String text) throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream(null, text);
        CharTermAttribute charTerm = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        HashMap<String, Integer> map = new HashMap<>();
        while (tokenStream.incrementToken()) {
            String key = charTerm.toString();
            if (map.containsKey(key)) {
                map.compute(key, (K, V) -> ++V);
            } else {
                map.put(key, 1);
            }
        }
        return map;
    }

    private ArrayList<int[]> vectorMatrix(HashMap<String, Integer> m1, HashMap<String, Integer> m2) {
        LinkedHashMap<String, Integer> m3 = new LinkedHashMap<>();
        ArrayList<int[]> list = new ArrayList<>();
        m3.putAll(m1);
        m3.putAll(m2);
        Set<String> keys = m3.keySet();
        //        System.out.println(m3);
        //        System.out.println(strings);
        int[] _m1 = new int[m3.size()];
        int[] _m2 = new int[m3.size()];
        int index = 0;
        for (String key : keys) {
            if (m1.containsKey(key)) {
                _m1[index] = m1.get(key);
            }
            if (m2.containsKey(key)) {
                _m2[index] = m2.get(key);
            }
            index++;
        }
        list.add(_m1);
        list.add(_m2);
        return list;
    }

    private double calIFIDF(ArrayList<int[]> list) {
        int[] m1 = list.get(0);
        int[] m2 = list.get(1);
        int fenmu = 0;
        int fenzi1 = 0;
        int fenzi2 = 0;
        for (int i = 0, len = m1.length; i < len; i++) {
            fenmu += m1[i] * m2[i];
            fenzi1 += m1[i] * m1[i];
            fenzi2 += m2[i] * m2[i];
        }
        System.out.println(fenmu + "-" + fenzi1 + "-" + fenzi2);
        Double tfidf = (fenmu * 1.0) / (Math.sqrt(fenzi1) * Math.sqrt(fenzi2));
        System.out.println(String.format("%.3f", tfidf));
        return tfidf;
    }
}
