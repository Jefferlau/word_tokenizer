package com.jusfoun;

import com.jusfoun.tokenizer.WordTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.io.File;

@SpringBootApplication
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner lookup(WordTokenizer wordTokenizer) {
        return args -> {
            String inputFile = "corpus.txt";
            String outputFile = "resultbig.txt";

            if (args.length > 0) {
                inputFile = getFilePath(args[0], inputFile);
                outputFile = getFilePath(args[1], outputFile);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("inputFile: " + inputFile);
                logger.debug("outputFile: " + outputFile);
            }

            String[] filterStrings = {"<content>", "/<content>"};

            wordTokenizer.readFileAndAnalysis(inputFile, outputFile, filterStrings);
        };
    }

    private String getFilePath(String arg, String defaultValue) {
        String filePath;
        if (StringUtils.isEmpty(arg)) {
            filePath = defaultValue;
        } else if (arg.startsWith("/")) {
            filePath = arg;
        } else {
            filePath = System.getProperty("user.dir") + File.separator + arg;
        }
        return filePath;
    }

}
