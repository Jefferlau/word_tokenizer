package com.jusfoun.tokenizer;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 分词器
 * Created by liutiyang on 2017/4/14.
 */
@Component
public class WordTokenizer {
    private static Logger logger = LoggerFactory.getLogger(WordTokenizer.class);
    private int termCount = 0;
    private Set<String> wordSet = new HashSet<>();

    public void readFileAndAnalysis(String filePath, String outFilePath, String[] filterStrings) throws IOException {
        logger.info("============ 开始 ===============");
        long start = System.currentTimeMillis();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
        String str;
        while ((str = in.readLine()) != null) {
            str = str.trim();
            str = filterString(str, filterStrings);

            if (str.length() > 0) {
                nlpAnalysisWrite(outFilePath, str);
            }
        }
        // testOut(sb.toString());
        long end = System.currentTimeMillis();

        logger.info("Term Count: " + termCount);
        logger.info("Word Count: " + wordSet.size());
        logger.info("Takes(S): " + ((end - start)/1000));
        logger.info("============ 完成 ===============");
    }

    private String filterString(String content, String[] filterStrings) {
        if (filterStrings != null && filterStrings.length > 0) {
            for (String filterString : filterStrings) {
                content = content.replaceAll(filterString, "");
            }
        }
        return content;
    }

    private void nlpAnalysisWrite(String filePath, String content) throws IOException {
        Result result = NlpAnalysis.parse(content);
        List<Term> termList = result.getTerms();

        StringBuilder sb = new StringBuilder();
        termCount += termList.size();
        for (Term term : termList) {
            sb.append(term.getName().trim()).append(" ");
            wordSet.add(term.getName().trim());
        }
        writeFile(filePath, sb.toString());
    }

    private void writeFile(String filePath, String content) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));
        out.write(content);
        out.flush();
        out.close();
    }

    private void testOut(String content, int size) {
        KeyWordComputer keyWordComputer = new KeyWordComputer(size);
        for (Keyword keyword : keyWordComputer.computeArticleTfidf(content)) {
            System.out.println(keyword.getName() + " " + keyword.getScore());
        }
    }

    private String nlpAnalysis(String content) {
        Result result = NlpAnalysis.parse(content);
        // System.out.println(result);
        List<Term> termList = result.getTerms();
        System.out.println("termList.size: " + termList.size());
        // testOut(content, termList.size());

        return result.toString();
    }
}
